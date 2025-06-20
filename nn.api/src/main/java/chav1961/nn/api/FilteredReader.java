package chav1961.nn.api;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Predicate;

import chav1961.nn.api.interfaces.Stemmer;
import chav1961.nn.api.interfaces.Vocabulary;

public class FilteredReader extends Reader {
	public FilteredReader(final Reader nested, final Predicate<CharSequence> filter) {
		
	}
	
	public FilteredReader(final Reader nested, final Stemmer... stemmers) {
		
	}

	public FilteredReader(final Reader nested, final Vocabulary... vocabs) {
		
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

}
