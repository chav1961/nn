package chav1961.nn.core.interfaces;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.ServiceLoader;

import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;

import chav1961.purelib.basic.interfaces.SpiServiceFactory;

public interface NeuralNetwork<Tr> extends SpiServiceFactory<NeuralNetwork<Tr>>, TrainerProvider<Tr>, Serializable, Cloneable {
	public static final String	NEURAL_NETWORK_SCHEME = "neuralnetwork";
	
	NetworkType getNetworkType();
	TensorFactory getTensorFactory();
	LayerFactory getLayerFactory();
	
    String getLabel();
    NeuralNetwork<Tr> setLabel(String label);
    NeuralNetwork<Tr> setOutputLabels(final String... outputLabels);
    String[] getOutputLabels();
    String getOutputLabel(int i);
	
	NeuralNetwork<Tr> addLayer(Layer layer);
    List<Layer> getLayers();
    Layer getInputLayer();
    NeuralNetwork<Tr> setInputLayer(Layer inputLayer);
    Layer getOutputLayer();
    NeuralNetwork<Tr> setOutputLayer(Layer outputLayer);    
	
    LossFunction getLossFunction();
    NeuralNetwork<Tr> setLossFunction(LossFunction lossFunction);
	
    NeuralNetwork<Tr> setInput(Tensor inputs);
    float[] getOutput();
    NeuralNetwork<Tr> setOutputError(float... outputErrors);
	
    NeuralNetwork<Tr> train(DataSet<? extends MLDataItem> trainingSet);
    EvaluationMetrics test(DataSet<MLDataItem> testSet);

    NeuralNetwork<Tr> applyWeightChanges();
    NeuralNetwork<Tr> forward();
    NeuralNetwork<Tr> backward();
    
    float getL1Reg();
    float getL2Reg();
    
	public static class Factory {
		private Factory() {
		}
		
		public static <T> NeuralNetwork<T> newInstance(final URI nnUri) throws NullPointerException, IllegalArgumentException {
			for (NeuralNetwork<T> item : ServiceLoader.load(NeuralNetwork.class)) {
				if (item.canServe(nnUri)) {
					return (NeuralNetwork<T>)item.newInstance(nnUri);
				}
			}
			throw new IllegalArgumentException("Network for URI ["+nnUri+"] not found");
		}
	}



}
