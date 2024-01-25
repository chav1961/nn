package chav1961.nn.core.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chav1961.nn.api.interfaces.AnyLayer;
import chav1961.nn.api.interfaces.AnyNeuralNetwork;
import chav1961.nn.api.interfaces.AnyLayer.LayerType;
import chav1961.nn.api.interfaces.factories.LayerFactory;
import chav1961.nn.api.interfaces.factories.TenzorFactory;
import chav1961.purelib.basic.Utils;

public class AbstractNeuralNetwork<T extends AnyNeuralNetwork<?,?>, L extends AnyLayer<?,?>> implements AnyNeuralNetwork<T,L> {
	private final TenzorFactory		tenzorFactory;
	private final LayerFactory		layerFactory;
	private final List<AnyLayer<T,?>>	layers = new ArrayList<>();
	private boolean 				prepared = false;
	private boolean					forwardOnly = true;
	
	protected AbstractNeuralNetwork(final TenzorFactory tenzorFactory, final LayerFactory layerFactory) {
		this.tenzorFactory = tenzorFactory;
		this.layerFactory = layerFactory;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TenzorFactory getTenzorFactory() {
		return tenzorFactory;
	}

	@Override
	public LayerFactory getLayerFactory() {
		return layerFactory;
	}

	@Override
	public T add(final AnyLayer<T,L>... layers) {
		if (layers == null || Utils.checkArrayContent4Nulls(layers) >= 0) {
			throw new IllegalArgumentException("Layer list is null or contains nulls inside");
		}
		else if (prepared) {
			throw new IllegalStateException("Can't call this method after calling prepare(...)");
		}
		else {
			this.layers.addAll(Arrays.asList(layers));
			return (T)this;
		}
	}

	@Override
	public AnyLayer<T,L>[] getLayers() {
		return layers.toArray(new AnyLayer[layers.size()]);
	}

	@Override
	public T prepare(final boolean forwardOnly) {
		if (prepared) {
			throw new IllegalStateException("Networs is already prepared");
		}
		else if (layers.size() < 2) {
			throw new IllegalStateException("Too few layers in the networs. At least two layers must present");
		}
		else if (layers.get(0).getLayerType() != LayerType.INPUT) {
			throw new IllegalStateException("The same first layer doesnt' have INPUT type");
		}
		else if (layers.get(layers.size() - 1).getLayerType() != LayerType.OUTPUT) {
			throw new IllegalStateException("The same last layer doesnt' have OUTPUT type");
		}
		else {
//			for(AnyLayer item : layers) {
//				item.prepare(this, forwardOnly);
//			}
//			for(int index = 1; index < layers.size(); index++) {
//				if (!layers.get(index).canConnectBefore(this, layers.get(index - 1))) {
//					throw new IllegalStateException("Layer at position ["+index+"] has unsupported predecessor");
//				}
//			}
//			for(int index = 0; index < layers.size() - 1; index++) {
//				if (!((XLayer)layers.get(index)).canConnectAfter(this, ((XLayer)layers.get(index + 1)))) {
//					throw new IllegalStateException("Layer at position ["+index+"] has unsupported follower");
//				}
//			}
//			for(int index = 1; index < layers.size(); index++) {
//				((XLayer)layers.get(index)).connectBefore(this, ((XLayer)layers.get(index - 1)));
//			}
//			for(int index = 0; index < layers.size() - 1; index++) {
//				((XLayer)layers.get(index)).connectAfter(this, ((XLayer)layers.get(index + 1)));
//			}
			this.prepared = true;
			setForwardOnly(forwardOnly);
			return (T)this;
		}
	}

	@Override
	public T unprepare() {
		// TODO Auto-generated method stub
		setForwardOnly(true);
		prepared = false;
		return (T)this;
	}

	protected boolean isForwardOnly() {
		return forwardOnly;
	}

	protected void setForwardOnly(final boolean forwardOnly) {
		this.forwardOnly = forwardOnly;
	}
}
