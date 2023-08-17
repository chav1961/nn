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

package chav1961.nn.core.network;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;

import chav1961.nn.core.eval.Evaluators;
import chav1961.nn.core.interfaces.BinaryCrossEntropyLoss;
import chav1961.nn.core.interfaces.CrossEntropyLoss;
import chav1961.nn.core.interfaces.Layer;
import chav1961.nn.core.interfaces.LossFunction;
import chav1961.nn.core.interfaces.LossType;
import chav1961.nn.core.interfaces.MLDataItem;
import chav1961.nn.core.interfaces.MeanSquaredErrorLoss;
import chav1961.nn.core.interfaces.NetworkType;
import chav1961.nn.core.interfaces.NeuralNetwork;
import chav1961.nn.core.interfaces.Tensor;
import chav1961.nn.core.interfaces.TensorFactory;
import chav1961.nn.core.interfaces.Trainer;
import chav1961.purelib.basic.exceptions.EnvironmentException;

/**
 * Base class for all neural networks in DeepNetts.
 * Holds a list of abstract layers and loss function.
 * Provides methods for forward and backward calculation, and to access input and output layers.
 * Also provides network and output labels.
 *
 * @see AbstractLayer
 * @see LossFunction
 * @author Zoran Sevarac <zoran.sevarac@deepnetts.com>
 */
public abstract class AbstractNeuralNetwork<T extends Trainer> implements NeuralNetwork<T>, Serializable {
	private static final long serialVersionUID = 1774974939138248165L;

	private T trainer;

    /**
     * Collection of all layers in this network (including input(first), output(last) and hidden(in between)).
     * As a minimum neural network must have an input and output layer.
     */
    private final List<Layer> layers = new ArrayList<>();

    /**
     * Loss function
     * Loss function represents total network error for some data, and network learns by minimizing that error.
     * Commonly used types of loss functions are Mean Squared Error for regression problems and and Cross Entropy for classification problems.
     */
    private LossFunction lossFunction;

    /**
     * Input layer.
     * This layer accepts external inputs and sends them to the next layer
     */
    private Layer inputLayer;

    /**
     * Output layer.
     * This layer is the final step of processing network's input and its output is network's output.
     */
    private Layer outputLayer;

    /**
     * Labels for network outputs (classes)
     */
    private String[] outputLabels;


    private Tensor inputWrapper;

    /**
     * Network's label
     */
    private String label;
    private float regularizationSum=0;

    protected AbstractNeuralNetwork() {
    }

	@Override
	public abstract NetworkType getNetworkType();
	@Override
	public abstract boolean canServe(URI resource) throws NullPointerException;
	@Override
	public abstract NeuralNetwork<T> newInstance(URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException;
	@Override
	public abstract TensorFactory getTensorFactory();
	@Override 
	public abstract NeuralNetwork<T> forward();
	@Override 
	public abstract NeuralNetwork<T> backward();
	
    /**
     * Sets network input vector and triggers forward pass.
     *
     * @param inputs  input tensor
     */
	@Override
	public NeuralNetwork<T> setInput(Tensor inputs) {
        inputLayer.setInput(inputs);
        forward();
        return this;
    }

    /**
     * Returns network's output.
     *
     * @return network's output
     */
	@Override
    public float[] getOutput() {
        return outputLayer.getOutputs().getValues();
    }

	@Override
	public NeuralNetwork<T> setOutputError(float... outputErrors) {
        outputLayer.setOutputErrors(outputErrors);
        return this;
    }

	@Override
	public NeuralNetwork<T> train(DataSet<? extends MLDataItem> trainingSet) {
        trainer.train(trainingSet);
        return this;
    }

	@Override
    public EvaluationMetrics test(DataSet<MLDataItem> testSet) {
        if (getLossFunction() instanceof CrossEntropyLoss || getLossFunction() instanceof BinaryCrossEntropyLoss) {
        	return Evaluators.evaluateClassifier(this, testSet);
        }
        else {
        	return Evaluators.evaluateRegressor(this, testSet);
        }
    }

    /**
     * Apply calculated weight changes to all layers.
     */
	@Override
	public NeuralNetwork<T> applyWeightChanges() {
        layers.forEach((layer) -> layer.applyWeightChanges()); // this can be parellelized since all layers are allraedy calculated - each layer cann apply changes in its own thread
        return this;
    }


	@Override
    public NeuralNetwork<T> addLayer(final Layer layer) {
        layers.add(layer);
        return this;
    }

	@Override
    public List<Layer> getLayers() {
        return layers;
    }

	@Override
    public Layer getInputLayer() {
       return inputLayer;
    }

	@Override
    public Layer getOutputLayer() {
        return outputLayer;
    }
  
	@Override
	public NeuralNetwork<T> setInputLayer(final Layer inputLayer) {
        this.inputLayer = inputLayer;
        return this;
    }

	@Override
	public NeuralNetwork<T> setOutputLayer(final Layer outputLayer) {
        this.outputLayer = outputLayer;
        return this;
    }

    public LossFunction getLossFunction() {
        return lossFunction;
    }

	@Override
	public NeuralNetwork<T> setLossFunction(LossFunction lossFunction) {
        this.lossFunction = lossFunction;
        
        if (lossFunction instanceof MeanSquaredErrorLoss) {
            outputLayer.setLossType(LossType.MEAN_SQUARED_ERROR);
        } else if ((lossFunction instanceof CrossEntropyLoss) || (lossFunction instanceof BinaryCrossEntropyLoss)) {
            outputLayer.setLossType(LossType.CROSS_ENTROPY);
        }
        return this;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public NeuralNetwork<T> setLabel(String label) {
        this.label = label;
        return this;
    }
	
    @Override
    public NeuralNetwork<T> setOutputLabels(final String... outputLabels) {
        this.outputLabels = outputLabels;
        return this;
    }    

    @Override
    public String[] getOutputLabels() {
        return outputLabels;
    }

    @Override
    public String getOutputLabel(int i) {
        return outputLabels[i];
    }

    @Override
    public float getL2Reg() {
        regularizationSum=0;
        for (int i = 1; i < layers.size(); i++) {   // starts from 1 to skip input layer
            regularizationSum += layers.get(i).getL2();
        }
        return regularizationSum;
    }

    @Override
    public float getL1Reg() {
        regularizationSum=0;
        for (int i = 1; i < layers.size(); i++) {   // starts from 1 to skip input layer
            regularizationSum += layers.get(i).getL1();
        }
        return regularizationSum;
    }

    @Override
    public T getTrainer() {
        return trainer;
    }

    @Override
    public void setTrainer(T trainer) {
        this.trainer = trainer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        layers.stream().forEach( layer -> sb.append(layer.toString()).append(System.lineSeparator()) );
                
        return sb.toString();
    }


	@Override
	public Class getSpiServiceClass() {
		return NeuralNetwork.class;
	}


	@Override
	public Class<?> getSpiServiceFactoryClass() {
		return NeuralNetwork.Factory.class;
	}


}