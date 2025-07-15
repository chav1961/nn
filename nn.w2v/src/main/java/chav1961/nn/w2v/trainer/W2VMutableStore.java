package chav1961.nn.w2v.trainer;

import java.io.DataOutput;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleSupplier;

import chav1961.nn.api.interfaces.Word;
import chav1961.nn.api.interfaces.WordForm;
import chav1961.nn.w2v.internal.W2VStore;
import chav1961.purelib.basic.AndOrTree;
import chav1961.purelib.basic.CharUtils;
import chav1961.purelib.basic.LineByLineProcessor;
import chav1961.purelib.basic.exceptions.CalculationException;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;

public class W2VMutableStore extends W2VStore {
	private int	unique = 0;
	
	public W2VMutableStore(SyntaxTreeInterface<Word[]> words) {
		super(words);
	}

	public void prepareVectors(final int vectorSize) throws CalculationException {
		if (vectorSize <= 0) {
			throw new IllegalArgumentException("Vector size ["+vectorSize+"] must be greater than 0");
		}
		else {
			prepareVectors(vectorSize, getCurrentVocab().size(), ()->Math.random());
		}
	}
	
	public void prepareVectors(final int vectorSize, final int vocabularySize, final DoubleSupplier source) throws CalculationException {
		if (vectorSize <= 0) {
			throw new IllegalArgumentException("Vector size ["+vectorSize+"] must be greater than 0");
		}
		else if (vocabularySize <= 0) {
			throw new IllegalArgumentException("Vocabulary size ["+vocabularySize+"] must be greater than 0");
		}
		else if (source == null) {
			throw new NullPointerException("Source can't be null");
		}
		else {
			final float[][][]	temp = new float[2][vocabularySize][vectorSize];
			
			for(float[][] matrix : temp) {
				for(float[] line : matrix) {
					for(int index = 0, maxIndex = temp.length; index < maxIndex; index++) {
						line[index] = (float) source.getAsDouble();
					}
				}
			}
		}
	}
	
	public void buildCurrentVocab(final Reader source) throws IOException {
		if (source == null) {
			throw new NullPointerException("Reader can't be null");
		}
		else {
			try(final LineByLineProcessor	lblp = new LineByLineProcessor((displacement, lineNo, data, from, length)->insert(data, from, length))) {
				lblp.write(source);
			} catch (SyntaxException e) {
				throw new IOException(e); 
			}
		}
	}

	public void buildCurrentVocab(final Iterable<Word> source) throws IOException {
		if (source == null) {
			throw new NullPointerException("Source can't be null");
		}
		else {
			final SyntaxTreeInterface<Word[]>	vocab = getVocab();
//			final SyntaxTreeInterface<WordRef>	currentVocab = getCurrentVocab();
//			final LongIdMap<WordRef>			currentIds = getCurrentIds();

			for(Word item : source) {
				insert(vocab, item);
			}
		}
		
	}

	public void forward(final float[] source, final float[] target) {
		if (source == null) {
			throw new NullPointerException("Source vector can't be null");
		}
		else if (source.length != getVocabSize()) {
			throw new IllegalArgumentException("Source vector size ["+source.length+"] differ with vocabulary size ["+getVocabSize()+"]");
		}
		else if (target == null) {
			throw new NullPointerException("Target vector can't be null");
		}
		else if (target.length != getVocabSize()) {
			throw new IllegalArgumentException("Target vector size ["+target.length+"] differ with vocabulary size ["+getVocabSize()+"]");
		}
		else {
			final float[][][]	temp = new float[2][getVocabSize()][getVectorsSize()];
			final float[]		internal = new float[getVectorsSize()];
			
			multiply(source, temp[0], internal);
			multiply(internal, temp[1], target);
			softMax(target);
		}
	}
	
	public void backward(final float[] delta, final float step) {
		if (delta == null) {
			throw new NullPointerException("Delta vector can't be null");
		}
		else if (delta.length != getVocabSize()) {
			throw new IllegalArgumentException("Delta vector size ["+delta.length+"] differ with vocabulary size ["+getVocabSize()+"]");
		}
		else {
			final float[][][]	temp = new float[2][getVocabSize()][getVectorsSize()];
			final float[]		internal = new float[getVectorsSize()];
			final float[]		input = new float[getVocabSize()];
			
			multiplyT(delta, temp[1], internal);
			correctWeights(temp[1], internal, step);
			multiplyT(internal, temp[0], input);
			correctWeights(temp[0], input, step);
		}
	}

	public void uploadVocab(final DataOutput out) throws IOException {
		if (out == null) {
			throw new NullPointerException("Data output can't be null");
		}
		else {
			final SyntaxTreeInterface<Word[]>	temp = getVocab();
			
			out.writeInt(getVocabSize());
			getCurrentIds().walk((id, ref)->{
				try {
					out.writeInt(ref.id);
					out.writeUTF(temp.getCargo(ref.cargoRef)[0].getWord());
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			});
		}
	}

	public void uploadVectors(final DataOutput out) throws IOException {
		if (out == null) {
			throw new NullPointerException("Data output can't be null");
		}
		else if (getVectors() == null) {
			throw new IllegalStateException("There are no prepared vectors to upload yet");
		}
		else {
			final float[][][]	temp = getVectors();
			
			out.writeInt(getVocabSize());
			out.writeInt(getVectorsSize());
			for(float[][] matrix : temp) {
				for(float[] line : matrix) {
					for(float item : line) {
						out.writeFloat(item);
					}
				}
			}
		}
	}

	private void insert(final char[] data, int from, final int length) {
		// TODO Auto-generated method stub
		final List<Lexema>	lex = new ArrayList<>();
		int	start;
		
		do {
			start = from;
			if (Character.isWhitespace(data[from])) {
				from++;
			}
			else if (Character.isDigit(data[from])) {
				while (Character.isDigit(data[from])) {
					from++;
				}
				lex.add(new Lexema(start, from, LexType.NUMBER));
			}
			else if (Character.isLetter(data[from])) {
				while (Character.isLetter(data[from])) {
					from++;
				}
				lex.add(new Lexema(start, from, LexType.WORD));
			}
			else if (data[from] == '-') {
				lex.add(new Lexema(start, from++, LexType.DEPHIS));
			}
			else {
				lex.add(new Lexema(start, from++, LexType.PUNCT));
			}
		} while (data[from] != '\n');
		
		for(int index = 0; index < lex.size(); index++) {
			if (index < lex.size()-2 && lex.get(index).type == LexType.WORD && lex.get(index+1).type == LexType.DEPHIS && lex.get(index+2).type == LexType.WORD) {
				final String	candidate = new StringBuilder()
												.append(data, lex.get(index).from, lex.get(index).to-lex.get(index).from)
												.append('-')
												.append(data, lex.get(index+2).from, lex.get(index+2).to-lex.get(index+2).from).toString().toLowerCase();
				if (appendWord(candidate)) {
					index += 2;
					continue;
				}
			}
			if (lex.get(index).type == LexType.WORD) {
				final String	candidate = new String(data, lex.get(index).from, lex.get(index).to-lex.get(index).from).toLowerCase(); 
				
				if (!appendWord(candidate)) {
					System.err.println("Fail: "+candidate);
				}
			}
		}
	}

	private boolean appendWord(final String candidate) {
		final long	id = getVocab().seekName(candidate);
		
		if (id < 0) {
			return false;
		}
		else if (getVocab().getCargo(id)[0].wordForm() == WordForm.FORM) {
			return appendWord(getVocab().getCargo(id)[0].getLemma().getWord());
		}
		else {
			final int		newId = unique++;
			final WordRef	ref = new WordRef(newId, id);
			
			getCurrentVocab().placeName(candidate, ref);
			getCurrentIds().put(newId, ref);
			return true;
		}
	}

	private void insert(final SyntaxTreeInterface<Word[]> vocab, final Word word) {
		final long	id = vocab.seekName(word.getWord());
		
		if (id < 0) {
			vocab.placeName(word.getWord(), new Word[] {word});
		}
		else {
			final Word[]	oldVal = vocab.getCargo(id);
			final Word[]	newVal = Arrays.copyOf(oldVal, oldVal.length + 1);
			
			newVal[newVal.length - 1] = word;
			vocab.setCargo(id, newVal);
		}
	}

	private void multiplyT(final float[] left, final float[][] right, final float[] result) {
		for(int x = 0, maxX = left.length; x < maxX; x++) {
			for(int y = 0, maxY = result.length; y < maxY; y++) {
				float sum = 0;
				
				for(int z = 0, maxZ = right[0].length; z < maxZ; z++) {
					sum += left[x]*right[z][y];
				}
				result[y] = sum;
			}
		}
	}
	
	private void correctWeights(final float[][] fs, final float[] internal, float step) {
		// TODO Auto-generated method stub
		
	}
	
	private static enum LexType {
		WORD, DEPHIS, NUMBER, PUNCT
	}
	
	private static class Lexema {
		private final int		from;
		private final int		to;
		private final LexType	type;
		
		private Lexema(int from, int to, LexType type) {
			this.from = from;
			this.to = to;
			this.type = type;
		}

		@Override
		public String toString() {
			return "Lexema [from=" + from + ", to=" + to + ", type=" + type + "]";
		}
	}
}
