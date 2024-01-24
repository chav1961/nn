package chav1961.nn.api.interfaces;

public interface NeuralNetwork extends AnyNeuralNetwork {
	Tenzor forward(Tenzor input);
	Tenzor backward(Tenzor errors);
}
