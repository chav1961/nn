package chav1961.nn.standalone.util;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.Tenzor.TenzorFactory;
import chav1961.nn.utils.calc.TenzorCalculationUtils;
import chav1961.nn.utils.calc.TenzorCalculationUtils.Command;
import chav1961.nn.utils.calc.TenzorCalculationUtils.FunctionType;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.cdb.SyntaxNode;

public class StandaloneTenzorFactory implements Tenzor.TenzorFactory {
	public static final URI				TENZOR_TYPE = URI.create(TENZOR_FACTORY_SCHEMA +":standalone:/");

	private static interface FunctionInterface extends Cloneable, Tenzor.ConvertCallback, Tenzor.ProcessCallback {
		FunctionInterface clone() throws CloneNotSupportedException;
		boolean isAggregate();
		void before();
		float after();
	}

	
	private static FunctionInterface	FI_SQRT = new FunctionInterface() {
											@Override public void process(float value, int... indices) {}
											@Override public void before() {}
											@Override public float after() {return 0;};
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return false;
											}

											@Override
											public float convert(float value, int... indices) {
												return (float)Math.sqrt(value);
											}
										};
	private static FunctionInterface	FI_LINEAR = new FunctionInterface() {
											@Override public void process(float value, int... indices) {}
											@Override public void before() {}
											@Override public float after() {return 0;};
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return false;
											}

											@Override
											public float convert(float value, int... indices) {
												return value;
											}
										};
	private static FunctionInterface	FI_D_LINEAR = new FunctionInterface() {
											@Override public void process(float value, int... indices) {}
											@Override public void before() {}
											@Override public float after() {return 0;};
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return false;
											}

											@Override
											public float convert(float value, int... indices) {
												return 1;
											}
										};
	private static FunctionInterface	FI_RELU = new FunctionInterface() {
											@Override public void process(float value, int... indices) {}
											@Override public void before() {}
											@Override public float after() {return 0;};
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return false;
											}

											@Override
											public float convert(float value, int... indices) {
												return value >= 0 ? value : 0;
											}
										};
	private static FunctionInterface	FI_D_RELU = new FunctionInterface() {
											@Override public void process(float value, int... indices) {}
											@Override public void before() {}
											@Override public float after() {return 0;};
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return false;
											}

											@Override
											public float convert(float value, int... indices) {
												return value >= 0 ? 1 : 0;
											}
										};
	private static FunctionInterface	FI_SIGMOID = new FunctionInterface() {
											@Override public void process(float value, int... indices) {}
											@Override public void before() {}
											@Override public float after() {return 0;};
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return false;
											}

											@Override
											public float convert(float value, int... indices) {
												return (float) (1.0/(1.0 + Math.exp(-value)));
											}
										};
	private static FunctionInterface	FI_D_SIGMOID = new FunctionInterface() {
											@Override public void process(float value, int... indices) {}
											@Override public void before() {}
											@Override public float after() {return 0;};
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return false;
											}

											@Override
											public float convert(float value, int... indices) {
												final double	temp = 1.0/(1.0 + Math.exp(-value));
												
												return (float) (temp * (1 - temp));
											}
										};
	private static FunctionInterface	FI_TANH = new FunctionInterface() {
											@Override public void process(float value, int... indices) {}
											@Override public void before() {}
											@Override public float after() {return 0;};
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return false;
											}

											@Override
											public float convert(float value, int... indices) {
												return (float)Math.tanh(value);
											}
										};
	private static FunctionInterface	FI_D_TANH = new FunctionInterface() {
											@Override public void process(float value, int... indices) {}
											@Override public void before() {}
											@Override public float after() {return 0;};
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return false;
											}

											@Override
											public float convert(float value, int... indices) {
												final double	temp = Math.tanh(value); 
												
												return (float) (1 - temp * temp);
											}
										};

	private static FunctionInterface	FI_SUMABS = new FunctionInterface() {
											private float	sum = 0;
											
											@Override public float convert(float value, int... indices) {return 0;}
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return true;
											}

											@Override 
											public void before() {
												sum = 0;
											}
											
											@Override 
											public void process(float value, int... indices) {
												sum += Math.abs(value);
											}
											
											@Override 
											public float after() {
												return sum;
											};
										};
	private static FunctionInterface	FI_SUMSQR = new FunctionInterface() {
											private float	sum = 0;
											
											@Override public float convert(float value, int... indices) {return 0;}
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return true;
											}

											@Override 
											public void before() {
												sum = 0;
											}
											
											@Override 
											public void process(float value, int... indices) {
												sum += value * value;
											}
											
											@Override 
											public float after() {
												return sum;
											};
										};
	private static FunctionInterface	FI_MIN = new FunctionInterface() {
											private float	min = 0;
											
											@Override public float convert(float value, int... indices) {return 0;}
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return true;
											}

											@Override 
											public void before() {
												min = Float.MAX_VALUE;
											}
											
											@Override 
											public void process(float value, int... indices) {
												min = Math.min(min, value);
											}
											
											@Override 
											public float after() {
												return min;
											};
										};
	private static FunctionInterface	FI_MAX = new FunctionInterface() {
											private float	max = 0;
											
											@Override public float convert(float value, int... indices) {return 0;}
											
											@Override
											public FunctionInterface clone() throws CloneNotSupportedException {
												return (FunctionInterface)super.clone();
											}
											
											@Override
											public boolean isAggregate() {
												return true;
											}

											@Override 
											public void before() {
												max = -Float.MAX_VALUE;
											}
											
											@Override 
											public void process(float value, int... indices) {
												max = Math.max(max, value);
											}
											
											@Override 
											public float after() {
												return max;
											};
										};
	private static final Map<FunctionType, FunctionInterface>	FUNCTIONS = new HashMap<>();

	static {
		FUNCTIONS.put(FunctionType.Max, FI_MAX);
		FUNCTIONS.put(FunctionType.Min, FI_MIN);
		FUNCTIONS.put(FunctionType.Sqrt, FI_SQRT);
		FUNCTIONS.put(FunctionType.SumAbs, FI_SUMABS);
		FUNCTIONS.put(FunctionType.SumSqr, FI_SUMSQR);
		
		FUNCTIONS.put(FunctionType.linear, FI_LINEAR);
		FUNCTIONS.put(FunctionType.relu, FI_RELU);
		FUNCTIONS.put(FunctionType.sigmoid, FI_SIGMOID);
		FUNCTIONS.put(FunctionType.tanh, FI_TANH);

		FUNCTIONS.put(FunctionType.Dlinear, FI_D_LINEAR);
		FUNCTIONS.put(FunctionType.Drelu, FI_D_RELU);
		FUNCTIONS.put(FunctionType.Dsigmoid, FI_D_SIGMOID);
		FUNCTIONS.put(FunctionType.Dtanh, FI_D_TANH);
	}
	
	public StandaloneTenzorFactory() {
		
	}

	@Override
	public boolean canServe(final URI resource) throws NullPointerException {
		if (resource == null) {
			throw new NullPointerException("Uri to test can't be null");
		}
		else {
			return URIUtils.canServeURI(resource, getDefaultTensorType());
		}
	}

	@Override
	public TenzorFactory newInstance(final URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		if (canServe(resource)) {
			return this;
		}
		else {
			throw new EnvironmentException("Resource ["+resource+"] can't be served");
		}
	}

	@Override
	public URI getDefaultTensorType() {
		return TENZOR_TYPE;
	}

	@Override
	public Tenzor newInstance(final int size, final int... advanced) {
		if (size <= 0) {
			throw new IllegalArgumentException("Size ["+size+"] must be greater than 0");
		}
		else if (advanced == null) {
			throw new NullPointerException("Advanced sizes can't be null");
		}
		else {
			for(int index = 0; index < advanced.length; index++) {
				if (advanced[index] <= 0) {
					throw new IllegalArgumentException("Sizes at position ["+index+"] contains negative or zero value");
				}
			}
			return new TenzorImpl(TenzorCalculationUtils.joinWithArray(size, advanced));
		}
	}

	@Override
	public Tenzor newInstance(final float[] content, final int size, final int... advanced) {
		if (content == null || content.length == 0) {
			throw new IllegalArgumentException("Content to fill can't be null or empty array");
		}
		else if (size <= 0) {
			throw new IllegalArgumentException("Size ["+size+"] must be greater than 0");
		}
		else if (advanced == null) {
			throw new NullPointerException("Advanced sizes can't be null");
		}
		else {
			for(int index = 0; index < advanced.length; index++) {
				if (advanced[index] <= 0) {
					throw new IllegalArgumentException("Sizes at position ["+index+"] contains negative or zero value");
				}
			}
			final int[]			arities = TenzorCalculationUtils.joinWithArray(size, advanced);
			final TenzorImpl	result = new TenzorImpl(arities);
			final int[]			dim = new int[arities.length];
			
			Arrays.fill(dim, -1);
			result.set(content, dim);
			return result;
		}
	}

	static Tenzor calculateInternal(final CharSequence charArray, final Tenzor[] parameters) throws SyntaxException {
		return calculate(TenzorCalculationUtils.parseCalcExpression(charArray), parameters);
	}

	private static Tenzor calculate(final SyntaxNode<Command, SyntaxNode<?, ?>> node, final Tenzor[] parameters) {
		switch (node.getType()) {
			case Function		:
				try{final Tenzor			value = calculate((SyntaxNode<Command, SyntaxNode<?, ?>>) node.children[0], parameters);
				
					if (FUNCTIONS.containsKey((FunctionType)node.cargo)) {
						final FunctionInterface	fi = FUNCTIONS.get((FunctionType)node.cargo).clone();
					
						if (fi.isAggregate()) {
							final Tenzor	result = new TenzorImpl(1);
							
							fi.before();
							value.forEach(fi);
							result.fill(fi.after(), 0);
							return result;
						}
						else {
							value.convert(fi);
							return value;
						}
					}
					else {
						switch ((FunctionType)node.cargo) {
							case Trans		:
								return value.trans();
							case DleakyReLu	:
								final float 	koeffD = node.children.length > 1 ? calculate((SyntaxNode<Command, SyntaxNode<?, ?>>) node.children[1], parameters).getContent()[0] : 0.1f;
								
								value.convert((n,i)->n >= 0 ? 1 : koeffD);
								return value;
							case leakyReLu	:
								final float 	koeff = node.children.length > 1 ? calculate((SyntaxNode<Command, SyntaxNode<?, ?>>) node.children[1], parameters).getContent()[0] : 0.1f;
								
								value.convert((n,i)->n >= 0 ? n : koeff * n);
								return value;
							case Dsoftmax	:
								final float[]	sourceD = value.getContent();
								final double[]	tempD = new double[sourceD.length]; 
								double			sumD = 0;
								
								for(int index = 0; index < tempD.length; index++) {
									final double	val = Math.exp(sourceD[index]);
									
									tempD[index] = val;
									sumD += val;
								}
								sumD = 1/sumD;
								for(int index = 0; index < tempD.length; index++) {
									sourceD[index] = (float) ((tempD[index] * sumD) * (1 - (tempD[index] * sumD)));
								}
								return value;
							case softmax	:
								final float[]	source = value.getContent();
								final double[]	temp = new double[source.length]; 
								double			sum = 0;
								
								for(int index = 0; index < temp.length; index++) {
									final double	val = Math.exp(source[index]);
									
									temp[index] = val;
									sum += val;
								}
								sum = 1/sum;
								for(int index = 0; index < temp.length; index++) {
									source[index] = (float) (temp[index] * sum);
								}
								return value;
							default :
								throw new UnsupportedOperationException("Function type ["+(FunctionType)node.cargo+"] is not supported yet");
						}
					}
				} catch (CloneNotSupportedException e) {
					throw new IllegalArgumentException(e);
				}
			case LoadConstant	:
				final Tenzor	constant = new TenzorImpl(1);
				
				constant.fill((float)Double.longBitsToDouble(node.value), 0);
				return constant;
			case LoadTenzor		:
				return parameters[(int)node.value].duplicate();
			case MulOper : case AddOper	:
				Tenzor		term = calculate((SyntaxNode<Command, SyntaxNode<?, ?>>) node.children[0], parameters);
				
				for(int index = 1; index < node.children.length; index++) {
					final Tenzor	temp = calculate((SyntaxNode<Command, SyntaxNode<?, ?>>) node.children[index], parameters);
					
					switch (((char[])node.cargo)[index-1]) {
						case '+' : case '-' : case '*' : case '/' :
							term = calculate(term, temp, ((char[])node.cargo)[index-1]);
							break;
						default :
							throw new UnsupportedOperationException("Add/mul operator ["+((char[])node.cargo)[index-1]+"] is not supported yet");
					}
				}
				return term;
			case Root			:
				throw new IllegalArgumentException("Root node can't be in the tree");
			case UnaryOper		:
				return calculate((SyntaxNode<Command, SyntaxNode<?, ?>>) node.children[0], parameters).convert((v,n)->-v);
			default :
				throw new UnsupportedOperationException("Command ["+node.getType()+"] is not supported yet");
		}
	}
	
	private static Tenzor calculate(final Tenzor left, final Tenzor right, final char operator) {
		if (left.getContent().length == right.getContent().length) {
			switch (operator) {
				case '+' :
					return left.add(right);
				case '-' :
					return left.sub(right);
				case '*' :
					return left.mul(right);
				case 'x' :
					return left.matrixMul(right);
				case '/' :
					return left.div(right);
			}
		}
		else if (left.getContent().length == 1 || right.getContent().length == 1) {
			if (left.getContent().length == 1 && right.getContent().length == 1) {
				switch (operator) {
					case '+' :
						return left.add(right);
					case '-' :
						return left.sub(right);
					case '*' :
						return left.mul(right);
					case 'x' :
						return left.matrixMul(right);
					case '/' :
						return left.div(right);
				}
			}
			else if (left.getContent().length == 1) {
				switch (operator) {
					case '+' :
						return right.add(left.get(0));
					case '-' :
						return right.sub(left.get(0));
					case '*' :
						return right.mul(left.get(0));
					case 'x' :
						return right.matrixMul(left);
					case '/' :
						return right.div(left.get(0));
				}
			}
			else {
				switch (operator) {
					case '+' :
						return left.add(right.get(0));
					case '-' :
						return left.sub(right.get(0));
					case '*' :
						return left.mul(right.get(0));
					case 'x' :
						return left.mul(right);
					case '/' :
						return left.div(right.get(0));
				}
			}
		}
		else {
			throw new IllegalArgumentException("Uncompatible sizes of the tenzors");
		}
		throw new IllegalArgumentException("Undefined operator ["+operator+"]");
	}

	private static int calcSize(final int[] dimensions) {
		int totalSize = 1;
		
		for(int dim : dimensions) {
			totalSize *= dim;
		}
		return totalSize;
	}

	private static class TenzorImpl implements Tenzor {
		private static final long serialVersionUID = -4849576541841339261L;
		
		private float[]		content;
		private final int[] dimensions;
		
		private TenzorImpl(final int... dimensions) {
			this.dimensions = dimensions.clone();
			this.content = new float[calcSize(dimensions)];
		}
		
		@Override
		public int getArity() {
			return dimensions.length;
		}

		@Override
		public int getSize(final int dimension) {
			if (dimension < 0 || dimension >= getArity()) {
				throw new IllegalArgumentException("Dimension number ["+dimension+"] out of range 0.."+(getArity()-1)); 
			}
			else {
				return dimensions[dimension];
			}
		}

		@Override
		public boolean equals(final Tenzor another, float epsilon) {
			if (another == this) {
				return true;
			}
			else if (another == null) {
				return false;
			}
			else if (another instanceof TenzorImpl) {
				return Arrays.equals(this.dimensions, ((TenzorImpl)another).dimensions) && compare(this.content, ((TenzorImpl)another).content, epsilon); 
			}
			else {
				return Arrays.equals(this.dimensions, buildDimensions(another)) && compare(this.content, another.getContent(), epsilon);
			}
		}

		@Override
		public float[] getContent() {
			return content;
		}
		
		@Override
		public float get(final int... indices) {
			if (indices == null || indices.length != getArity()) {
				throw new IllegalArgumentException("Wrong number of indices ["+indices.length+"]. Must contans exactly ["+getArity()+"] elements"); 
			}
			else {
				return content[calculateDispl(dimensions, indices)];
			}
		}

		@Override
		public Tenzor get(final float[] target, final int... indices) {
			if (target == null || target.length == 0) {
				throw new IllegalArgumentException("Target is null or empty array"); 
			}
			else if (indices == null || indices.length > getArity()) {
				throw new IllegalArgumentException("Wrong number of indices ["+indices.length+"]. Must contans exactly ["+getArity()+"] elements"); 
			}
			else {
				final float[] source = this.content;
				final int[] dim = this.dimensions;
				final int[] currentIndices = new int[getArity()];
				boolean underflow = false;
				int targetIndex = 0, count = 0;
				
				for(int index = 0; index < source.length; index++, count++) {
					if (targetIndex >= target.length) {
						underflow = true;
					}
					else if (canUse(currentIndices, indices)) {
						target[targetIndex++] = source[index];
					}
					for(int dimIndex = currentIndices.length - 1; dimIndex >= 0; dimIndex--) {
						if (++currentIndices[dimIndex] < dim[dimIndex]) {
							break;
						}
						else {
							currentIndices[dimIndex] = 0;
						}
					}
				}
				if (underflow) {
					throw new IllegalArgumentException("Too few data to get - array has ["+source.length+"] elements, nut must contain at least ["+count+"] elements");
				}
				else {
					return this;
				}
			}
		}

		@Override
		public Tenzor set(final float value, final int... indices) {
			if (indices == null || indices.length != getArity()) {
				throw new IllegalArgumentException("Wrong number of indices ["+indices.length+"]. Must contans exactly ["+getArity()+"] elements"); 
			}
			else {
				content[calculateDispl(dimensions, indices)] = value;
				return this;
			}
		}

		@Override
		public Tenzor set(final float[] source, final int... indices) {
			if (source == null || source.length == 0) {
				throw new IllegalArgumentException("Source content is null or empty array"); 
			}
			else if (indices == null || indices.length != getArity()) {
				throw new IllegalArgumentException("Wrong number of indices ["+indices.length+"]. Must contans exactly ["+getArity()+"] elements"); 
			}
			else {
				final float[] target = this.content;
				final int[] dim = this.dimensions;
				final int[] currentIndices = new int[getArity()];
				boolean underflow = false;
				int sourceIndex = 0, count = 0;
				
				for(int index = 0; index < target.length; index++, count++) {
					if (canUse(currentIndices, indices)) {
						if (sourceIndex >= source.length) {
							underflow = true;
						}
						else {
							target[index] = source[sourceIndex++];
						}
					}
					for(int dimIndex = currentIndices.length - 1; dimIndex >= 0; dimIndex--) {
						if (++currentIndices[dimIndex] < dim[dimIndex]) {
							break;
						}
						else {
							currentIndices[dimIndex] = 0;
						}
					}
				}
				if (underflow) {
					throw new IllegalArgumentException("Too few data to set - array has ["+source.length+"] elements, nut must contain at least ["+count+"] elements");
				}
				else {
					return this;
				}
			}
		}

		@Override
		public Tenzor set(final Tenzor toSet) {
			if (toSet == null) {
				throw new NullPointerException("Tenzor to set can't be null"); 
			}
			else if (!Arrays.equals(dimensions, buildDimensions(toSet))) {
				throw new IllegalArgumentException("Tenzor to set has dimensions "+Arrays.toString(buildDimensions(toSet))+" differ to this tenzor dimensions "+Arrays.toString(dimensions)); 
			}
			else {
				final float[] content = toSet.getContent();
				
				System.arraycopy(content, 0, this.content, 0, content.length);
				return this;
			}
		}

		@Override
		public Tenzor fill(final float value, final int... indices) {
			if (indices == null || indices.length != getArity()) {
				throw new IllegalArgumentException("Wrong number of indices ["+indices.length+"]. Must contans exactly ["+getArity()+"] elements"); 
			}
			else {
				final float[] target = this.content;
				final int[] dim = this.dimensions;
				final int[] curentIndices = new int[getArity()];
				
				for(int index = 0; index < target.length; index++) {
					if (canUse(curentIndices, indices)) {
						target[index] = value;
					}
					for(int dimIndex = curentIndices.length - 1; dimIndex >= 0; dimIndex--) {
						if (++curentIndices[dimIndex] < dim[dimIndex]) {
							break;
						}
						else {
							curentIndices[dimIndex] = 0;
						}
					}
				}
				return this;
			}
		}

		@Override
		public Tenzor duplicate() {
			final TenzorImpl	result = new TenzorImpl(dimensions);
			
			System.arraycopy(content, 0, result.content, 0, content.length);
			return result;
		}

		@Override
		public Tenzor add(final Tenzor toAdd) {
			if (toAdd == null) {
				throw new NullPointerException("Tenzor to add can't be null"); 
			}
			else if (!Arrays.equals(dimensions, buildDimensions(toAdd))) {
				throw new IllegalArgumentException("Tenzor to add has dimensions "+Arrays.toString(buildDimensions(toAdd))+" differ to this tenzor dimensions "+Arrays.toString(dimensions)); 
			}
			else {
				final float[] content = toAdd.getContent();
				final float[] target = this.content;
				
				for(int index = 0, maxIndex = target.length; index < maxIndex; index++) {
					target[index] += content[index];
				}
				return this;
			}
		}

		@Override
		public Tenzor add(final float toAdd) {
			final float[] target = this.content;
			
			for(int index = 0, maxIndex = target.length; index < maxIndex; index++) {
				target[index] += toAdd;
			}
			return this;
		}

		@Override
		public Tenzor sub(final Tenzor toSubtract) {
			if (toSubtract == null) {
				throw new NullPointerException("Tenzor to subtract can't be null"); 
			}
			else if (!Arrays.equals(dimensions, buildDimensions(toSubtract))) {
				throw new IllegalArgumentException("Tenzor to subtract has dimensions "+Arrays.toString(buildDimensions(toSubtract))+" differ to this tenzor dimensions "+Arrays.toString(dimensions)); 
			}
			else {
				final float[] content = toSubtract.getContent();
				final float[] target = this.content;
				
				for(int index = 0, maxIndex = target.length; index < maxIndex; index++) {
					target[index] -= content[index];
				}
				return this;
			}
		}

		@Override
		public Tenzor sub(final float toSubtract) {
			final float[] target = this.content;
			
			for(int index = 0, maxIndex = target.length; index < maxIndex; index++) {
				target[index] -= toSubtract;
			}
			return this;
		}

		@Override
		public Tenzor mul(final Tenzor toMultiply) {
			if (toMultiply == null) {
				throw new NullPointerException("Tenzor to multiply can't be null"); 
			}
			else if (!Arrays.equals(dimensions, buildDimensions(toMultiply))) {
				throw new IllegalArgumentException("Tenzor to multiply has dimensions "+Arrays.toString(buildDimensions(toMultiply))+" differ to this tenzor dimensions "+Arrays.toString(dimensions)); 
			}
			else {
				final float[] content = toMultiply.getContent();
				final float[] target = this.content;
				
				for(int index = 0, maxIndex = target.length; index < maxIndex; index++) {
					target[index] *= content[index];
				}
				return this;
			}
		}

		@Override
		public Tenzor mul(final float toMultiply) {
			final float[] target = this.content;
			
			for(int index = 0, maxIndex = target.length; index < maxIndex; index++) {
				target[index] *= toMultiply;
			}
			return this;
		}

		@Override
		public Tenzor div(final Tenzor toDivide) {
			if (toDivide == null) {
				throw new NullPointerException("Tenzor to divide can't be null"); 
			}
			else if (!Arrays.equals(dimensions, buildDimensions(toDivide))) {
				throw new IllegalArgumentException("Tenzor to divide has dimensions "+Arrays.toString(buildDimensions(toDivide))+" differ to this tenzor dimensions "+Arrays.toString(dimensions)); 
			}
			else {
				final float[] content = toDivide.getContent();
				final float[] target = this.content;
				
				for(int index = 0, maxIndex = target.length; index < maxIndex; index++) {
					target[index] /= content[index];
				}
				return this;
			}
		}

		@Override
		public Tenzor div(final float toDivide) {
			final float[] target = this.content;
			final float inv = 1/toDivide;
			
			for(int index = 0, maxIndex = target.length; index < maxIndex; index++) {
				target[index] *= inv;
			}
			return this;
		}

		@Override
		public Tenzor matrixMul(final Tenzor toMultiply) {
			if (toMultiply == null) {
				throw new NullPointerException("Tenzor to multiply can't be null"); 
			}
			else if (toMultiply.getArity() != 2) {
				throw new IllegalArgumentException("Tenzor to matrix multiply mus have arity [2] but really has ["+toMultiply.getArity()+"]"); 
			}
			else if (getArity() != 2) {
				throw new IllegalStateException("Self tenzor to matrix multiply mus have arity [2] but really has ["+getArity()+"]"); 
			}
			else if (getSize(1) != toMultiply.getSize(0)) {
				throw new IllegalArgumentException("Self tenzor size ["+getSize(1)+"] doesn't corresponding mupltiply tenzor size ["+toMultiply.getSize(0)+"]"); 
			}
			else {
				final int		xDim = getSize(0), yDim = toMultiply.getSize(1), zDim = getSize(1);
				final float[]	temp = new float[xDim * yDim];
				final float[]	thisData = getContent(), anotherData = toMultiply.getContent();
				
				for(int x = 0; x < xDim; x++) {
					for(int y = 0; y < yDim; y++) {
						double	sum = 0;
						
						for(int z = 0; z < zDim; z++) {
							sum += thisData/*[x][z]*/[x * zDim + z] * anotherData/*[z][y]*/[z * yDim + y];
						}
						temp/*[x][y]*/[x * xDim + y] = (float)sum;
					}
				}
				content = temp;
				return this;
			}
		}

		@Override
		public Tenzor trans() {
			if (getArity() != 2) {
				throw new IllegalStateException("Only matrices (two-dimensional tenzors) support the operation");
			}
			else {
				final float[]	content = getContent(), result = content.clone();
				final int 		maxX = getSize(0), maxY = getSize(1);
				
				for(int x = 0; x < maxX; x++) {
					for(int y = 0; y < maxY; y++) {
						result/*[x][y]*/[y*maxX + x] = content/*[y][x]*/[x*maxY + y];
					}
				}
				System.arraycopy(result, 0, content, 0, content.length);
				this.dimensions[0] = maxY;  
				this.dimensions[1] = maxX;  
				return this;
			}
		}
		
		@Override
		public Tenzor calculate(final CharSequence expression, final Tenzor... parameters) throws SyntaxException {
			if (expression == null || expression.isEmpty()) {
				throw new IllegalArgumentException("Expression can't be null or empty");
			}
			else if (parameters == null || Utils.checkArrayContent4Nulls(parameters) >= 0) {
				throw new IllegalArgumentException("Tenzor list is null or contans nulls inside");
			}
			else {
				final Tenzor[] temp = new Tenzor[parameters.length + 1];
				
				temp[0] = this;
				System.arraycopy(parameters, 0, temp, 1, parameters.length);
				return calculateInternal(expression, temp);
			}
		}

		@Override
		public Tenzor convert(final ConvertCallback callback) {
			if (callback == null) {
				throw new NullPointerException("Convert callback can't be null"); 
			}
			else {
				final float[] target = this.content;
				final int[] dim = this.dimensions;
				final int[] indices = new int[getArity()];
				
				for(int index = 0; index < target.length; index++) {
					target[index] = callback.convert(target[index], indices);
					
					for(int dimIndex = indices.length - 1; dimIndex >= 0; dimIndex--) {
						if (++indices[dimIndex] < dim[dimIndex]) {
							break;
						}
						else {
							indices[dimIndex] = 0;
						}
					}
				}
				return this;
			}
		}

		@Override
		public void forEach(ProcessCallback callback) {
			if (callback == null) {
				throw new NullPointerException("Process callback can't be null"); 
			}
			else {
				final float[] target = this.content;
				final int[] dim = this.dimensions;
				final int[] indices = new int[getArity()];
				
				for(int index = 0; index < target.length; index++) {
					callback.process(target[index], indices);
					
					for(int dimIndex = indices.length - 1; dimIndex >= 0; dimIndex--) {
						if (++indices[dimIndex] < dim[dimIndex]) {
							break;
						}
						else {
							indices[dimIndex] = 0;
						}
					}
				}
			}
		}
		
		private static boolean compare(final float[] left, final float[] right, final float epsilon) {
			if (left.length != right.length) {
				return false;
			}
			else {
				for(int index = 0; index < left.length; index++) {
					final float delta = left[index] - right[index];
					
					if (delta < -epsilon || delta > epsilon) {
						return false;
					}
				}
				return true;
			}
		}

		private static int[] buildDimensions(final Tenzor another) {
			if (another instanceof TenzorImpl) {
				return ((TenzorImpl)another).dimensions;
			}
			else {
				final int[] result = new int[another.getArity()];
				
				for(int index = 0; index < result.length; index++) {
					result[index] = another.getSize(index);
				}
				return result;
			}
		}

		private int calculateDispl(final int[] dimensions, final int[] indices) {
			int displ = 0, piece = 1;
			
			for(int index = indices.length - 1; index >= 0; index--) {
				int currentIndex = indices[index];
				
				if (currentIndex < 0 || currentIndex >= dimensions[index]) {
					throw new IllegalArgumentException("Index ["+currentIndex+"] an position ["+index+"] out of range 0.."+(dimensions[index]-1)); 
				}
				else {
					displ += piece * currentIndex;
					piece *= dimensions[index];
				}
			}
			return displ;
		}

		private boolean canUse(final int[] indices, final int[] template) {
			for(int index = 0; index < template.length; index++) {
				if (index < template.length) {
					if (template[index] != -1 && template[index] != indices[index]) {
						return false;
					}
				}
			}
			return true;
		}
	}
}
