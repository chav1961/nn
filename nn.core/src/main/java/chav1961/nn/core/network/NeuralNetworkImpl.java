package chav1961.nn.core.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.Layer.LayerType;
import chav1961.nn.api.interfaces.LayerFactory;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.TenzorFactory;
import chav1961.purelib.basic.Utils;

public class NeuralNetworkImpl implements NeuralNetwork {
	private final TenzorFactory	tf;
	private final LayerFactory	lf;
	private final List<Layer>	layers = new ArrayList<>();
	private boolean				prepared = false;
	
	public NeuralNetworkImpl(final TenzorFactory tf, final LayerFactory lf) {
		if (tf == null) {
			throw new NullPointerException("Tenzor factory can't be null");
		}
		else if (lf == null) {
			throw new NullPointerException("Layer factory can't be null");
		}
		else {
			this.tf = tf;
			this.lf = lf;
		}
	}

	@Override
	public TenzorFactory getTenzorFactory() {
		return tf;
	}

	@Override
	public LayerFactory getLayerFactory() {
		return lf;
	}

	@Override
	public NeuralNetwork add(final Layer... layers) {
		if (layers == null || Utils.checkArrayContent4Nulls(layers) >= 0) {
			throw new IllegalArgumentException("Layers list is null or contains nulls inside");
		}
		else if (prepared) {
			throw new IllegalStateException("Can't add new layers after preparation. Call unprepare(...) before");
		}
		else {
			this.layers.addAll(Arrays.asList(layers));
			return this;
		}
	}
	
	@Override
	public Layer[] getLayers() {
		return layers.toArray(new Layer[layers.size()]);
	}

	@Override
	public NeuralNetwork prepare(final boolean forwardOnly) {
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
			for(Layer item : layers) {
				item.prepare(this, forwardOnly);
			}
			for(int index = 1; index < layers.size(); index++) {
				if (!layers.get(index).canConnectBefore(this, layers.get(index - 1))) {
					throw new IllegalStateException("Layer at position ["+index+"] has unsupported predecessor");
				}
			}
			for(int index = 0; index < layers.size() - 1; index++) {
				if (!layers.get(index).canConnectAfter(this, layers.get(index + 1))) {
					throw new IllegalStateException("Layer at position ["+index+"] has unsupported follower");
				}
			}
			for(int index = 1; index < layers.size(); index++) {
				layers.get(index).connectBefore(this, layers.get(index - 1));
			}
			for(int index = 0; index < layers.size() - 1; index++) {
				layers.get(index).connectAfter(this, layers.get(index + 1));
			}
			prepared = true;
			return this;
		}
	}

	@Override
	public Tenzor forward(final Tenzor input) {
		if (input == null) {
			throw new NullPointerException("Input tenzor can't be null");
		}
		else if (!prepared) {
			throw new IllegalStateException("Calling forward(...) before preparation. Call prepare(...) before");
		}
		else {
			Tenzor	t = input;
			
			for(int index = 0; index < layers.size(); index++) {
				t = layers.get(index).forward(this, t);
			}
			return t;
		}
	}

	@Override
	public Tenzor backward(final Tenzor errors) {
		if (errors == null) {
			throw new NullPointerException("Input tenzor can't be null");
		}
		else if (!prepared) {
			throw new IllegalStateException("Calling forward(...) before preparation. Call prepare(...) before");
		}
		else {
			Tenzor	t = errors;
			
			for(int index = layers.size() - 1; index >= 0; index--) {
				t = layers.get(index).backward(this, t);
			}
			return t;
		}
	}

	@Override
	public NeuralNetwork unprepare() {
		if (!prepared) {
			throw new IllegalStateException("Networs is not prepared or was already unprepared");
		}
		else {
			for(Layer item : layers) {
				item.unprepare(this);
			}
			prepared = false;
			return this;
		}
	}

}
