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
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.core.interfaces.BinaryCrossEntropyLoss;
import chav1961.nn.core.interfaces.CrossEntropyLoss;
import chav1961.nn.core.interfaces.LossFunction;
import chav1961.nn.core.interfaces.LossType;
import chav1961.nn.core.interfaces.MeanSquaredErrorLoss;
import chav1961.nn.core.interfaces.NetworkType;
import chav1961.nn.core.interfaces.NeuralNetwork;
import chav1961.nn.core.interfaces.Tensor;
import chav1961.nn.core.layers.AbstractLayer;
import chav1961.nn.core.network.AbstractNeuralNetwork;
import chav1961.nn.core.train.BackpropagationTrainer;
import chav1961.nn.core.utils.RandomGenerator;
import chav1961.nn.core.utils.Tensors;
import chav1961.nn.standalone.internal.ConvolutionalLayer;
import chav1961.nn.standalone.internal.FullyConnectedLayer;
import chav1961.nn.standalone.internal.InputLayer;
import chav1961.nn.standalone.internal.MaxPoolingLayer;
import chav1961.nn.standalone.internal.OutputLayer;
import chav1961.nn.standalone.internal.SoftmaxOutputLayer;
import chav1961.nn.standalone.internal.ConvolutionalLayer;
import chav1961.nn.standalone.internal.TensorImpl;
import chav1961.purelib.basic.exceptions.EnvironmentException;

/**
 * Convolutional neural network is an extension of feed forward network, which can
 * include  2D and 3D adaptive preprocessing layers (Convolutional and MaxPooling layer),
 * which specialized to learn to recognize features in images. Images are fed as 3-dimensional tensors.
 * Although primary used for images, they can also be applied to other types of
 * multidimensional problems.
 *
 * @see ConvolutionalLayer
 * @see MaxPoolingLayer
 * @see BackpropagationTrainer
 *
 * @author Zoran Sevarac
 */
public class ConvolutionalNetwork extends StandaloneNeuralNetwork<BackpropagationTrainer> implements Serializable {
	private static final long serialVersionUID = 6311052836990578126L;

    private ConvolutionalNetwork() {
        super();
        setTrainer(new BackpropagationTrainer(this));
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

    public static ConvolutionalNetwork.Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final ConvolutionalNetwork neuralNet = new ConvolutionalNetwork();

        private ActivationType defaultActivationType = ActivationType.RELU;
        private Class<CrossEntropyLoss> defaultLossFunction = CrossEntropyLoss.class;
        private boolean setDefaultActivation = false;

        /**
         * Input layer with specified width and height, and 3 channels by
         * default.
         *
         * @param width
         * @param height
         * @return
         */
        public Builder addInputLayer(int width, int height) {
            final InputLayer inLayer = (InputLayer)neuralNet.getLayerFactory().newInputLayer(width, height, 3);
            
            neuralNet.setInputLayer(inLayer);
            neuralNet.addLayer(inLayer);
            inLayer.setActivation(InternalUtils.create(inLayer.getActivationType()));

            return this;
        }

        /**
         * Input layer with specified width, height and number of channels (depth).
         *
         * @param width
         * @param height
         * @param channels
         * @return
         */
        public Builder addInputLayer(int width, int height, int channels) {
            final InputLayer inLayer = (InputLayer)neuralNet.getLayerFactory().newInputLayer(width, height, channels);
            
            neuralNet.setInputLayer(inLayer);
            neuralNet.addLayer(inLayer);
            inLayer.setActivation(InternalUtils.create(inLayer.getActivationType()));

            return this;
        }

        public Builder addFullyConnectedLayer(int width) {
            final FullyConnectedLayer layer = (FullyConnectedLayer)neuralNet.getLayerFactory().newFullyConnectedLayer(width);
            
            neuralNet.addLayer(layer);
            layer.setActivation(InternalUtils.create(layer.getActivationType()));
            return this;
        }

        /**
         * Add dense layer with specified width and activation function.
         * In dense layer each neuron is connected to all outputs from previous layer.
         * @param width layer width
         * @param activationType type of activation function
         * @return current builder instance
         * @see ActivationType
         */
        public Builder addFullyConnectedLayer(int width, ActivationType activationType) {
            final FullyConnectedLayer layer = (FullyConnectedLayer)neuralNet.getLayerFactory().newFullyConnectedLayer(width, activationType);
            
            neuralNet.addLayer(layer);
            layer.setActivation(InternalUtils.create(layer.getActivationType()));
            return this;
        }

//        public Builder addOutputLayer(int width, Class<? extends OutputLayer> clazz) { // ActivationType.SOFTMAX
//            try {
//                OutputLayer outputLayer = clazz.getDeclaredConstructor(Integer.TYPE).newInstance(width);
//                neuralNet.addLayer(outputLayer);
//                outputLayer.setActivation(InternalUtils.create(outputLayer.getActivationType()));
//                neuralNet.setOutputLayer(outputLayer);
//            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
//                Logger.getLogger(ConvolutionalNetwork.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            return this;
//        }

        public Builder addOutputLayer(int width, ActivationType activationType) {
            final OutputLayer outputLayer;
            
            if (activationType.equals(ActivationType.SOFTMAX)) {
                outputLayer = (OutputLayer)neuralNet.getLayerFactory().newSoftmaxOutputLayer(width);
            } else {
                outputLayer = (OutputLayer)neuralNet.getLayerFactory().newOutputLayer(width);
                outputLayer.setActivationType(activationType);
                outputLayer.setActivation(InternalUtils.create(activationType));
            }

            neuralNet.setOutputLayer(outputLayer);
            neuralNet.addLayer(outputLayer);

            return this;
        }

        // stride???
        public Builder addConvolutionalLayer(int filterSize, int channels) {
            final ConvolutionalLayer convolutionalLayer = (ConvolutionalLayer)neuralNet.getLayerFactory().newConvolutionalLayer(filterSize, filterSize, channels, defaultActivationType);
            
            neuralNet.addLayer(convolutionalLayer);
            convolutionalLayer.setActivation(InternalUtils.create(convolutionalLayer.getActivationType()));
            return this;
        }

        public Builder addConvolutionalLayer(int filterSize, int channels, ActivationType activationType) {
            final ConvolutionalLayer convolutionalLayer = (ConvolutionalLayer)neuralNet.getLayerFactory().newConvolutionalLayer(filterSize, filterSize, channels, activationType);
            
            neuralNet.addLayer(convolutionalLayer);
            convolutionalLayer.setActivation(InternalUtils.create(convolutionalLayer.getActivationType()));
            return this;
        }

        public Builder addConvolutionalLayer(int filterWidth, int filterHeight, int channels) {
            final ConvolutionalLayer convolutionalLayer = (ConvolutionalLayer)neuralNet.getLayerFactory().newConvolutionalLayer(filterWidth, filterHeight, channels, defaultActivationType);
            
            neuralNet.addLayer(convolutionalLayer);
            convolutionalLayer.setActivation(InternalUtils.create(convolutionalLayer.getActivationType()));
            return this;
        }

        public Builder addConvolutionalLayer(int filterWidth, int filterHeight, int channels, int stride) {
            final ConvolutionalLayer convolutionalLayer = (ConvolutionalLayer)neuralNet.getLayerFactory().newConvolutionalLayer(filterWidth, filterHeight, channels, stride, defaultActivationType);
            
            neuralNet.addLayer(convolutionalLayer);
            convolutionalLayer.setActivation(InternalUtils.create(convolutionalLayer.getActivationType()));
            return this;
        }

        public Builder addConvolutionalLayer(int filterWidth, int filterHeight, int channels, ActivationType activationType) {
            final ConvolutionalLayer convolutionalLayer = (ConvolutionalLayer)neuralNet.getLayerFactory().newConvolutionalLayer(filterWidth, filterHeight, channels, activationType);
            
            neuralNet.addLayer(convolutionalLayer);
            convolutionalLayer.setActivation(InternalUtils.create(convolutionalLayer.getActivationType()));
            return this;
        }

        public Builder addConvolutionalLayer(int filterWidth, int filterHeight, int channels, int stride, ActivationType activationType) {
            final ConvolutionalLayer convolutionalLayer = (ConvolutionalLayer)neuralNet.getLayerFactory().newConvolutionalLayer(filterWidth, filterHeight, channels, stride, activationType);
            
            neuralNet.addLayer(convolutionalLayer);
            convolutionalLayer.setActivation(InternalUtils.create(convolutionalLayer.getActivationType()));
            return this;
        }

        public Builder addMaxPoolingLayer(int filterSize, int stride) {
            final MaxPoolingLayer poolingLayer = (MaxPoolingLayer)neuralNet.getLayerFactory().newMaxPoolingLayer(filterSize, filterSize, stride);
            
            neuralNet.addLayer(poolingLayer);
            poolingLayer.setActivation(InternalUtils.create(poolingLayer.getActivationType()));
            return this;
        }

        public Builder addMaxPoolingLayer(int filterWidth, int filterHeight, int stride) {
            final MaxPoolingLayer poolingLayer = (MaxPoolingLayer)neuralNet.getLayerFactory().newMaxPoolingLayer(filterWidth, filterHeight, stride);
            
            neuralNet.addLayer(poolingLayer);
            poolingLayer.setActivation(InternalUtils.create(poolingLayer.getActivationType()));
            return this;
        }

        public Builder hiddenActivationFunction(ActivationType activationType) {
            this.defaultActivationType = activationType;
            setDefaultActivation = true;
            return this;
        }

        public Builder lossFunction(Class<? extends LossFunction> clazz) {
            try {
                LossFunction loss = clazz.getDeclaredConstructor(AbstractNeuralNetwork.class).newInstance(neuralNet);
                neuralNet.setLossFunction(loss);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ConvolutionalNetwork.class.getName()).log(Level.SEVERE, null, ex);
            }

            return this;
        }

        public Builder lossFunction(LossType lossType) {
            LossFunction loss = null;
            switch (lossType) {
                case MEAN_SQUARED_ERROR:
                    loss = new MeanSquaredErrorLoss(neuralNet);
                    break;
                case CROSS_ENTROPY:
                    if (neuralNet.getOutputLayer().getWidth() == 1) {
                        if (neuralNet.getOutputLayer().getActivationType() != ActivationType.SIGMOID )
                            throw new IllegalArgumentException("Illegal combination of activation and loss functions (Sigmoid activation must be used with Cross Entropy Loss)");
                        loss = new BinaryCrossEntropyLoss(neuralNet);
                    } else {
                        if (neuralNet.getOutputLayer().getActivationType() != ActivationType.SOFTMAX )
                        loss = new CrossEntropyLoss(neuralNet);
                    }
                    break;
            }
            neuralNet.setLossFunction(loss);

            return this;
        }

        public Builder randomSeed(long seed) {
            RandomGenerator.getDefault().initSeed(seed);
            return this;
        }

        public ConvolutionalNetwork build() {
            // connect and init layers, weights matrices etc.
            AbstractLayer prevLayer = null;

            for (int i = 0; i < neuralNet.getLayers().size(); i++) {
                AbstractLayer layer = neuralNet.getLayers().get(i);
                 if (setDefaultActivation && !(layer instanceof InputLayer) && !(layer instanceof OutputLayer)) { // ne za izlazni layer
                    layer.setActivationType(defaultActivationType); // ali ovo ne treba ovako!!! ako je vec nesto setovano onda nemoj to d agazis
                    layer.setActivation(InternalUtils.create(defaultActivationType));
                }
                layer.setPrevLayer(prevLayer);
                if (prevLayer != null) {
                    prevLayer.setNextlayer(layer);
                }
                prevLayer = layer; // current layer becomes prev layer in next iteration
            }

            // init all layers
            neuralNet.initClassFieldsOfNetAndAllLayers();

            // if loss is not set use default loss function
            if (neuralNet.getLossFunction() == null) {
                Builder.this.lossFunction(defaultLossFunction);
            }

            return neuralNet;
        }
    }

    /**
     * Returns all weights from this network as list of strings.
     * TODO; for convolutional layer get filter weights
     *
     * @return
     */
    public List<String> getWeights() {
        List weightsList = new ArrayList();
        for (AbstractLayer layer : getLayers()) {
            if (layer instanceof ConvolutionalLayer) {
                Tensor[] filters = ((ConvolutionalLayer)layer).getFilters();
                String filterStr = Tensors.valuesAsString(filters);
                weightsList.add(filterStr);
            } else {
                weightsList.add(layer.getDeltaWeights().toString());
            }
        }
        return weightsList;
    }

    public void setWeights(List<String> weights) {
        int weightsIdx=0;

        for (int layerIdx = 1; layerIdx < getLayers().size(); layerIdx++) {
            AbstractLayer layer = getLayers().get(layerIdx);
            if (layer instanceof ConvolutionalLayer) {
                ((ConvolutionalLayer)layer).setFilters(weights.get(weightsIdx));
                weightsIdx++;
            } else if (layer instanceof FullyConnectedLayer || layer instanceof OutputLayer) {
                layer.setWeights(weights.get(weightsIdx));
                weightsIdx++;
            }
        }
    }




    public List<String> getDeltaWeights() {
        List weightsList = new ArrayList();
        for (AbstractLayer layer : getLayers()) {
                weightsList.add(layer.getDeltaWeights().toString());
        }
        return weightsList;
    }

    public List<String> getAllOutputs() {
        List outputsList = new ArrayList();
        for (AbstractLayer layer : getLayers()) {
            outputsList.add(layer.getOutputs());
        }
        return outputsList;
    }

	@Override
	public NetworkType getNetworkType() {
		return NetworkType.CONVOLUTIONAL;
	}

	@Override
	public NeuralNetwork<BackpropagationTrainer> newInstance(URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
}
