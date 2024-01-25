package chav1961.nn.api.interfaces;

public interface NeuralNetwork<T extends AnyNeuralNetwork<?,?>, L extends AnyLayer<?,?>> extends AnyNeuralNetwork<T,L> {
	Tenzor forward(Tenzor input);
	Tenzor backward(Tenzor errors);
}
