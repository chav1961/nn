package chav1961.nn.core.interfaces;

public interface TensorFactory {
    Tensor newInstance(int cols);
    Tensor newInstance(int cols, float val);
    Tensor newInstance(final float[] values);
    
    Tensor newInstance(int rows, int cols);
    Tensor newInstance(int rows, int cols, float[] values);
    Tensor newInstance(final float[][] vals);
    
    Tensor newInstance(int rows, int cols, int depth);
    Tensor newInstance(int rows, int cols, int depth, float[] values);
    Tensor newInstance(final float[][][] vals);
    
    Tensor newInstanceTensorImpl(final float[][][][] vals);
    Tensor newInstance(int rows, int cols, int depth, int fourthDim);
    Tensor newInstance(int rows, int cols, int depth, int fourthDim, float[] values);

    Tensor newInstance(Tensor t);
}
