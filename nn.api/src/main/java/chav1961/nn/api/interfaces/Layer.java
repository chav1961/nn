package chav1961.nn.api.interfaces;

import java.net.URI;
import java.util.ServiceLoader;

import chav1961.nn.api.interfaces.factories.LayerFactory;

public interface Layer<T extends AnyNeuralNetwork<?,?>, L extends AnyLayer<?,?>> extends AnyLayer<T,L> {

	Layer<T,L> setActivationType(ActivationType activationType, String... parameters);
	Layer<T,L> setLossType(LossType lossType);
	Layer<T,L> setOptimizerType(OptimizerType optimizerType);
	
	Tenzor forward(NeuralNetwork nn, Tenzor input);
	Tenzor backward(NeuralNetwork nn, Tenzor errors);
	XTenzor forward(NeuralNetwork nn, XTenzor input);
	XTenzor backward(NeuralNetwork nn, XTenzor errors);
}
