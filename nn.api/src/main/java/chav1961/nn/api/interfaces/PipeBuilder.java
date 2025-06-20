package chav1961.nn.api.interfaces;

public interface PipeBuilder extends Iterable<PipeBuilderStage>{
	PipeBuilder setParallelism(int numberOfThreads);
	PipeBuilder setReadOnly(boolean readOnly);
	PipeBuilder setPrecision(CalculationPrecision precision);
	PipeBuilder mul(MatrixWrapper matrix);
	PipeBuilder activate(ActivationType type);
	int getParallelism();
	boolean isReadOnly();
	CalculationPrecision getPrecision();
	Pipe build();
	
	public static PipeBuilder newInstance() {
		return new PipeBuilderImpl();
	}
}
