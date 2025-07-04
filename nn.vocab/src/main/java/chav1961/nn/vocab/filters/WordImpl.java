package chav1961.nn.vocab.filters;

import java.util.Arrays;
import java.util.Iterator;

import chav1961.nn.vocab.interfaces.Grammeme;
import chav1961.nn.vocab.interfaces.LangPart;
import chav1961.nn.vocab.interfaces.Word;
import chav1961.nn.vocab.interfaces.WordForm;
import chav1961.nn.vocab.interfaces.WordLink;
import chav1961.purelib.basic.Utils;

class WordImpl implements Word {
	private final LangPart		part;
	private final String		word;
	private final Grammeme[]	grams;
	
	WordImpl(final LangPart part, final String word, final Grammeme... grammemes) {
		this.part = part;
		this.word = word;
		this.grams = grammemes;
	}

	@Override
	public Iterator<Grammeme> iterator() {
		return Arrays.asList(grams).iterator();
	}

	@Override
	public int id() {
		return -1;
	}

	@Override
	public String getWord() {
		return word;
	}

	@Override
	public int getWord(final char[] where, final int from) {
		if (where == null || where.length == 0) {
			throw new IllegalArgumentException("Where buffer can be niehter null nor empty");
		}
		else if (from < 0 || from >= where.length) {
			throw new IllegalArgumentException("From position ["+from+"] out of range 0.."+(where.length-1));
		}
		else {
			final int	tail = Math.min(where.length - from, word.length());  

			word.getChars(0, tail, where, from);
			return tail;
		}
	}

	@Override
	public Word getLemma() {
		return null;
	}

	@Override
	public WordLink getLinks() {
		return null;
	}

	@Override
	public WordForm wordForm() {
		return WordForm.LEMMA;
	}

	@Override
	public LangPart part() {
		return part;
	}

	@Override
	public boolean hasAttribute(final String attr) {
		if (Utils.checkEmptyOrNullString(attr)) {
			throw new IllegalArgumentException("Attribute to test can be neither null nor empty");
		}
		else {
			for (Grammeme item : this) {
				if (item.getName().equals(attr)) {
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public int numberOfAttributes() {
		return grams.length;
	}
}