package chav1961.nn.api.interfaces;

public interface XNeuralNetwork extends AnyNeuralNetwork {
	XTenzor forward(XTenzor input);
	XTenzor backward(XTenzor errors);
}
