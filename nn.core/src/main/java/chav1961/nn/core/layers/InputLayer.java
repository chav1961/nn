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
package chav1961.nn.core.layers;

import chav1961.nn.core.interfaces.LayerType;
import chav1961.nn.core.interfaces.NeuralNetwork;
import chav1961.nn.core.interfaces.Tensor;

/**
 * Input layer in neural network, which accepts external input, and sends it to next layer in a network.
 * It is always the first layer in the network. Input can be 1D, 2D or 3D vectors/tensors of float values.
 *
 * @author Zoran Sevarac
 */
public class InputLayer extends AbstractLayer {
	private static final long serialVersionUID = 4852747073698145422L;
	
	
    /**
     * Creates input layer with specified width, height, and depth (number of
     * channels).
     *
     * @param width layer width
     * @param height layer height
     * @param depth layer depth (number of input channels)
     */
    public InputLayer(int width, int height, int depth) {
    	super(LayerType.INPUT);
    	
        this.width = width;
        this.height = height;
        this.depth = depth;  
    }

    /**
     * Creates input layer with specified width and height, with depth=1 (single channel).
     *
     * @param width layer width
     * @param height layer height
     */
    public InputLayer(int width, int height) {
    	super(LayerType.INPUT);
    	
        this.width = width;
        this.height = height;
        this.depth = 1; 
    }

    /**
     * Creates input layer with specified width, and with height and depth equals to one.
     *
     * @param width layer width
     */
    public InputLayer(int width) {
    	super(LayerType.INPUT);
    	
        this.width = width;
        this.height = 1;
        this.depth = 1;
    }

	@Override
	public void setPrevLayer(final AbstractLayer prevLayer) {
		throw new IllegalStateException("Input layer can't have previous layers"); 
	}

	@Override
	public void setNextlayer(final AbstractLayer nextlayer) {
		setNextLayerInternal(nextlayer);
	}
    
    /**
     * Initialize this layer in network.
     */
    @Override
    public final void init(final NeuralNetwork<?> network) {
        inputs = network.getTensorFactory().newInstance(height, width, depth);
        outputs = inputs;  
    }

    /**
     * Sets network input
     *
     * @param in input matrix/array
     */
    public void setInput(final Tensor in) {
        // TODO: check input tensor dimensions and throw exception if they dont match
        inputs.setValues(in.getValues());
    }

    /**
     * This method does nothing in input layer
     */
    @Override
    public <Tr> void forward(final NeuralNetwork<Tr> network) {
        throw new IllegalStateException("This method does nothing and should never be called");
    }

    /**
     * This method does nothing in input layer
     */
    @Override
    public <Tr> void backward(final NeuralNetwork<Tr> network) {
        throw new IllegalStateException("This method does nothing and should never be called");
    }

    /**
     * This method does nothing in input layer
     */
    @Override
    public void applyWeightChanges() {
    }
    
    @Override
    public String toString() {
        if (height==1 && depth==1)
            return "Input Layer { width:"+width+", height:"+height+", depth:"+depth+" }";
        else
            return "Input Layer { width:"+width+", height:"+height+", depth:"+depth+" }";
    }

}
