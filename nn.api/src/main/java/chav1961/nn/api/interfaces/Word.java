package chav1961.nn.api.interfaces;

import chav1961.nn.api.Grammeme;

public interface Word extends Iterable<Grammeme>{
	int seqId();
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
