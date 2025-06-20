package chav1961.nn.api.interfaces;

public enum MatrixClass {
	FLOAT_ARRAY(float.class, 1),
	FLOAT2_ARRAY(float.class, 2),
	DOUBLE_ARRAY(double.class, 1),
	DOUBLE2_ARRAY(double.class, 2),
	;
	
	private final Class<?>	clazz;
	private final int		dimensions;
	
	private MatrixClass(final Class<?> clazz, final int dimensions) {
		this.clazz = clazz;
		this.dimensions = dimensions;
	}
	
	public Class<?> contentClass() {
		return clazz;
	}
	
	public int numberOfDimensions() {
		return dimensions;
	}
}
