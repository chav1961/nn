package chav1961.nn.core.dataset;

import java.io.File;
import java.util.function.BiConsumer;

import chav1961.nn.api.interfaces.Tenzor;

public class DataSetManager {
	private DataSetManager() {
		
	}
	
	public DataSetManager[] split(final float percentage) {
		return null;
	}
	
	public void forEach(final Tenzor inputTemplate, final Tenzor outputTemplate, final BiConsumer<Tenzor, Tenzor> callback) {
		
	}
	
	
	public static DataSetManager fromCsv(final File... csv) {
		return null;
	}

	public static DataSetManager fromImages(final File... images) {
		return null;
	}
}
