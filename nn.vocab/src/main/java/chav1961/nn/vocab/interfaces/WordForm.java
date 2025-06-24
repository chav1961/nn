package chav1961.nn.vocab.interfaces;

import chav1961.purelib.basic.Utils;

public enum WordForm {
	LEMMA,
	FORM;
	
	public static WordForm from(final String value) {
		if (Utils.checkEmptyOrNullString(value)) {
			throw new IllegalArgumentException("Value must be neither null nor empty");
		}
		else {
			for (WordForm item : values()) {
				if (item.name().equalsIgnoreCase(value)) {
					return item;
				}
			}
			throw new IllegalArgumentException("Ilegal value ["+value+"] for WordForm");
		}
	}
}
