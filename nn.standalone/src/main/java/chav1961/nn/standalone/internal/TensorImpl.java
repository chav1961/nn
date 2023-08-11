/**
 *  DeepNetts is pure Java Deep Learning Library with support for Backpropagation
 *  based learning and image recognition.
 *
 *  Copyright (C) 2017  Zoran Sevarac <sevarac@gmail.com>
 *
 * This file is part of DeepNetts.
 *
 * DeepNetts is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <https://www.gnu.org/licenses/>.package
 * deepnetts.core;
 */
package chav1961.nn.standalone.internal;

import java.util.Arrays;
import java.util.function.Function;

import chav1961.nn.core.interfaces.Tensor;
import chav1961.nn.core.utils.RandomGenerator;

/**
 * This class represents multidimensional array/matrix (can be 1D, 2D, 3D or 4D).
 *
 * @author Zoran Sevarac
 */
public class TensorImpl implements Tensor {
	private static final long serialVersionUID = -2345745004528761209L;
	
	
    // tensor dimensions - better use shape
    private final int cols, rows, depth, fourthDim, dimensions;
    private final int[] shape = new int[4];
    private int rank; 
    private int size; 

    /**
     * Values stored in this tensor make it final , only input layer and tests
     * sets values
     */
    private float values[];

    /**
     * Creates a single row tensor with specified values.
     *
     * @param values values of column tensor
     */
    public TensorImpl(final float[] values) {
        this.rows = 1;
        this.cols = values.length;
        this.depth = 1;
        this.fourthDim = 1;
        this.dimensions = 1;
        this.values = values;
    }

    /**
     * Creates a 2D tensor / matrix with specified values.
     *
     * @param vals
     */
    public TensorImpl(final float[][] vals) {
        this.rows = vals.length;
        this.cols = vals[0].length;
        this.depth = 1;
        this.fourthDim = 1;
        this.dimensions = 2;
        this.values = new float[rows * cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                set(row, col, vals[row][col]);
            }
        }
    }

    /**
     * Creates a 3D tensor from specified 3D array
     *
     * @param vals 2D array of tensor values
     */
    public TensorImpl(final float[][][] vals) {
        this.depth = vals.length;
        this.rows = vals[0].length;
        this.cols = vals[0][0].length;

        this.fourthDim = 1;
        this.dimensions = 3;
        this.values = new float[rows * cols * depth];

        for (int z = 0; z < depth; z++) {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    set(row, col, z, vals[z][row][col]);
                }
            }
        }
    }

    public TensorImpl(final float[][][][] vals) {
        this.fourthDim = vals.length;
        this.depth = vals[0].length;
        this.rows = vals[0][0].length;
        this.cols = vals[0][0][0].length;

        this.dimensions = 4;
        this.values = new float[rows * cols * depth * fourthDim];

        for (int f = 0; f < fourthDim; f++) {
            for (int z = 0; z < depth; z++) {
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++) {
                        set(row, col, z, f, vals[f][z][row][col]);
                    }
                }
            }
        }
    }

    /**
     * Creates an empty single row tensor with specified number of columns.
     * TODO: this should be rows!!!
     * @param cols number of columns
     */
    public TensorImpl(int cols) {
        if (cols < 0) {
            throw new IllegalArgumentException("Number of cols cannot be negative: " + cols);
        }

        this.cols = cols;
        this.rows = 1;
        this.depth = 1;
        this.fourthDim = 1;
        this.dimensions = 1;
        values = new float[cols];
    }

    public TensorImpl(int cols, float val) {
        if (cols < 0) {
            throw new IllegalArgumentException("Number of cols cannot be negative: " + cols);
        }

        this.cols = cols;
        this.rows = 1;
        this.depth = 1;
        this.fourthDim = 1;
        this.dimensions = 1;
        values = new float[cols];

        for (int i = 0; i < values.length; i++) {
            values[i] = val;
        }
    }

    /**
     * Creates a tensor with specified number of rows and columns.
     *
     * @param rows number of rows
     * @param cols number of columns
     */
    public TensorImpl(int rows, int cols) {
        if (rows < 0) {
            throw new IllegalArgumentException("Number of rows cannot be negative: " + rows);
        }
        if (cols < 0) {
            throw new IllegalArgumentException("Number of cols cannot be negative: " + cols);
        }

        this.rows = rows;
        this.cols = cols;
        this.depth = 1;
        this.fourthDim = 1;
        this.dimensions = 2;
        values = new float[rows * cols];
    }

    public TensorImpl(int rows, int cols, float[] values) {
        if (rows < 0) {
            throw new IllegalArgumentException("Number of rows cannot be negative: " + rows);
        }
        if (cols < 0) {
            throw new IllegalArgumentException("Number of cols cannot be negative: " + cols);
        }
        if (rows * cols != values.length) {
            throw new IllegalArgumentException("Number of values does not match tensor dimensions! " + values.length);
        }

        this.rows = rows;
        this.cols = cols;
        this.depth = 1;
        this.fourthDim = 1;
        this.dimensions = 2;

        this.values = values;
    }

    /**
     * Creates a 3D tensor with specified number of rows, cols and depth.
     *
     * @param rows number of rows
     * @param cols number of columns
     * @param depth tensor depth
     */
    public TensorImpl(int rows, int cols, int depth) { // trebalo bi depth, rows, cols
        if (rows < 0) {
            throw new IllegalArgumentException("Number of rows cannot be negative: " + rows);
        }
        if (cols < 0) {
            throw new IllegalArgumentException("Number of cols cannot be negative: " + cols);
        }
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative: " + depth);
        }

        this.rows = rows;
        this.cols = cols;
        this.depth = depth;
        this.fourthDim = 1;
        this.dimensions = 3;
        this.values = new float[rows * cols * depth];
    }

    // cols, rows, 3rd, 4th?
    public TensorImpl(int rows, int cols, int depth, int fourthDim) { // trebalo bi fourthDim, depth, rows, cols
        if (rows < 0) {
            throw new IllegalArgumentException("Number of rows cannot be negative: " + rows);
        }
        if (cols < 0) {
            throw new IllegalArgumentException("Number of cols cannot be negative: " + cols);
        }
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative: " + depth);
        }
        if (fourthDim < 0) {
            throw new IllegalArgumentException("fourthDim cannot be negative: " + fourthDim);
        }

        this.rows = rows;
        this.cols = cols;
        this.depth = depth;
        this.fourthDim = fourthDim;
        this.dimensions = 4;
        this.values = new float[rows * cols * depth * fourthDim];
    }

    public TensorImpl(int rows, int cols, int depth, int fourthDim, float[] values) {
        if (rows < 0) {
            throw new IllegalArgumentException("Number of rows cannot be negative: " + rows);
        }
        if (cols < 0) {
            throw new IllegalArgumentException("Number of cols cannot be negative: " + cols);
        }
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative: " + depth);
        }
        if (fourthDim < 0) {
            throw new IllegalArgumentException("fourthDim cannot be negative: " + fourthDim);
        }

        this.rows = rows;
        this.cols = cols;
        this.depth = depth;
        this.fourthDim = fourthDim;
        this.dimensions = 4;
        this.values = values;
    }

    public TensorImpl(int rows, int cols, int depth, float[] values) {
        if (rows < 0) {
            throw new IllegalArgumentException("Number of rows cannot be negative: " + rows);
        }
        if (cols < 0) {
            throw new IllegalArgumentException("Number of cols cannot be negative: " + cols);
        }
        if (depth < 0) {
            throw new IllegalArgumentException("Depth cannot be negative: " + depth);
        }
        if (rows * cols * depth != values.length) {
            throw new IllegalArgumentException("Number of values does not match tensor dimensions! " + values.length);
        }

        this.cols = cols;
        this.rows = rows;
        this.depth = depth;
        this.fourthDim = 1;
        this.dimensions = 3;
        this.values = values;
    }

    public TensorImpl(Tensor t) {
        this.dimensions = t.getDimensions();
        this.cols = t.getCols();
        this.rows = t.getRows();
        this.depth = t.getDepth();
        this.fourthDim = t.getFourthDim();
        this.values = t.getValues().clone();
    }

    /**
     * Gets value at specified index position.
     *
     * @param idx
     * @return
     */
    @Override
    public final float get(final int idx) {
        return values[idx];
    }

    /**
     * Sets value at specified index position.
     *
     * @param idx
     * @param val
     * @return
     */
    @Override
    public final float set(final int idx, final float val) {
        return values[idx] = val;
    }

    /**
     * Returns matrix value at row, col
     *
     * @param col
     * @param row
     * @return value at [row, col]
     */
    @Override
    public final float get(final int row, final int col) {
    	return get(row * cols + col);
//        final int idx = row * cols + col;
//        return values[idx];
    }

    /**
     * Sets matrix value at specified [row, col] position
     *
     * @param row matrix roe
     * @param col matrix col
     * @param val value to set
     */
    @Override
    public final void set(final int row, final int col, final float val) {
    	set(row * cols + col, val);
//        final int idx = row * cols + col;
//        values[idx] = val;
    }

    /**
     * Returns value at row, col, z
     *
     * @param col
     * @param row
     * @param z
     * @return
     */
    @Override
    public final float get(final int row, final int col, final int z) {
    	return get(z * cols * rows + row * cols + col);
//        final int idx = z * cols * rows + row * cols + col;
//        return values[idx];
    }

    @Override
    public final void set(final int row, final int col, final int z, final float val) {
    	set(z * cols * rows + row * cols + col, val);
//        final int idx = z * cols * rows + row * cols + col;
//        values[idx] = val;
    }

    @Override
    public final float get(final int row, final int col, final int z, final int fourth) {
    	return get(fourth * rows * cols * depth + z * rows * cols + row * cols + col);
//        final int idx = fourth * rows * cols * depth + z * rows * cols + row * cols + col;
//        return values[idx];
    }

    @Override
    public final void set(final int row, final int col, final int z, final int fourth, final float val) {
        set(fourth * rows * cols * depth + z * rows * cols + row * cols + col, val);
//    	final int idx = fourth * rows * cols * depth + z * rows * cols + row * cols + col;        
//        values[idx] = val;
    }

    @Override
    public final float getWithStride(final int[] idxs) {
        final int idx = idxs[0] * shape[1] * shape[2] * shape[3] + idxs[1] * shape[2] * shape[3] + idxs[2] * shape[3] + idxs[3];
        return values[idx];
    }

    @Override
    public final float[] getValues() {
        return values;
    }

    @Override
    public final void setValues(final float... values) {
        this.values = values;
    }

    @Override
    public final void copyFrom(final float[] src) {
        System.arraycopy(src, 0, values, 0, values.length);
    }

    @Override
    public final int getCols() {
        return cols;
    }

    @Override
    public final int getRows() {
        return rows;
    }

    @Override
    public final int getDepth() {
        return depth;
    }

    @Override
    public final int getFourthDim() {
        return fourthDim;
    }

    @Override
    public final int getDimensions() {
        return dimensions;
    }

    @Override
    public final int size() {
        return values.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]);
            if ( ((i+1) % cols == 0) && (i < values.length - 1)  ) {
                sb.append("; ");
            } else if (i < values.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");

        return sb.toString();
    }

    @Override
    public final void add(final int idx, final float value) {
        values[idx] += value;
    }

    /**
     * Adds specified value to matrix value at position x, y
     *
     * @param col
     * @param row
     * @param value
     */
    @Override
    public final void add(final int row, final int col, final float value) {
    	add(row * cols + col, value);
//        final int idx = row * cols + col;
//        values[idx] += value;
    }

    @Override
    public final void  add(final int row, final int col, final int z, final float value) {
    	add(z * cols * rows + row * cols + col, value);
//        final int idx = z * cols * rows + row * cols + col;
//        values[idx] += value;
    }

    @Override
    public final void add(final int row, final int col, final int z, final int fourth, final float value) {
    	add(fourth * cols * rows * depth + z * cols * rows + row * cols + col, value);
//        final int idx = fourth * cols * rows * depth + z * cols * rows + row * cols + col;
//        values[idx] += value;
    }



    /**
     * Adds specified tensor t to this tensor.
     *
     * @param t tensor to add
     */
    @Override
    public final void add(Tensor t) {
        for (int i = 0; i < values.length; i++) {
            values[i] += t.getValues()[i];
        }
    }

    @Override
    public final void sub(final int idx, final float value) {
        values[idx] -= value;
    }
    
    @Override
    public final void sub(final int row, final int col, final float value) {
    	sub(row * cols + col, value);
//        final int idx = row * cols + col;
//        values[idx] -= value;
    }

    @Override
    public final void sub(final int row, final int col, final int z, final float value) {
    	sub(z * rows * cols + row * cols + col, value);
//        final int idx = z * rows * cols + row * cols + col;
//        values[idx] -= value;
    }

    @Override
    public final void sub(final int row, final int col, final int z, final int fourth, final float value) {
    	sub(fourth * rows * cols * depth + z * rows * cols + row * cols + col, value);
//        final int idx = fourth * rows * cols * depth + z * rows * cols + row * cols + col;
//        values[idx] -= value;
    }

    /**
     * Subtracts specified tensor t from this tensor.
     *
     * @param t tensor to subtract
     */
    @Override
    public final void sub(final Tensor t) {
        for (int i = 0; i < values.length; i++) {
            values[i] -= t.getValues()[i];
        }
    }
    
    @Override
    public final void sub(final float val) {
        for (int i = 0; i < values.length; i++) {
            values[i] -= val;
        }
    }    

    /**
     * Divide all values in this tensor with specified value.
     *
     * @param value
     */
    @Override
    public final void div(final float value) {
        for (int i = 0; i < values.length; i++) {
            values[i] /= value;
        }
    }
    
    // element wise division
    @Override
    public final void div(final float[] divisors) {
        for (int i = 0; i < values.length; i++) {
            values[i] /= divisors[i];
        }
    }    

    /**
     * Fills the entire tensor with specified value.
     *
     * @param value value used to fill tensor
     */
    @Override
    public final void fill(final float value) {
        for (int i = 0; i < values.length; i++) {
            values[i] = value;
        }
    }

    @Override
    public final void div(Tensor t) {
        for (int i = 0; i < values.length; i++) {
            this.getValues()[i] /= t.getValues()[i];
        }
    }
    
    // TODO: fix number of dimensions
    @Override
    public TensorImpl copy() {
        TensorImpl newTensor = new TensorImpl(rows, cols,  depth, fourthDim);    
        System.arraycopy(this.values, 0, newTensor.values, 0, this.values.length);                
        return newTensor;
    }
    
    @Override
    public void apply(final FloatFunction f) {
        for(int i=0; i<values.length; i++) {
            values[i] = f.apply(values[i]);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TensorImpl other = (TensorImpl) obj;
        if (this.cols != other.cols) {
            return false;
        }
        if (this.rows != other.rows) {
            return false;
        }
        if (this.depth != other.depth) {
            return false;
        }
        if (this.fourthDim != other.fourthDim) {
            return false;
        }
        if (this.dimensions != other.dimensions) {
            return false;
        }
        if (!Arrays.equals(this.values, other.values)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + this.cols;
        hash = 41 * hash + this.rows;
        hash = 41 * hash + this.depth;
        hash = 41 * hash + this.fourthDim;
        hash = 41 * hash + this.dimensions;
        hash = 41 * hash + Arrays.hashCode(this.values);
        return hash;
    }

    @Override
    public boolean equals(final Tensor t2, float delta) {
        float[] arr2 = t2.getValues();

        for (int i = 0; i < values.length; i++) {
            if (Math.abs(values[i] - arr2[i]) > delta) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets tensor values from csv string.
     *
     * @param values csv string with values
     */
    @Override
    public void setValuesFromString(String values) {
        String[] strArr = values.split(",");
        for (int i = 0; i < strArr.length; i++) {
            this.values[i] = Float.parseFloat(strArr[i]);
        }
    }

    
    /**
     * Returns sum of abs values of this tensor - L1 norm
     *
     * @return L1 norm
     */
    @Override
    public float sumAbs() {
        float sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += Math.abs(values[i]);
        }
        return sum;
    }

    /**
     * Returns sum of sqr values of this tensor - L2 norm
     *
     * @return L2 norm
     */
    @Override
    public float sumSqr() {
        float sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i] * values[i];
        }
        return sum;
    }

    // works for 2d tensors
    @Override
    public void randomize() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                values[r * cols + c] = RandomGenerator.getDefault().nextFloat();
            }
        }
    }

    @Override
    public void multiply(Tensor tensor2) {
        for(int i=0; i<values.length; i++) {
            values[i] *= tensor2.getValues()[i];
        }
    }

    @Override
    public void multiply(float m) {
        for(int i=0; i<values.length; i++) {
            values[i] *= m;
        }
    }

    @Override
    public void sqrt() {
        for (int i = 0; i < values.length; i++) {
            values[i] = (float)Math.sqrt(values[i]);
        }
    }
}
