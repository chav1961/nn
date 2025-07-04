package chav1961.nn.vocab.interfaces;

import chav1961.purelib.basic.Utils;

public enum LangPart {
    NOUN("СУЩ"),
    ADJF("ПРИЛ"),
    ADJS("КР_ПРИЛ"),
    COMP("КОМП"),
    VERB("ГЛ"),
    INFN("ИНФ"),
    PRTF("ПРИЧ"),
    PRTS("КР_ПРИЧ"),
    GRND("ДЕЕПР"),
    NUMR("ЧИСЛ"),
    ADVB("Н"),
    NPRO("МС"),
    PRED("ПРЕДК"),
    PREP("ПР"),
    CONJ("СОЮЗ"),
    PRCL("ЧАСТ"),
    INTJ("МЕЖД"),
    PUNCT("ПУНКТ"),
    UNKNOWN("НЕИЗВ");
	
	private final String	alias;
	
	private LangPart(final String alias) {
		this.alias = alias;
	}
	
	public String getAlias() {
		return alias;
	}
	
	public static LangPart from(final String value) {
		if (Utils.checkEmptyOrNullString(value)) {
			throw new IllegalArgumentException("Value must be neither null nor empty");
		}
		else {
			for (LangPart item : values()) {
				if (item.name().equalsIgnoreCase(value)) {
					return item;
				}
			}
			throw new IllegalArgumentException("Ilegal value ["+value+"] for LangPart");
		}
	}
	
}
