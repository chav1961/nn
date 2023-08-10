package chav1961.nn.standalone;


import java.net.URI;

import chav1961.nn.core.AbstractNeuralNetwork;
import chav1961.nn.core.interfaces.Trainer;
import chav1961.purelib.basic.URIUtils;

abstract class StandaloneNeuralNetwork<Tr extends Trainer> extends AbstractNeuralNetwork<Tr> {
	private static final long serialVersionUID = -1188484880395191026L;

	public static final String	STANDALONE_SCHEME = "standalone";
	private static final URI	STANDALONE_URL = URI.create(NEURAL_NETWORK_SCHEME+":"+STANDALONE_SCHEME+":/");
	
	StandaloneNeuralNetwork() {
	}

	@Override
	public boolean canServe(final URI resource) throws NullPointerException { 
		if (resource == null) {
			throw new NullPointerException("Resource URI can't be null");
		}
		else {
			return URIUtils.canServeURI(resource, STANDALONE_URL) && resource.toString().contains(":"+getNetworkType().getSubscheme()+":");
		}
	}
}
