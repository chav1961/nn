package chav1961.nn.vocab;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import chav1961.nn.vocab.loaders.OpCorporaLoader;
import chav1961.purelib.basic.exceptions.SyntaxException;

public class Application {
	public static void main(final String[] args) throws IOException, SyntaxException {
		try(final Reader			rdr = new InputStreamReader(System.in);
			final DataOutputStream	dos = new DataOutputStream(System.out)) {

			System.err.println("Conversion started, please wait...");
			final long	start = System.currentTimeMillis();
			final OpCorporaLoader	ldr = new OpCorporaLoader(rdr);
			
			ldr.store(dos);
			dos.flush();
			System.err.println("Conversion completed, "+ldr.getVocab().size()
					+" items processed, duration="+(System.currentTimeMillis()-start)+" msec");
		}
	}
}
