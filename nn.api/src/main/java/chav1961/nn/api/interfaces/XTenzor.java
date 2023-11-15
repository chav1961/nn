package chav1961.nn.api.interfaces;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.ServiceLoader;

import chav1961.purelib.basic.exceptions.SyntaxException;

public interface XTenzor extends Serializable {
	@FunctionalInterface
	public static interface ConvertCallback {
		double convert(double value, int... indices);
	}

	@FunctionalInterface
	public static interface ProcessCallback {
		void process(double value, int... indices);
	}
	
	int getArity();
	int getSize(int dimension);
	TenzorFactory getFactory();

	boolean equals(XTenzor another, double epsilon);
	boolean sizeEquals(XTenzor another);
	
	double[] getContent();
	double get(int... indices);
	XTenzor get(double[] target, int... indices);
	XTenzor set(double value, int... indices);
	XTenzor set(double[] toSet, int... indices);
	XTenzor set(XTenzor toSet);
	XTenzor fill(double value, int... indices);
	
	XTenzor duplicate();
	
	XTenzor add(XTenzor toAdd);
	default XTenzor addN(XTenzor toAdd) {
		return duplicate().add(toAdd);
	}
	XTenzor add(double toAdd);
	default XTenzor addN(double toAdd) {
		return duplicate().add(toAdd);
	}
	XTenzor sub(XTenzor toSubtract);
	default XTenzor subN(XTenzor toSubtract) {
		return duplicate().sub(toSubtract);
	}
	XTenzor sub(double toSubtract);
	default XTenzor subN(double toSubtract) {
		return duplicate().sub(toSubtract);
	}
	XTenzor mul(XTenzor toMultiply);
	default XTenzor mulN(XTenzor toMultiply) {
		return duplicate().mul(toMultiply);
	}
	XTenzor mul(double toMultiply);
	default XTenzor mulN(double toMultiply) {
		return duplicate().mul(toMultiply);
	}
	XTenzor div(XTenzor toDivide);
	default XTenzor divN(XTenzor toDivide) {
		return duplicate().div(toDivide);
	}
	XTenzor div(double toDivide);
	default XTenzor divN(double toDivide) {
		return duplicate().div(toDivide);
	}
	XTenzor matrixMul(XTenzor toMultiply);
	default XTenzor matrixMulN(XTenzor toMultiply) {
		return duplicate().matrixMul(toMultiply);
	}
	
	XTenzor trans();
	
	/*
	 * %0 + 2 * %1 *( sqrt(%2) - max(%3) + sum(%3)/sum2(%3))
	 */
	XTenzor calculate(CharSequence expression, XTenzor... parameters) throws SyntaxException; 
	default XTenzor calculateN(CharSequence expression, XTenzor... parameters) throws SyntaxException {
		return duplicate().calculate(expression, parameters);
	}
	
	XTenzor convert(ConvertCallback callback);
	default XTenzor convertN(ConvertCallback callback) {
		return duplicate().convert(callback);
	}
	
	void forEach(ProcessCallback callback);
	
	
	public static XTenzor valueOf(final CharSequence content, final int[] indices) {
		return null;
	}
	
	public class Factory {
		private static TenzorFactory	factory;
		private static URI					tenzorType;
		
		static {
			for(TenzorFactory item : ServiceLoader.load(TenzorFactory.class)) {
				tenzorType = item.getDefaultTenzorType();
				factory = item;
				break;
			}
		}
		
		public static URI getDefaultTenzorURI() {
			return tenzorType;
		}

		public static void setDefaultTensorURI(final URI newTenzorType) {
			if (newTenzorType == null) {
				throw new NullPointerException("Tenzor type to set cen't be null");
			}
			else {
				factory = getFactory(newTenzorType);
				tenzorType = factory.getDefaultTenzorType();
			}
		}
		
		public static TenzorFactory getFactory(final URI newTenzorType) {
			if (newTenzorType == null) {
				throw new NullPointerException("Tenzor type to set cen't be null");
			}
			else {
				for(TenzorFactory item : ServiceLoader.load(TenzorFactory.class)) {
					if (item.canServe(newTenzorType)) {
						return item;
					}
				}
				throw new IllegalArgumentException("No any service provider found to support tenzor type ["+newTenzorType+"]"); 
			}
		}
		
		public static Tenzor newInstance(final int... sizes) {
			if (getDefaultTenzorURI() == null) {
				throw new IllegalStateException("No tenzor type defined. Call setDefaultTensorType(...) before");
			}
			else {
				return newInstance(getDefaultTenzorURI(), sizes);
			}
		}
		
		public static Tenzor newInstance(final float[] content, final int... sizes) {
			if (getDefaultTenzorURI() == null) {
				throw new IllegalStateException("No default tensor type defined. Call setDefaultTensorType(...) before");
			}
			else {
				return newInstance(getDefaultTenzorURI(), content, sizes);
			}
		}

		public static Tenzor newInstance(final URI tenzorType, final int... sizes) {
			if (getDefaultTenzorURI() == null) {
				throw new NullPointerException("Tenzor type can't be null");
			}
			else if (sizes == null || sizes.length == 0) {
				throw new IllegalArgumentException("Size list can't be null or empty array");
			}
			else {
				TenzorFactory	f = null;
				
				if (tenzorType.equals(getDefaultTenzorURI())) {
					if (factory == null) {
						for(TenzorFactory item : ServiceLoader.load(TenzorFactory.class)) {
							if (item.canServe(tenzorType)) {
								f = item;
								break;
							}
						}
						if (f == null) {
							throw new IllegalStateException("No tenzor factory found for ["+tenzorType+"]");
						}
					}
					else {
						f = factory;
					}
				}
				else {
					for(TenzorFactory item : ServiceLoader.load(TenzorFactory.class)) {
						if (item.canServe(tenzorType)) {
							f = item;
							break;
						}
					}
					if (f == null) {
						throw new IllegalStateException("No tenzor factory found for ["+tenzorType+"]");
					}
				}
				return f.newInstance(sizes[0], Arrays.copyOfRange(sizes, 1, sizes.length));
			}
		}
		
		public static Tenzor newInstance(final URI tenzorType, final float[] content, final int... sizes) {
			final Tenzor	result = newInstance(tenzorType, sizes);
			final int[]		dimensions = new int[sizes.length];
			
			Arrays.fill(dimensions, -1);
			result.set(content, dimensions);			
			return result;
		}
	}
}
