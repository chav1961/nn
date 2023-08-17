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
package chav1961.nn.standalone.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.List;

import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.core.interfaces.BinaryCrossEntropyLoss;
import chav1961.nn.core.interfaces.CrossEntropyLoss;
import chav1961.nn.core.interfaces.LossFunction;
import chav1961.nn.core.interfaces.LossType;
import chav1961.nn.core.interfaces.MeanSquaredErrorLoss;
import chav1961.nn.core.interfaces.NetworkType;
import chav1961.nn.core.interfaces.NeuralNetwork;
import chav1961.nn.core.layers.AbstractLayer;
import chav1961.nn.core.train.BackpropagationTrainer;
import chav1961.nn.core.utils.RandomGenerator;
import chav1961.nn.standalone.internal.FullyConnectedLayer;
import chav1961.nn.standalone.internal.InputLayer;
import chav1961.nn.standalone.internal.OutputLayer;
import chav1961.nn.standalone.internal.SoftmaxOutputLayer;
import chav1961.nn.standalone.internal.TensorImpl;
import chav1961.purelib.basic.exceptions.EnvironmentException;

/**
 * Feed forward neural network architecture, also known as Multi Layer Perceptron.
 * Consists of a sequence of layers trained by Backpropagation algorithm.
 *
 * @author Zoran Sevarac <zoran.sevarac@deepnetts.com>
 */
public final class FeedForwardNetwork extends StandaloneNeuralNetwork<BackpropagationTrainer> {

    private static final long serialVersionUID = 5819940381359274290L;    
    
    private transient TensorImpl inputTensor;
    

    /**
     * Private constructor allows instantiation only using builder
     */
    private FeedForwardNetwork() {
        super();
        setTrainer(new BackpropagationTrainer(this));
    }

    public void setInput(float[] inputs) {
        inputTensor.setValues(inputs); 
        setInput(inputTensor);
    }

    /**
     * Predict output for the given input
     * @param inputs
     * @return
     */
    public float[] predict(float[] inputs) {
        setInput(inputs);
        return getOutput();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException
    {
        ois.defaultReadObject();
        
        //This has to be enabled when layers.init() methods stop touching not null class members (class fields) after deserialization.
        if (false) {
        	initClassFieldsOfNetAndAllLayers();
        }
    }
    
    /**
     * This method is called in 2 scenarios:
     * 1. Creating new ConvolutionalNetwork instance, where all class members are null and have to be initialized.
     * 2. After deserialization from saved net file, where some class members will be initialized by reading the stream during deserialization in defaultReadObject method. 
     * 
     * Init methods of all layers must be sensitive for both scenarios and check if field is null before initializing it with default new objects.
     * In most cases if the field is not null, than init method should not touch it.
     */
    private void initClassFieldsOfNetAndAllLayers() {
    	List<AbstractLayer> layers = this.getLayers();
        for (AbstractLayer cur: layers) {
            cur.init(this);
        }
    }    
    
	@Override
	public NeuralNetwork<BackpropagationTrainer> forward() {
        for (int i = 1; i < getLayers().size(); i++) {   // starts from 1 to skip input layer
        	getLayers().get(i).forward(this);
        }
        return this;
    }

	@Override
	public NeuralNetwork<BackpropagationTrainer> backward() {
        for (int i = getLayers().size() - 1; i > 0; i--) {
        	getLayers().get(i).backward(this);
        }
        return this;
    }
    
    /**
     * Returns builder for Feed Forward Network
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for FeedForwardNetwork
     */
    public static class Builder {

        /**
         * FeedForwardNetwork network that will be created and configured using
         * this builder.
         */
        private final FeedForwardNetwork network = new FeedForwardNetwork();
        private ActivationType defaultActivationType = ActivationType.TANH;
        private boolean setDefaultActivation = false;

        /**
         * Adds input addLayer with specified width to the network.
         *
         * @param width addLayer width
         * @return builder instance
         */
        public Builder addInputLayer(int width) {
            final InputLayer inLayer = (InputLayer)network.getLayerFactory().newInputLayer(width);
            
            network.addLayer(inLayer);
            network.setInputLayer(inLayer);
            network.inputTensor = new TensorImpl(width);

            return this;
        }

        /**
         * Adds fully connected layer with specified width and Sigmoid
         * activation function to the network.
         *
         * @param width layer width / number of neurons
         * @return builder instance
         */
        public Builder addFullyConnectedLayer(int width) {
            final FullyConnectedLayer layer = (FullyConnectedLayer)network.getLayerFactory().newFullyConnectedLayer(width);
            
            network.addLayer(layer);
            layer.setActivation(InternalUtils.create(layer.getActivationType()));
            return this;
        }

        public Builder addFullyConnectedLayers(int... widths) {
            for(int width : widths) {
                final FullyConnectedLayer layer = (FullyConnectedLayer)network.getLayerFactory().newFullyConnectedLayer(width);
                
                network.addLayer(layer);
                layer.setActivation(InternalUtils.create(layer.getActivationType()));
            }
            return this;
        }

        /**
         * Adds fully connected addLayer with specified width and activation
         * function to the network.
         *
         * @param width layer width / number of neurons
         * @param activation activation function to use for this layer
         *
         * @return builder instance
         * @see ActivationFunctions
         */
        public Builder addFullyConnectedLayer(int width, ActivationType activationType) {
            final FullyConnectedLayer layer = (FullyConnectedLayer)network.getLayerFactory().newFullyConnectedLayer(width, activationType);
            
            network.addLayer(layer);
            layer.setActivation(InternalUtils.create(layer.getActivationType()));
            return this;
        }

        public Builder addFullyConnectedLayers(ActivationType activationType, int... widths) {
            for(int width : widths) {
                final FullyConnectedLayer layer = (FullyConnectedLayer)network.getLayerFactory().newFullyConnectedLayer(width, activationType);
                
                network.addLayer(layer);
                layer.setActivation(InternalUtils.create(layer.getActivationType()));
            }
            return this;
        }

        /**
         * Adds custom layer to this network (which inherits from AbstractLayer)
         *
         * @param layer
         * @return
         */
        public Builder addLayer(AbstractLayer layer) {
            network.addLayer(layer);
            layer.setActivation(InternalUtils.create(layer.getActivationType()));
            return this;
        }

        public Builder addOutputLayer(int width, ActivationType activationType) {
            final OutputLayer outputLayer;
            
            if (activationType.equals(ActivationType.SOFTMAX)) {
                outputLayer = (OutputLayer)network.getLayerFactory().newSoftmaxOutputLayer(width);
            } else {
                outputLayer = (OutputLayer)network.getLayerFactory().newOutputLayer(width, activationType);
                outputLayer.setActivation(InternalUtils.create(outputLayer.getActivationType()));
            }

            network.setOutputLayer(outputLayer);
            network.addLayer(outputLayer);

            return this;
        }


        // hidden activation function
        public Builder hiddenActivationFunction(ActivationType activationType) {
            this.defaultActivationType = activationType;
            setDefaultActivation = true;
            return this;
        }

        public Builder lossFunction(LossType lossType) {
            LossFunction loss = null;
            switch (lossType) {
                case MEAN_SQUARED_ERROR:
                    loss = new MeanSquaredErrorLoss(network);
                    break;
                case CROSS_ENTROPY:
                    if (network.getOutputLayer().getWidth() == 1) {
                        loss = new BinaryCrossEntropyLoss(network);
                    } else {
                        loss = new CrossEntropyLoss(network);
                    }
                    break;
            }
            network.setLossFunction(loss);
            return this;
        }

        /**
         * Initializes random number generator with specified seed in order to
         * get same random number sequences (used for weights initialization).
         *
         * @param seed
         * @return
         */
        public Builder randomSeed(long seed) {
            RandomGenerator.getDefault().initSeed(seed);
            return this;
        }

        public FeedForwardNetwork build() {

            // prodji kroz celu mrezu i inicijalizuj matrice tezina / konekcije
            // povezi sve lejere
            AbstractLayer prevLayer = null;

            // connect layers
            for (int i = 0; i < network.getLayers().size(); i++) {
                AbstractLayer layer = network.getLayers().get(i);
                if (setDefaultActivation && !(layer instanceof InputLayer) && !(layer instanceof OutputLayer)) { // ne za izlazni layer
                    layer.setActivationType(defaultActivationType); // ali ovo ne treba ovako!!! ako je vec nesto setovano onda nemoj to d agazis
                    layer.setActivation(InternalUtils.create(defaultActivationType));
                }
                if (i > 0) {
                    layer.setPrevLayer(prevLayer);
                }
                if (prevLayer != null) {
                    prevLayer.setNextlayer(layer);
                }
                prevLayer = layer;
            }

            // init internal layer structures (weights, outputs, deltas etc. for each layer)
            for (AbstractLayer layer : network.getLayers()) {
                layer.init(network);
            }

            // throw excption if loss is null - ili generalno nesto nije setovano kako treba
            return network;
        }

    }

	@Override
	public NetworkType getNetworkType() {
		return NetworkType.FEEDFORWARD;
	}

	@Override
	public NeuralNetwork<BackpropagationTrainer> newInstance(URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
}
