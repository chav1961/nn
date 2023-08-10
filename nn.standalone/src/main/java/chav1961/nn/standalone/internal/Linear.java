package chav1961.nn.standalone.internal;

import java.io.Serializable;

import chav1961.nn.core.interfaces.ActivationFunction;

/**
 * Linear activation function and its derivative
 * 
 * y = x
 * y' = 1
 * 
 * @author zoran
 */
public class Linear implements ActivationFunction, Serializable {
	private static final long serialVersionUID = -2110162625610977346L;

	@Override
    public float getValue(final float x) {
        return x;
    }

    @Override
    public float getPrime(final float y) {
        return 1;
    }
    
}