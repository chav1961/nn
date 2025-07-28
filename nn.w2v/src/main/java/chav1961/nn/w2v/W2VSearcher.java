package chav1961.nn.w2v;

import java.io.DataInput;
import java.io.IOException;
import java.util.function.Function;

import chav1961.nn.api.interfaces.FloatPredicate;
import chav1961.nn.api.interfaces.MatrixWrapper;
import chav1961.nn.api.interfaces.Word;
import chav1961.nn.utils.MatrixUtils;
import chav1961.purelib.basic.AndOrTree;
import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.CalculationException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;

public class W2VSearcher {
	private static final int	VECTOR_MAGIC = 0x16061A01; 

	private final DottedVersion	version = new DottedVersion("1.0");
	private final SyntaxTreeInterface<Word[]>	currentVocab = new AndOrTree<>(1, 1);
	private VectorStore	vectors = null;
	
	public W2VSearcher() {
	}

	public void downloadCurrentVocab(final DataInput in) throws IOException {
	}	
	
	public SyntaxTreeInterface<Word[]> getCurrentVocab() {
		return currentVocab;
	}
	
	public void downloadVectors(final DataInput in) throws IOException {
		if (in == null) {
			throw new NullPointerException("Data input can't be null");
		}
		else {
			final int	magic = in.readInt(), endMagic;
			
			if (magic != VECTOR_MAGIC) {
				throw new IllegalArgumentException("Illegal magic ["+magic+"] in the data input, must be ["+VECTOR_MAGIC+"]");
			}
			else {
				final DottedVersion	currentVersion = new DottedVersion(in.readUTF());
				
				if (!currentVersion.equals(version)) {
					throw new IllegalArgumentException("Unsupported version ["+version+"] in the data input");
				}
				else {
					final int		rowSize1 = in.readInt(), colSize1 = in.readInt();
					final float[][]	matrix1 = new float[rowSize1][];
					
					for(int y = 0; y < rowSize1; y++) {
						final float[]	line = new float[colSize1];
						
						for(int x = 0; x < colSize1; x++) {
							line[x] = in.readFloat();
						}
						matrix1[y] = line;
					}
					final int		rowSize2 = in.readInt(), colSize2 = in.readInt();
					final float[][]	matrix2 = new float[rowSize1][];
					
					for(int y = 0; y < rowSize2; y++) {
						final float[]	line = new float[colSize2];
						
						for(int x = 0; x < colSize2; x++) {
							line[x] = in.readFloat();
						}
						matrix2[y] = line;
					}
					if ((endMagic = in.readInt()) != VECTOR_MAGIC) {
						throw new IllegalArgumentException("Illegal magic ["+endMagic+"] in the end of the data input, must be ["+VECTOR_MAGIC+"]");
					}
					else {
						this.vectors = new VectorStore(rowSize1, colSize1, matrix1, rowSize2, colSize2, matrix2);
					}
				}
			}
		}
	}
	
	public float[] encode(final Word... window) throws CalculationException {
		return encode((w)->w, window);
	}
	
	public float[] encode(final Function<Word, Word> resolver, final Word... window) throws CalculationException {
		if (resolver == null) {
			throw new NullPointerException("Resolver can't be null"); 
		}
		else if (window == null || Utils.checkArrayContent4Nulls(window) >= 0) {
			throw new IllegalArgumentException("Window list is null or contains nulls inside"); 
		}
		else if (getCurrentVocab() == null || vectors == null) {
			throw new IllegalStateException("Either vocabulary or vectors was not loaded yet. Invocation rejected"); 
		}
		else {
			final SyntaxTreeInterface<Word[]>	vocab = getCurrentVocab();
			final float[]	vector = new float[vocab.size()];
			
			for(Word item : window) {
				long	id = vocab.seekNameI(item.getWord());
				
				if (id < 0) {
					id = vocab.seekNameI(resolver.apply(item).getWord());
					
					if (id < 0) {
						throw new IllegalArgumentException("Word ["+item.toString()+"] is not resolved anywhere"); 
					}
				}
				vector[minId(vocab.getCargo(id))] = 1.0f;
			}
			return vector;
		}
	}

	public Word[] decode(final float[] map, final FloatPredicate fp) throws CalculationException {
		if (map == null) {
			throw new NullPointerException("Map to decode can't be null"); 
		}
		else if (fp == null) {
			throw new NullPointerException("Float predicate can't be null"); 
		}
		else if (getCurrentVocab() == null || vectors == null) {
			throw new IllegalStateException("Either vocabulary or vectors was not loaded yet. Invocation rejected"); 
		}
		else {
			final SyntaxTreeInterface<Word[]>	vocab = getCurrentVocab();
			int	resultCount = 0;
			
			for (float item : map) {
				if (fp.test(item)) {
					resultCount++;
				}
			}
			final Word[]	vector = new Word[resultCount];
			
			resultCount = 0;
			for (int index = 0, maxIndex = vocab.size(); index < maxIndex; index++) {
				final float	val = map[index]; 
						
				if (fp.test(val)) {
					final Word[]	temp = vocab.getCargo(index);
					
					vector[resultCount++] = temp[minId(temp)]; 
				}
			}
			return vector;
		}
	}
	
	public float[] bow(final float[] map) throws CalculationException {
		if (map == null) {
			throw new NullPointerException("Map to process can't be null"); 
		}
		else if (getCurrentVocab() == null || vectors == null) {
			throw new IllegalStateException("Either vocabulary or vectors was not loaded yet. Invocation rejected"); 
		}
		else if (vectors.rowSize1 != map.length) {
			throw new IllegalStateException("Map length ["+map.length+"] is not correnspoding with the first matrix row count ["+vectors.rowSize1+"]"); 
		}
		else {
			final MatrixWrapper	step1 = MatrixUtils.getDefaultInstance().multiplyVectorAndMatrix(
										MatrixWrapper.of(map), 
										MatrixWrapper.of(vectors.matrix1)
									);
			final MatrixWrapper	step2 = MatrixUtils.getDefaultInstance().multiplyVectorAndMatrix(
										step1, 
										MatrixWrapper.of(vectors.matrix2)
									);
			
			return step2.getContent();
		}
	}

	public float[] gram(final float[] map) throws CalculationException {
		return null;
	}

	private static int minId(final Word[] cargo) {
		int	min = cargo[0].seqId();
		
		for(Word val : cargo) {
			min = Math.min(min, val.seqId());
		}
		return min;
	}

	
	private static class VectorStore {
		final int		rowSize1;
		final int		colSize1;
		final float[][]	matrix1;
		final int		rowSize2;
		final int		colSize2;
		final float[][]	matrix2;
		
		VectorStore(final int rowSize1, final int colSize1, final float[][] matrix1, final int rowSize2, final int colSize2, final float[][] matrix2) {
			this.rowSize1 = rowSize1;
			this.colSize1 = colSize1;
			this.matrix1 = matrix1;
			this.rowSize2 = rowSize2;
			this.colSize2 = colSize2;
			this.matrix2 = matrix2;
		}
	}
}
