package chav1961.nn.standalone.internal;

import java.io.Serializable;

import chav1961.nn.core.interfaces.ActivationFunction;
import chav1961.nn.core.interfaces.ActivationType;

/**
 * Rectified Linear Activation and its Derivative.
 * 
 * y = max(0, x)
 *        - 
 *       | 1, x > 0
 * y' = <
 *       | 0, 0x<=0 
 *        -
 * 
 * @author Zoran Sevarac
 */
public final class Relu implements ActivationFunction, Serializable {

    @Override
    public float getValue(final float x) {
        return Math.max(0, x);  
    }

    @Override
    public float getPrime(final float y) {
         return ( y > 0 ? 1 : 0);
    }
    

	@Override
	public ActivationType getActivationType() {
		return ActivationType.RELU;
	}
}