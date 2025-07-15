package chav1961.nn.vocab.loaders;

import chav1961.nn.api.interfaces.Word;
import chav1961.nn.api.interfaces.WordLinkType;

class LinkDescriptor {
	final WordLinkType	type;
	final boolean		left;
	final Word			word;
	
	LinkDescriptor(final WordLinkType type, final boolean left, final Word word) {
		this.type = type;
		this.left = left;
		this.word = word;
	}
}