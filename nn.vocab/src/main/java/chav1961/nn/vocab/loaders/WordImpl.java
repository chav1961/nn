package chav1961.nn.vocab.loaders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import chav1961.nn.vocab.interfaces.LangPart;
import chav1961.nn.vocab.interfaces.Word;
import chav1961.nn.vocab.interfaces.WordForm;
import chav1961.nn.vocab.interfaces.WordLink;
import chav1961.purelib.basic.Utils;

class WordImpl implements Word {
	private final int			id;
	private final WordForm		form;
	private final LangPart		part;
	private final String		word;
	private final Word			lemma;
	private final Grammeme[]	attrs;
	private final int			numberOfAttr;
	private volatile WordLink	link = null;

	WordImpl(final int id, final LangPart part, final String word, final Grammeme... attrs) {
		this.id = id;
		this.form = WordForm.LEMMA;
		this.part = part;
		this.word = word;
		this.lemma = null;
		this.attrs = attrs;
		this.numberOfAttr = calcNumberOfAttrs(attrs);
	}
	
	WordImpl(final int id, final Word lemma, final LangPart part, final String word, final Grammeme... attrs) {
		this.id = id;
		this.form = WordForm.FORM;
		this.part = part;
		this.word = word;
		this.lemma = lemma;
		this.attrs = attrs;
		this.numberOfAttr = calcNumberOfAttrs(attrs);
	}

	@Override
	public Iterator<String> iterator() {
		final List<String>	result = new ArrayList<>();
		
		for (Grammeme item : attrs) {
			result.add(LoaderUtils.toString(item));
			for (Grammeme child : item.children) {
				result.add(LoaderUtils.toString(child));
			}
		}
		return result.iterator();
	}

	@Override
	public int id() {
		return id;
	}
	
	@Override
	public String getWord() {
		return word;
	}

	@Override
	public int getWord(final char[] where, final int from) {
		if (where == null || where.length == 0) {
			throw new IllegalArgumentException("Array to store word must be neither null nor empty");
		}
		else if (from < 0 || from >= where.length) {
			throw new IllegalArgumentException("From value ["+from+"] out of range 0.."+(where.length-1));
		}
		else {
			final int	len = Math.min(word.length(), where.length-from);
			
			word.getChars(0, len, where, from);
			return len;
		}
	}

	@Override
	public Word getLemma() {
		if (form == WordForm.LEMMA) {
			throw new IllegalStateException("Lemma can't have parent lemma");
		}
		else {
			return lemma;
		}
	}

	@Override
	public WordLink getLinks() {
		return link;
	}
	
	@Override
	public WordForm wordForm() {
		return form;
	}

	@Override
	public LangPart part() {
		return part;
	}

	@Override
	public boolean hasAttribute(final String attr) {
		if (Utils.checkEmptyOrNullString(attr)) {
			throw new NullPointerException("Attribuate name must be neither null nor empty");
		}
		else {
			for (Grammeme item : attrs) {
				if (item.name.equals(attr)) {
					return true;
				}
				else {
					for (Grammeme child : item.children) {
						if (child.name.equals(attr)) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}

	@Override
	public int numberOfAttributes() {
		return numberOfAttr;
	}
	
	@Override
	public String toString() {
		return "WordImpl [id=" + id + ", form=" + form + ", part=" + part + ", word=" + word + ", attrs="
				+ Arrays.toString(attrs) + "]";
	}

	void setLinks(final WordLink link) {
		this.link = link;
	}
	
	private int calcNumberOfAttrs(final Grammeme... attrs) {
		int	count = 0;
		
		for (Grammeme item : attrs) {
			for (Grammeme child : item.children) {
				count++;
			}
		}
		return count;
	}
}