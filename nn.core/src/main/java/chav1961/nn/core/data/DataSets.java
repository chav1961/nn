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

package chav1961.nn.core.data;

import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.data.preprocessing.Scaler;

import chav1961.nn.core.data.preprocessing.MaxScaler;
import chav1961.nn.core.data.preprocessing.Standardizer;
import chav1961.nn.core.interfaces.NeuralNetwork;
import chav1961.nn.core.interfaces.TensorFactory;


/**
 * Data set related utility methods.
 * 
 * @author zoran
 */
public class DataSets {
    /**
     * Normalize specified data set and return used normalizer.
     * 
     * @param dataSet Data set to normalize
     * @return normalizer used to normalize data set
     */
    public static Scaler scaleMax(DataSet dataSet) {
        MaxScaler scaler = new MaxScaler(dataSet);
        scaler.apply(dataSet);
        
        return scaler;
    }
    
    public static Scaler standardize(final TensorFactory factory, DataSet dataSet) {
        Standardizer scaler = new Standardizer(factory, dataSet);
        scaler.apply(dataSet);
        
        return scaler;
    }    

    // encode single row
    public static float[] oneHotEncode(final String label, final String[] labels) {   // different labels
        final float[] vect = new float[labels.length];
        // ako su brojeci i ako su stringovi, ako su sve nule, negative ...

        for(int i=0; i<labels.length; i++) {
            if (labels[i].equals(label)) {
                vect[i] = 1;
            }
        }
        // kako rsiti negative vektore?
        return vect;
    }

    public static DataSet<?>[] trainTestSplit(DataSet<?> dataSet, double split) {
        dataSet.shuffle();
        return dataSet.split(split, 1-split);
    }

}
