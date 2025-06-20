package chav1961.nn.vocab.filters;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chav1961.nn.api.interfaces.Stemmer;
import chav1961.purelib.basic.CharUtils;
import chav1961.purelib.basic.Utils;

public class RuleBasedStemmer implements Stemmer {
    private static final Pattern PERFECTIVEGROUND = Pattern.compile("((ив|ивши|ившись|ыв|ывши|ывшись)|((?<=[ая])(в|вши|вшись)))$");
    private static final Pattern REFLEXIVE = Pattern.compile("(с[яь])$");
    private static final Pattern ADJECTIVE = Pattern.compile("(ее|ие|ые|ое|ими|ыми|ей|ий|ый|ой|ем|им|ым|ом|его|ого|ему|ому|их|ых|ую|юю|ая|яя|ою|ею)$");
    private static final Pattern PARTICIPLE = Pattern.compile("((ивш|ывш|ующ)|((?<=[ая])(ем|нн|вш|ющ|щ)))$");
    private static final Pattern VERB = Pattern.compile("((ила|ыла|ена|ейте|уйте|ите|или|ыли|ей|уй|ил|ыл|им|ым|ен|ило|ыло|ено|ят|ует|уют|ит|ыт|ены|ить|ыть|ишь|ую|ю)|((?<=[ая])(ла|на|ете|йте|ли|й|л|ем|н|ло|но|ет|ют|ны|ть|ешь|нно)))$");
    private static final Pattern NOUN = Pattern.compile("(а|ев|ов|ие|ье|е|иями|ями|ами|еи|ии|и|ией|ей|ой|ий|й|иям|ям|ием|ем|ам|ом|о|у|ах|иях|ях|ы|ь|ию|ью|ю|ия|ья|я)$");
    private static final Pattern RVRE = Pattern.compile("^(.*?[аеиоуыэюя])(.*)$");
    private static final Pattern DERIVATIONAL = Pattern.compile(".*[^аеиоуыэюя]+[аеиоуыэюя].*ость?$");
    private static final Pattern DER = Pattern.compile("ость?$");
    private static final Pattern SUPERLATIVE = Pattern.compile("(ейше|ейш)$");

    private static final Pattern I = Pattern.compile("и$");
    private static final Pattern P = Pattern.compile("ь$");
    private static final Pattern NN = Pattern.compile("нн$");

	@Override
	public CharSequence resolve(CharSequence source) {
		if (Utils.checkEmptyOrNullString(source)) {
			throw new IllegalArgumentException("Source can't be null or empty");
		}
		else {
			final CharSequence	result = resolveInternal(CharUtils.toString(source));
			
			return result != null ? result : source;
		}
	}

	@Override
	public CharSequence resolve(final CharSequence source, final Stemmer... chain) {
		// TODO Auto-generated method stub
		if (Utils.checkEmptyOrNullString(source)) {
			throw new IllegalArgumentException("Source can't be null or empty");
		}
		else if (chain == null || Utils.checkArrayContent4Nulls(chain) >= 0) {
			throw new IllegalArgumentException("Chain is null or contains nulls inside");
		}
		else {
			final CharSequence	result = resolveInternal(CharUtils.toString(source));

			if (result != null) {
				return result;
			}
			else if (chain.length == 0) {
				return source;
			}
			else {
				final Stemmer[][]	pieces = Utils.splitArray(chain, 0);
				
				return pieces[0][0].resolve(source, pieces[1]);
			}
		}
	}

	@Override
	public CharSequence resolve(final CharSequence source, final Function<CharSequence, CharSequence> resolver) {
		if (Utils.checkEmptyOrNullString(source)) {
			throw new IllegalArgumentException("Source can't be null or empty");
		}
		else if (resolver == null) {
			throw new NullPointerException("Resolver can't be null");
		}
		else {
			CharSequence	result = resolveInternal(CharUtils.toString(source));

			return result != null ? result : resolver.apply(source);
		}
	}
	
	private static String resolveInternal(final String source) {
        String word = source.toLowerCase().replace('ё', 'е');
        Matcher m = RVRE.matcher(word);
        
        if (m.matches()) {
            String pre = m.group(1);
            String rv = m.group(2);
            String temp = PERFECTIVEGROUND.matcher(rv).replaceFirst("");
            if (temp.equals(rv)) {
                rv = REFLEXIVE.matcher(rv).replaceFirst("");
                temp = ADJECTIVE.matcher(rv).replaceFirst("");
                if (!temp.equals(rv)) {
                    rv = temp;
                    rv = PARTICIPLE.matcher(rv).replaceFirst("");
                } 
                else {
                    temp = VERB.matcher(rv).replaceFirst("");
                    if (temp.equals(rv)) {
                        rv = NOUN.matcher(rv).replaceFirst("");
                    } 
                    else {
                        rv = temp;
                    }
                }

            } 
            else {
                rv = temp;
            }

            rv = I.matcher(rv).replaceFirst("");

            if (DERIVATIONAL.matcher(rv).matches()) {
                rv = DER.matcher(rv).replaceFirst("");
            }

            temp = P.matcher(rv).replaceFirst("");
            if (temp.equals(rv)) {
                rv = SUPERLATIVE.matcher(rv).replaceFirst("");
                rv = NN.matcher(rv).replaceFirst("н");
            }
            else{
                rv = temp;
            }
            word = pre + rv;
        }

        return word;
	}
}
