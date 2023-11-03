package chav1961.nn.core.util;

import java.util.Random;

import chav1961.nn.api.interfaces.Layer;
import chav1961.nn.api.interfaces.Layer.InternalTenzorType;
import chav1961.nn.api.interfaces.Tenzor;

public class RandomGenerator {
	private static final Random	RANDOM = new Random();
	
	public static void prepareLayer(final Layer layer) {
		prepareLayer(layer, 0);
	}
	
	public static void prepareLayer(final Layer layer, final long seed) {
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
						prepareRandom(layer.getInternalTenzor(type), RANDOM);
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
}
