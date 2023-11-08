package chav1961.nn.core.persistence;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.Layer.ActivationType;
import chav1961.nn.api.interfaces.Layer.InternalTenzorType;
import chav1961.nn.api.interfaces.Layer.LayerType;
import chav1961.nn.api.interfaces.Layer.LossType;
import chav1961.nn.api.interfaces.Layer.OptimizerType;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.core.network.NeuralNetworkImpl;

public class PersistenceUtil {
	private static final int	MAGIC = 0x00CACA00;
	private static final int	VERSION = 0;
	
	public static NeuralNetwork load(final DataInputStream dis) throws IOException {
		if (dis == null) {
			throw new NullPointerException("Input stream can't be null");
		}
		else {
			return load(dis, Tenzor.Factory.getDefaultTenzorURI(), Layer.Factory.getDefaultLayerURI());
		}
	}

	public static NeuralNetwork load(final DataInputStream dis, final URI tenzorURI, final URI layerURI) throws IOException {
		if (dis == null) {
			throw new NullPointerException("Input stream can't be null");
		}
		else if (tenzorURI == null) {
			throw new NullPointerException("Tenzor factory URI can't be null");
		}
		else if (layerURI == null) {
			throw new NullPointerException("Layer factory URI can't be null");
		}
		else {
			return load(dis, Tenzor.Factory.getFactory(tenzorURI), Layer.Factory.getFactory(layerURI));
		}
	}

	public static NeuralNetwork load(final DataInputStream dis, final Tenzor.TenzorFactory tenzorFactory, final Layer.LayerFactory layerFactory) throws IOException {
		if (dis == null) {
			throw new NullPointerException("Input stream can't be null");
		}
		else if (tenzorFactory == null) {
			throw new NullPointerException("Tenzor factory can't be null");
		}
		else if (layerFactory == null) {
			throw new NullPointerException("Layer factory can't be null");
		}
		else if (dis.readInt() != MAGIC) {
			throw new IOException("Illegal start magic in the input stream"); 
		}
		else {
			
			if (dis.readInt() != MAGIC) {
				throw new IOException("Illegal terminal magic in the input stream");
			}
			else {
				final int	version = dis.readInt();
				
				if (version > VERSION) {
					throw new IOException("Input content version ["+version+"] is greater than last supported ["+VERSION+"]");
				}
				else {
					final NeuralNetwork	nn = new NeuralNetworkImpl(tenzorFactory, layerFactory);
					final int			layerAmount = dis.readInt();
					
					for(int layerNo = 0; layerNo < layerAmount; layerNo++) {
						final LayerType			layerType = LayerType.valueOf(dis.readUTF());
						final OptimizerType		optimizerType = OptimizerType.valueOf(dis.readUTF());
						final LossType			loassType = LossType.valueOf(dis.readUTF());
						final ActivationType	activationType = ActivationType.valueOf(dis.readUTF()); 
						final int				activationParameterCount = dis.readInt();
						final String[]			parameters = new String[activationParameterCount];
						
						for(int index = 0; index < activationParameterCount; index++) {
							parameters[index] = dis.readUTF();
						}

						final int				arity = dis.readInt();
						final int[]				sizes = new int[arity];

						for(int index = 0; index < arity; index++) {
							sizes[index] = dis.readInt();
						}
						
						final int					tenzorCount = dis.readInt();
						final Tenzor[]				tenzors = new Tenzor[tenzorCount];
						final InternalTenzorType[]	tenzorTypes = new InternalTenzorType[tenzorCount]; 
								
						for(int index = 0; index < tenzorCount; index++) {
							final InternalTenzorType	tenzorType = InternalTenzorType.valueOf(dis.readUTF());
							final int					tenzorArity = dis.readInt();
							final int[]					dimensions = new int[tenzorArity];
							
							for(int item = 0; item < tenzorArity; item++) {
								dimensions[index] = dis.readInt();
							}
							
							final int					contentLength = dis.readInt();
							final float[]				content = new float[contentLength];
							
							for(int item = 0; item < arity; item++) {
								content[index] = dis.readFloat();
							}
							tenzors[index] = tenzorFactory.newInstance(content, dimensions);
							tenzorTypes[index] = tenzorType;
						}
						
						final Layer		layer = layerFactory.newInstance(layerType, sizes);

						layer.setOptimizerType(optimizerType);
						layer.setLossType(loassType);
						layer.setActivationType(activationType, parameters);
						
						for(int index = 0; index < tenzorCount; index++) {
							layer.setInternalTenzor(tenzorTypes[index], tenzors[index]);
						}
						
						nn.add(layer);
					}
					return nn;
				}
			}
		}
	}
	
	public static void store(final NeuralNetwork nn, final DataOutputStream dos) throws IOException {
		if (nn == null) {
			throw new NullPointerException("Network can't be null");
		}
		else if (dos == null) {
			throw new NullPointerException("Output stream can't be null");
		}
		else {
			final Layer[]	layers = nn.getLayers();
			
			dos.writeInt(MAGIC);
			dos.writeInt(VERSION);
			dos.writeInt(layers.length);
			
			for (Layer layer : layers) {
				dos.writeUTF(layer.getLayerType().name());
				dos.writeUTF(layer.getOptimizerType().name());
				dos.writeUTF(layer.getLossType().name());
				dos.writeUTF(layer.getActivationType().name());
				
				final String[]	parm = layer.getActivationParameters();
				
				dos.writeInt(parm.length);
				if (parm.length > 0) {
					for(String item : parm) {
						dos.writeUTF(item);
					}
				}

				dos.writeInt(layer.getArity());
				for(int index = 0; index < layer.getArity(); index++) {
					dos.writeInt(layer.getSize(index));
				}
				
				int count = 0;
				for (InternalTenzorType item : InternalTenzorType.values()) {
					if (layer.isInternalTenzorSupported(item)) {
						count++;
					}
				}
				
				dos.writeInt(count);
				if (count > 0) {
					for (InternalTenzorType item : InternalTenzorType.values()) {
						if (layer.isInternalTenzorSupported(item)) {
							final Tenzor	value = layer.getInternalTenzor(item);
							final float[]	content = value.getContent();
							 
							dos.writeUTF(item.name());
							dos.writeInt(value.getArity());
							if (value.getArity() > 0) {
								for(int index = 0; index < value.getArity(); index++) {
									dos.writeInt(value.getSize(index));
								}
							}
							dos.writeInt(content.length);
							if (content.length > 0) {
								for(float floatVal : content) {
									dos.writeFloat(floatVal);
								}
							}
						}
					}
				}
			}
			
			dos.writeInt(MAGIC);
		}
	}
}
