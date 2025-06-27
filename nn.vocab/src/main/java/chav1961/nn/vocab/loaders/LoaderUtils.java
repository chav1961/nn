package chav1961.nn.vocab.loaders;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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
		else if (gram.parent != null) {
			return gram.parent.name+'.'+gram.name;
		}
		else {
			return gram.name;
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
				if (item.name.equals(parts[0])) {
					if (parts.length > 1) {
						for (Grammeme child : item.children) {
							if (child.name.equals(parts[1])) {
								return child;
							}
						}
						throw new IllegalArgumentException("Grammeme ["+source+"] not found in the grammemes list");
					}
					else {
						return item;
					}
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
			out.writeInt(grams.length);
			for (Grammeme item : grams) {
				out.writeUTF(item.name);
				out.writeUTF(item.alias);
				out.writeUTF(item.description);
				out.writeInt(item.children.length);
				for (Grammeme child : item.children) {
					out.writeUTF(child.name);
					out.writeUTF(child.alias);
					out.writeUTF(child.description);
				}
			}
			out.writeInt(GRAMMEME_MAGIC);
		}
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
			out.writeInt(restr.length);
			for (Restriction item : restr) {
				out.writeByte(item.type.ordinal());
				out.writeBoolean(item.auto);
				out.writeByte(item.leftForm.ordinal());
				out.writeUTF(toString(item.leftGram));
				out.writeByte(item.rightForm.ordinal());
				out.writeUTF(toString(item.rightGram));
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
						for(String item : cargo) {
							out.writeUTF(item);
						}
						final WordLink	link = cargo.getLinks();
						
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
						out.writeByte(0xFF);
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
				final String		name = in.readUTF();
				final String		alias = in.readUTF();
				final String		desc = in.readUTF();
				final Grammeme[]	children = new Grammeme[in.readByte()];
				
				result[index] = new Grammeme(null, name, alias, desc, children);
				for(int childIndex = 0; childIndex < children.length; childIndex++) {
					final String	childName = in.readUTF();
					final String	childAlias = in.readUTF();
					final String	childDesc = in.readUTF();
					
					children[index] = new Grammeme(result[index], childName, childAlias, childDesc);
				}
			}
			if ((temp = in.readInt()) != GRAMMEME_MAGIC) {
				throw new IOException("Illegal magic ["+temp+"] in the input stream, ["+GRAMMEME_MAGIC+"] awaited");
			}
			else {
				return result;
			}
		}
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
			final Restriction[]	result = new Restriction[in.readByte()];
			
			for (int index = 0; index < result.length; index++) {
				final WordRestrictionType	type = WordRestrictionType.values()[in.readByte()];
				final boolean	auto = in.readBoolean();
				final WordForm	leftForm = WordForm.values()[in.readByte()];
				final Grammeme	leftGram = fromString(in.readUTF(), grams);
				final WordForm	rightForm = WordForm.values()[in.readByte()];
				final Grammeme	rightGram = fromString(in.readUTF(), grams);
				
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
	
	public static void downloadVocab(final DataInput in, final SyntaxTreeInterface<Word[]> vocab, final Function<String, Grammeme> grammemDecoder) throws IOException {
		downloadVocab(in, vocab, (x)->x, grammemDecoder);
	}	
	
	public static void downloadVocab(final DataInput in, final SyntaxTreeInterface<Word[]> vocab, final IdDecoder idDecoder, final Function<String, Grammeme> grammemDecoder) throws IOException {
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
		else if (grammemDecoder == null) {
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
			final Set<String>			set = new HashSet<>();
			final List<int[]>			links = new ArrayList<>();
			final List<LinkDescriptor>	desc = new ArrayList<>();
			
			for(int index = 0, maxIndex = in.readInt(); index < maxIndex; index++) {
				final String	word = in.readUTF();
				final Word[]	words = new Word[in.readShort()];
				
				for(int currentWordIndex = 0, maxWordIndex = words.length; currentWordIndex < maxWordIndex; currentWordIndex++) {
					final int			id = idDecoder.decode(in.readInt());
					final WordForm		form = WordForm.values()[in.readByte()];
					final LangPart		part = LangPart.values()[in.readByte()];
					final int			lemmaId = form == WordForm.FORM ? idDecoder.decode(in.readInt()) : -1;
					
					set.clear();
					for(int	count = 0, maxCount = in.readByte(); count < maxCount; count++) {
						set.add(in.readUTF());
					}
					final Word	w = form == WordForm.FORM 
										? new WordImpl(id, part, word, toGrammemes(set, grammemDecoder))
										: new WordImpl(id, wordIndex.get(lemmaId), part, word, toGrammemes(set, grammemDecoder));

					wordIndex.put(id, w);
					words[index] = w;					
					int	linkType = in.readByte();
					
					while (linkType != 0xFF) {
						final int			linkCount = in.readShort();
						
						for(int linkIndex = 0; linkIndex < linkCount; linkIndex++) {
							links.add(new int[] {id, linkType, idDecoder.decode(in.readInt())});
						}
						linkType = in.readByte();
					}
				}
				vocab.placeName(word, words);
			}
			if ((temp = in.readInt()) != VOCAB_MAGIC) {
				throw new IOException("Illegal magic ["+temp+"] in the input stream, ["+VOCAB_MAGIC+"] awaited");
			}
			else {
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

	private static Grammeme[] toGrammemes(final Set<String> source, final Function<String, Grammeme> grammemDecoder) {
		final Grammeme[]	result = new Grammeme[source.size()];
		int	index = 0;

		for(String item : source) {
			result[index++] = grammemDecoder.apply(item); 
		}
		return result;
	}
}
