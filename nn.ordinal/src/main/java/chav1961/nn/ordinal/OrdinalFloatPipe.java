package chav1961.nn.ordinal;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import chav1961.nn.api.interfaces.ActivationType;
import chav1961.nn.api.interfaces.MatrixClass;
import chav1961.nn.api.interfaces.MatrixWrapper;
import chav1961.nn.api.interfaces.Pipe;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.CalculationException;
import chav1961.purelib.basic.interfaces.ProgressIndicator;

public class OrdinalFloatPipe implements Pipe {
	private final boolean 		readOnly;
	private final int			parallelism;
	private final List<Object>	content = new ArrayList<>();
	
	OrdinalFloatPipe(final boolean readOnly, final int parallelism, final MatrixClass clazz) {
		this.readOnly = readOnly;
		this.parallelism = parallelism;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public int getParallelism() {
		return parallelism;
	}

	@Override
	public MatrixWrapper forward(final MatrixWrapper matrix, final ProgressIndicator pi) throws CalculationException {
		if (matrix == null) {
			throw new NullPointerException("Matrix to process can't be null");
		}
		else if (matrix.getMatrixClass().contentClass() != float.class) {
			throw new IllegalArgumentException("Matrix content class ["+matrix.getMatrixClass().contentClass()+"] is incompatible with awaited content ["+float.class+"]");
		}
		else if (matrix.getDimensions().length != 1) {
			throw new IllegalArgumentException("Matrix is not a vector, but two-dimensional matrix");
		}
		else if (pi == null) {
			throw new NullPointerException("Progress indicator can't be null");
		}
		else {
			float[]	current = matrix.getContent();
			int		step = 0;
			
			pi.start("Forward calculation", content.size());
			for(Object item : content) {
				if (item instanceof MatrixWrapper) {
					final MatrixWrapper	wrapper = (MatrixWrapper)item;
					
					current = forwardMutiply(current, wrapper.getContent(), getParallelism());
				}
				else if (item instanceof ActivationKeeper) {
					switch (((ActivationKeeper)item).type) {
						case SOFTMAX	:
							current = activateSoftMax(current, getParallelism());
							break;
						default	:
							throw new UnsupportedOperationException("Activation type ["+item+"] is not supported yet");
					}
				}
				pi.processed(++step);
			}
			pi.end();
			return MatrixWrapper.of(current);
		}
	}

	@Override
	public MatrixWrapper backward(MatrixWrapper matrix, ProgressIndicator pi) throws CalculationException {
		if (matrix == null) {
			throw new NullPointerException("Matrix to process can't be null");
		}
		else if (pi == null) {
			throw new NullPointerException("Progress indicator can't be null");
		}
		else if (isReadOnly()) {
			throw new IllegalStateException("Attempt to call this method on read-only pipe");
		}			
		else {
			// TODO Auto-generated method stub
			
			return null;
		}
	}
	
	public void add(final MatrixWrapper matrix) {
		if (matrix == null) {
			throw new NullPointerException("Matrix wrapper to add can't be null");
		}
		else if (matrix.getMatrixClass().contentClass() != float.class) {
			throw new IllegalArgumentException("Matrix content class ["+matrix.getMatrixClass().contentClass()+"] is incompatible with awaited content ["+float.class+"]");
		}
		else if (matrix.getDimensions().length != 2) {
			throw new IllegalArgumentException("Matrix to add must be two-dimensional matrix");
		}
		else {
			int	currentSize = matrix.getDimensions()[0];
					
			for(int index = content.size()-1; index >= 0; index--) {
				final Object	item = content.get(index);
				
				if (item instanceof MatrixWrapper) {
					if (((MatrixWrapper)item).getDimensions()[1] != currentSize) {
						throw new IllegalArgumentException("Number of rows ["+currentSize+"] of matrix to add conflicts with number of columns ["+((MatrixWrapper)item).getDimensions()[1]+"] of the previously added matrix"); 
					}
					else {
						break;
					}
				}
			}
			content.add(matrix);
		}
	}

	public void add(final ActivationType activation, final Object... parameters) {
		if (activation == null) {
			throw new NullPointerException("Activation type to add can't be null");
		}
		else if (parameters == null || Utils.checkArrayContent4Nulls(parameters) >= 0) {
			throw new IllegalArgumentException("Parameters is null or contains nulls inside");
		}
		else {
			content.add(new ActivationKeeper(activation, parameters));
		}
	}
	
	static float[] forwardMutiply(final float[] source, final float[][] matrix, final int parellelism) throws CalculationException {
		final float[]			result = new float[matrix[0].length];
		final int				effectiveParellelism = Math.min(source.length, parellelism); 
		final Future<float[]>[]	subtasks = new Future[effectiveParellelism];
		
		for(int index = 0, maxIndex = effectiveParellelism; index < maxIndex; index++) {
			final int	start = index;	// Prepare subtasks
			
			subtasks[index] = ForkJoinPool.commonPool().submit( 
					()->{
						final float[] temp = new float[matrix[0].length];
						
						for(int y = start, maxY = source.length; y < maxY; y += effectiveParellelism) {
							final float		val = source[y];
							final float[]	line = matrix[y];
							
							for(int x = 0, maxX = temp.length; x < maxX; x++) {
								temp[x] += val*line[x];
							}
						}
						return temp;
					}
				);
		}		
		for(Future<float[]> item : subtasks) {	// Await subtasks termination
			try {
				final float[]	temp = item.get();
				
				for(int index = 0; index < temp.length; index++) {
					result[index] += temp[index];
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new CalculationException(e);
			} catch (ExecutionException e) {
				throw new CalculationException(e.getCause());
			}
		}
		return result;
	}

	static float[] backwardMutiply(final float[] source, final float[][] matrix, final int parellelism) throws CalculationException {
		final float[]			result = new float[matrix.length];
		final int				effectiveParellelism = Math.min(source.length, parellelism); 
		final Future<float[]>[]	subtasks = new Future[effectiveParellelism];
		
		for(int index = 0, maxIndex = effectiveParellelism; index < maxIndex; index++) {
			final int	start = index;	// Prepare subtasks
			
			subtasks[index] = ForkJoinPool.commonPool().submit( 
					()->{
						final float[] temp = new float[matrix.length];
						
						for(int y = start, maxY = source.length; y < maxY; y += effectiveParellelism) {
							final float		val = source[y];
							
							for(int x = 0, maxX = temp.length; x < maxX; x++) {
								temp[x] += val*matrix[x][y];
							}
						}
						return temp;
					}
				);
		}		
		for(Future<float[]> item : subtasks) {	// Await subtasks termination
			try {
				final float[]	temp = item.get();
				
				for(int index = 0; index < temp.length; index++) {
					result[index] += temp[index];
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new CalculationException(e);
			} catch (ExecutionException e) {
				throw new CalculationException(e.getCause());
			}
		}
		return result;
	}
	
	static float[] activateSoftMax(final float[] content, final int parellelism) throws CalculationException {
		final int			effectiveParellelism = Math.min(content.length, parellelism); 
		final int			piece = (content.length + effectiveParellelism - 1) / effectiveParellelism;
		final float[]		result = new float[content.length];
		final float[]		sum = new float[effectiveParellelism];
		final int[]			ranges = new int[2*(effectiveParellelism+1)];
		final Future<?>[]	subtasks = new Future[effectiveParellelism];
		int		where = 1;

		ranges[0] = 0;	// Split data to subtasks
		for(int index = 0, maxIndex = content.length; index < maxIndex; index+= piece) {
			ranges[where++] = Math.min(index + piece - 1, content.length-1);
			ranges[where++] = Math.min(index + piece, content.length);
		}
		ranges[where++] = content.length;
		
		for(int index = 0, maxIndex = effectiveParellelism; index < maxIndex; index++) {
			final int	pos = index;	// Start subtasks
			final int	from = ranges[2*index];
			final int	to = ranges[2*index + 1];
			subtasks[index] = ForkJoinPool.commonPool().submit( 
				()->{
					float[]	temp = new float[to-from+1];
					
					for(int current = from; current <= to; current++) {
						temp[current-from] = result[current] = (float) Math.exp(content[current]);
					}
					float 	currentSum = 0;
					
					Arrays.sort(temp);
					for(float item : temp) {
						currentSum += item;
					}
					sum[pos] = currentSum;
				});
		}
		for(Future<?> item : subtasks) {	// Await subtasks termination
			try {
				item.get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new CalculationException(e);
			} catch (ExecutionException e) {
				throw new CalculationException(e.getCause());
			}
		}
		float	total = 0;
		
		Arrays.sort(sum);
		for(float item : sum) {	// Calculate total sum
			total += item;
		}
		total = 1/total;
		for(int index = 0, maxIndex = content.length; index < maxIndex; index++) {
			result[index] *= total; 	// Calculate softmax
		}
		return result;
	}

	private static class ActivationKeeper {
		final ActivationType	type;
		final Object[]			parameters;
		
		private ActivationKeeper(final ActivationType type, final Object... parameters) {
			this.type = type;
			this.parameters = parameters;
		}

		@Override
		public String toString() {
			return "ActivationKeeper [type=" + type + ", parameters=" + Arrays.toString(parameters) + "]";
		}
	}
}
