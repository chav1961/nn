package chav1961.nn.w2v.internal;

import java.io.DataInput;
import java.io.IOException;
import java.util.function.Function;

import chav1961.nn.api.interfaces.FloatPredicate;
import chav1961.nn.api.interfaces.Word;
import chav1961.purelib.basic.AndOrTree;
import chav1961.purelib.basic.LongIdMap;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.CalculationException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;

public class W2VStore {
	private static final FloatPredicate		USUAL_FILTER = (x)->x > 0.95f;
	
	private float[][][]						vectors;
	private SyntaxTreeInterface<Word[]>		words;
	private SyntaxTreeInterface<WordRef>	currentWords = new AndOrTree<>();
	private LongIdMap<WordRef>				currentIds = new LongIdMap<>(WordRef.class);

	protected W2VStore(final SyntaxTreeInterface<Word[]> words) {
		this.words = words;
	}
	
	public void downloadVocab(final DataInput in) throws IOException {
		if (in == null) {
			throw new NullPointerException("Data input can't be null");
		}
		else {
			for(int index = 0, maxIndex = in.readInt(); index < maxIndex; index++) {
				final int		id = in.readInt();
				final String	w = in.readUTF();
				final long		ref = words.seekName(w);
				
				if (ref < 0) {
					throw new IOException("Name ["+w+"] not found in total vocabulary");
				}
				else {
					final WordRef	wr = new WordRef(id, ref);
					
					currentWords.placeName(w, wr);
					currentIds.put(id, wr);
				}
			}
		}
	}

	public void downloadVectors(final DataInput in) throws IOException {
		if (in == null) {
			throw new NullPointerException("Data input can't be null");
		}
		else {
			final int	vocabSize = in.readInt(), vectorSize = in.readInt();
			
			if (getVocabSize() != vocabSize) {
				throw new IllegalStateException("Vocabulary size in the input ["+vocabSize+"] differ from current vocabulary size ["+getCurrentVocab().size()+"]"); 
			}
			else {
				final float[][][]	vectors = new float[2][vocabSize][vectorSize];
				
				for(float[][] matrix : vectors) {
					for(float[] line : matrix) {
						for(int index = 0, maxIndex = line.length; index < maxIndex; index++) {
							line[index] = in.readFloat();
						}
					}
				}
				setVectors(vectors);
			}
		}
	}
	
	public float[] encode(final Word... window) throws CalculationException {
		if (window == null || window.length == 0 ||  Utils.checkArrayContent4Nulls(window) >= 0) {
			throw new IllegalArgumentException("Word window is null, empty or contains nulls inside");
		}
		else {
			final float[]	result = new float[getVocabSize()];
			
			encode(result, window);
			return result;
		}
	}

	public void encode(final float[] source, final Word... window) throws CalculationException {
		if (source == null || source.length != getVocabSize()) {
			throw new IllegalArgumentException("Source vector is null or it's size doesn't match vocabulary size ["+getVocabSize()+"]"); 
		}
		else if (window == null || window.length == 0 ||  Utils.checkArrayContent4Nulls(window) >= 0) {
			throw new IllegalArgumentException("Word window is null, empty or contains nulls inside");
		}
		else {
			encode(source, (x)->x, window);
		}
	}
	
	public float[] encode(final Function<Word, Word> resolver, final Word... window) throws CalculationException {
		if (resolver == null) {
			throw new NullPointerException("Resolver can't be null"); 
		}
		else if (window == null || window.length == 0 ||  Utils.checkArrayContent4Nulls(window) >= 0) {
			throw new IllegalArgumentException("Word window is null, empty or contains nulls inside");
		}
		else {
			final float[]	result = new float[getVocabSize()];
			
			encode(result, resolver, window);
			return result;
		}
	}

	public void encode(final float[] source, final Function<Word, Word> resolver, final Word... window) throws CalculationException {
		if (source == null || source.length != getVocabSize()) {
			throw new IllegalArgumentException("Source vector is null or it's size doesn't match vocabulary size ["+getVocabSize()+"]"); 
		}
		else if (resolver == null) {
			throw new NullPointerException("Resolver can't be null"); 
		}
		else if (window == null || window.length == 0 ||  Utils.checkArrayContent4Nulls(window) >= 0) {
			throw new IllegalArgumentException("Word window is null, empty or contains nulls inside");
		}
		else {
			Utils.fillArray(source, 0f);

			for (Word item : window) {
				final Word	w = resolver.apply(item);
				
				if (w.id() <= 0) {
					source[0] = 1f;
				}
			}
		}
	}
	
	public Word[][] decode(final float[] source) throws CalculationException {
		if (source == null || source.length != getVocabSize()) {
			throw new IllegalArgumentException("Source vector is null or it's size doesn't match vocabulary size ["+getVocabSize()+"]"); 
		}
		else {
			return decode(USUAL_FILTER, source);
		}
	}

	public Word[][] decode(final FloatPredicate pred, final float[] source) throws CalculationException {
		if (pred == null) {
			throw new NullPointerException("Predicate to test can't be null");
		}
		else if (source == null || source.length != getVocabSize()) {
			throw new IllegalArgumentException("Source vector is null or it's size doesn't match vocabulary size ["+getVocabSize()+"]"); 
		}
		else {
			int	count = 0;
			
			for(float item : source) {
				if (pred.test(item)) {
					count++;
				}
			}
			final Word[][]	result = new Word[count][];
					
			count = 0;
			for(int index = 0; index < source.length; index++) {
				if (pred.test(source[index])) {
					result[count++] = words.getCargo(currentIds.get(index).id);
				}
			}
			return result;
		}
	}

	public int decode(final float[] source, final Word[][] target) throws CalculationException {
		if (source == null || source.length != getVocabSize()) {
			throw new IllegalArgumentException("Source vector is null or it's size doesn't match vocabulary size ["+getVocabSize()+"]"); 
		}
		else if (target == null || target.length == 0) {
			throw new IllegalArgumentException("Target vector can be neither null nor empty"); 
		}
		else {
			return decode(USUAL_FILTER, source, target);
		}
	}
	
	public int decode(final FloatPredicate pred, final float[] source, final Word[][] target) throws CalculationException {
		if (pred == null) {
			throw new NullPointerException("Predicate to test can't be null");
		}
		else if (source == null || source.length != getVocabSize()) {
			throw new IllegalArgumentException("Source vector is null or it's size doesn't match vocabulary size ["+getVocabSize()+"]"); 
		}
		else if (target == null || target.length == 0) {
			throw new IllegalArgumentException("Target vector can be neither null nor empty"); 
		}
		else {
			int	count = 0;
			
			for(float item : source) {
				if (pred.test(item)) {
					count++;
				}
			}
					
			count = 0;
			for(int index = 0; index < source.length; index++) {
				if (pred.test(source[index])) {
					if (count < target.length) {
						target[count++] = words.getCargo(currentIds.get(index).id);
					}
					count++;
				}
			}
			return count > target.length ? -count : count;
		}
	}
	
	protected int getVectorsSize() {
		return vectors != null ? vectors[0][0].length : 0;
	}

	protected int getVocabSize() {
		return vectors != null ? vectors[0].length : 0;
	}
	
	protected float[][][] getVectors() {
		return vectors;
	}
	
	protected void setVectors(final float[][][] vectors) {
		
	}

	protected SyntaxTreeInterface<Word[]> getVocab() {
		return words;
	}
	
	protected SyntaxTreeInterface<WordRef> getCurrentVocab() {
		return currentWords;
	}

	protected LongIdMap<WordRef> getCurrentIds() {
		return currentIds;
	}
	
	protected void multiply(final float[] left, final float[][] right, final float[] result) {
		for(int x = 0, maxX = left.length; x < maxX; x++) {
			for(int y = 0, maxY = result.length; y < maxY; y++) {
				float sum = 0;
				
				for(int z = 0, maxZ = right[0].length; z < maxZ; z++) {
					sum += left[x]*right[z][y];
				}
				result[y] = sum;
			}
		}
	}
	
	protected void softMax(final float[] content) {
		final float[]	temp = new float[content.length];
		float		sum = 0;
		
		for(int index = 0, maxIndex = temp.length; index < maxIndex; index++) {
			sum += temp[index] = (float)Math.exp(content[index]);
		}
		sum = 1/sum;
		for(int index = 0, maxIndex = temp.length; index < maxIndex; index++) {
			content[index] = temp[index] * sum;
		}
	}
	
	protected static class WordRef {
		public final int	id;
		public final long	cargoRef;
		
		public WordRef(final int id, final long cargoRef) {
			this.id = id;
			this.cargoRef = cargoRef;
		}
	}
}
