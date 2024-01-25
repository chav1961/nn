package chav1961.nn.standalone;

import java.io.IOException;

import chav1961.nn.api.interfaces.AnyLayer;
import chav1961.nn.api.interfaces.AnyNeuralNetwork;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.XNeuralNetwork;
import chav1961.nn.api.interfaces.XTenzor;
import chav1961.nn.api.interfaces.factories.LayerFactory;
import chav1961.nn.api.interfaces.factories.TenzorFactory;

public class StandaloneXNetworkImpl implements XNeuralNetwork {

	@Override
	public TenzorFactory getTenzorFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LayerFactory getLayerFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NeuralNetwork add(AnyLayer... layers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnyLayer[] getLayers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnyNeuralNetwork prepare(boolean forwardOnly) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnyNeuralNetwork unprepare() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public XTenzor forward(XTenzor input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor backward(XTenzor errors) {
		// TODO Auto-generated method stub
		return null;
	}

}
