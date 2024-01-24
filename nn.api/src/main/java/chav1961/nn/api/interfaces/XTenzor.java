package chav1961.nn.api.interfaces;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.ServiceLoader;

import chav1961.nn.api.interfaces.factories.TenzorFactory;
import chav1961.purelib.basic.exceptions.SyntaxException;

public interface XTenzor extends Serializable, AnyTenzor {
	@FunctionalInterface
	public static interface ConvertCallback {
		double convert(double value, int... indices);
	}

	@FunctionalInterface
	public static interface ProcessCallback {
		void process(double value, int... indices);
	}
	
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
	XTenzor add(double toAdd);
	XTenzor sub(XTenzor toSubtract);
	XTenzor sub(double toSubtract);
	XTenzor mul(XTenzor toMultiply);
	XTenzor mul(double toMultiply);
	XTenzor div(XTenzor toDivide);
	XTenzor div(double toDivide);
	XTenzor matrixMul(XTenzor toMultiply);
	XTenzor trans();
	
	/*
	 * %0 + 2 * %1 *( sqrt(%2) - max(%3) + sum(%3)/sum2(%3))
	 */
	XTenzor calculate(CharSequence expression, XTenzor... parameters) throws SyntaxException; 
	XTenzor convert(ConvertCallback callback);
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
