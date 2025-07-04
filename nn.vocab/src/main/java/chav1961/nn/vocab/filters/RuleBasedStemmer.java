package chav1961.nn.vocab.filters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import chav1961.nn.vocab.interfaces.Grammeme;
import chav1961.nn.vocab.interfaces.LangPart;
import chav1961.nn.vocab.interfaces.Word;
import chav1961.nn.vocab.interfaces.WordForm;
import chav1961.nn.vocab.interfaces.WordPipe;

public class RuleBasedStemmer implements WordPipe {
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

    private final WordPipe	nested;
    
    public RuleBasedStemmer(final WordPipe nested) {
    	if (nested == null) {
    		throw new NullPointerException("Nested words pipe can't be null");
    	}
    	else {
    		this.nested = nested;
    	}
    }

	@Override
	public Iterator<Word[]> iterator() {
		return new Iterator<Word[]>() {
			final Iterator<Word[]>	it = nested.iterator();
			
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}
			
			@Override
			public Word[] next() {
				final Word[]	words = it.next().clone();
						
				for(int index = 0; index < words.length; index++) {
					if (words[index].part() == LangPart.UNKNOWN) {
						final List<Grammeme>	temp = new ArrayList<>(); 
						
						for(Grammeme item : words[index]) {
							temp.add(item);
						}
						words[index] = new WordImpl(LangPart.UNKNOWN, resolveInternal(words[index].getWord()), temp.toArray(new Grammeme[temp.size()]));
					}
					else if (words[index].wordForm() == WordForm.FORM) {
						words[index] = words[index].getLemma();
					}
				}
				return words;
			}
			
		};
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
