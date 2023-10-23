package chav1961.nn.utils.calc;

import chav1961.nn.api.interfaces.Layer;

public class LayerUtils {
	public static int[] extractDimension(final Layer layer) {
		if (layer == null) {
			throw new NullPointerException("Layer to extract dimensions can't be null");
		}
		else {
			final int[]	result = new int[layer.getArity()];
			
			for(int index = 0; index < result.length; index++) {
				result[index] = layer.getSize(index);
			}
			return result;
		}
	}
}
