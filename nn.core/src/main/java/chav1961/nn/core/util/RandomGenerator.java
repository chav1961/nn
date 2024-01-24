package chav1961.nn.core.util;

import java.util.Random;

import chav1961.nn.api.interfaces.AnyLayer;
import chav1961.nn.api.interfaces.AnyLayer.InternalTenzorType;
import chav1961.nn.api.interfaces.AnyTenzor;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.XTenzor;

public class RandomGenerator {
	private static final Random	RANDOM = new Random();
	
	public static void prepareLayer(final AnyLayer layer) {
		prepareLayer(layer, 0);
	}
	
	public static void prepareLayer(final AnyLayer layer, final long seed) {
		if (layer == null) {
			throw new NullPointerException("Layer to prepare can't be null");
		}
		else {
			synchronized (RANDOM) {
				if (seed != 0) {
					RANDOM.setSeed(seed);
				}
				for (InternalTenzorType type : InternalTenzorType.values()) {
					if (layer.isInternalTenzorSupported(type)) {
						final AnyTenzor	tenzor = layer.getInternalTenzor(type); 
						
						if (tenzor instanceof Tenzor) {
							prepareRandom((Tenzor)tenzor, RANDOM);
						}
						else if (tenzor instanceof XTenzor) {
							prepareRandom((XTenzor)tenzor, RANDOM);
						}
						else {
							throw new UnsupportedOperationException("Tenzor type differ than Tenzor and XTenzor is not supported yet");
						}
					}
				}
			}
		}
	}

	private static void prepareRandom(final Tenzor internalTenzor, final Random r) {
		final float[]	content = internalTenzor.getContent();
		
		for(int index = 0; index < content.length; index++) {
			content[index] = r.nextFloat(0, 1);
		}
	}

	private static void prepareRandom(final XTenzor internalTenzor, final Random r) {
		final double[]	content = internalTenzor.getContent();
		
		for(int index = 0; index < content.length; index++) {
			content[index] = r.nextDouble(0, 1);
		}
	}
}
