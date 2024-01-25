package chav1961.nn.standalone.tenzors;

import chav1961.nn.api.interfaces.XTenzor;
import chav1961.nn.api.interfaces.factories.TenzorFactory;
import chav1961.purelib.basic.exceptions.SyntaxException;

public class StandaloneXTenzor implements XTenzor {
	private static final long serialVersionUID = -8473446694692085383L;

	private final int[]	dimensions;
	
	public StandaloneXTenzor(final int... dimensions) {
		if (dimensions == null || dimensions.length == 0) {
			throw new IllegalArgumentException("Dimensions can't be null or empty array");
		}
		else {
			this.dimensions = dimensions;
		}
	}
	
	@Override
	public int getArity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSize(int dimension) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TenzorFactory getFactory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(XTenzor another, double epsilon) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sizeEquals(XTenzor another) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double[] getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double get(int... indices) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public XTenzor get(double[] target, int... indices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor set(double value, int... indices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor set(double[] toSet, int... indices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor set(XTenzor toSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor fill(double value, int... indices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor add(XTenzor toAdd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor add(double toAdd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor sub(XTenzor toSubtract) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor sub(double toSubtract) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor mul(XTenzor toMultiply) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor mul(double toMultiply) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor div(XTenzor toDivide) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor div(double toDivide) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor matrixMul(XTenzor toMultiply) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor trans() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor calculate(CharSequence expression, XTenzor... parameters) throws SyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XTenzor convert(ConvertCallback callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void forEach(ProcessCallback callback) {
		// TODO Auto-generated method stub
		
	}

}
