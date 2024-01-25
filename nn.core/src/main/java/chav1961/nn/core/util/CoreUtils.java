package chav1961.nn.core.util;

public class CoreUtils {
	public static int[] joinArray(final int value, final int... values) {
		if (values == null) {
			throw new NullPointerException("Values can't be null");
		}
		else {
			final int[]	result = new int[values.length + 1];
			
			result[0] = value;
			System.arraycopy(values, 0, result, 1, values.length);
			return result;
		}
	}
	
	public static boolean areSizesValid(final int... sizes) {
		if (sizes == null) {
			throw new NullPointerException("Sizes can't be null");
		}
		else {
			for(int value : sizes) {
				if (value <= 0) {
					return false;
				}
			}
			return true;
		}
	}
}
