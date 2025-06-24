package chav1961.nn.vocab.loaders;


import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import chav1961.nn.vocab.interfaces.LangPart;
import chav1961.nn.vocab.interfaces.Word;
import chav1961.nn.vocab.interfaces.WordForm;
import chav1961.nn.vocab.interfaces.WordLink;
import chav1961.nn.vocab.interfaces.WordLinkType;
import chav1961.nn.vocab.interfaces.WordRestrictionType;
import chav1961.purelib.basic.AndOrTree;
import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.LongIdMap;
import chav1961.purelib.basic.Utils;
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
	private static final QName		ATTR_REV = QName.valueOf("rev");
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

	public SyntaxTreeInterface<Word[]> getVocab() {
		return vocab;
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
	
	private static class Grammeme {
		private final Grammeme		parent;
		private final String		name;
		private final String		alias;
		private final String		description;
		private final Grammeme[]	children;
		
		private Grammeme(final Grammeme parent, final String name, final String alias, final String description, final Grammeme... children) {
			this.parent = parent;
			this.name = name;
			this.alias = alias;
			this.description = description;
			this.children = children;
		}

		@Override
		public String toString() {
			if (parent != null) {
				return "Grammeme [parent=" + parent.name + ", name=" + name + ", alias=" + alias + ", description=" + description + "]";
			}
			else {
				return "Grammeme [name=" + name + ", alias=" + alias + ", description=" + description + ", children=" + Arrays.toString(children) + "]";
			}
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
	
	private static class Restriction {
		private final WordRestrictionType	type;
		private final boolean	auto;
		private final WordForm	leftForm;
		private final Grammeme	leftGram;
		private final WordForm	rightForm;
		private final Grammeme	rightGram;
		
		private Restriction(WordRestrictionType type, boolean auto, WordForm leftForm, Grammeme leftGram, WordForm rightForm, Grammeme rightGram) {
			this.type = type;
			this.auto = auto;
			this.leftForm = leftForm;
			this.leftGram = leftGram;
			this.rightForm = rightForm;
			this.rightGram = rightGram;
		}
	}
	
	private static class WordImpl implements Word {
		private final int			id;
		private final WordForm		form;
		private final LangPart		part;
		private final String		word;
		private final Word			lemma;
		private final Grammeme[]	attrs;
		private volatile WordLink	link = null;

		private WordImpl(final int id, final LangPart part, final String word, final Grammeme... attrs) {
			this.id = id;
			this.form = WordForm.LEMMA;
			this.part = part;
			this.word = word;
			this.lemma = null;
			this.attrs = attrs;
		}
		
		private WordImpl(final int id, final Word lemma, final LangPart part, final String word, final Grammeme... attrs) {
			this.id = id;
			this.form = WordForm.FORM;
			this.part = part;
			this.word = word;
			this.lemma = lemma;
			this.attrs = attrs;
		}

		@Override
		public Iterator<String> iterator() {
			final List<String>	result = new ArrayList<>();
			
			for (Grammeme item : attrs) {
				for (Grammeme child : item.children) {
					result.add(item.name+"."+child.name);
				}
			}
			return result.iterator();
		}

		@Override
		public int id() {
			return id;
		}
		
		@Override
		public String getWord() {
			return word;
		}

		@Override
		public int getWord(final char[] where, final int from) {
			if (where == null || where.length == 0) {
				throw new IllegalArgumentException("Array to store word must be neither null nor empty");
			}
			else if (from < 0 || from >= where.length) {
				throw new IllegalArgumentException("From value ["+from+"] out of range 0.."+(where.length-1));
			}
			else {
				final int	len = Math.min(word.length(), where.length-from);
				
				word.getChars(0, len, where, from);
				return len;
			}
		}

		@Override
		public Word getLemma() {
			if (form == WordForm.LEMMA) {
				throw new IllegalStateException("Lemma can't have parent lemma");
			}
			else {
				return lemma;
			}
		}

		@Override
		public WordLink getLinks() {
			return link;
		}
		
		@Override
		public WordForm wordForm() {
			return form;
		}

		@Override
		public LangPart part() {
			return part;
		}

		@Override
		public boolean hasAttribute(final String attr) {
			if (Utils.checkEmptyOrNullString(attr)) {
				throw new NullPointerException("Attribuate name must be neither null nor empty");
			}
			else {
				for (Grammeme item : attrs) {
					if (item.name.equals(attr)) {
						return true;
					}
					else {
						for (Grammeme child : item.children) {
							if (child.name.equals(attr)) {
								return true;
							}
						}
					}
				}
				return false;
			}
		}

		@Override
		public String toString() {
			return "WordImpl [id=" + id + ", form=" + form + ", part=" + part + ", word=" + word + ", attrs="
					+ Arrays.toString(attrs) + "]";
		}

		private void setLinks(final WordLink link) {
			this.link = link;
		}
	}

	private static class LinkDescriptor {
		final WordLinkType	type;
		final boolean		left;
		final Word			word;
		
		private LinkDescriptor(final WordLinkType type, final boolean left, final Word word) {
			this.type = type;
			this.left = left;
			this.word = word;
		}
	}
	
	private static class LinkImpl implements WordLink {
		private final Word				current;
		private final LinkDescriptor[]	links;
		
		private LinkImpl(Word current, LinkDescriptor... links) {
			this.current = current;
			this.links = links;
		}

		@Override
		public boolean hasLinkFrom(final Word another) {
			if (another == null) {
				throw new NullPointerException("Word to test can't be null");
			}
			else {
				for (LinkDescriptor item : links) {
					if (!item.left && item.word == another) {
						return true;
					}
				}
				return false;
			}
		}

		@Override
		public boolean hasLinkTo(final Word another) {
			if (another == null) {
				throw new NullPointerException("Word to test can't be null");
			}
			else {
				for (LinkDescriptor item : links) {
					if (item.left && item.word == another) {
						return true;
					}
				}
				return false;
			}
		}

		@Override
		public boolean hasLinkFrom(final WordLinkType type) {
			if (type == null) {
				throw new NullPointerException("Link type to test can't be null");
			}
			else {
				for (LinkDescriptor item : links) {
					if (!item.left && item.type == type) {
						return true;
					}
				}
				return false;
			}
		}

		@Override
		public boolean hasLinkTo(final WordLinkType type) {
			if (type == null) {
				throw new NullPointerException("Link type to test can't be null");
			}
			else {
				for (LinkDescriptor item : links) {
					if (item.left && item.type == type) {
						return true;
					}
				}
				return false;
			}
		}

		@Override
		public boolean hasLinkFrom(final WordLinkType type, final Word another) {
			if (type == null) {
				throw new NullPointerException("Link type to test can't be null");
			}
			else if (another == null) {
				throw new NullPointerException("Word to test can't be null");
			}
			else {
				for (LinkDescriptor item : links) {
					if (!item.left && item.word == another && item.type == type) {
						return true;
					}
				}
				return false;
			}
		}

		@Override
		public boolean hasLinkTo(final WordLinkType type, final Word another) {
			if (type == null) {
				throw new NullPointerException("Link type to test can't be null");
			}
			else if (another == null) {
				throw new NullPointerException("Word to test can't be null");
			}
			else {
				for (LinkDescriptor item : links) {
					if (item.left && item.word == another && item.type == type) {
						return true;
					}
				}
				return false;
			}
		}

		@Override
		public Word getLinkFrom(final WordLinkType type) {
			if (type == null) {
				throw new NullPointerException("Link type to test can't be null");
			}
			else {
				for (LinkDescriptor item : links) {
					if (!item.left && item.type == type) {
						return item.word;
					}
				}
				throw new IllegalArgumentException("Word link type ["+type+"] not found anywhere");
			}
		}

		@Override
		public Word getLinkTo(final WordLinkType type) {
			if (type == null) {
				throw new NullPointerException("Link type to test can't be null");
			}
			else {
				for (LinkDescriptor item : links) {
					if (item.left && item.type == type) {
						return item.word;
					}
				}
				throw new IllegalArgumentException("Word link type ["+type+"] not found anywhere");
			}
		}

		@Override
		public Word[] getLinksFrom(final WordLinkType type) {
			if (type == null) {
				throw new NullPointerException("Link type to test can't be null");
			}
			else {
				int	count = 0;
				
				for (LinkDescriptor item : links) {
					if (!item.left && item.type == type) {
						count++;
					}
				}
				if (count == 0) {
					throw new IllegalArgumentException("Word link type ["+type+"] not found anywhere");
				}
				else {
					final Word[]	result = new Word[count];
					
					count = 0;
					for (LinkDescriptor item : links) {
						if (!item.left && item.type == type) {
							result[count++] = item.word;
						}
					}
					return result;
				}
			}
		}

		@Override
		public Word[] getLinksTo(final WordLinkType type) {
			if (type == null) {
				throw new NullPointerException("Link type to test can't be null");
			}
			else {
				int	count = 0;
				
				for (LinkDescriptor item : links) {
					if (item.left && item.type == type) {
						count++;
					}
				}
				if (count == 0) {
					throw new IllegalArgumentException("Word link type ["+type+"] not found anywhere");
				}
				else {
					final Word[]	result = new Word[count];
					
					count = 0;
					for (LinkDescriptor item : links) {
						if (item.left && item.type == type) {
							result[count++] = item.word;
						}
					}
					return result;
				}
			}
		}
	}
	
	private void load(final XMLEventReader rdr) throws EOFException, XMLStreamException {
		event = skipTo(rdr, TAG_DICTIONARY);
		
		this.version = new DottedVersion(event.asStartElement().getAttributeByName(ATTR_VERSION).getValue());
		this.revision = Integer.valueOf(event.asStartElement().getAttributeByName(ATTR_REVISION).getValue());
		
		event = skipTo(rdr, TAG_GRAMMEMES);
		final List<TempGrammeme>	grammemes = new ArrayList<>();
		
		event = skipTo(rdr, TAG_GRAMMEME);
		while (event.isStartElement() && event.asStartElement().getName().equals(TAG_GRAMMEME)) {
			loadGrammeme(rdr, grammemes);
			event = next(rdr);
		}
		if (event.isEndElement() && event.asEndElement().getName().equals(TAG_GRAMMEMES)) {
			this.gramms = buildGrammemes(grammemes);
		}

		event = skipTo(rdr, TAG_RESTRICTIONS);
		final List<TempRestriction>	restrictions = new ArrayList<>();
		
		event = skipTo(rdr, TAG_RESTRICTION);
		while (event.isStartElement() && event.asStartElement().getName().equals(TAG_RESTRICTION)) {
			loadRestriction(rdr, restrictions);
			event = next(rdr);
		}
		if (event.isEndElement() && event.asEndElement().getName().equals(TAG_RESTRICTIONS)) {
			this.restr = buildRestriction(restrictions, this.gramms);
		}
		final Map<String, Grammeme>	map = new HashMap<>();

		for(Grammeme item : gramms) {
			map.put(item.name, item);
			for(Grammeme child : item.children) {
				map.put(child.name, child);
			}
		}
		final Grammeme[]	temp = new Grammeme[map.size()];
		
		event = skipTo(rdr, TAG_LEMMATA);
		event = skipTo(rdr, TAG_LEMMA);
		while (event.isStartElement() && event.asStartElement().getName().equals(TAG_LEMMA)) {
			loadLemma(rdr, map, temp, vocab, wordIndex);
			event = next(rdr);
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
		return event;
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

	private Grammeme[] buildGrammemes(final List<TempGrammeme> grammemes) {
		final Map<String, Grammeme[]>	top = new HashMap<>();
		int		topCount = 0;
		
		for(TempGrammeme current : grammemes) {
			if (current.parent.isEmpty()) {
				int	count = 0;
				
				for(TempGrammeme child : grammemes) {
					if (child.parent.equals(current.name)) {
						count++;
					}
				}
				top.put(current.name, new Grammeme[count]);
				topCount++;
			}
		}
		final Grammeme[]	result = new Grammeme[topCount];
		
		topCount = 0;
		for(TempGrammeme current : grammemes) {
			if (current.parent.isEmpty()) {
				final Grammeme	parent = result[topCount++] = new Grammeme(null, current.name, current.alias, current.description, top.get(current.name));
				int	count = 0;
				
				for(TempGrammeme child : grammemes) {
					if (child.parent.equals(current.name)) {
						parent.children[count++] = new Grammeme(parent, child.name, child.alias, child.description);
					}
				}
			}
		}
		return result;
	}

	private Restriction[] buildRestriction(final List<TempRestriction> restrictions, final Grammeme[] grams) {
		final Restriction[]	result = new Restriction[restrictions.size()];
		int	count = 0;
		
		for (TempRestriction item : restrictions) {
			result[count++] = new Restriction(
					WordRestrictionType.from(item.type), 
					"1".equals(item.auto), 
					WordForm.from(item.left.type), 
					seekGrammeme(grams, item.left.value), 
					WordForm.from(item.right.type),
					seekGrammeme(grams, item.right.value));
		}
		return result;
	}
	
	private Grammeme seekGrammeme(final Grammeme[] grams, final String value) {
		for (Grammeme item : grams) {
			if (item.name.equals(value)) {
				return item;
			}
			else {
				for(Grammeme child : item.children) {
					if (child.name.equals(value)) {
						return child;
					}
				}
			}
		}
		throw new IllegalArgumentException("Reference to grammeme ["+value+"] not found anywhere");
	}

	private void loadLemma(final XMLEventReader rdr, final Map<String, Grammeme> grammemes, final Grammeme[] temp, final SyntaxTreeInterface<Word[]> vocab, final LongIdMap<Word> index) throws EOFException, XMLStreamException {
		final int		id = Integer.valueOf(event.asStartElement().getAttributeByName(ATTR_ID).getValue());
		final int		rev = Integer.valueOf(event.asStartElement().getAttributeByName(ATTR_REV).getValue());
		
		event = skipTo(rdr, TAG_L);
		final String	lemma = event.asStartElement().getAttributeByName(ATTR_T).getValue();

		event = skipTo(rdr, TAG_G);
		final LangPart	part = LangPart.from(event.asStartElement().getAttributeByName(ATTR_V).getValue());
		
		event = next(rdr);
		event = next(rdr);
		int	count = 0;
		while (event.isStartElement() && event.asStartElement().getName().equals(TAG_G)) {
			temp[count++] = grammemes.get(event.asStartElement().getAttributeByName(ATTR_V).getValue());
			event = next(rdr);
			event = next(rdr);
		}
		event = next(rdr);
		final Word	parent = new WordImpl(id, part, lemma, Arrays.copyOf(temp, count));

		appendWord(vocab, index, parent);
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
		final long	id = vocab.seekName(word.getWord());
		
		if (id < 0) {
			vocab.placeName(word.getWord(), new Word[] {word});
		}
		else {
			final Word[]	oldContent = vocab.getCargo(id);
			final Word[]	newContent = Arrays.copyOf(oldContent, oldContent.length+1);
			
			newContent[newContent.length - 1] = word;
			vocab.setCargo(id, newContent);
		}
		if (word.wordForm() == WordForm.LEMMA) {
			index.put(word.id(), word);
		}
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
}
