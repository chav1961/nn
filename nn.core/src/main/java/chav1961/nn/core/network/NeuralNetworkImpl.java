package chav1961.nn.core.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chav1961.nn.api.interfaces.AnyLayer;
import chav1961.nn.api.interfaces.AnyLayer.LayerType;
import chav1961.nn.api.interfaces.AnyNeuralNetwork;
import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.factories.LayerFactory;
import chav1961.nn.api.interfaces.factories.TenzorFactory;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.XLayer;
import chav1961.nn.api.interfaces.XNeuralNetwork;
import chav1961.nn.api.interfaces.XTenzor;
import chav1961.purelib.basic.Utils;

public class NeuralNetworkImpl implements NeuralNetwork, XNeuralNetwork {
	private final TenzorFactory		tf;
	private final LayerFactory		lf;
	private final List<AnyLayer>	layers = new ArrayList<>();
	private int						layerCount = 0, xLayerCount = 0;
	private boolean					prepared = false;
	private boolean					forwardOnly = false;
	
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
	public NeuralNetwork add(final AnyLayer... layers) {
		if (layers == null || Utils.checkArrayContent4Nulls(layers) >= 0) {
			throw new IllegalArgumentException("Layers list is null or contains nulls inside");
		}
		else if (prepared) {
			throw new IllegalStateException("Can't add new layers after preparation. Call unprepare(...) before");
		}
		else {
			int 	count = 0, xcount = 0;
			
			for(AnyLayer layer : layers) {
				if (layer instanceof Layer) {
					count++;
				}
				else if (layer instanceof XLayer) {
					xcount++;
				}
			}
			if (count != 0 && xcount != 0) {
				throw new IllegalArgumentException("Mix Layer and XLayer instances in the parameter's list");
			}
			else {
				this.layers.addAll(Arrays.asList(layers));
				this.layerCount += count;
				this.xLayerCount += xcount;
			}
			return this;
		}
	}
	
	@Override
	public AnyLayer[] getLayers() {
		return layers.toArray(new AnyLayer[layers.size()]);
	}

	@Override
	public AnyNeuralNetwork prepare(final boolean forwardOnly) {
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
		else if (layerCount != 0 && xLayerCount != 0) {
			throw new IllegalStateException("Mix Layer and XLayer instances in the layer's list");
		}
		else if (layerCount > 0) {
			for(AnyLayer item : layers) {
				((Layer)item).prepare(this, forwardOnly);
			}
			for(int index = 1; index < layers.size(); index++) {
				if (!((Layer)layers.get(index)).canConnectBefore(this, ((Layer)layers.get(index - 1)))) {
					throw new IllegalStateException("Layer at position ["+index+"] has unsupported predecessor");
				}
			}
			for(int index = 0; index < layers.size() - 1; index++) {
				if (!((Layer)layers.get(index)).canConnectAfter(this, ((Layer)layers.get(index + 1)))) {
					throw new IllegalStateException("Layer at position ["+index+"] has unsupported follower");
				}
			}
			for(int index = 1; index < layers.size(); index++) {
				((Layer)layers.get(index)).connectBefore(this, ((Layer)layers.get(index - 1)));
			}
			for(int index = 0; index < layers.size() - 1; index++) {
				((Layer)layers.get(index)).connectAfter(this, ((Layer)layers.get(index + 1)));
			}
			this.prepared = true;
			this.forwardOnly = forwardOnly;
			return this;
		}
		else if (xLayerCount > 0) {
			for(AnyLayer item : layers) {
				((XLayer)item).prepare(this, forwardOnly);
			}
			for(int index = 1; index < layers.size(); index++) {
				if (!((XLayer)layers.get(index)).canConnectBefore(this, ((XLayer)layers.get(index - 1)))) {
					throw new IllegalStateException("Layer at position ["+index+"] has unsupported predecessor");
				}
			}
			for(int index = 0; index < layers.size() - 1; index++) {
				if (!((XLayer)layers.get(index)).canConnectAfter(this, ((XLayer)layers.get(index + 1)))) {
					throw new IllegalStateException("Layer at position ["+index+"] has unsupported follower");
				}
			}
			for(int index = 1; index < layers.size(); index++) {
				((XLayer)layers.get(index)).connectBefore(this, ((XLayer)layers.get(index - 1)));
			}
			for(int index = 0; index < layers.size() - 1; index++) {
				((XLayer)layers.get(index)).connectAfter(this, ((XLayer)layers.get(index + 1)));
			}
			this.prepared = true;
			this.forwardOnly = forwardOnly;
			return this;
		}
		else {
			throw new UnsupportedOperationException("Layer types differ than Layer and XLayer are not supported yet");
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
				t = ((Layer)layers.get(index)).forward(this, t);
			}
			return t;
		}
	}

	@Override
	public XTenzor forward(final XTenzor input) {
		if (input == null) {
			throw new NullPointerException("Input tenzor can't be null");
		}
		else if (!prepared) {
			throw new IllegalStateException("Calling forward(...) before preparation. Call prepare(...) before");
		}
		else {
			XTenzor	t = input;
			
			for(int index = 0; index < layers.size(); index++) {
				t = ((XLayer)layers.get(index)).forward(this, t);
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
			throw new IllegalStateException("Calling backward(...) before preparation. Call prepare(...) before");
		}
		else if (forwardOnly) {
			throw new IllegalStateException("Calling backward(...) is not appicable for forward-only prepared network");
		}
		else if (layerCount != 0) {
			Tenzor	t = errors;
			
			for(int index = layers.size() - 1; index >= 0; index--) {
				t = ((Layer)layers.get(index)).backward(this, t);
			}
			return t;
		}
		else {
			throw new IllegalStateException("Backward with Tenzor type is not applicable for XTenzor layers list");
		}
	}

	@Override
	public XTenzor backward(final XTenzor errors) {
		if (errors == null) {
			throw new NullPointerException("Input tenzor can't be null");
		}
		else if (!prepared) {
			throw new IllegalStateException("Calling forward(...) before preparation. Call prepare(...) before");
		}
		else if (!prepared) {
			throw new IllegalStateException("Calling backward(...) before preparation. Call prepare(...) before");
		}
		else if (forwardOnly) {
			throw new IllegalStateException("Calling backward(...) is not appicable for forward-only prepared network");
		}
		else if (xLayerCount != 0) {
			XTenzor	t = errors;
			
			for(int index = layers.size() - 1; index >= 0; index--) {
				t = ((XLayer)layers.get(index)).backward(this, t);
			}
			return t;
		}
		else {
			throw new IllegalStateException("Backward with XTenzor type is not applicable for Tenzor layers list");
		}
	}
	
	@Override
	public AnyNeuralNetwork unprepare() {
		if (!prepared) {
			throw new IllegalStateException("Networs is not prepared or was already unprepared");
		}
		else {
			if (layerCount > 0) {
				for(AnyLayer item : layers) {
					((Layer)item).unprepare(this);
				}
			}
			else if (xLayerCount > 0) {
				for(AnyLayer item : layers) {
					((XLayer)item).unprepare(this);
				}
			}
			else {
				throw new UnsupportedOperationException("Layer types differ than Layer and XLayer are not supported yet");
			}
			prepared = false;
			return this;
		}
	}

}
