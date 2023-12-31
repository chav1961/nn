package chav1961.nn.utils.calc;

import java.util.ArrayList;
import java.util.List;

import chav1961.nn.api.interfaces.Tenzor;
import chav1961.purelib.basic.AndOrTree;
import chav1961.purelib.basic.CharUtils;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;
import chav1961.purelib.cdb.SyntaxNode;

public class TenzorCalculationUtils {
	private static final SyntaxTreeInterface<FunctionType>	FUNCTIONS = new AndOrTree<>();
	private static final SyntaxTreeInterface<FunctionType>	SUFFIX = new AndOrTree<>();
	private static final char								EOF = '\uFFFF';
	
	static {
		FUNCTIONS.placeName((CharSequence)"sqrt", FunctionType.Sqrt);
		FUNCTIONS.placeName((CharSequence)"sumAbs", FunctionType.SumAbs);
		FUNCTIONS.placeName((CharSequence)"sumSqr", FunctionType.SumSqr);
		FUNCTIONS.placeName((CharSequence)"min", FunctionType.Min);
		FUNCTIONS.placeName((CharSequence)"max", FunctionType.Max);
//		FUNCTIONS.placeName((CharSequence)"trans", FunctionType.Trans);
//		FUNCTIONS.placeName((CharSequence)"matrix", FunctionType.Matrix);
//		FUNCTIONS.placeName((CharSequence)"vector", FunctionType.Vector);
		
		FUNCTIONS.placeName((CharSequence)"leakyReLu", FunctionType.leakyReLu);
		FUNCTIONS.placeName((CharSequence)"linear", FunctionType.linear);
		FUNCTIONS.placeName((CharSequence)"relu", FunctionType.relu);
		FUNCTIONS.placeName((CharSequence)"sigmoid", FunctionType.sigmoid);
		FUNCTIONS.placeName((CharSequence)"softmax", FunctionType.softmax);
		FUNCTIONS.placeName((CharSequence)"tanh", FunctionType.tanh);
		
		FUNCTIONS.placeName((CharSequence)"DleakyReLu", FunctionType.DleakyReLu);
		FUNCTIONS.placeName((CharSequence)"Dlinear", FunctionType.Dlinear);
		FUNCTIONS.placeName((CharSequence)"Drelu", FunctionType.Drelu);
		FUNCTIONS.placeName((CharSequence)"Dsigmoid", FunctionType.Dsigmoid);
		FUNCTIONS.placeName((CharSequence)"Dsoftmax", FunctionType.Dsoftmax);
		FUNCTIONS.placeName((CharSequence)"Dtanh", FunctionType.Dtanh);

		SUFFIX.placeName((CharSequence)"T", FunctionType.Trans);
		SUFFIX.placeName((CharSequence)"m1", FunctionType.Matrix1);
		SUFFIX.placeName((CharSequence)"m2", FunctionType.Matrix2);
		SUFFIX.placeName((CharSequence)"v", FunctionType.Vector);
	}

	public static enum Command {
		Root,
		LoadTenzor,
		LoadConstant,
		UnaryOper,
		AddOper,
		MulOper,
		Function,
	}

	public static enum FunctionType {
		Sqrt,
		SumAbs,
		SumSqr,
		Min,
		Max,
		Trans,
		Matrix1,
		Matrix2,
		Vector,
		leakyReLu,
		linear,
		relu,
		sigmoid,
		softmax,
		tanh,
		DleakyReLu,
		Dlinear,
		Drelu,
		Dsigmoid,
		Dsoftmax,
		Dtanh;
	}
	
	private static enum Depth {
		Add,
		Mul,
		Unary,
		Term
	}

	/*
	 * %0 + 2 * %1 * ( sqrt(%2) - max(%3) + sum(%3)/sum2(%3))
	 */

	public static SyntaxNode<Command, SyntaxNode<?,?>> parseCalcExpression(final CharSequence expr) throws SyntaxException {
		if (Utils.checkEmptyOrNullString(expr)) {
			throw new IllegalArgumentException("Expression can't be null or empty");
		}
		else {
			final SyntaxNode<Command, SyntaxNode<?,?>> 	root = new SyntaxNode<>(0, 0, Command.Root, 0, null);  
			final List<Lexema>	lex = new ArrayList<>();
			final char[] 		charArray = CharUtils.terminateAndConvert2CharArray(expr, EOF);  
			
			lexParse(charArray, lex);
			final Lexema[]	content = lex.toArray(new Lexema[lex.size()]);
			final int		pos = buildSyntaxTree(Depth.Add, content, 0, root); 
			
			if (content[pos].type != Lexema.LexType.EOF) {
				throw new SyntaxException(0, content[pos].pos, "Unparsed tail in the expression"); 
			}
			else {
				return root;
			}
		}
	}
	
	public static int[] joinWithArray(final int value, final int... array) {
		if (array == null) {
			throw new NullPointerException("Array to join can't be null");
		}
		else {
			final int[]	result = new int[array.length + 1];
			
			result[0] = value;
			System.arraycopy(array, 0, result, 1, array.length);
			return result;
		}
	}
	
	private static void lexParse(final char[] source, final List<Lexema> lex) throws SyntaxException {
		final float[]	forNumber = new float[1]; 
		final long[]	forIds = new long[1]; 
		final int[]		forNames = new int[2]; 
		int 			from = 0;
		
		for(;;) {
			from = CharUtils.skipBlank(source, from, true);
			switch (source[from]) {
				case EOF		:
					lex.add(new Lexema(from, Lexema.LexType.EOF));
					return;
				case '('		:
					lex.add(new Lexema(from++, Lexema.LexType.Open));
					break;
				case ')'		: 
					lex.add(new Lexema(from++, Lexema.LexType.Close));
					break;
				case '.'		: 
					lex.add(new Lexema(from++, Lexema.LexType.Dot));
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
				case 'x'		: 
					lex.add(new Lexema(from++, Lexema.LexType.MulOperator, 'x'));
					break;
				case '/'		: 
					lex.add(new Lexema(from++, Lexema.LexType.MulOperator, '/'));
					break;
				case '%'		: 
					if (source[from + 1] >= '0' && source[from + 1] <= '9') {
						from = CharUtils.parseLong(source, from + 1, forIds, true);
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
						final long	funcId = FUNCTIONS.seekNameI(source, forNames[0], forNames[1] + 1);
						
						if (funcId < 0) {
							final long	suffixId = SUFFIX.seekNameI(source, forNames[0], forNames[1] + 1);
							
							if (suffixId < 0) {
								throw new SyntaxException(0, from, "Unknown suffix ["+new String(source, forNames[0], forNames[1] - forNames[0] + 1)+"]");
							}
							else {
								lex.add(new Lexema(from, Lexema.LexType.Suffix, SUFFIX.getCargo(suffixId)));
							}
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
		
						sb.append(source[from++].getOperator());
						from = buildSyntaxTree(Depth.Mul, source, from, right);
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
						
						sb.append(source[from++].getOperator());
						from = buildSyntaxTree(Depth.Unary, source, from, right);
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
						node.type = Command.LoadConstant;
						node.value = Double.doubleToLongBits(source[from].getValue());
						from++;
						break;
					case AddOperator : case MulOperator : case Close : case EOF : case UnaryOperator	:
						throw new SyntaxException(0, source[from].pos, "tenzor, constant or function awaited");
					case Function		:
						final FunctionType									type = source[from].funcType;
						final List<SyntaxNode<Command, SyntaxNode<?,?>>>	args= new ArrayList<>();
						
						if (source[++from].getType() == Lexema.LexType.Open) {
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
						}
						else {
							throw new SyntaxException(0, source[from].pos, "Missing '('");
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
				if (source[from].type == Lexema.LexType.Dot) {
					if (source[++from].type == Lexema.LexType.Suffix) {
						switch (source[from].funcType) {
							case Trans : case Matrix1 : case Matrix2 : case Vector :
								final SyntaxNode<Command, SyntaxNode<?,?>>		parm = (SyntaxNode<Command, SyntaxNode<?, ?>>) node.clone();
								
								node.type = Command.Function;
								node.cargo = source[from].funcType;
								node.value = 0;
								node.children = new SyntaxNode[] {parm};
								from++;
								break;
							default :
								throw new SyntaxException(0, source[from].pos, "Unsupported suffix ["+source[from].funcType+"]");
						}
					}
					else {
						throw new SyntaxException(0, source[from].pos, "Missing suffix");
					}
				}
				break;
			default:
				throw new UnsupportedOperationException("Depth ["+depth+"] is not supported yet");
		}
		return from;
	}
	
	private static class Lexema {
		private static enum LexType {
			Tensor,
			Constant,
			Function,
			Suffix,
			Dot,
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
	
}
