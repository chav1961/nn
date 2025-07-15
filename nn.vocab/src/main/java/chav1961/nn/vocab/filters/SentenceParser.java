package chav1961.nn.vocab.filters;

import java.util.Iterator;

import chav1961.nn.api.interfaces.LangPart;
import chav1961.nn.api.interfaces.Word;
import chav1961.nn.api.interfaces.WordPipe;
import chav1961.purelib.basic.AndOrTree;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;

public class SentenceParser implements WordPipe {
	private final CharSequence	sentence;
	private final SyntaxTreeInterface<Word[]>	words;
	
	public SentenceParser(final CharSequence sentence) {
		if (sentence == null) {
			throw new NullPointerException("Sentence to parse can't be null");
		}
		else {
			this.sentence = sentence;
			this.words = new AndOrTree<>();
		}
	}

	public SentenceParser(final CharSequence sentence, final SyntaxTreeInterface<Word[]> words) {
		if (sentence == null) {
			throw new NullPointerException("Sentence to parse can't be null");
		}
		else if (words == null) {
			throw new NullPointerException("Words vocabulary can't be null");
		}
		else {
			this.sentence = sentence;
			this.words = words;
		}
	}
	
	@Override
	public Iterator<Word[]> iterator() {
		return new Iterator<Word[]>() {
			final char[]	content = new char[256];
			int	index, where, type;

			@Override
			public boolean hasNext() {
				while (index < sentence.length() && Character.isWhitespace(sentence.charAt(index))) {
					index++;
				}
				if (index < sentence.length()) {
					if (Character.isLetter(sentence.charAt(index))) {
						type = 0;
						where = 0;
						
						while (index < sentence.length() && Character.isLetter(sentence.charAt(index))) {
							if (where < content.length) {
								content[where++] = sentence.charAt(index);
							}
							index++;
						}
					}
					else if (Character.isDigit(sentence.charAt(index))) {
						type = 1;
						where = 0;
						
						while (index < sentence.length() && Character.isDigit(sentence.charAt(index))) {
							if (where < content.length) {
								content[where++] = sentence.charAt(index);
							}
							index++;
						}
					}
					else {
						type = 2;
						where = 0;
						
						while (index < sentence.length() && !(Character.isWhitespace(sentence.charAt(index)) || Character.isLetter(sentence.charAt(index)) || Character.isDigit(sentence.charAt(index)))) {
							if (where < content.length) {
								content[where++] = sentence.charAt(index);
							}
							index++;
						}
					}
					return true;
				}
				else {
					return false;
				}
			}
			
			@Override
			public Word[] next() {
				switch (type) {
					case 0:
						final long	id = words.seekName(content, 0, where-1);
						
						if (id >= 0) {
							return words.getCargo(id).clone();
						}
						else {
							return createWord(LangPart.UNKNOWN, new String(content, 0, where));
						}
					case 1:
						return createWord(LangPart.NUMR, new String(content, 0, where));
					case 2:
						return createWord(LangPart.PUNCT, new String(content, 0, where));
					default :
						throw new UnsupportedOperationException();
				}
			}

		};
	}

	private Word[] createWord(final LangPart part, final String content) {
		return new WordImpl[] {new WordImpl(part, content)};
	}
}
