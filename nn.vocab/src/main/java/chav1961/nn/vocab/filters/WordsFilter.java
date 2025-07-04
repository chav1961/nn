package chav1961.nn.vocab.filters;

import java.util.Iterator;
import java.util.function.Predicate;

import chav1961.nn.vocab.interfaces.Grammeme;
import chav1961.nn.vocab.interfaces.Word;
import chav1961.nn.vocab.interfaces.WordPipe;
import chav1961.purelib.basic.Utils;

public class WordsFilter implements WordPipe {
	private final WordPipe	nested;
	private final Predicate<Word>	pred;
	
	public WordsFilter(final WordPipe nested, final Predicate<Word> pred) {
		if (nested == null) {
			throw new NullPointerException("Nested word pipe can't be null");
		}
		else if (pred == null) {
			throw new NullPointerException("Testing predicate can't be null");
		}
		else {
			this.nested = nested;
			this.pred = pred;
		}
	}
	
	@Override
	public Iterator<Word[]> iterator() {
		return new Iterator<Word[]>() {
			final Iterator<Word[]>	it = nested.iterator();
			Word[]	current;
			
			@Override
			public boolean hasNext() {
				if (it.hasNext()) {
					current = it.next();
					for (Word item : current) {
						if (pred.test(item)) {
							return true;
						}
					}
					return hasNext(); 
				}
				else {
					return false;
				}
			}
			
			@Override
			public Word[] next() {
				return current;
			}
		};
	}

	public static class UsualFilter implements Predicate<Word> {
		private final int			minLength;
		private final Grammeme[]	excludes;
		
		public UsualFilter(final int minLength, final Grammeme... toExclude) {
			if (minLength < 0) {
				throw new IllegalArgumentException("Minimal length ["+minLength+"] must be greater or equals than 0");
			}
			else if (toExclude == null || Utils.checkArrayContent4Nulls(toExclude) >= 0) {
				throw new IllegalArgumentException("Grammemes to exclude is null or contains nulls inside");
			}
			else {
				this.minLength = minLength;
				this.excludes = toExclude;
			}
		}
		
		@Override
		public boolean test(final Word word) {
			if (word != null) {
				for (Grammeme item : excludes) {
					if (word.hasAttribute(item.getName())) {
						return false;
					}
				}
				return word.getWord().length() > minLength;
			}
			else {
				return false;
			}
		}
	}
}
