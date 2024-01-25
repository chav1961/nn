package chav1961.nn.standalone.factories;

import java.net.URI;
import java.util.Arrays;

import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.XTenzor;
import chav1961.nn.api.interfaces.factories.TenzorFactory;
import chav1961.nn.core.util.CoreUtils;
import chav1961.nn.standalone.tenzors.StandaloneTenzor;
import chav1961.nn.standalone.tenzors.StandaloneXTenzor;
import chav1961.nn.standalone.util.Constants;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.exceptions.EnvironmentException;

public class StandaloneTenzorFactory implements TenzorFactory {
	private static final URI	URI_TEMPLATE = URI.create(TENZOR_FACTORY_SCHEMA+':'+Constants.SCHEMA_SUFFIX+":/");

	@Override
	public boolean canServe(final URI resource) throws NullPointerException {
		if (resource == null) {
			throw new NullPointerException("Resource URI can't be null");
		}
		else {
			return URIUtils.canServeURI(resource, URI_TEMPLATE);
		}
	}

	@Override
	public TenzorFactory newInstance(final URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
		if (resource == null) {
			throw new NullPointerException("Resource URI can't be null");
		}
		else if (!canServe(resource)) {
			throw new IllegalArgumentException("Resource URI ["+resource+"] can't eb served by this factory");
		}
		else {
			return this;
		}
	}

	@Override
	public boolean isXTenzorSupported() {
		return true;
	}

	@Override
	public URI getDefaultTenzorType() {
		return URI_TEMPLATE;
	}

	@Override
	public Tenzor newInstance(final int size, final int... advanced) {
		if (advanced == null) {
			throw new NullPointerException("Advanced can't be null");
		}
		else if (size <= 0) {
			throw new IllegalArgumentException("Size ["+size+"] must be greater than 0");
		}
		else if (!CoreUtils.areSizesValid(advanced)) {
			throw new IllegalArgumentException("Advanced list "+Arrays.toString(advanced)+" contains zero or negative values");
		}
		else {
			return new StandaloneTenzor(CoreUtils.joinArray(size, advanced));
		}
	}

	@Override
	public Tenzor newInstance(final float[] content, final int size, final int... advanced) {
		if (content == null) {
			throw new NullPointerException("Content can't be null");
		}
		else {
			final Tenzor	result = newInstance(size, advanced);
			
			result.fill(size, advanced);
			return result;
		}
	}

	@Override
	public XTenzor newInstanceX(final int size, final int... advanced) {
		if (advanced == null) {
			throw new NullPointerException("Advanced can't be null");
		}
		else if (size <= 0) {
			throw new IllegalArgumentException("Size ["+size+"] must be greater than 0");
		}
		else if (!CoreUtils.areSizesValid(advanced)) {
			throw new IllegalArgumentException("Advanced list "+Arrays.toString(advanced)+" contains zero or negative values");
		}
		else {
			return new StandaloneXTenzor(CoreUtils.joinArray(size, advanced));
		}
	}

	@Override
	public XTenzor newInstanceX(final double[] content, final int size, final int... advanced) {
		if (content == null) {
			throw new NullPointerException("Content can't be null");
		}
		else {
			final XTenzor	result = newInstanceX(size, advanced);
			
			result.fill(size, advanced);
			return result;
		}
	}
}
