package chav1961.nn.ordinal;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import chav1961.nn.api.interfaces.MatrixClass;
import chav1961.nn.api.interfaces.MatrixWrapper;
import chav1961.nn.api.interfaces.Pipe;
import chav1961.purelib.basic.exceptions.CalculationException;
import chav1961.purelib.basic.interfaces.ProgressIndicator;

public class OrdinalFloatPipe implements Pipe {
	private final boolean 		readOnly;
	private final int			parallelism;
	
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
	public MatrixWrapper forward(MatrixWrapper matrix, ProgressIndicator pi) throws CalculationException {
		if (matrix == null) {
			throw new NullPointerException("Matrix to process can't be null");
		}
		else if (matrix.getMatrixClass().contentClass() != float.class) {
			throw new IllegalArgumentException("Matrix content class ["+matrix.getMatrixClass().contentClass()+"] is incompatible with awaited content ["+float.class+"]");
		}
		else if (pi == null) {
			throw new NullPointerException("Progress indicator can't be null");
		}
		else {
			// TODO Auto-generated method stub
			
			return null;
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
	
	private float[] forwardMutiply(final float[] source, final float[][] matrix) throws CalculationException {
		final float[]		result = new float[matrix[0].length];
		final int			yStep = getParallelism();
		final Future<?>[]	subtasks = new Future[yStep];
		
		for(int index = 0, maxIndex = getParallelism(); index < maxIndex; index++) {
			final int	start = index;	// Prepare subtasks
			subtasks[index] = ForkJoinPool.commonPool().submit( 
					()->{
						for(int y = start, maxY = source.length; y < maxY; y += yStep) {
							for(int x = 0, maxX = result.length; x < maxX; x++) {
								float	sum = 0;
								
								for(int z = 0, maxZ = matrix.length; z < maxZ; z++) {
									sum += source[z]*matrix[z][x];
								}
								result[y] = sum;
							}
						}
					}
				);
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
		return result;
	}
	
	private void activateSoftMax(final float[] content) throws CalculationException {
		final int			piece = (content.length + getParallelism() - 1) / getParallelism();
		final float[]		sum = new float[getParallelism()];
		final int[]			ranges = new int[2*getParallelism()];
		final Future<?>[]	subtasks = new Future[getParallelism()];
		int		where = 1;

		ranges[0] = 0;	// Split data to subtasks
		for(int index = 0, maxIndex = content.length; index < maxIndex; index+= piece) {
			ranges[where++] = index + piece - 1;
			ranges[where++] = index + piece;
		}
		ranges[where++] = content.length;
		
		for(int index = 0, maxIndex = getParallelism(); index < maxIndex; index++) {
			final int	pos = index;	// Start subtasks
			final int	from = ranges[2*index];
			final int	to = ranges[2*index + 1];
			subtasks[index] = ForkJoinPool.commonPool().submit( 
				()->{
					float 	currentSum = 0;
					
					for(int current = from; current < to; current++) {
						currentSum += content[current] = (float) Math.exp(content[current]);
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
		
		for(float item : sum) {	// Calculate total sum
			total += item;
		}
		total = 1/total;
		for(int index = 0, maxIndex = content.length; index < maxIndex; index++) {
			content[index] *= total; 	// Calculate softmax
		}
	}
}
