package chav1961.nn.api.interfaces;

public interface WordLink {
	boolean hasLinkFrom(Word another);
	boolean hasLinkTo(Word another);
	default boolean hasLink(Word another) {
		return hasLinkFrom(another) || hasLinkTo(another); 
	}
	
	boolean hasLinkFrom(WordLinkType type);
	boolean hasLinkTo(WordLinkType type);
	default boolean hasLink(WordLinkType type) {
		return hasLinkFrom(type) || hasLinkTo(type); 
	}
	
	boolean hasLinkFrom(WordLinkType type, Word another);
	boolean hasLinkTo(WordLinkType type, Word another);
	default boolean hasLink(WordLinkType type, Word another) {
		return hasLinkFrom(type, another) || hasLinkTo(type, another); 
	}
	
	Word getLinkFrom(WordLinkType type);
	Word getLinkTo(WordLinkType type);
	default Word getLink(WordLinkType type) {
		return hasLinkFrom(type) ? getLinkFrom(type) : getLinkTo(type);
	}
	
	Word[] getLinksFrom(WordLinkType type);
	Word[] getLinksTo(WordLinkType type);
}
