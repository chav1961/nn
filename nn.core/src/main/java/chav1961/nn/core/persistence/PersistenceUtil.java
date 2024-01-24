package chav1961.nn.core.persistence;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;

import chav1961.nn.api.interfaces.AnyLayer;
import chav1961.nn.api.interfaces.AnyLayer.ActivationType;
import chav1961.nn.api.interfaces.AnyLayer.InternalTenzorType;
import chav1961.nn.api.interfaces.AnyLayer.LayerType;
import chav1961.nn.api.interfaces.AnyLayer.LossType;
import chav1961.nn.api.interfaces.AnyLayer.OptimizerType;
import chav1961.nn.api.interfaces.AnyTenzor;
import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.factories.LayerFactory;
import chav1961.nn.api.interfaces.factories.TenzorFactory;
import chav1961.nn.api.interfaces.NeuralNetwork;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.XTenzor;
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

	public static NeuralNetwork load(final DataInputStream dis, final TenzorFactory tenzorFactory, final LayerFactory layerFactory) throws IOException {
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
						final AnyTenzor[]			tenzors = new Tenzor[tenzorCount];
						final InternalTenzorType[]	tenzorTypes = new InternalTenzorType[tenzorCount]; 
								
						for(int index = 0; index < tenzorCount; index++) {
							final InternalTenzorType	tenzorType = InternalTenzorType.valueOf(dis.readUTF());
							final int					tenzorArity = dis.readInt();
							final int[]					dimensions = new int[tenzorArity];
							
							for(int item = 0; item < tenzorArity; item++) {
								dimensions[index] = dis.readInt();
							}
							
							final int				contentType = dis.readByte(); 
							final int				contentLength = dis.readInt();
							
							
							switch (contentType) {
								case 0 :
									final float[]	floatContent = new float[contentLength];
									
									for(int item = 0; item < arity; item++) {
										floatContent[index] = dis.readFloat();
									}
									tenzors[index] = tenzorFactory.newInstance(floatContent, dimensions);
									tenzorTypes[index] = tenzorType;
									break;
								case 1 :
									final double[]	doubleContent = new double[contentLength];
									
									for(int item = 0; item < arity; item++) {
										doubleContent[index] = dis.readDouble();
									}
									tenzors[index] = tenzorFactory.newInstanceX(doubleContent, dimensions);
									tenzorTypes[index] = tenzorType;
									break;
								default :
							}
							
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
			final AnyLayer[]	layers = nn.getLayers();
			
			dos.writeInt(MAGIC);
			dos.writeInt(VERSION);
			dos.writeInt(layers.length);
			
			for (AnyLayer layer : layers) {
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
							final AnyTenzor	value = layer.getInternalTenzor(item);
							 
							dos.writeUTF(item.name());
							dos.writeInt(value.getArity());
							if (value.getArity() > 0) {
								for(int index = 0; index < value.getArity(); index++) {
									dos.writeInt(value.getSize(index));
								}
							}
							if (value instanceof Tenzor) {
								final float[]	content = ((Tenzor)value).getContent();
								
								dos.writeByte(0);
								dos.writeInt(content.length);
								if (content.length > 0) {
									for(float floatVal : content) {
										dos.writeFloat(floatVal);
									}
								}
							}
							else if (value instanceof XTenzor) {
								final double[]	content = ((XTenzor)value).getContent();
								
								dos.writeByte(1);
								dos.writeInt(content.length);
								if (content.length > 0) {
									for(double doubleVal : content) {
										dos.writeDouble(doubleVal);
									}
								}
							}
							else {
								
							}
						}
					}
				}
			}
			
			dos.writeInt(MAGIC);
		}
	}
}
