package chav1961.nn.api.interfaces;

import chav1961.purelib.basic.exceptions.CalculationException;
import chav1961.purelib.basic.interfaces.ProgressIndicator;

public interface Pipe {
	boolean isReadOnly();
	int getParallelism();
	
	MatrixWrapper forward(MatrixWrapper matrix, ProgressIndicator pi) throws CalculationException;

	default MatrixWrapper forward(MatrixWrapper matrix) throws CalculationException {
		return forward(matrix, ProgressIndicator.DUMMY);
	}
	
	MatrixWrapper backward(MatrixWrapper matrix, ProgressIndicator pi) throws CalculationException;

	default MatrixWrapper backward(MatrixWrapper matrix) throws CalculationException {
		return backward(matrix, ProgressIndicator.DUMMY);
	}
}
