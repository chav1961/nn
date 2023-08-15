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

package chav1961.nn.core.data.preprocessing;

import java.io.Serializable;

import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.data.preprocessing.Scaler;

import chav1961.nn.core.interfaces.MLDataItem;
import chav1961.nn.core.interfaces.Tensor;
import chav1961.nn.core.interfaces.TensorFactory;

/**
 * Performs standardization in order to get desired statistical properties of the data set.
 * Zero mean and one standard deviation.  
 * 
 * X = (X - MEAN) / STD
 * 
 * @author Zoran Sevarac
 */
public class Standardizer implements Scaler<DataSet<MLDataItem>>, Serializable {
	private static final long serialVersionUID = 3090230949401627734L;

	private final Tensor mean;
    private final Tensor std;
    
    public Standardizer(final TensorFactory factory, final DataSet<MLDataItem> dataSet) {
        Tensor t = dataSet.get(0).getInput();
        // int dims = t.getDimensions();
        mean = factory.newInstance(t.getCols());
        std = factory.newInstance(t.getCols());
        
        for(MLDataItem item : dataSet) {
            mean.add(item.getInput());
        }         
        mean.div((float)dataSet.size());
        
        
        //std = sqrt(sum((x-m)^2)/n);
        
        for(MLDataItem item : dataSet) {
            Tensor diff = item.getInput().copy();
            diff.sub(mean);
            diff.multiply(diff);  // ^2
            std.add(diff);            
        }        
        std.div(dataSet.size()-1);
        std.sqrt();           
    }
        
    @Override
    public void apply(DataSet<MLDataItem> dataSet) {
        for (MLDataItem item : dataSet) {
            item.getInput().sub(mean);
            item.getInput().div(std);
        }
    }
    
}