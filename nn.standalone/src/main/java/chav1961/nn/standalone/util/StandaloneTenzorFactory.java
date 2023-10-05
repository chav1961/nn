package chav1961.nn.standalone.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import chav1961.nn.core.interfaces.Tenzor;
import chav1961.nn.core.interfaces.Tenzor.TenzorFactory;
import chav1961.purelib.basic.AndOrTree;
import chav1961.purelib.basic.CharUtils;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;
import chav1961.purelib.cdb.SyntaxNode;

public class StandaloneTenzorFactory implements Tenzor.TenzorFactory {
	private static final URI	TENZOR_TYPE = URI.create(TENZOR_FACTORY_SCHEMA +":standalone:/");
	private static final SyntaxTreeInterface<FunctionType>	FUNCTIONS = new AndOrTree<>();

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
	
	static {
		FUNCTIONS.placeName((CharSequence)"sqrt", FunctionType.Sqrt);
		FUNCTIONS.placeName((CharSequence)"sumAbs", FunctionType.SumAbs);
		FUNCTIONS.placeName((CharSequence)"sumSqr", FunctionType.SumSqr);
		FUNCTIONS.placeName((CharSequence)"min", FunctionType.Min);
		FUNCTIONS.placeName((CharSequence)"max", FunctionType.Max);
	}

	private static enum Command {
		Root,
		LoadTenzor,
		LoadConstant,
		UnaryOper,
		AddOper,
		MulOper,
		Function,
	}

	private static enum FunctionType {
		Sqrt(FI_SQRT),
		SumAbs(FI_SUMABS),
		SumSqr(FI_SUMSQR),
		Min(FI_MIN),
		Max(FI_MAX);
		
		private final FunctionInterface	fi;
		
		private FunctionType(final FunctionInterface fi) {
			this.fi = fi;
		}
		
		private FunctionInterface getFunctionInterface() {
			return fi;
		}
	}

	private static enum Depth {
		Add,
		Mul,
		Unary,
		Term
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
	public Tenzor newInstance(final int... sizes) {
		if (sizes == null || sizes.length == 0) {
			throw new IllegalArgumentException("Sizes can't be null or empty array");
		}
		else {
			for(int index = 0; index < sizes.length; index++) {
				if (sizes[index] <= 0) {
					throw new IllegalArgumentException("Sizes at position ["+index+"] contains negative or zero value");
				}
			}
			return new TenzorImpl(sizes);
		}
	}

	@Override
	public Tenzor newInstance(final float[] content, final int... sizes) {
		if (sizes == null || sizes.length == 0) {
			throw new IllegalArgumentException("Sizes can't be null or empty array");
		}
		else {
			for(int index = 0; index < sizes.length; index++) {
				if (sizes[index] <= 0) {
					throw new IllegalArgumentException("Sizes at position ["+index+"] contains negative or zero value");
				}
			}
			final TenzorImpl	result = new TenzorImpl(sizes);
			final int[]			dim = new int[sizes.length];
			
			Arrays.fill(dim, -1);
			result.set(content, dim);
			return result;
		}
	}

	static Tenzor calculateInternal(final char[] charArray, final Tenzor[] parameters) throws SyntaxException {
		// TODO Auto-generated method stub
		final List<Lexema>							lex = new ArrayList<>();
		final SyntaxNode<Command, SyntaxNode<?,?>> 	root = new SyntaxNode<>(0, 0, Command.Root, 0, null);  
		
		lexParse(charArray, lex);
		final Lexema[]	content = lex.toArray(new Lexema[lex.size()]);
		final int		pos = buildSyntaxTree(Depth.Add, content, 0, root); 
		
		if (content[pos].type != Lexema.LexType.EOF) {
			throw new SyntaxException(0, content[pos].pos, "Unparsed tail in the expression"); 
		}
		else {
			return calculate(root, parameters);
		}
	}

	/*
	 * %0 + 2 * %1 *( sqrt(%2) - max(%3) + sum(%3)/sum2(%3))
	 */
	
	private static void lexParse(final char[] source, final List<Lexema> lex) throws SyntaxException {
		final float[]	forNumber = new float[1]; 
		final long[]	forIds = new long[1]; 
		final int[]		forNames = new int[2]; 
		int 			from = 0;
		
		for(;;) {
			from = CharUtils.skipBlank(source, from, true);
			switch (source[from]) {
				case '\uFFFF'	:
					lex.add(new Lexema(from, Lexema.LexType.EOF));
					return;
				case '('		:
					lex.add(new Lexema(from++, Lexema.LexType.Open));
					break;
				case ')'		: 
					lex.add(new Lexema(from++, Lexema.LexType.Close));
					break;
				case ','		: 
					lex.add(new Lexema(from++, Lexema.LexType.Div));
					break;
				case '+'		: 
					lex.add(new Lexema(from++, Lexema.LexType.AddOperator, '+'));
					break;
				case '-'		: 
					lex.add(new Lexema(from++, Lexema.LexType.AddOperator, '-'));
					break;
				case '*'		: 
					lex.add(new Lexema(from++, Lexema.LexType.MulOperator, '*'));
					break;
				case '/'		: 
					lex.add(new Lexema(from++, Lexema.LexType.MulOperator, '/'));
					break;
				case '%'		: 
					if (source[from + 1] >= '0' && source[from + 1] <= '9') {
						from = CharUtils.parseLong(source, from, forIds, true);
						lex.add(new Lexema(from, Lexema.LexType.Tensor, (int)forIds[0]));
						break;
					}
					else {
						throw new SyntaxException(0, from, "Number is missing");
					}
				case '0' : case '1' : case '2' : case '3' : case '4' : case '5' : case '6' : case '7' : case '8' : case '9' :
					from = CharUtils.parseFloat(source, from, forNumber, true);
					lex.add(new Lexema(from, Lexema.LexType.Constant, forNumber[0]));
					break;
				default :
					if (Character.isJavaIdentifierStart(source[from])) {
						from = CharUtils.parseName(source, from, forNames);
						final long	funcId = FUNCTIONS.seekNameI(source, forNames[0], forNames[1]);
						
						if (funcId < 0) {
							throw new SyntaxException(0, from, "Unknown function");
						}
						else {
							lex.add(new Lexema(from, Lexema.LexType.Function, FUNCTIONS.getCargo(funcId)));
						}
						break;
					}
					else {
						throw new SyntaxException(0, from, "Unknown symbol");
					}
			}
		}
	}

	private static int buildSyntaxTree(final Depth depth, final Lexema[] source, int from, final SyntaxNode<Command, SyntaxNode<?,?>> node) throws SyntaxException {
		node.row = 0;
		node.col = source[from].pos;
		
		switch (depth) {
			case Add	:
				from = buildSyntaxTree(Depth.Mul, source, from, node);
				if (source[from].getType() == Lexema.LexType.AddOperator) {
					final List<SyntaxNode<Command, SyntaxNode<?,?>>>	list = new ArrayList<>();
					final StringBuilder 								sb = new StringBuilder();
					
					list.add((SyntaxNode<Command, SyntaxNode<?, ?>>) node.clone());
					do {final SyntaxNode<Command, SyntaxNode<?,?>>		right = (SyntaxNode<Command, SyntaxNode<?, ?>>) node.clone();
		
						sb.append(source[from++].operator);
						from = buildSyntaxTree(Depth.Mul, source, from, node);
						list.add(right);
					} while (source[from].getType() == Lexema.LexType.AddOperator);
					node.type = Command.AddOper;
					node.cargo = sb.toString().toCharArray();
					node.children = list.toArray(new SyntaxNode[list.size()]);
				}
				break;
			case Mul	:
				from = buildSyntaxTree(Depth.Unary, source, from, node);
				if (source[from].getType() == Lexema.LexType.MulOperator) {
					final List<SyntaxNode<Command, SyntaxNode<?,?>>>	list = new ArrayList<>();
					final StringBuilder 								sb = new StringBuilder();
					
					list.add((SyntaxNode<Command, SyntaxNode<?, ?>>) node.clone());
					do {final SyntaxNode<Command, SyntaxNode<?,?>>		right = (SyntaxNode<Command, SyntaxNode<?, ?>>) node.clone();
						
						sb.append(source[from++].operator);
						from = buildSyntaxTree(Depth.Unary, source, from, node);
						list.add(right);
					} while (source[from].getType() == Lexema.LexType.MulOperator);
					node.type = Command.MulOper;
					node.cargo = sb.toString().toCharArray();
					node.children = list.toArray(new SyntaxNode[list.size()]);
				}
				break;
			case Unary	:
				if (source[from].getType() == Lexema.LexType.AddOperator && source[from].operator == '-') {
					final SyntaxNode<Command, SyntaxNode<?,?>>		right = (SyntaxNode<Command, SyntaxNode<?, ?>>) node.clone();
					
					from = buildSyntaxTree(Depth.Term, source, from + 1, right);
					node.type = Command.UnaryOper;
					node.cargo = new char[] {'-'};
					node.children = new SyntaxNode[] {right};
				}
				else {
					from = buildSyntaxTree(Depth.Term, source, from, node);
				}
				break;
			case Term	:
				switch (source[from].getType()) {
					case Constant		:
						node.type = Command.LoadTenzor;
						node.value = Double.doubleToLongBits(source[from].getValue());
						from++;
						break;
					case AddOperator : case MulOperator : case Close : case EOF : case UnaryOperator	:
						throw new SyntaxException(0, source[from].pos, "tenzor, constant or function awaited");
					case Function		:
						final FunctionType									type = source[from].funcType;
						final List<SyntaxNode<Command, SyntaxNode<?,?>>>	args= new ArrayList<>();
						
						do {final SyntaxNode<Command, SyntaxNode<?,?>>		parm = (SyntaxNode<Command, SyntaxNode<?, ?>>) node.clone();
							
							from = buildSyntaxTree(Depth.Add, source, from + 1, parm);
							args.add(parm);
						} while (source[from].getType() == Lexema.LexType.Div);
						
						if (source[from].type == Lexema.LexType.Close) {
							node.type = Command.Function;
							node.cargo = type;
							node.children = args.toArray(new SyntaxNode[args.size()]);
							from++;
						}
						else {
							throw new SyntaxException(0, source[from].pos, "Missing ')'");
						}
						break;
					case Open			:
						from = buildSyntaxTree(Depth.Add, source, from + 1, node);
						if (source[from].type == Lexema.LexType.Close) {
							from++;
						}
						else {
							throw new SyntaxException(0, source[from].pos, "Missing ')'");
						}
						break;
					case Tensor			:
						node.type = Command.LoadTenzor;
						node.value = source[from].getTenzorId();
						from++;
						break;
					default:
						break;
				
				}
				break;
			default:
				throw new UnsupportedOperationException("Depth ["+depth+"] is not supported yet");
		}
		return from;
	}

	private static Tenzor calculate(final SyntaxNode<Command, SyntaxNode<?, ?>> node, final Tenzor[] parameters) {
		switch (node.getType()) {
			case Function		:
				try{final FunctionInterface	fi = ((FunctionType)node.cargo).getFunctionInterface().clone();
					final Tenzor			value = calculate((SyntaxNode<Command, SyntaxNode<?, ?>>) node.children[0], parameters);
				
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
							term = calculate(term, temp,((char[])node.cargo)[index-1]);
							break;
						default :
							throw new UnsupportedOperationException("Additional operator ["+((char[])node.cargo)[index-1]+"] is not supported yet");
					}
				}
				break;
			case Root			:
				throw new IllegalArgumentException("Root node can't be in the tree");
			case UnaryOper		:
				return calculate((SyntaxNode<Command, SyntaxNode<?, ?>>) node.children[0], parameters).convert((v,n)->-v);
			default :
				throw new UnsupportedOperationException("Command ["+node.getType()+"] is not supported yet");
		}
		return null;
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

	private static class Lexema {
		private static enum LexType {
			Tensor,
			Constant,
			Function,
			Div,
			Open,
			Close,
			UnaryOperator,
			MulOperator,
			AddOperator,
			EOF
		}

		private final int 			pos;
		private final LexType		type;
		private final char			operator;
		private final int			tenzorId;
		private final float			value;
		private final FunctionType	funcType;

		private Lexema(final int pos, final LexType type) {
			this.pos = pos;
			this.type = type;
			this.operator = ' ';
			this.tenzorId = -1;
			this.value = 0;
			this.funcType = null;
		}
		
		private Lexema(final int pos, final LexType type, final char operator) {
			this.pos = pos;
			this.type = type;
			this.operator = operator;
			this.tenzorId = -1;
			this.value = 0f;
			this.funcType = null;
		}
		
		private Lexema(final int pos, final LexType type, final float value) {
			this.pos = pos;
			this.type = type;
			this.operator = ' ';
			this.tenzorId = -1;
			this.value = value;
			this.funcType = null;
		}

		private Lexema(final int pos, final LexType type, final int tenzorId) {
			this.pos = pos;
			this.type = type;
			this.operator = ' ';
			this.tenzorId = tenzorId;
			this.value = 0;
			this.funcType = null;
		}

		private Lexema(final int pos, final LexType type, final FunctionType funcType) {
			this.pos = pos;
			this.type = type;
			this.operator = ' ';
			this.tenzorId = -1;
			this.value = 0;
			this.funcType = funcType;
		}
		
		public int getPos() {
			return pos;
		}

		public LexType getType() {
			return type;
		}

		public char getOperator() {
			return operator;
		}

		public int getTenzorId() {
			return tenzorId;
		}

		public float getValue() {
			return value;
		}

		public FunctionType getFuncType() {
			return funcType;
		}

		@Override
		public String toString() {
			return "Lexema [pos=" + pos + ", type=" + type + ", operator=" + operator + ", tenzorId=" + tenzorId + ", value=" + value + ", funcType=" + funcType + "]";
		}
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
				return Arrays.equals(this.dimensions, buildDimensions(another)) && compare(this.content, content, epsilon);
			}
		}

		@Override
		public float[] getContent() {
			return content;
		}
		
		@Override
		public float get(final int... indices) {
			if (indices == null || indices.length != getArity()) {
				throw new IllegalArgumentException("Indices list is null or it's size differ than ["+getArity()+"]"); 
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
				throw new IllegalArgumentException("Indices list is null or contains more than ["+getArity()+"] elements"); 
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
						target[index] = source[targetIndex++];
					}
					for(int dimIndex = currentIndices.length; dimIndex >= 0; dimIndex--) {
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
				throw new IllegalArgumentException("Indices list is null or it's size differ than ["+getArity()+"]"); 
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
			else if (indices == null || indices.length > getArity()) {
				throw new IllegalArgumentException("Indices list is null or contains more than ["+getArity()+"] elements"); 
			}
			else {
				final float[] target = this.content;
				final int[] dim = this.dimensions;
				final int[] currentIndices = new int[getArity()];
				boolean underflow = false;
				int sourceIndex = 0, count = 0;
				
				for(int index = 0; index < target.length; index++, count++) {
					if (sourceIndex >= source.length) {
						underflow = true;
					}
					else if (canUse(currentIndices, indices)) {
						target[index] = source[sourceIndex++];
					}
					for(int dimIndex = currentIndices.length; dimIndex >= 0; dimIndex--) {
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
			if (indices == null || indices.length > getArity()) {
				throw new IllegalArgumentException("Indices list is null or contains more than ["+getArity()+"] elements"); 
			}
			else {
				final float[] target = this.content;
				final int[] dim = this.dimensions;
				final int[] couurentIndices = new int[getArity()];
				
				for(int index = 0; index < target.length; index++) {
					if (canUse(couurentIndices, indices)) {
						target[index] = value;
					}
					for(int dimIndex = couurentIndices.length; dimIndex >= 0; dimIndex--) {
						if (++couurentIndices[dimIndex] < dim[dimIndex]) {
							break;
						}
						else {
							couurentIndices[dimIndex] = 0;
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
				
				for(int index = 0; index < this.content.length; index++) {
					target[index] += content[index];
				}
				return this;
			}
		}

		@Override
		public Tenzor add(final float toAdd) {
			final float[] target = this.content;
			
			for(int index = 0; index < this.content.length; index++) {
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
				
				for(int index = 0; index < this.content.length; index++) {
					target[index] -= content[index];
				}
				return this;
			}
		}

		@Override
		public Tenzor sub(final float toSubtract) {
			final float[] target = this.content;
			
			for(int index = 0; index < this.content.length; index++) {
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
				
				for(int index = 0; index < this.content.length; index++) {
					target[index] *= content[index];
				}
				return this;
			}
		}

		@Override
		public Tenzor mul(final float toMultiply) {
			final float[] target = this.content;
			
			for(int index = 0; index < this.content.length; index++) {
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
				
				for(int index = 0; index < this.content.length; index++) {
					target[index] /= content[index];
				}
				return this;
			}
		}

		@Override
		public Tenzor div(final float toDivide) {
			final float[] target = this.content;
			final float inv = 1/toDivide;
			
			for(int index = 0; index < this.content.length; index++) {
				target[index] *= inv;
			}
			return this;
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
				return calculateInternal(CharUtils.toCharArray(expression, '\uFFFF'), parameters);
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
					
					for(int dimIndex = indices.length; dimIndex >= 0; dimIndex--) {
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
					
					for(int dimIndex = indices.length; dimIndex >= 0; dimIndex--) {
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
			final int[] result = new int[another.getArity()];
			
			for(int index = 0; index < result.length; index++) {
				result[index] = another.getSize(index);
			}
			return result;
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
