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
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
    
package chav1961.nn.core.interfaces;

import java.io.Serializable;

import chav1961.nn.core.network.AbstractNeuralNetwork;

/**
 * Cross Entropy Loss for binary(single output, two classes) classification.
 * It should be used in combination with sigmoid output activation function
 * 
 * @author Zoran Sevarac <zoran.sevarac@deepnetts.com>
 */
public class BinaryCrossEntropyLoss implements LossFunction, Serializable {
	private static final long serialVersionUID = 3271809444066219883L;

	private final float[] outputError;
    private float totalError;
    private int patternCount=0;    
     
    public BinaryCrossEntropyLoss(NeuralNetwork<?> neuralNet) {
        if (neuralNet.getOutputLayer().getWidth()>1) throw new IllegalArgumentException("BinaryCrossEntropyLoss can be only used with networks with single sigmoid output!");
        
        outputError = new float[1];
    }
       
    /**
     * Calculates pattern error to total error, and 
     * returns output error vector for specified actual and target outputs.
     * Also adds pattern error to total error .
     * 
     * @param actual actual output from the neural network
     * @param target target/desired output of the neural network
     * @return error vector for specified actual and target outputs
     */
    @Override
    public float[] addPatternError(final float[] actual,  final float[] target) {                
        outputError[0] = actual[0] - target[0]; // ovo je dL/dy izvod loss funkcije u odnosu na izlaz ovog neurona, kada je u outputu sigmoidna f-ja. ovo se koristi za deltu izlaznog neurona              
        totalError += (float)(target[0] * Math.log(actual[0]) + (1-target[0]) * Math.log(1-actual[0]));
        patternCount++;
                
        return outputError;        
    }
    
    @Override
    public void addRegularizationSum(final float reg) {
        totalError += reg;
    }       
       
    @Override
    public float getTotal() {
        return  -totalError / patternCount;
    }
    
    @Override
    public void reset() {
        totalError = 0;
        patternCount=0;
    }

    
}