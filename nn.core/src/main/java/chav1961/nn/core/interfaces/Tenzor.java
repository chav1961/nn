package chav1961.nn.core.interfaces;

import java.io.Serializable;
import java.net.URI;

import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SpiService;

public interface Tenzor extends Serializable {
	public static interface TenzorFactory extends SpiService<Tenzor.TenzorFactory> {
		String TENZOR_FACTORY_SCHEMA = "tenzorfactory"; 
		
		URI getDefaultTensorType();
		Tenzor newInstance(final int... sizes);		
		Tenzor newInstance(final float[] content, final int... sizes);
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
	default Tenzor calculateN(String expression, Tenzor... parameters) throws SyntaxException {
		return duplicate().calculate(expression, parameters);
	}
	
	Tenzor convert(ConvertCallback callback);
	default Tenzor convertN(ConvertCallback callback) {
		return duplicate().convert(callback);
	}
	
	void forEach(ProcessCallback callback);
	
	
	public static Tenzor valueOf(final CharSequence content) {
		return null;
	}
	
	public class Factory {
		public static URI getDefaultTensorType() {
			return null;
		}

		public static void setDefaultTensorType(final URI TenzorType) {
			
		}
		
		public static Tenzor newInstance(final int... sizes) {
			return null;
		}
		
		public static Tenzor newInstance(final float[] content, final int... sizes) {
			return null;
		}

		public static Tenzor newInstance(final URI TenzorType, final int... sizes) {
			return null;
		}
		
		public static Tenzor newInstance(final URI TenzorType, final float[] content, final int... sizes) {
			return null;
		}
	}
}
