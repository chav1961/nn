/**  
 *  DeepNetts is pure Java Deep Learning Library with support for Backpropagation 
 *  based learning and image recognition.
 * 
 *  Copyright (C) 2017  Zoran Sevarac <sevarac@gmail.com>
 *
 *  This file is part of DeepNetts.
 *
 *  DeepNetts is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.package deepnetts.core;
 */

package chav1961.nn.core.utils;

import chav1961.nn.core.interfaces.Tensor;

/**
 * Static utility methods for tensors.
 * 
 * @author Zoran Sevarac
 */
public class Tensors {

    public static float[] copyOf(float[] arr) {
    	return arr.clone();
//        float[] copy = new float[arr.length];
//        System.arraycopy(arr, 0, copy, 0, arr.length);
//        return copy;
    }

    public static float[] sub(float[] arr, float val) {
        for(int i=0; i < arr.length; i++) {
             arr[i] = arr[i] - val;
        }
        return arr;
    }

    public static float[] multiply(float[] arr1, float[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            arr1[i] *= arr2[i];
        }
        return arr1;
    }


    /**
     * Prevent instantiation of this class.
     */
    private Tensors() { }
    
    /**
     * Returns tensors with max value for each component of input tensors.
     * 
     * @param t 
     * @param max proposed max tensor
     * @return tensor with max value for each component
     */
    public static Tensor absMax(final Tensor t, final Tensor max) {
        final float[] tValues= t.getValues();
        final float[] maxValues= max.getValues();
        
        for(int i=0; i < tValues.length; i++) {
            if (Math.abs(tValues[i]) > maxValues[i]) maxValues[i] = Math.abs(tValues[i]);
        }
        return max;       
    }
    
    /**
     * Returns array with max values for each position in the given input vectors.
     * Stores max values in second parameter.
     * 
     * @param arr
     * @param max
     * @return 
     */
    public static float[] absMax(final float[] arr, final float[] max) {    
        for(int i=0; i < max.length; i++) {
            if (Math.abs(arr[i]) > max[i]) max[i] = Math.abs(arr[i]);
        }
        return max;       
    }    
    
    public static Tensor absMin(final Tensor t, final Tensor min) {
        final float[] tValues= t.getValues();
        final float[] minValues= min.getValues();
        
        for(int i=0; i < tValues.length; i++) {
            if (Math.abs(tValues[i]) < Math.abs(minValues[i])) minValues[i] = Math.abs(tValues[i]);
        }
        return min;       
    }    
    
    public static float[] absMin(final float[] arr, final float[] min) {       
        for(int i=0; i < arr.length; i++) {
            if (Math.abs(arr[i]) < Math.abs(min[i])) min[i] = Math.abs(arr[i]);
        }
        return min;       
    }        
    
    public static float[] div(final float[] array, final float val) {
        for (int i = 0; i < array.length; i++) {
            array[i] /= val;
        }
        return array;
    }

    public static float[] div(final float[] array, final float[] divisor) {
        for (int i = 0; i < array.length; i++) {
            array[i] /= divisor[i];
        }
        return array;
    }
    
    public static float[] sub(final float[] array1, final float[] array2) {
        for (int i = 0; i < array1.length; i++) {
            array1[i] -= array2[i];
        }
        return array1;
    }

    public static float[] add(final float[] array1, final float[] array2) {
        for (int i = 0; i < array1.length; i++) {
            array1[i] += array2[i];
        }
        return array1;
    }    
    
    /**
     * Subtracts tensor t2 from t1. The result is t1.
     *
     * @param t1
     * @param t2
     */
    public final static void sub(final Tensor t1, final Tensor t2) {
        for (int i = 0; i < t1.getValues().length; i++) {
            t1.getValues()[i] -= t2.getValues()[i];
        }
    }

    public static final void fill(final float[] array, final float val) {
        for (int i = 0; i < array.length; i++) {
            array[i] = val;
        }
    }

    // TODO: also set dimensions for dst
    public static final void copy(final Tensor src, final Tensor dest) {
        System.arraycopy(src.getValues(), 0, dest.getValues(), 0, src.getValues().length);
    }

    public static final void copy(final float[] src, final float[] dest) {
        System.arraycopy(src, 0, dest, 0, src.length);
    }
    
    public static String valuesAsString(Tensor... tensors) {
        StringBuilder sb = new StringBuilder();

        for (Tensor t : tensors) {
            sb.append(t.toString());
        }

        return sb.toString();
    }
}
