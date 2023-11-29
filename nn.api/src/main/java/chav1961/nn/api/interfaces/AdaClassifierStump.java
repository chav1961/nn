package chav1961.nn.api.interfaces;

import chav1961.purelib.basic.Utils;

@FunctionalInterface
public interface AdaClassifierStump {
	double classify(final double... source);
	
	default float classify(final float... source) {
		if (source == null) {
			throw new NullPointerException("Source can't be null"); 
		}
		else {
			final double[]	temp = new double[source.length];
			
			for(int index = 0; index < source.length; index++) {
				temp[index] = source[index];
			}
			return (float)classify(temp);
		}
	}

	public static AdaClassifierStump train(final AdaClassifierStump[] classifiers, final double[][] source, final double[] labels, final int maxIterations, final double epsilon) {
		if (classifiers == null || classifiers.length == 0 || Utils.checkArrayContent4Nulls(classifiers) >= 0) {
			throw new IllegalArgumentException("Classifiers list is null or empty array or contains nulls inside");
		}
		else if (source == null || source.length == 0 || Utils.checkArrayContent4Nulls(classifiers) >= 0) {
			throw new IllegalArgumentException("Source list is null or empty array or contains nulls inside");
		}
		else if (labels == null || labels.length == 0) {
			throw new IllegalArgumentException("Labels list is null or empty array");
		}
		else if (maxIterations <= 0) {
			throw new IllegalArgumentException("Max iterations ["+maxIterations+"] must be greater than 0");
		}
		else if (epsilon <= 0) {
			throw new IllegalArgumentException("Epsilon ["+epsilon+"] must be greater than 0");
		}
		else if (!isArrayAMatrix(source)) {
			throw new IllegalArgumentException("Source list is not a matrix, because it's columns has different dimensions row-by-row");
		}
		else {
			final double[]	weights = new double[labels.length];
			final double[]	alphas = new double[labels.length];

			weights[0] = 1.0 / weights.length;	// Fill initial weights
			System.arraycopy(weights, 0, weights, 1, weights.length - 1);

			for (int iteration = 0; iteration < maxIterations; iteration++) {
				AdaClassifierStump	bestStump = null;
				int					bestStumpIndex = -1;
				double 				minError = Double.MAX_VALUE;
						
				for (int classifierIndex = 0; classifierIndex < classifiers.length; classifierIndex++) {	// Find the best classifier for source
					final AdaClassifierStump 	item = classifiers[classifierIndex];
					double	error = 0;
					
					for (int index = 0; index < labels.length; index++) {
						if (Math.abs(item.classify(source[index]) - labels[index]) > epsilon) {
							error += weights[index];
						}
					}
					if (error < minError) {
						minError = error;
						bestStump = item;
						bestStumpIndex = classifierIndex;
					}
				}
				
				if (bestStump == null) {
					break;
				}
				else {
					double	alpha = 0.5*Math.log((1-minError)/minError);
					double	sum = 0;
					
					for(int index = 0; index < weights.length; index++) {
						weights[index] *= Math.exp(-alpha*(Math.abs(bestStump.classify(source[index]) - labels[index]) > epsilon ? 1 : -1));
						sum += weights[index]; 
					}
					sum = 1 / sum;
					for(int index = 0; index < weights.length; index++) {
						weights[index] *= sum; 
					}
					alphas[bestStumpIndex] = alpha;
				}
			}
			return new AdaClassifierStump() {
				@Override
				public double classify(final double... source) {
					double	result = 0;
					
					for (int index = 0; index < classifiers.length; index++) {
						result += alphas[index] * classifiers[index].classify(source);
					}
					return result;
				}
			};
		}
	}

	public static AdaClassifierStump train(final AdaClassifierStump[] classifiers, final float[][] source, final float[] labels, final int maxIterations, final double epsilon) {
		if (classifiers == null || classifiers.length == 0 || Utils.checkArrayContent4Nulls(classifiers) >= 0) {
			throw new IllegalArgumentException("Classifiers list is null or empty array or contains nulls inside");
		}
		else if (source == null || source.length == 0 || Utils.checkArrayContent4Nulls(classifiers) >= 0) {
			throw new IllegalArgumentException("Source list is null or empty array or contains nulls inside");
		}
		else if (labels == null || labels.length == 0) {
			throw new IllegalArgumentException("Labels list is null or empty array");
		}
		else if (maxIterations <= 0) {
			throw new IllegalArgumentException("Max iterations ["+maxIterations+"] must be greater than 0");
		}
		else if (epsilon <= 0) {
			throw new IllegalArgumentException("Epsilon ["+epsilon+"] must be greater than 0");
		}
		else if (!isArrayAMatrix(source)) {
			throw new IllegalArgumentException("Source list is not a matrix, because it's columns has different dimensions row-by-row");
		}
		else {
			final double[]	weights = new double[labels.length];
			final double[]	alphas = new double[labels.length];

			weights[0] = 1.0 / weights.length;	// Fill initial weights
			System.arraycopy(weights, 0, weights, 1, weights.length - 1);

			for (int iteration = 0; iteration < maxIterations; iteration++) {
				AdaClassifierStump	bestStump = null;
				int					bestStumpIndex = -1;
				double 				maxError = Double.MAX_VALUE;
						
				for (int classifierIndex = 0; classifierIndex < classifiers.length; classifierIndex++) {	// Find the best classifier for source
					final AdaClassifierStump 	item = classifiers[classifierIndex];
					double	error = 0;
					
					for (int index = 0; index < labels.length; index++) {
						if (Math.abs(item.classify(source[index]) - labels[index]) > epsilon) {
							error += weights[index];
						}
					}
					if (error < maxError) {
						maxError = error;
						bestStump = item;
						bestStumpIndex = classifierIndex;
					}
				}
				
				if (bestStump == null) {
					break;
				}
				else {
					double	alpha = 0.5*Math.log((1-maxError)/maxError);
					double	sum = 0;
					
					for(int index = 0; index < weights.length; index++) {
						weights[index] *= Math.exp(-alpha*(Math.abs(bestStump.classify(source[index]) - labels[index]) > epsilon ? 1 : -1));
						sum += weights[index]; 
					}
					sum = 1 / sum;
					for(int index = 0; index < weights.length; index++) {
						weights[index] *= sum; 
					}
					alphas[bestStumpIndex] = alpha;
				}
			}
			return new AdaClassifierStump() {
				@Override
				public float classify(final float... source) {
					double	result = 0;
					
					for (int index = 0; index < classifiers.length; index++) {
						result += alphas[index] * classifiers[index].classify(source);
					}
					return (float)result;
				}

				@Override
				public double classify(double... source) {
					double	result = 0;
					
					for (int index = 0; index < classifiers.length; index++) {
						result += alphas[index] * classifiers[index].classify(source);
					}
					return result;
				}
			};
		}
	}
	
	private static boolean isArrayAMatrix(final double[][] source) {
		int	size = source[0].length;
		
		for (double[] item : source) {
			if (item.length != size) {
				return false;
			}
		}
		return true;
	}

	private static boolean isArrayAMatrix(final float[][] source) {
		int	size = source[0].length;
		
		for (float[] item : source) {
			if (item.length != size) {
				return false;
			}
		}
		return true;
	}
}

