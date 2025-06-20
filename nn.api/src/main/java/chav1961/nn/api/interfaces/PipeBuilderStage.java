package chav1961.nn.api.interfaces;

public interface PipeBuilderStage {
	int getStageNumber();
	boolean isMatrix();
	MatrixWrapper getMatrix();
	boolean isActivation();
	ActivationType getActivationType();
}