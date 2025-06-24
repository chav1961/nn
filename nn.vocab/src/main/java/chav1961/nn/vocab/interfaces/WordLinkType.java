package chav1961.nn.vocab.interfaces;

import chav1961.purelib.basic.Utils;

public enum WordLinkType {
	ADJF_2_ADJS(1,"ADJF","ADJS"),
	ADJF_2_COMP(2,"ADJF","COMP"),
	INFN_2_VERB(3,"INFN","VERB"),
	INFN_2_PRTF(4,"INFN","PRTF"),
	INFN_2_GRND(5,"INFN","GRND"),
	PRTF_2_PRTS(6,"PRTF","PRTS"),
	NAME_2_PATR(7,"NAME","PATR"),
	PATR_MASC_2_PATR_FEMN(8,"PATR_MASC","PATR_FEMN"),
	SURN_MASC_2_SURN_FEMN(9,"SURN_MASC","SURN_FEMN"),
	SURN_MASC_2_SURN_PLUR(10,"SURN_MASC","SURN_PLUR"),
	PERF_2_IMPF(11,"PERF","IMPF"),
	ADJF_2_SUPR_ejsh(12,"ADJF","SUPR_ejsh"),
	PATR_MASC_FORM_2_PATR_MASC_INFR(13,"PATR_MASC_FORM","PATR_MASC_INFR"),
	PATR_FEMN_FORM_2_PATR_FEMN_INFR(14,"PATR_FEMN_FORM","PATR_FEMN_INFR"),
	ADJF_eish_2_SUPR_nai_eish(15,"ADJF_eish","SUPR_nai_eish"),
	ADJF_2_SUPR_ajsh(16,"ADJF","SUPR_ajsh"),
	ADJF_aish_2_SUPR_nai_aish(17,"ADJF_aish","SUPR_nai_aish"),
	ADJF_2_SUPR_suppl(18,"ADJF","SUPR_suppl"),
	ADJF_2_SUPR_nai(19,"ADJF","SUPR_nai"),
	ADJF_2_SUPR_slng(20,"ADJF","SUPR_slng"),
	FULL_2_CONTRACTED(21,"FULL","CONTRACTED"),
	NORM_2_ORPHOVAR(22,"NORM","ORPHOVAR"),
	CARDINAL_2_ORDINAL(23,"CARDINAL","ORDINAL"),
	SBST_MASC_2_SBST_FEMN(24,"SBST_MASC","SBST_FEMN"),
	SBST_MASC_2_SBST_PLUR(25,"SBST_MASC","SBST_PLUR"),
	ADVB_2_COMP(26,"ADVB","COMP"),
	ADJF_TEXT_2_ADJF_NUMBER(27,"ADJF_TEXT","ADJF_NUMBER");
	
	private final int		id;
	private final String	left;
	private final String	right;
	
	private WordLinkType(final int id, final String left, final String right) {
		this.id = id;
		this.left = left;
		this.right = right;
	}
	
	public int getId() {
		return id;
	}
	
	public String getLeft() {
		return left;
	}
	
	public String getRight() {
		return right;
	}
	
	public static WordLinkType from(final int id) {
		if (id >= 1 && id <= values().length) {
			return values()[id-1];
		}
		else {
			throw new IllegalArgumentException("Id ["+id+"] not found in the WordLinkType");
		}
	}
}
