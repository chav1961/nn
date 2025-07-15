package chav1961.nn.vocab.loaders;

import chav1961.nn.api.interfaces.Word;
import chav1961.nn.api.interfaces.WordLink;
import chav1961.nn.api.interfaces.WordLinkType;

class LinkImpl implements WordLink {
	private final Word				current;
	private final LinkDescriptor[]	links;
	
	LinkImpl(Word current, LinkDescriptor... links) {
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