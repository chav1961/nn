package chav1961.nn.utils.calc;

import java.util.Arrays;

import chav1961.nn.api.interfaces.Tenzor;

public class TenzorUtils {
	public static int[] extractDimension(final Tenzor tenzor) {
		if (tenzor == null) {
			throw new NullPointerException("Tenzor to extract dimensions can't be null");
		}
		else {
			final int[]	result = new int[tenzor.getArity()];
			
			for(int index = 0; index < result.length; index++) {
				result[index] = tenzor.getSize(index);
			}
			return result;
		}
	}
	
	public static int[] allIndicesMask(final Tenzor tenzor) {
		if (tenzor == null) {
			throw new NullPointerException("Tenzor to build indices mask can't be null");
		}
		else {
			final int[]	result = new int[tenzor.getArity()];
			
			Arrays.fill(result, -1);
			return result;
		}
	}
}
