package chav1961.nn.w2v;

import java.io.DataInput;
import java.io.IOException;
import java.util.function.Function;

import chav1961.nn.api.interfaces.Word;
import chav1961.purelib.basic.exceptions.CalculationException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;

public class W2VSearcher {
	public void downloadVocab(final DataInput in) throws IOException {
		
	}

	public SyntaxTreeInterface<Word[]> getCurrentVocab() {
		return null;
	}
	
	public void downloadVectors(final DataInput in) throws IOException {
		
	}
	
	public float[] encode(final Word... window) throws CalculationException {
		return null;
	}
	
	public float[] encode(final Function<Word, Word> resolver, final Word... window) throws CalculationException {
		return null;
	}

	public Word[] decode(final float[] map) throws CalculationException {
		return null;
	}
	
	public float[] bow(final float[] map) throws CalculationException {
		return null;
	}

	public float[] gram(final float[] map) throws CalculationException {
		return null;
	}
}
