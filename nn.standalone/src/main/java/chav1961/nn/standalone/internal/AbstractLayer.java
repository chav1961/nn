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

import java.io.Serializable;

import chav1961.nn.core.interfaces.ActivationFunction;
import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.core.interfaces.Layer;
import chav1961.nn.core.interfaces.LayerType;
import chav1961.nn.core.interfaces.NeuralNetwork;
import chav1961.nn.core.interfaces.Optimizer;
import chav1961.nn.core.interfaces.OptimizerType;
import chav1961.nn.core.interfaces.RandomWeightsType;
import chav1961.nn.core.interfaces.Tensor;

/**
 * Base class for different types of layers (except data/input layer) Provides
 * common functionality for all type of layers
 *
 * @author Zoran Sevarac
 */
public abstract class AbstractLayer implements Layer, Serializable {
	private static final long serialVersionUID = 4172662870441249554L;
	

    /**
     * Input weight matrix / connectivity matrix for previous layer 
     */
    protected Tensor weights;

    /**
     * Inputs to this layer (a reference to outputs matrix in prev layer, or
     * external input in input layer)
     */
    protected Tensor inputs;

    /**
     * Layer outputs
     */
    protected Tensor outputs;

    /**
     * Deltas used for learning
     */
    protected Tensor deltas;

    /**
     * Previous delta sums used by AdaGrad and AdaDelta
     */
    protected Tensor prevGradSqrSum, prevBiasSqrSum, prevDeltaWeightSqrSum, prevDeltaBiasSqrSum;

    /**
     * Weight changes for current and previous iteration
     */
    protected Tensor deltaWeights, prevDeltaWeights;

    protected Tensor gradients;
    
    protected ActivationFunction activation;

    /**
     * Learning rate for this layer
     */
    protected float learningRate = 0.1f;
    
    protected float momentum = 0f;
    
    protected float regularization = 0f;

    /**
     * Activation function type for this layer.
     */
    protected ActivationType activationType;

    private OptimizerType optimizerType = OptimizerType.SGD;
    private boolean batchMode = false;
    private int batchSize = 0;
    
    protected int width, height, depth; // layer dimensions - width and height

    // biases are used by output, fully connected and convolutional layers
    protected float[] biases;
    protected float[] deltaBiases; 
    protected float[] prevDeltaBiases;
    
    protected Optimizer optim;
    
    protected RandomWeightsType randomWeightsType = RandomWeightsType.XAVIER;

    private final LayerType	layerType;

    private AbstractLayer prevLayer;
    private AbstractLayer nextLayer;

    
    protected AbstractLayer(final LayerType layerType) {
    	this.layerType = layerType;
    }
    
    /**
     * This method should implement layer initialization when layer is added to
     * network (create weights, outputs, deltas, randomization etc.)
     * 
     * The code is called in 2 scenarios:
     * 1. Creating new ConvolutionalNetwork instance, where all class members are null and have to be initialized.
     * 2. After deserialization from saved net file, where some class members will be initialized by reading the stream during deserialization in defaultReadObject method. 
     * 
     * Init methods of all layers must be sensitive for both scenarios and check if field is null before initializing it with default new objects.
     * In most cases if the field is not null, than init method should not touch it.
     */
    public abstract void init(final NeuralNetwork<?> network);

    /**
     * This method should implement forward pass in subclasses
     */
    @Override
    public abstract <Tr> void forward(final NeuralNetwork<Tr> network);

    /**
     * This method should implement backward pass in subclasses
     */
    @Override
    public abstract <Tr> void backward(final NeuralNetwork<Tr> network);
    
    /**
     * Applies weight changes to current weights Must be different for
     * convolutional does nothing for MaxPooling Same for FullyConnected and
     * OutputLayer
     *
     */
    public abstract void applyWeightChanges();

    public abstract void setPrevLayer(AbstractLayer prevLayer);
    public abstract void setNextlayer(AbstractLayer nextlayer);
    
    @Override
    public LayerType getLayerType() {
    	return layerType;
    }
    
    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    public Layer getPrevLayer() {
        return prevLayer;
    }

    public Layer getNextLayer() {
        return nextLayer;
    }
    
    @Override
    public Tensor getWeights() {
        return weights;
    }

    public float[] getBiases() {
        return biases;
    }

    public void setBiases(float[] biases) {
        this.biases = biases;
    }

    @Override
    public final Tensor getOutputs() {
        return outputs;
    }

    @Override
    public final Tensor getDeltas() {
        return deltas;
    }

    public final Tensor getGradients() {
        return gradients;
    }

    public Tensor getDeltaWeights() {
        return deltaWeights;
    }

    public Tensor getPrevDeltaWeights() {
        return prevDeltaWeights;
    }

    public void setPrevDeltaWeights(Tensor prevDeltaWeights) {
        this.prevDeltaWeights = prevDeltaWeights;
    }

    public float[] getPrevDeltaBiases() {
        return prevDeltaBiases;
    }

    public float[] getDeltaBiases() {
        return deltaBiases;
    }

    public final void setOutputs(Tensor outputs) {
        this.outputs = outputs;
    }

    public void setWeights(Tensor weights) {
        this.weights = weights;
    }

    public void setWeights(String weightStr) {
        weights.setValuesFromString(weightStr);
    }

    public final void setDeltas(Tensor deltas) {
        this.deltas = deltas;
    }

    public ActivationFunction getActivation() {
        return activation;
    }

    public void setActivation(ActivationFunction activation) {
        this.activation = activation;
    }

    @Override
    public float getLearningRate() {
        return learningRate;
    }

    @Override
    public void setLearningRate(float learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public boolean isBatchMode() {
        return batchMode;
    }

    @Override
    public void setBatchMode(boolean batchMode) {
        this.batchMode = batchMode;
    }

    @Override
    public int getBatchSize() {
        return batchSize;
    }

    @Override
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setMomentum(float momentum) {
        this.momentum = momentum;
    }

    public float getMomentum() {
        return momentum;
    }

    @Override
    public OptimizerType getOptimizerType() {
        return optimizerType;
    }

    @Override
    public void setOptimizerType(OptimizerType optType) {
        this.optimizerType = optType;
        optim = Optimizer.create(optType, this);
    }

    public ActivationType getActivationType() {
        return activationType;
    }

    public final void setActivationType(ActivationType activationType) {
        this.activationType = activationType;
//        if (activationType != ActivationType.SOFTMAX) this.activation = ActivationFunction.create(activationType); // we use different layer for softmax
    }

    @Override
    public float getL1() {
        return weights.sumAbs();
    }

    @Override
    public float getL2() {
        return weights.sumSqr();
    }

    @Override
    public void setRegularization(float reg) {
        this.regularization = reg;
    }

    @Override
    public float getRegularization() {
        return this.regularization;
    }

    
    protected void setPrevLayerInternal(final AbstractLayer prevLayer) {
    	this.prevLayer = prevLayer;
    }    

    protected void setNextLayerInternal(final AbstractLayer nextLayer) {
    	this.nextLayer = nextLayer;
    }    
}
