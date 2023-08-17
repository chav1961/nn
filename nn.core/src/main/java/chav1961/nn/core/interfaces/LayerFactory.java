package chav1961.nn.core.interfaces;

public interface LayerFactory {
	Layer newConvolutionalLayer(int filterWidth, int filterHeight, int channels, ActivationType activationType);
	Layer newConvolutionalLayer(int filterWidth, int filterHeight, int channels, int stride, ActivationType activationType);
	
    Layer newFullyConnectedLayer(int width);
    Layer newFullyConnectedLayer(int width, ActivationType actType);

    Layer newInputLayer(int width, int height, int depth);
    Layer newInputLayer(int width, int height);
    Layer newInputLayer(int width);
    
    Layer newMaxPoolingLayer(int filterWidth, int filterHeight, int stride);

    Layer newOutputLayer(int width);
    Layer newOutputLayer(int width, ActivationType actType);
    Layer newOutputLayer(String... outputLabels);
    Layer newOutputLayer(ActivationType actType, String... outputLabels);

    Layer newSoftmaxOutputLayer(int width);
    Layer newSoftmaxOutputLayer(String... labels);
}
