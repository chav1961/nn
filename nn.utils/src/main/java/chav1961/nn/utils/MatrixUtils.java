package chav1961.nn.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import chav1961.nn.api.interfaces.MatrixWrapper;
import chav1961.purelib.basic.exceptions.CalculationException;
import chav1961.purelib.cdb.CompilerUtils;
import chav1961.purelib.sql.util.Temp;

public class MatrixUtils {
	private MatrixUtils() {
		
	}

	public MatrixWrapper multiplyVectorAndMatrix(final MatrixWrapper vector, final MatrixWrapper matrix) throws CalculationException {
		return multiplyVectorAndMatrix(vector, matrix, Runtime.getRuntime().availableProcessors());
	}	
	
	public MatrixWrapper multiplyVectorAndMatrix(final MatrixWrapper vector, final MatrixWrapper matrix, final int parallelism) throws CalculationException {
		if (vector == null) {
			throw new NullPointerException("Vector to process can't be null");
		}
		else if (vector.getDimensions().length != 1) {
			throw new IllegalArgumentException("Matrix is not a vector, but two-dimensional matrix");
		}
		else if (matrix == null) {
			throw new NullPointerException("Matrix to process can't be null");
		}
		else if (matrix.getDimensions().length != 2) {
			throw new IllegalArgumentException("Matrix is a vector, but two-dimensional matrix required");
		}
		else if (vector.getMatrixClass().contentClass() != matrix.getMatrixClass().contentClass()) {
			throw new IllegalArgumentException("Vector values type ["+vector.getMatrixClass().contentClass()+"] is differ with matrix values type ["+matrix.getMatrixClass().contentClass()+"]");
		}
		else if (vector.getDimensions()[0] != matrix.getDimensions()[0]) {
			throw new IllegalArgumentException("Vector size ["+vector.getDimensions()[0]+"] is not corresponding with matrix row size ["+matrix.getDimensions()[0]+"]");
		}
		else if (parallelism <= 0) {
			throw new IllegalArgumentException("Parallelism ["+parallelism+"] must be greater than 0");
		}
		else {
			switch (CompilerUtils.defineClassType(vector.getMatrixClass().contentClass())) {
				case CompilerUtils.CLASSTYPE_FLOAT 	:
					return MatrixWrapper.of(multiplyFloatVectorAndMatrix((float[])vector.getContent(), (float[][])matrix.getContent(), parallelism));
				case CompilerUtils.CLASSTYPE_DOUBLE :
					return MatrixWrapper.of(multiplyDoubleVectorAndMatrix((double[])vector.getContent(), (double[][])matrix.getContent(), parallelism));
				default :
					throw new UnsupportedOperationException("Class type ["+vector.getMatrixClass().contentClass()+"] is not supported");
			}
		}
	}
	
	public static MatrixUtils getDefaultInstance() {
		return new MatrixUtils();
	}

	static float[] multiplyFloatVectorAndMatrix(final float[] vector, final float[][] matrix, final int parallelism) throws CalculationException {
		final float[]			result = new float[matrix[0].length];
		final int				effectiveParellelism = Math.min(vector.length, parallelism); 
		final Future<float[]>[]	subtasks = new Future[effectiveParellelism];
		
		for(int index = 0, maxIndex = effectiveParellelism; index < maxIndex; index++) {
			final int	start = index;	// Prepare subtasks
			
			subtasks[index] = ForkJoinPool.commonPool().submit( 
					()->{
						final float[] temp = new float[matrix[0].length];
						
						for(int y = start, maxY = vector.length; y < maxY; y += effectiveParellelism) {
							final float		val = vector[y];
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
	
	static double[] multiplyDoubleVectorAndMatrix(final double[] vector, final double[][] matrix, final int parallelism) throws CalculationException {
		final double[]				result = new double[matrix[0].length];
		final int					effectiveParellelism = Math.min(vector.length, parallelism); 
		final Future<double[]>[]	subtasks = new Future[effectiveParellelism];
		
		for(int index = 0, maxIndex = effectiveParellelism; index < maxIndex; index++) {
			final int	start = index;	// Prepare subtasks
			
			subtasks[index] = ForkJoinPool.commonPool().submit( 
					()->{
						final double[] temp = new double[matrix[0].length];
						
						for(int y = start, maxY = vector.length; y < maxY; y += effectiveParellelism) {
							final double	val = vector[y];
							final double[]	line = matrix[y];
							
							for(int x = 0, maxX = temp.length; x < maxX; x++) {
								temp[x] += val*line[x];
							}
						}
						return temp;
					}
				);
		}		
		for(Future<double[]> item : subtasks) {	// Await subtasks termination
			try {
				final double[]	temp = item.get();
				
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

	static float[][] transposeFloatMatrix(final float[][] matrix, final int parallelism) throws CalculationException {
		final float[][]			result = new float[matrix[0].length][];
		final int				effectiveParellelism = Math.min(matrix[0].length, parallelism); 
		final Future<float[][]>[]	subtasks = new Future[effectiveParellelism];
		
		for(int index = 0, maxIndex = effectiveParellelism; index < maxIndex; index++) {
			final int	start = index;	// Prepare subtasks
			
			subtasks[index] = ForkJoinPool.commonPool().submit( 
					()->{
						final float[][] temp = new float[Math.min(matrix[0].length/effectiveParellelism, matrix[0].length-start*effectiveParellelism)][];
						
						for(int y = 0, maxY = temp.length; y < maxY; y++) {
							final float[] line = new float[matrix.length];
							
							for(int x = 0, maxX = temp.length; x < maxX; x++) {
								line[x] = matrix[x][start*parallelism+y];
							}
							temp[y] = line;
						}
						return temp;
					}
				);
		}
		int where = 0;
		
		for(Future<float[][]> item : subtasks) {	// Await subtasks termination
			try {
				for(float[] line : item.get()) {
					result[where++] = line; 
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

	static double[][] transposeDoubleMatrix(final double[][] matrix, final int parallelism) throws CalculationException {
		final double[][]			result = new double[matrix[0].length][];
		final int					effectiveParellelism = Math.min(matrix[0].length, parallelism); 
		final Future<double[]>[]	subtasks = new Future[effectiveParellelism];
		
		for(int index = 0, maxIndex = effectiveParellelism; index < maxIndex; index++) {
			final int	start = index;	// Prepare subtasks
			
			subtasks[index] = ForkJoinPool.commonPool().submit( 
					()->{
						final double[] temp = new double[matrix.length];
						
						for(int x = 0, maxX = temp.length; x < maxX; x++) {
							temp[x] = matrix[x][start];
						}
						return temp;
					}
				);
		}
		int where = 0;
		
		for(Future<double[]> item : subtasks) {	// Await subtasks termination
			try {
				result[where++] = item.get();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new CalculationException(e);
			} catch (ExecutionException e) {
				throw new CalculationException(e.getCause());
			}
		}
		return result;
	}
}

