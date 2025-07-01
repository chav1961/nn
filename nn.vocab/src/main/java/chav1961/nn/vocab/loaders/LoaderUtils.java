package chav1961.nn.vocab.loaders;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

import chav1961.nn.vocab.interfaces.Grammeme;
import chav1961.nn.vocab.interfaces.LangPart;
import chav1961.nn.vocab.interfaces.Word;
import chav1961.nn.vocab.interfaces.WordForm;
import chav1961.nn.vocab.interfaces.WordLink;
import chav1961.nn.vocab.interfaces.WordLinkType;
import chav1961.nn.vocab.interfaces.WordRestrictionType;
import chav1961.purelib.basic.LongIdMap;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;

public class LoaderUtils {
	private static final int	VOCAB_MAGIC = 0x16061900;
	private static final int	VOCAB_VERSION = 0x0101;
	private static final int	GRAMMEME_MAGIC = 0x16061901;
	private static final int	GRAMMEME_VERSION = 0x0101;
	private static final int	RESTRICTION_MAGIC = 0x16061902;
	private static final int	RESTRICTION_VERSION = 0x0101;

	public static String toString(final Grammeme gram) {
		if (gram == null) {
			throw new NullPointerException("Grammeme to convert can't be null");
		}
		else if (gram.getParent() != null) {
			return toString(gram.getParent())+'.'+gram.getName();
		}
		else {
			return gram.getName();
		}
	}
	
	public static Grammeme fromString(final String source, final Grammeme... grams) {
		if (Utils.checkEmptyOrNullString(source)) {
			throw new IllegalArgumentException("Source string can be neither null nor empty"); 
		}
		else if (grams == null || grams.length == 0 || Utils.checkArrayContent4Nulls(grams) >= 0) {
			throw new IllegalArgumentException("Grammemes list is null, empty or contains nulls inside"); 
		}
		else {
			final String[]	parts = source.split("\\.");
			
			for (Grammeme item : grams) {
				Grammeme	g;
				
				if ((g = item.seek((n)->n.getName().equals(parts[parts.length-1]) ? n : null)) != null) {
					return g;
				}
			}
			throw new IllegalArgumentException("Grammeme ["+source+"] not found in the grammemes list");
		}
	}

	public static Grammeme fromIndex(final int source, final Grammeme... grams) {
		if (grams == null || grams.length == 0 || Utils.checkArrayContent4Nulls(grams) >= 0) {
			throw new IllegalArgumentException("Grammemes list is null, empty or contains nulls inside"); 
		}
		else {
			for (Grammeme item : grams) {
				Grammeme	g;
				
				if ((g = item.seek((n)->n.getIndex() == source ? n : null)) != null) {
					return g;
				}
			}
			throw new IllegalArgumentException("Grammeme ["+source+"] not found in the grammemes list");
		}
	}
	
	public static void uploadGrammemes(final Grammeme[] grams, final DataOutput out) throws IOException {
		if (grams == null) {
			throw new NullPointerException("Grammemes to upload can't be null");
		}
		else if (out == null) {
			throw new NullPointerException("Data output to upload can't be null");
		}
		else {
			out.writeInt(GRAMMEME_MAGIC);
			out.writeInt(GRAMMEME_VERSION);
			out.writeByte(grams.length);
			int	count = 0;
			for (Grammeme item : grams) {
				count = uploadGrammeme(count, item, out);
			}
			out.writeInt(GRAMMEME_MAGIC);
		}
	}
	
	private static int uploadGrammeme(int count, final Grammeme item, final DataOutput out) throws IOException {
		out.writeShort(count++);
		out.writeUTF(item.getName());
		out.writeUTF(item.getAlias());
		out.writeUTF(item.getDescription());
		out.writeByte(item.getChildren().length);
		for (Grammeme child : item.getChildren()) {
			count = uploadGrammeme(count, child, out);
		}
		return count;
	}

	public static void uploadRestrictions(final Restriction[] restr, final DataOutput out) throws IOException {
		if (restr == null) {
			throw new NullPointerException("Restrictions to upload can't be null");
		}
		else if (out == null) {
			throw new NullPointerException("Data output to upload can't be null");
		}
		else {
			out.writeInt(RESTRICTION_MAGIC);
			out.writeInt(RESTRICTION_VERSION);
			out.writeShort(restr.length);
			for (Restriction item : restr) {
				out.writeByte(item.type.ordinal());
				out.writeBoolean(item.auto);
				out.writeByte(item.leftForm.ordinal());
				out.writeUTF(item.leftGram == null ? "" : toString(item.leftGram));
				out.writeByte(item.rightForm.ordinal());
				out.writeUTF(item.rightGram == null ? "" : toString(item.rightGram));
			}
			out.writeInt(RESTRICTION_MAGIC);
		}
	}
	
	public static void uploadVocab(final SyntaxTreeInterface<Word[]> vocab, final DataOutput out) throws IOException {
		if (vocab == null) {
			throw new NullPointerException("Vocabulary to upload can't be null");
		}
		else if (out == null) {
			throw new NullPointerException("Data output to upload can't be null");
		}
		else {
			out.writeInt(VOCAB_MAGIC);
			out.writeInt(VOCAB_VERSION);
			out.writeInt(vocab.size());
			vocab.walk((name, len, id, list)->{
				try {
					out.writeUTF(list[0].getWord());
					out.writeShort(list.length);
					for (Word cargo : list) {
						out.writeInt(cargo.id());
						out.writeByte(cargo.wordForm().ordinal());
						out.writeByte(cargo.part().ordinal());
						if (cargo.wordForm() == WordForm.FORM) {
							out.writeInt(cargo.getLemma().id());
						}
						out.writeByte(cargo.numberOfAttributes());
						for(Grammeme item : cargo) {
							out.writeShort(item.getIndex());
						}
						final WordLink	link = cargo.getLinks();
						
						if (link != null) {
							for (WordLinkType item : WordLinkType.values()) {
								if (link.hasLinkFrom(item)) {
									final Word[]	words = link.getLinksFrom(item);
		
									out.writeByte(2 * item.ordinal());
									out.writeShort(words.length);
									for(Word w : words) {
										out.writeInt(w.id());
									}
								}
								if (link.hasLinkTo(item)) {
									final Word[]	words = link.getLinksTo(item);
		
									out.writeByte(2 * item.ordinal() + 1);
									out.writeShort(words.length);
									for(Word w : words) {
										out.writeInt(w.id());
									}
								}
							}
						}
						out.writeByte(-1);
					}
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			});
			out.writeInt(VOCAB_MAGIC);
		}
	}

	@FunctionalInterface
	public static interface IdDecoder {
		int decode(int source);
	}

	public static Grammeme[] downloadGrammemes(final DataInput in) throws IOException {
		int	temp;
		
		if (in == null) {
			throw new NullPointerException("Data input can't be null");
		}
		else if ((temp = in.readInt()) != GRAMMEME_MAGIC) {
			throw new IOException("Illegal magic ["+temp+"] in the input stream, ["+GRAMMEME_MAGIC+"] awaited");
		}
		else if ((temp = in.readInt()) != GRAMMEME_VERSION) {
			throw new IOException("Unsupported version ["+temp+"] in the input stream");
		}
		else {
			final Grammeme[]	result = new Grammeme[in.readByte()];
			
			for(int index = 0; index < result.length; index++) {
				result[index] = downloadGrammeme(null, in);
			}
			if ((temp = in.readInt()) != GRAMMEME_MAGIC) {
				throw new IOException("Illegal magic ["+temp+"] in the input stream, ["+GRAMMEME_MAGIC+"] awaited");
			}
			else {
				return result;
			}
		}
	}

	private static Grammeme downloadGrammeme(final Grammeme parent,final DataInput in) throws IOException {
		final short			gIndex = in.readShort();
		final String		name = in.readUTF();
		final String		alias = in.readUTF();
		final String		desc = in.readUTF();
		final Grammeme[]	children = new Grammeme[in.readByte()];
		final Grammeme		gr = new Grammeme(gIndex, ()->parent, name, alias, desc, children); 
		
		for(int index = 0; index < children.length; index++) {
			children[index] = downloadGrammeme(gr, in);
		}
		return gr;
	}
	
	public static Restriction[] downloadRestrictions(final DataInput in, final Grammeme[] grams) throws IOException {
		int	temp;
		
		if (in == null) {
			throw new NullPointerException("Data input can't be null");
		}
		else if (grams == null || grams.length == 0 || Utils.checkArrayContent4Nulls(grams) >= 0) {
			throw new IllegalArgumentException("Grammemes list is null, empty or contains nulls inside"); 
		}
		else if ((temp = in.readInt()) != RESTRICTION_MAGIC) {
			throw new IOException("Illegal magic ["+temp+"] in the input stream, ["+RESTRICTION_MAGIC+"] awaited");
		}
		else if ((temp = in.readInt()) != RESTRICTION_VERSION) {
			throw new IOException("Unsupported version ["+temp+"] in the input stream");
		}
		else {
			final Restriction[]	result = new Restriction[in.readShort()];
			
			for (int index = 0; index < result.length; index++) {
				final WordRestrictionType	type = WordRestrictionType.values()[in.readByte()];
				final boolean	auto = in.readBoolean();
				final WordForm	leftForm = WordForm.values()[in.readByte()];
				final String	leftString = in.readUTF();
				final Grammeme	leftGram = leftString.isEmpty() ? null : fromString(leftString, grams);
				final WordForm	rightForm = WordForm.values()[in.readByte()];
				final String	rightString = in.readUTF();
				final Grammeme	rightGram = rightString.isEmpty() ? null : fromString(rightString, grams);
				
				result[index] = new Restriction(type, auto, leftForm, leftGram, rightForm, rightGram);
			}
			if ((temp = in.readInt()) != RESTRICTION_MAGIC) {
				throw new IOException("Illegal magic ["+temp+"] in the input stream, ["+RESTRICTION_MAGIC+"] awaited");
			}
			else {
				return result;
			}
		}
	}
	
	public static void downloadVocab(final DataInput in, final SyntaxTreeInterface<Word[]> vocab, final IntFunction<Grammeme> grammemeDecoder) throws IOException {
		downloadVocab(in, vocab, (x)->x, grammemeDecoder);
	}	
	
	public static void downloadVocab(final DataInput in, final SyntaxTreeInterface<Word[]> vocab, final IdDecoder idDecoder, final IntFunction<Grammeme> grammemeDecoder) throws IOException {
		int	temp = 0;
		
		if (in == null) {
			throw new NullPointerException("Data input to download can't be null");
		}
		else if (vocab == null) {
			throw new NullPointerException("Vocabulary to download can't be null");
		}
		else if (idDecoder == null) {
			throw new NullPointerException("ID decoder can't be null");
		}
		else if (grammemeDecoder == null) {
			throw new NullPointerException("Grammeme decoder can't be null");
		}
		else if ((temp = in.readInt()) != VOCAB_MAGIC) {
			throw new IOException("Illegal magic ["+temp+"] in the input stream, ["+VOCAB_MAGIC+"] awaited");
		}
		else if ((temp = in.readInt()) != VOCAB_VERSION) {
			throw new IOException("Unsupported version ["+temp+"] in the input stream");
		}
		else {
			final LongIdMap<Word>		wordIndex = new LongIdMap<Word>(Word.class);
			final List<int[]>			links = new ArrayList<>();
			final Grammeme[]			grammemes = new Grammeme[calcNumberOfGrammemes(grammemeDecoder)];
			
			for(int index = 0; index < grammemes.length; index++) {
				grammemes[index] = grammemeDecoder.apply(index);
			}
			
			for(int index = 0, maxIndex = in.readInt(); index < maxIndex; index++) {
				final String	word = in.readUTF();
				final Word[]	words = new Word[in.readShort()];
				
				for(int currentWordIndex = 0, maxWordIndex = words.length; currentWordIndex < maxWordIndex; currentWordIndex++) {
					final int			id = idDecoder.decode(in.readInt());
					final WordForm		form = WordForm.values()[in.readByte()];
					final LangPart		part = LangPart.values()[in.readByte()];
					final int			lemmaId = form == WordForm.FORM ? idDecoder.decode(in.readInt()) : -1;
					final Grammeme[]	grams = new Grammeme[in.readByte()];
					
					for(int	count = 0, maxCount = grams.length; count < maxCount; count++) {
						grams[count] = grammemes[in.readShort()];
					}
					final Word	w = form == WordForm.FORM 
										? new WordImpl(id, part, word, grams)
										: new WordImpl(id, wordIndex.get(lemmaId), part, word, grams);

					wordIndex.put(id, w);
					words[currentWordIndex] = w;					
					int	linkType = in.readByte();
					
					while (linkType != -1) {
						if ((linkType >> 1) < 0 || (linkType >> 1) >= WordLinkType.values().length) {
							throw new EOFException("Illegal linkType ["+linkType+"] detected"); 
						}
						else {
							final int			linkCount = in.readShort();
							
							for(int linkIndex = 0; linkIndex < linkCount; linkIndex++) {
								links.add(new int[] {id, linkType, idDecoder.decode(in.readInt())});
							}
							linkType = in.readByte();
						}
					}
				}
				vocab.placeName(word, words);
			}
			if ((temp = in.readInt()) != VOCAB_MAGIC) {
				throw new IOException("Illegal magic ["+temp+"] in the input stream, ["+VOCAB_MAGIC+"] awaited");
			}
			else {
				final List<LinkDescriptor>	desc = new ArrayList<>();
				int			prevId = -1;
				WordImpl	current = null;
				
				for (int[] item : links) {
					if (item[0] != prevId) {
						prevId = item[0]; 
						if (current != null) {
							current.setLinks(new LinkImpl(current, desc.toArray(new LinkDescriptor[desc.size()])));
							desc.clear();
						}
						current = (WordImpl)wordIndex.get(item[0]);
					}
					final WordLinkType	type = WordLinkType.values()[item[1] >> 1];
					final boolean		isRight = (item[1] & 0x01) != 0;
					
					desc.add(new LinkDescriptor(type, isRight, wordIndex.get(item[2])));
				}
				current.setLinks(new LinkImpl(current, desc.toArray(new LinkDescriptor[desc.size()])));
			}
		}
	}

	private static int calcNumberOfGrammemes(final IntFunction<Grammeme> grammemeDecoder) {
		for(int index = 0; index < Integer.MAX_VALUE; index++) {
			try {
				if (grammemeDecoder.apply(index) == null) {
					return index;
				}
			} catch (IllegalArgumentException exc) {
				return index;
			}
		}
		throw new IllegalArgumentException("Grammeme decoder has tool long resultset"); 
	}
}
