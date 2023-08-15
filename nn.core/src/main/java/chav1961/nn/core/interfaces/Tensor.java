package chav1961.nn.core.interfaces;

import java.io.Serializable;

public interface Tensor extends Serializable, Cloneable {
	@FunctionalInterface
	public static interface FloatFunction {
		float apply(float value);
	}
	
    void fill(float value);
	
    float get(final int idx);
    float set(final int idx, final float val);
    void add(final int idx, final float value);
    void sub(final int idx, final float value);
    
    float get(final int row, final int col);
    void set(final int row, final int col, final float val);
    void add(final int row, final int col, final float value);
    void sub(final int row, final int col, final float value);
    
    float get(final int row, final int col, final int z);
    void set(final int row, final int col, final int z, final float val);
    void add(final int row, final int col, final int z, final float value);
    void sub(final int row, final int col, final int z, final float value);
    void sub(final float val);
    
    float get(final int row, final int col, final int z, final int fourth);
    void set(final int row, final int col, final int z, final int fourth, final float val);
    void add(final int row, final int col, final int z, final int fourth, final float value);
    void sub(final int row, final int col, final int z, final int fourth, final float value);

    void multiply(float m);
    
    void div(final float value);
    void div(final float[] divisors);
    
    Tensor copy();
    
    void add(Tensor t);
    void sub(Tensor t);
    void multiply(Tensor t);
    void div(Tensor t);

    void apply(FloatFunction f);
    void apply(ActivationFunction f);
    
    float getWithStride(int[] idxs);
    float[] getValues();
    void setValues(float... values);
    void copyFrom(float... src);
    int getCols();
    int getRows();
    int getDepth();
    int getFourthDim();
    int getDimensions();
    int size();

    boolean equals(Tensor t2, float delta);

    float sumAbs();
    float sumSqr();
    void sqrt();
    void randomize();
    
    void setValuesFromString(String values);
    void done();
}
