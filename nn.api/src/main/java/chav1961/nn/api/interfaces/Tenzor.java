package chav1961.nn.api.interfaces;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.ServiceLoader;

import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SpiService;

public interface Tenzor extends Serializable {
	public static interface TenzorFactory extends SpiService<Tenzor.TenzorFactory> {
		String TENZOR_FACTORY_SCHEMA = "tenzorfactory"; 
		
		URI getDefaultTensorType();
		Tenzor newInstance(int size, int... advanced);		
		Tenzor newInstance(float[] content, int size, int... advanced);
	}
	
	@FunctionalInterface
	public static interface ConvertCallback {
		float convert(float value, int... indices);
	}

	@FunctionalInterface
	public static interface ProcessCallback {
		void process(float value, int... indices);
	}
	
	int getArity();
	int getSize(int dimension);

	boolean equals(Tenzor another, float epsilon);
	
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
	default Tenzor addN(float toAdd) {
		return duplicate().add(toAdd);
	}
	Tenzor sub(Tenzor toSubtract);
	default Tenzor subN(Tenzor toSubtract) {
		return duplicate().sub(toSubtract);
	}
	Tenzor sub(float toSubtract);
	default Tenzor subN(float toSubtract) {
		return duplicate().sub(toSubtract);
	}
	Tenzor mul(Tenzor toMultiply);
	default Tenzor mulN(Tenzor toMultiply) {
		return duplicate().mul(toMultiply);
	}
	Tenzor mul(float toMultiply);
	default Tenzor mulN(float toMultiply) {
		return duplicate().mul(toMultiply);
	}
	Tenzor div(Tenzor toDivide);
	default Tenzor divN(Tenzor toDivide) {
		return duplicate().div(toDivide);
	}
	Tenzor div(float toDivide);
	default Tenzor divN(float toDivide) {
		return duplicate().div(toDivide);
	}
	/*
	 * %0 + 2 * %1 *( sqrt(%2) - max(%3) + sum(%3)/sum2(%3))
	 */
	Tenzor calculate(CharSequence expression, Tenzor... parameters) throws SyntaxException; 
	default Tenzor calculateN(CharSequence expression, Tenzor... parameters) throws SyntaxException {
		return duplicate().calculate(expression, parameters);
	}
	
	Tenzor convert(ConvertCallback callback);
	default Tenzor convertN(ConvertCallback callback) {
		return duplicate().convert(callback);
	}
	
	void forEach(ProcessCallback callback);
	
	
	public static Tenzor valueOf(final CharSequence content, final int[] indices) {
		return null;
	}
	
	public class Factory {
		private static Tenzor.TenzorFactory	factory;
		private static URI	tenzorType;
		
		static {
			for(Tenzor.TenzorFactory item : ServiceLoader.load(Tenzor.TenzorFactory.class)) {
				tenzorType = item.getDefaultTensorType();
				break;
			}
		}
		
		public static URI getDefaultTensorType() {
			return tenzorType;
		}

		public static void setDefaultTensorType(final URI newTenzorType) {
			if (newTenzorType == null) {
				throw new NullPointerException("Tenzor type to set cen't be null");
			}
			else {
				tenzorType = newTenzorType;
				factory = null;
			}
		}
		
		public static Tenzor newInstance(final int... sizes) {
			if (tenzorType == null) {
				throw new IllegalStateException("No default tensor type defines. Call setDefaultTensorType(...) before");
			}
			else {
				return newInstance(tenzorType, sizes);
			}
		}
		
		public static Tenzor newInstance(final float[] content, final int... sizes) {
			if (tenzorType == null) {
				throw new IllegalStateException("No default tensor type defines. Call setDefaultTensorType(...) before");
			}
			else {
				return newInstance(tenzorType, content, sizes);
			}
		}

		public static Tenzor newInstance(final URI tenzorType, final int... sizes) {
			if (tenzorType == null) {
				throw new NullPointerException("Tenzor type can't be null");
			}
			else if (sizes == null || sizes.length == 0) {
				throw new IllegalArgumentException("Size list can't be null or empty array");
			}
			else {
				Tenzor.TenzorFactory	f = null;
				
				if (tenzorType.equals(getDefaultTensorType())) {
					if (factory == null) {
						for(Tenzor.TenzorFactory item : ServiceLoader.load(Tenzor.TenzorFactory.class)) {
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
					for(Tenzor.TenzorFactory item : ServiceLoader.load(Tenzor.TenzorFactory.class)) {
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
