package chav1961.nn.vocab.interfaces;

import chav1961.purelib.basic.Utils;

public enum WordRestrictionType {
	OBLIGATORY,
	MAYBE,
	FORBIDDEN;
	
	public static WordRestrictionType from(final String value) {
		if (Utils.checkEmptyOrNullString(value)) {
			throw new IllegalArgumentException("Value must be neither null nor empty");
		}
		else {
			for (WordRestrictionType item : values()) {
				if (item.name().equalsIgnoreCase(value)) {
					return item;
				}
			}
			throw new IllegalArgumentException("Ilegal value ["+value+"] for WordRestrictionType");
		}
	}
}
