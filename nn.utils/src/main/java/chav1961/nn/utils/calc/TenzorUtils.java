package chav1961.nn.utils.calc;

import java.util.Arrays;

import chav1961.nn.api.interfaces.AnyTenzor;
import chav1961.nn.api.interfaces.Tenzor;

public class TenzorUtils {
	public static int[] extractDimension(final AnyTenzor tenzor) {
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
	
	public static int[] allIndicesMask(final AnyTenzor tenzor) {
		if (tenzor == null) {
			throw new NullPointerException("Tenzor to build indices mask can't be null");
		}
		else {
			final int[]	result = new int[tenzor.getArity()];
			
			Arrays.fill(result, -1);
			return result;
		}
	}
	
	public static Tenzor toMatrix(final Tenzor source, final int rows, final int cols) {
		if (source == null) {
			throw new NullPointerException("Source tenzor can't be null");
		}
		else if (rows <= 0) {
			throw new IllegalArgumentException("Rows ["+rows+"] must be greater than 0");
		}
		else if (cols <= 0) {
			throw new IllegalArgumentException("Cols ["+cols+"] must be greater than 0");
		}
		else {
			return source.getFactory().newInstance(source.getContent(), rows, cols);
		}
	}

	public static Tenzor toVector(final Tenzor source) {
		if (source == null) {
			throw new NullPointerException("Source tenzor can't be null");
		}
		else if (source.getArity() != 2) {
			throw new IllegalArgumentException("Source tenzor is not a matrix");
		}
		else if (source.getSize(0) != 1 && source.getSize(1) != 1) {
			throw new IllegalArgumentException("Matrix dimensions "+Arrays.toString(extractDimension(source))+" must contain at least one 1");
		}
		else if (source.getSize(0) == 1) {
			return source.getFactory().newInstance(source.getContent(), source.getSize(1));
		}
		else  {
			return source.getFactory().newInstance(source.getContent(), source.getSize(0));
		}
	}
}
