package chav1961.nn.standalone.tenzors;

import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.factories.TenzorFactory;
import chav1961.purelib.basic.exceptions.SyntaxException;

public class StandaloneTenzor implements Tenzor {
	private static final long serialVersionUID = -3308820261943498187L;
	
	private final int[]	dimensions;
	
	public StandaloneTenzor(final int... dimensions) {
		if (dimensions == null || dimensions.length == 0) {
			throw new IllegalArgumentException("Dimensions can't be null or empty array");
		}
		else {
			this.dimensions = dimensions;
		}
	}

	@Override
	public int getArity() {
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
	public boolean equals(Tenzor another, float epsilon) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sizeEquals(Tenzor another) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float[] getContent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float get(int... indices) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Tenzor get(float[] target, int... indices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor set(float value, int... indices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor set(float[] toSet, int... indices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor set(Tenzor toSet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor fill(float value, int... indices) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor duplicate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor add(Tenzor toAdd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor add(float toAdd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor sub(Tenzor toSubtract) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor sub(float toSubtract) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor mul(Tenzor toMultiply) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor mul(float toMultiply) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor div(Tenzor toDivide) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor div(float toDivide) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor matrixMul(Tenzor toMultiply) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor trans() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor calculate(CharSequence expression, Tenzor... parameters) throws SyntaxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tenzor convert(ConvertCallback callback) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void forEach(ProcessCallback callback) {
		// TODO Auto-generated method stub
		
	}

}
