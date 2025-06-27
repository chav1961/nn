package chav1961.nn.vocab.interfaces;

public interface Word extends Iterable<String>{
	int id();
	String getWord();
	int getWord(char[] where, int from);
	Word getLemma();
	WordLink getLinks();
	WordForm wordForm();
	LangPart part();
	boolean hasAttribute(String attr);
	int numberOfAttributes();
}
