package chav1961.nn.standalone.internal;

import java.io.Serializable;

import chav1961.nn.core.interfaces.ActivationFunction;

/**
 * Sigmoid activation function
 * 
 * TODO: slope, amplitude?, avoid NaN
 * 
 * @see  <a href=" https://en.wikipedia.org/wiki/Sigmoid_function">Sigmoid_function on Wikipedia</a>
 * @author Zoran Sevarac
 */
public final class Sigmoid implements ActivationFunction, Serializable {

    @Override
    public float getValue(final float x) {
        return 1 / (1 + (float) Math.exp(-x));
    }

    @Override
    public float getPrime(final float y) {
        return y*(1-y);
    }
       
}
