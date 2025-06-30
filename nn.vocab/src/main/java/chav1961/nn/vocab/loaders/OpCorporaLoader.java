package chav1961.nn.vocab.loaders;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import chav1961.nn.vocab.interfaces.LangPart;
import chav1961.nn.vocab.interfaces.Word;
import chav1961.nn.vocab.interfaces.WordForm;
import chav1961.nn.vocab.interfaces.WordLinkType;
import chav1961.nn.vocab.interfaces.WordRestrictionType;
import chav1961.purelib.basic.AndOrTree;
import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.LongIdMap;
import chav1961.purelib.basic.exceptions.SyntaxException;
import chav1961.purelib.basic.interfaces.SyntaxTreeInterface;

public class OpCorporaLoader {
	private static final QName		TAG_DICTIONARY = QName.valueOf("dictionary");
	private static final QName		ATTR_VERSION = QName.valueOf("version");
	private static final QName		ATTR_REVISION = QName.valueOf("revision");
	private static final QName		TAG_GRAMMEMES = QName.valueOf("grammemes");
	private static final QName		TAG_GRAMMEME = QName.valueOf("grammeme");
	private static final QName		ATTR_PARENT = QName.valueOf("parent");
	private static final QName		TAG_NAME = QName.valueOf("name");
	private static final QName		TAG_ALIAS = QName.valueOf("alias");
	private static final QName		TAG_DESCRIPTION = QName.valueOf("description");
	private static final QName		TAG_RESTRICTIONS = QName.valueOf("restrictions");
	private static final QName		TAG_RESTRICTION = QName.valueOf("restr");
	private static final QName		ATTR_TYPE = QName.valueOf("type");
	private static final QName		ATTR_AUTO = QName.valueOf("auto");
	private static final QName		TAG_LEFT = QName.valueOf("left");
	private static final QName		TAG_RIGHT = QName.valueOf("right");
	private static final QName		TAG_LEMMATA = QName.valueOf("lemmata");
	private static final QName		TAG_LEMMA = QName.valueOf("lemma");
	private static final QName		ATTR_ID = QName.valueOf("id");
//	private static final QName		ATTR_REV = QName.valueOf("rev");
	private static final QName		TAG_L = QName.valueOf("l");
	private static final QName		TAG_G = QName.valueOf("g");
	private static final QName		TAG_F = QName.valueOf("f");
	private static final QName		ATTR_T = QName.valueOf("t");
	private static final QName		ATTR_V = QName.valueOf("v");
	private static final QName		TAG_LINK_TYPES = QName.valueOf("link_types");
	private static final QName		TAG_TYPE = QName.valueOf("type");
	private static final QName		TAG_LINKS = QName.valueOf("links");
	private static final QName		TAG_LINK = QName.valueOf("link");
	private static final QName		ATTR_FROM = QName.valueOf("from");
	private static final QName		ATTR_TO = QName.valueOf("to");

	private static final int	CORPORA_MAGIC = 0x16061903;
	
	
	private final SyntaxTreeInterface<Word[]>	vocab = new AndOrTree<>(1,1);
	private final LongIdMap<Word>	wordIndex = new LongIdMap<Word>(Word.class);
	private DottedVersion	version;
	private int				revision;
	private Grammeme[]		gramms;
	private Restriction[]	restr;
	private XMLEvent		event;
	
	public OpCorporaLoader(final Reader rdr) throws IOException, SyntaxException {
		if (rdr == null) {
			throw new NullPointerException("Reader can't be null");
		}
		else {
			try {
				final XMLInputFactory 	xmlInputFactory = XMLInputFactory.newInstance();
				final XMLEventReader 	reader = xmlInputFactory.createXMLEventReader(rdr);

				event = next(reader);
				load(reader);
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}			
		}
	}

	public OpCorporaLoader(final DataInput in) throws IOException, SyntaxException {
		int	temp;
		
		if (in == null) {
			throw new NullPointerException("Data input can't be null");
		}
		else if ((temp = in.readInt()) != CORPORA_MAGIC) {
			throw new IllegalArgumentException("Illegal magic ["+temp+"] in the input stream, ["+CORPORA_MAGIC+"] awaited"); 
		}
		else {
			this.version = new DottedVersion(in.readUTF());
			this.revision = in.readInt();
			this.gramms = LoaderUtils.downloadGrammemes(in);
			this.restr = LoaderUtils.downloadRestrictions(in, gramms);
			LoaderUtils.downloadVocab(in, vocab, (s)->LoaderUtils.fromString(s, gramms));
		}
	}
	
	public SyntaxTreeInterface<Word[]> getVocab() {
		return vocab;
	}
	
	public Grammeme[] getGrammemes() {
		return gramms;
	}
	
	public Restriction[] getRestrictions() {
		return restr;
	}
	
	public void store(final DataOutput out) throws IOException {
		if (out == null) {
			throw new NullPointerException("Data output can't be null");
		}
		else {
			out.writeInt(CORPORA_MAGIC);
			out.writeUTF(version.toString());
			out.writeInt(revision);
			LoaderUtils.uploadGrammemes(getGrammemes(), out);
			LoaderUtils.uploadRestrictions(getRestrictions(), out);
			LoaderUtils.uploadVocab(getVocab(), out);
			out.writeInt(CORPORA_MAGIC);
		}
	}
	
	private static class TempGrammeme {
		private final String	parent;
		private final String	name;
		private final String	alias;
		private final String	description;

		private TempGrammeme(String parent, String name, String alias, String description) {
			this.parent = parent;
			this.name = name;
			this.alias = alias;
			this.description = description;
		}
	}
	
	private static class TempRestrictionItem {
		private final String	type;
		private final String	value;
		
		private TempRestrictionItem(final String type, final String value) {
			this.type = type;
			this.value = value;
		}
	}
	
	private static class TempRestriction {
		private final String	type;
		private final String	auto;
		private final TempRestrictionItem	left;
		private final TempRestrictionItem	right;
		
		private TempRestriction(String type, String auto, TempRestrictionItem left, TempRestrictionItem right) {
			this.type = type;
			this.auto = auto;
			this.left = left;
			this.right = right;
		}
	}
	
	private void load(final XMLEventReader rdr) throws EOFException, XMLStreamException {
		event = skipTo(rdr, TAG_DICTIONARY);
		
		this.version = new DottedVersion(event.asStartElement().getAttributeByName(ATTR_VERSION).getValue());
		this.revision = Integer.valueOf(event.asStartElement().getAttributeByName(ATTR_REVISION).getValue());
		final List<TempGrammeme>	grammemes = new ArrayList<>();

		event = skipTo(rdr, TAG_GRAMMEMES);
		event = skipTo(rdr, TAG_GRAMMEME);
		while (event.isStartElement() && event.asStartElement().getName().equals(TAG_GRAMMEME)) {
			loadGrammeme(rdr, grammemes);
			event = next(rdr);
		}
		if (event.isEndElement() && event.asEndElement().getName().equals(TAG_GRAMMEMES)) {
			this.gramms = buildGrammemes(grammemes, "", null);
//			printGrammemes(System.err, "", 0, gramms);
		}
		else {
			throw new EOFException("Grammemes part not terminated correctry");
		}
		final List<TempRestriction>	restrictions = new ArrayList<>();

		event = skipTo(rdr, TAG_RESTRICTIONS);
		event = skipTo(rdr, TAG_RESTRICTION);
		while (event.isStartElement() && event.asStartElement().getName().equals(TAG_RESTRICTION)) {
			loadRestriction(rdr, restrictions);
			event = next(rdr);
		}
		if (event.isEndElement() && event.asEndElement().getName().equals(TAG_RESTRICTIONS)) {
			this.restr = buildRestriction(restrictions, this.gramms);
//			printRestrictions(System.err, restr);
		}
		else {
			throw new EOFException("Restrictions part not terminated correctry");
		}
		final Map<String, Grammeme>	map = new HashMap<>();

		walkGrammemes(gramms, (n)->map.put(n.getName(), n));
		final Grammeme[]	temp = new Grammeme[map.size()];
		
		event = skipTo(rdr, TAG_LEMMATA);
		event = skipTo(rdr, TAG_LEMMA);
		while (event.isStartElement() && event.asStartElement().getName().equals(TAG_LEMMA)) {
			loadLemma(rdr, map, temp, vocab, wordIndex);
			event = next(rdr);
		}
		if (!(event.isEndElement() && event.asEndElement().getName().equals(TAG_LEMMATA))) {
			throw new EOFException("Lemmata part not terminated correctry");
		}
		event = skipTo(rdr, TAG_LINK_TYPES);
		event = skipTo(rdr, TAG_TYPE);
		while (event.isStartElement() && event.asStartElement().getName().equals(TAG_TYPE)) {
			WordLinkType.from(Integer.valueOf(event.asStartElement().getAttributeByName(ATTR_ID).getValue())); 
			event = next(rdr);
			event = next(rdr);
		}
		final List<int[]>	links = new ArrayList<>();
		
		event = skipTo(rdr, TAG_LINKS);
		event = skipTo(rdr, TAG_LINK);
		while (event.isStartElement() && event.asStartElement().getName().equals(TAG_LINK)) {
			loadLink(rdr, links);
			event = next(rdr);
		}
		if (event.isEndElement() && event.asEndElement().getName().equals(TAG_LINKS)) {
			associateLinks(vocab, wordIndex, links.toArray(new int[links.size()][]));
			event = next(rdr);
			
			if (event.isEndElement() && event.asEndElement().getName().equals(TAG_DICTIONARY)) {
				event = next(rdr);
			}
		}
	}

	private void walkGrammemes(final Grammeme[] gramms, final Consumer<Grammeme> callback) {
		for (Grammeme item : gramms) {
			item.walk(callback);
		}
	}

	private XMLEvent loadGrammeme(final XMLEventReader rdr, final List<TempGrammeme> grammemes) throws EOFException, XMLStreamException {
		grammemes.add(new TempGrammeme(
			event.asStartElement().getAttributeByName(ATTR_PARENT).getValue(), 
			extractTag(rdr, TAG_NAME), 
			extractTag(rdr, TAG_ALIAS), 
			extractTag(rdr, TAG_DESCRIPTION))
		);
		return event;
	}

	private XMLEvent loadRestriction(final XMLEventReader rdr, final List<TempRestriction> restrictions) throws EOFException, XMLStreamException {
		restrictions.add(new TempRestriction(
			event.asStartElement().getAttributeByName(ATTR_TYPE).getValue(),
			event.asStartElement().getAttributeByName(ATTR_AUTO).getValue(),
			extractRestrictionItem(rdr, TAG_LEFT),
			extractRestrictionItem(rdr, TAG_RIGHT))
		);
		return event = next(rdr);
	}

	private TempRestrictionItem extractRestrictionItem(final XMLEventReader rdr, final QName tagName) throws EOFException, XMLStreamException {
		event = skipTo(rdr, tagName);
		final String	type = event.asStartElement().getAttributeByName(ATTR_TYPE).getValue(); 

		event = next(rdr);
		final String	value; 
		
		if (event.isCharacters()) {
			value = event.asCharacters().getData();
			event = next(rdr);
		}
		else {
			value = "";
		}
		return new TempRestrictionItem(type, value);
	}

	private int calculateGrammemes(final List<TempGrammeme> grammemes, final String parent) {
		int	count = 0;
		
		for (TempGrammeme item : grammemes) {
			if (item.parent.equals(parent)) {
				count++;
			}
		}
		return count;
	}	
	
	private Grammeme[] buildGrammemes(final List<TempGrammeme> grammemes, final String parent, final Supplier<Grammeme> parentRef) {
		final Grammeme[]	result = new Grammeme[calculateGrammemes(grammemes, parent)];
		int	count = 0;
		
		for(TempGrammeme current : grammemes) {
			if (current.parent.equals(parent)) {
				final int	temp = count;
				
				result[temp] = new Grammeme(parentRef, 
										current.name, 
										current.alias, 
										current.description, 
										buildGrammemes(grammemes, current.name, ()->call(result,temp)));
				count++;
			}
		}
		return result;
	}

	private Grammeme call(final Grammeme[] result, final int temp) {
		return result[temp];
	}

	private Restriction[] buildRestriction(final List<TempRestriction> restrictions, final Grammeme[] grams) {
		final Restriction[]	result = new Restriction[restrictions.size()];
		int	count = 0;
		
		for (TempRestriction item : restrictions) {
			result[count++] = new Restriction(
					WordRestrictionType.from(item.type), 
					"1".equals(item.auto), 
					WordForm.from(item.left.type), 
					item.left.value.isEmpty() ? null : seekGrammeme(grams, item.left.value), 
					WordForm.from(item.right.type),
					item.right.value.isEmpty() ? null : seekGrammeme(grams, item.right.value));
		}
		return result;
	}
	
	private Grammeme seekGrammeme(final Grammeme[] grams, final String value) {
		final Grammeme[] result = new Grammeme[1];
		
		walkGrammemes(grams, (n)->{
			if (n.getName().equals(value)) {
				result[0] = n;
			}
		});
		if (result[0] == null) {
			throw new IllegalArgumentException("Reference to grammeme ["+value+"] not found anywhere");
		}
		else {
			return result[0];
		}
	}

	private void loadLemma(final XMLEventReader rdr, final Map<String, Grammeme> grammemes, final Grammeme[] temp, final SyntaxTreeInterface<Word[]> vocab, final LongIdMap<Word> index) throws EOFException, XMLStreamException {
		final int		id = Integer.valueOf(event.asStartElement().getAttributeByName(ATTR_ID).getValue());
//		final int		rev = Integer.valueOf(event.asStartElement().getAttributeByName(ATTR_REV).getValue());
		
		event = skipTo(rdr, TAG_L);
		final String	lemma = event.asStartElement().getAttributeByName(ATTR_T).getValue();

		event = skipTo(rdr, TAG_G);
		final LangPart	part = LangPart.from(event.asStartElement().getAttributeByName(ATTR_V).getValue());
		
		event = next(rdr);
		event = next(rdr);
		int	count = 0;
		while (event.isStartElement() && event.asStartElement().getName().equals(TAG_G)) {
			final String	val = event.asStartElement().getAttributeByName(ATTR_V).getValue();
			
			if (grammemes.containsKey(val)) {
				temp[count++] = grammemes.get(val);
				event = next(rdr);
				event = next(rdr);
			}
			else {
				throw new XMLStreamException("Illegal grammeme ["+val+"] found");
			}
		}
		event = next(rdr);
		final Word	parent = new WordImpl(id, part, lemma, Arrays.copyOf(temp, count));

		appendWord(vocab, index, parent);
		index.put(parent.id(), parent);
		while (event.isStartElement() && event.asStartElement().getName().equals(TAG_F)) {
			final String	form = event.asStartElement().getAttributeByName(ATTR_T).getValue();
			
			event = next(rdr);
			count = 0;
			while (event.isStartElement() && event.asStartElement().getName().equals(TAG_G)) {
				temp[count++] = grammemes.get(event.asStartElement().getAttributeByName(ATTR_V).getValue());
				event = next(rdr);
				event = next(rdr);
			}
			final Word	child = new WordImpl(id, parent, part, form, Arrays.copyOf(temp, count));
			
			appendWord(vocab, index, child);
			event = next(rdr);
		}
	}
	
	private void appendWord(final SyntaxTreeInterface<Word[]> vocab, final LongIdMap<Word> index, final Word word) {
		final String	w = word.getWord();
		final long		id = vocab.seekName(w);
		
		if (id < 0) {
			vocab.placeName(w, new Word[] {word});
		}
		else {
			final Word[]	oldContent = vocab.getCargo(id);
			final Word[]	newContent = Arrays.copyOf(oldContent, oldContent.length + 1);
			
			newContent[newContent.length - 1] = word;
			vocab.setCargo(id, newContent);
		}
//		if (word.wordForm() == WordForm.LEMMA) {
//			index.put(word.id(), word);
//		}
	}

	private void loadLink(final XMLEventReader rdr, final List<int[]> links) throws EOFException, XMLStreamException {
		links.add(new int[] {
			Integer.valueOf(event.asStartElement().getAttributeByName(ATTR_ID).getValue()),	
			Integer.valueOf(event.asStartElement().getAttributeByName(ATTR_FROM).getValue()),	
			Integer.valueOf(event.asStartElement().getAttributeByName(ATTR_TO).getValue()),	
			Integer.valueOf(event.asStartElement().getAttributeByName(ATTR_TYPE).getValue()),	
		});
		event = next(rdr);
	}

	private void associateLinks(final SyntaxTreeInterface<Word[]> vocab, final LongIdMap<Word> wordIndex, final int[][] links) {
		final int[][]	temp = new int[2*links.length][];
		int		to;
		
		for(int index = 0, maxIndex = links.length; index < maxIndex; index++) {
			temp[2*index] = links[index];
			temp[2*index+1] = new int[]{links[index][0], links[index][2], links[index][1], -links[index][3]};
		}
		Arrays.sort(temp, (o1,o2)->o1[1]-o2[1]);
		
		for(int index = 0, maxIndex = links.length; index < maxIndex; index = to) {
			final int		from = index;
			final int		currentWord = temp[from][1];
			final WordImpl	wi = (WordImpl)wordIndex.get(currentWord);
			
			for(to = from; to < maxIndex; to++) {
				if (temp[to][1] != currentWord) {
					break;
				}
			}
			final LinkDescriptor[]	desc = new LinkDescriptor[to-from];
			
			for(int linkIndex = from; linkIndex < to; linkIndex++) {
				desc[linkIndex-from] = new LinkDescriptor(
									WordLinkType.from(Math.abs(temp[linkIndex][3])), 
									temp[linkIndex][3] < 0, 
									wordIndex.get(temp[linkIndex][2]));
			}
			
			wi.setLinks(new LinkImpl(wi, desc));
		}
	}
	
	private String extractTag(final XMLEventReader rdr, final QName tagName) throws EOFException, XMLStreamException {
		event = skipTo(rdr, tagName);
		event = next(rdr);
		if (event.isCharacters()) {
			final String	result = event.asCharacters().getData();

			event = next(rdr);
			if (event.isEndElement() && event.asEndElement().getName().equals(tagName)) {
				event = next(rdr);
				return result;
			}
			else {
				throw new EOFException();
			}
		}
		else {
			throw new EOFException();
		}
	}

	private XMLEvent skipTo(XMLEventReader rdr, QName tagName) throws EOFException, XMLStreamException {
		for(;;) {
			if (event.isStartElement() && event.asStartElement().getName().equals(tagName)) {
				return event;
			}
			else {
				event = next(rdr);
			}
		}
	}
	
	private XMLEvent next(final XMLEventReader reader) throws XMLStreamException, EOFException {
		while (reader.hasNext()) {
			final XMLEvent	event = reader.nextEvent();

			if (event.isCharacters() && event.asCharacters().getData().trim().isEmpty()) {
				continue;
			}
			else {
				return event;
			}
		}
		throw new EOFException();
	}
	
	private static int printGrammemes(final PrintStream ps, final String prefix, int count, final Grammeme... grams) {
		for(Grammeme item : grams) {
			ps.println(String.format("%1$3d:%2$s %3$s (%4$s)", count, prefix, item.getName(), item.getDescription()));
			count = printGrammemes(ps, prefix+"    ", ++count, item.getChildren());
		}
		return count;
	}

	private static void printRestrictions(final PrintStream ps, final Restriction... restr) {
		int	count = 0;
		
		for(Restriction item : restr) {
			ps.println(String.format("%1$3d: %2$s (%3$s/%4$s -> %5$s/%6$s)", count, item.type, item.leftForm, item.leftGram, item.rightForm, item.rightGram));
			count++;
		}
	}
}
