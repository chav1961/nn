package chav1961.nn.api.interfaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class PipeBuilderImpl implements PipeBuilder {
	private final List<Object>		content = new ArrayList<>();
	private MatrixWrapper			prev = null;
	private int						numberOfThreads = 1;
	private boolean					readOnly = false;
	private CalculationPrecision	precision = CalculationPrecision.QUICK;

	@Override
	public PipeBuilder setParallelism(final int numberOfThreads) {
		if (numberOfThreads <= 0) {
			throw new IllegalArgumentException("Number of threads must be greater than 0");
		}
		else {
			this.numberOfThreads = numberOfThreads;
			return this;
		}
	}

	@Override
	public PipeBuilder setReadOnly(final boolean readOnly) {
		this.readOnly = readOnly;
		return this;
	}

	@Override
	public PipeBuilder mul(final MatrixWrapper matrix) {
		if (matrix == null) {
			throw new NullPointerException("Matrix can't be null");
		}
		else if (prev != null) {
			if (prev.getMatrixClass().contentClass() != matrix.getMatrixClass().contentClass()) {
				throw new IllegalArgumentException("Content class of the current matrix ["+matrix.getMatrixClass().contentClass()
						+"] is not compatible with the content class of previous matrix ["+prev.getMatrixClass().contentClass()
						+"]");
			}
			else if (getLastDimension(prev) != getFirstDimension(matrix)) {
				throw new IllegalArgumentException("Matrix first dimension ["+getFirstDimension(matrix)
						+"] is differ with previous matrix last dimension ["+getLastDimension(prev)
						+"]");
				
			}
			else {
				prev = matrix;
				content.add(matrix);
				return this;
			}
		}
		else {
			content.add(matrix);
			return this;
		}
	}

	@Override
	public PipeBuilder activate(ActivationType type) {
		if (type == null) {
			throw new NullPointerException("Activation type can't be null");
		}
		else if (!content.isEmpty() && (content.get(content.size()-1) instanceof ActivationType)) {
			throw new IllegalArgumentException("Activation type to add immediately follows another activation type was added.");
		}
		else {
			content.add(type);
			return this;
		}
	}

	@Override
	public Pipe build() {
		if (content.isEmpty() || prev == null) {
			throw new IllegalStateException("No any matrices was added to builder");
		}
		else {
			
			return null;
		}
	}

	@Override
	public Iterator<PipeBuilderStage> iterator() {
		final List<PipeBuilderStage>	result=  new ArrayList<>();
		final AtomicInteger	ai = new AtomicInteger();
		
		for(Object item : content) {
			if (item instanceof MatrixWrapper) {
				final MatrixWrapper		wrapper = (MatrixWrapper)item;
				
				result.add(new PipeBuilderStage() {
					final int	stage = ai.getAndIncrement();

					@Override
					public int getStageNumber() {
						return stage;
					}

					@Override
					public boolean isMatrix() {
						return true;
					}

					@Override
					public MatrixWrapper getMatrix() {
						return wrapper;
					}

					@Override
					public boolean isActivation() {
						return false;
					}

					@Override
					public ActivationType getActivationType() {
						throw new IllegalStateException("This method can't be called for the given stage type");
					}
				});
			}
			else if (item instanceof ActivationType) {
				final ActivationType	type = (ActivationType)item;
				
				result.add(new PipeBuilderStage() {
					final int	stage = ai.getAndIncrement();
					
					@Override
					public boolean isMatrix() {
						return false;
					}
					
					@Override
					public boolean isActivation() {
						return true;
					}
					
					@Override
					public int getStageNumber() {
						return stage;
					}
					
					@Override
					public MatrixWrapper getMatrix() {
						throw new IllegalStateException("This method can't be called for the given stage type");
					}
					
					@Override
					public ActivationType getActivationType() {
						return type;
					}
				});
			}
			else {
				throw new UnsupportedOperationException("Illegal list content detected"); 
			}
		}
		return result.iterator();
	}

	@Override
	public int getParallelism() {
		return numberOfThreads;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
	}
	
	private static int getFirstDimension(final MatrixWrapper matrix) {
		return matrix.getDimensions()[0];
	}
	
	private static int getLastDimension(final MatrixWrapper matrix) {
		return matrix.getDimensions()[matrix.getDimensions().length-1];
	}

	@Override
	public PipeBuilder setPrecision(final CalculationPrecision precision) {
		if (precision == null) {
			throw new NullPointerException("Calculation precision can't be null");
		}
		else {
			this.precision = precision;
			return this;
		}
	}

	@Override
	public CalculationPrecision getPrecision() {
		return precision;
	}
}
