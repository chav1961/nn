package chav1961.nn.api.interfaces;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.ServiceLoader;

import chav1961.nn.api.interfaces.factories.TenzorFactory;
import chav1961.purelib.basic.exceptions.SyntaxException;

public interface Tenzor extends Serializable, AnyTenzor {
	@FunctionalInterface
	public static interface ConvertCallback {
		float convert(float value, int... indices);
	}

	@FunctionalInterface
	public static interface ProcessCallback {
		void process(float value, int... indices);
	}
	
	boolean equals(Tenzor another, float epsilon);
	boolean sizeEquals(Tenzor another);
	
	float[] getContent();
	float get(int... indices);
	Tenzor get(float[] target, int... indices);
	Tenzor set(float value, int... indices);
	Tenzor set(float[] toSet, int... indices);
	Tenzor set(Tenzor toSet);
	Tenzor fill(float value, int... indices);
	
	Tenzor duplicate();
	
	Tenzor add(Tenzor toAdd);
	default Tenzor addN(Tenzor toAdd) {
		return duplicate().add(toAdd);
	}
	Tenzor add(float toAdd);
	Tenzor sub(Tenzor toSubtract);
	Tenzor sub(float toSubtract);
	Tenzor mul(Tenzor toMultiply);
	Tenzor mul(float toMultiply);
	Tenzor div(Tenzor toDivide);
	Tenzor div(float toDivide);
	Tenzor matrixMul(Tenzor toMultiply);
	Tenzor trans();
	
	/*
	 * %0 + 2 * %1 *( sqrt(%2) - max(%3) + sum(%3)/sum2(%3))
	 */
	Tenzor calculate(CharSequence expression, Tenzor... parameters) throws SyntaxException; 
	Tenzor convert(ConvertCallback callback);
	void forEach(ProcessCallback callback);
	
	public static Tenzor valueOf(final CharSequence content, final int[] indices) {
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
