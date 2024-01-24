package chav1961.nn.core.dataset;


import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.imageio.ImageIO;

import chav1961.nn.api.interfaces.AnyTenzor;
import chav1961.nn.api.interfaces.Tenzor;
import chav1961.nn.api.interfaces.XTenzor;
import chav1961.nn.api.interfaces.factories.TenzorFactory;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.Utils;

public class DataSetManager {
	private final AnyTenzor[]	inputs;
	private final AnyTenzor[]	outputs;
	private final boolean		xSupported;
	
	protected DataSetManager(final AnyTenzor[] inputs, final AnyTenzor[] outputs, final boolean xSupported) {
		this.inputs = inputs;
		this.outputs = outputs;
		this.xSupported = xSupported;
	}

	public int size() {
		return inputs.length;
	}
	
	public boolean isXSupported() {
		return xSupported;
	}

	public void forEach(final BiConsumer<Tenzor, Tenzor> callback) {
		if (callback == null) {
			throw new NullPointerException("Callback can't be null"); 
		}
		else if (isXSupported()) {
			throw new IllegalStateException("This dataset containx XTenzor content, not Tenzor. Call forEachX(...) method instead"); 
		}
		else {
			for(int index = 0; index < inputs.length; index++) {
				callback.accept((Tenzor)inputs[index], (Tenzor)outputs[index]);
			}
		}
 	}

	public void forEachX(final BiConsumer<XTenzor, XTenzor> callback) {
		if (callback == null) {
			throw new NullPointerException("Callback can't be null"); 
		}
		else if (!isXSupported()) {
			throw new IllegalStateException("This dataset contains Tenzor content, not XTenzor. Call forEach(...) method instead"); 
		}
		else {
			for(int index = 0; index < inputs.length; index++) {
				callback.accept((XTenzor)inputs[index], (XTenzor)outputs[index]);
			}
		}
 	}
	
	public DataSetManager[] split(final float percentage) {
		if (percentage <= 0 || percentage >= 1) {
			throw new IllegalArgumentException("Illegal percentage value ["+percentage+"] : must be in 0..1 range (exclusive)"); 
		}
		else {
			final List<AnyTenzor>	firstInput = new ArrayList<>();
			final List<AnyTenzor>	firstOutput = new ArrayList<>();
			final List<AnyTenzor>	secondInput = new ArrayList<>();
			final List<AnyTenzor>	secondOutput = new ArrayList<>();
			
			for (int index = 0; index < inputs.length; index++) {
				if (Math.random() < percentage) {
					firstInput.add(inputs[index]);
					firstOutput.add(outputs[index]);
				}
				else {
					secondInput.add(inputs[index]);
					secondOutput.add(outputs[index]);
				}
			}
			return new DataSetManager[] {
				new DataSetManager(firstInput.toArray(new AnyTenzor[firstInput.size()]), firstOutput.toArray(new AnyTenzor[firstOutput.size()]), isXSupported()),
				new DataSetManager(secondInput.toArray(new AnyTenzor[secondInput.size()]), secondOutput.toArray(new AnyTenzor[secondOutput.size()]), isXSupported())
			};
		}
	}

	public static DataSetManager fromCsv(final int numberOfInputs, final boolean hasCaptions, final boolean ignoreCaptions, final boolean useXTenzors, final TenzorFactory factory, final File csv) throws IOException {
		if (numberOfInputs <= 0) {
			throw new IllegalArgumentException("Number of inputs must be greater than 0");
		}
		else if (factory == null) {
			throw new NullPointerException("Tenzor factory can't be null");
		}
		else if (csv == null) {
			throw new NullPointerException("CSV file can't be null");
		}
		else if (!csv.exists() || !csv.isFile() || !csv.canRead()) {
			throw new IllegalArgumentException("CSV file ["+csv.getAbsolutePath()+"] not exists, is not a file or can't be read for you");
		}
		else {
			try(final InputStream	is = new FileInputStream(csv)) {
				return fromCsv(numberOfInputs, hasCaptions, ignoreCaptions, useXTenzors, factory, is);
			}
		}
	}

	public static DataSetManager fromCsv(final int numberOfInputs, final boolean hasCaptions, final boolean ignoreCaptions, final boolean useXTenzors, final TenzorFactory factory, final InputStream csv) throws IOException {
		if (numberOfInputs <= 0) {
			throw new IllegalArgumentException("Number of inputs must be greater than 0");
		}
		else if (factory == null) {
			throw new NullPointerException("Tenzor factory can't be null");
		}
		else if (csv == null) {
			throw new NullPointerException("CSV stream can't be null");
		}
		else {
			return fromCsv(numberOfInputs, hasCaptions, ignoreCaptions, useXTenzors, factory, new InputStreamReader(csv, PureLibSettings.DEFAULT_CONTENT_ENCODING));
		}
	}

	public static DataSetManager fromCsv(final int numberOfInputs, final boolean hasCaptions, final boolean ignoreCaptions, final boolean useXTenzors, final TenzorFactory factory, final Reader csv) throws IOException {
		if (numberOfInputs <= 0) {
			throw new IllegalArgumentException("Number of inputs must be greater than 0");
		}
		else if (factory == null) {
			throw new NullPointerException("Tenzor factory can't be null");
		}
		else if (csv == null) {
			throw new NullPointerException("CSV stream can't be null");
		}
		else {
			return fromCsv( hasCaptions, ignoreCaptions, (in)->convert(in, numberOfInputs, factory, useXTenzors), csv);
		}
	}

	public static DataSetManager fromCsv(final boolean hasCaptions, final boolean ignoreCaptions, final Function<String, AnyTenzor[]> converter, final File csv) throws IOException {
		if (converter == null) {
			throw new NullPointerException("Converter function can't be null");
		}
		else if (csv == null) {
			throw new NullPointerException("CSV file can't be null");
		}
		else if (!csv.exists() || !csv.isFile() || !csv.canRead()) {
			throw new IllegalArgumentException("CSV file ["+csv.getAbsolutePath()+"] not exists, is not a file or can't be read for you");
		}
		else {
			try(final InputStream	is = new FileInputStream(csv)) {
				return fromCsv(hasCaptions, ignoreCaptions, converter, is);
			}
		}
	}

	public static DataSetManager fromCsv(final boolean hasCaptions, final boolean ignoreCaptions, final Function<String, AnyTenzor[]> converter, final InputStream csv) throws IOException {
		if (converter == null) {
			throw new NullPointerException("Converter function can't be null");
		}
		else if (csv == null) {
			throw new NullPointerException("CSV stream can't be null");
		}
		else {
			return fromCsv(hasCaptions, ignoreCaptions, converter, new InputStreamReader(csv, PureLibSettings.DEFAULT_CONTENT_ENCODING));
		}
	}

	public static DataSetManager fromCsv(final boolean hasCaptions, final boolean ignoreCaptions, final Function<String, AnyTenzor[]> converter, final Reader csv) throws IOException {
		if (converter == null) {
			throw new NullPointerException("Converter function can't be null");
		}
		else if (csv == null) {
			throw new NullPointerException("CSV stream can't be null");
		}
		else {
			final BufferedReader	brdr = new BufferedReader(csv);
			String	line;
			int		lineNo = 1;
			
			if (hasCaptions) {
				line = brdr.readLine();
				lineNo++;
			}
			else {
				line = "";
			}
			if (line != null) {
				if (!ignoreCaptions) {
					processCaption(line);
				}
				final List<AnyTenzor>	input = new ArrayList<>();
				final List<AnyTenzor>	output = new ArrayList<>();
				int		count = 0, xcount = 0;
				
				while ((line = brdr.readLine()) != null) {
					if (!(line = line.trim()).isEmpty()) {
						try {
							final AnyTenzor[]	result = converter.apply(line);
							
							input.add(result[0]);
							output.add(result[1]);
							if (result[0] instanceof Tenzor) {
								count++;
							}
							else if (result[0] instanceof XTenzor) {
								xcount++;
							}
							if (result[1] instanceof Tenzor) {
								count++;
							}
							else if (result[1] instanceof XTenzor) {
								xcount++;
							}
						} catch (RuntimeException exc) {
							throw new IOException("Line ["+lineNo+"]: conversion error ["+exc.getClass().getSimpleName()+"]: "+exc.getLocalizedMessage()); 
						}
					}
					lineNo++;
				}
				return new DataSetManager(input.toArray(new AnyTenzor[input.size()]), output.toArray(new AnyTenzor[output.size()]), xcount != 0); 
			}
			else {
				return new DataSetManager(new AnyTenzor[0], new AnyTenzor[0], false);
			}
		}
	}
	
	
	private static AnyTenzor[] convert(final String content, final int numberOfInputs, final TenzorFactory factory, final boolean useXTenzor) {
		final String[]		parts = content.split(",");
		
		if (useXTenzor && factory.isXTenzorSupported()) {
			final double[]	input = new double[numberOfInputs];
			final double[]	output = new double[parts.length - numberOfInputs];
			
			for(int index = 0; index < parts.length; index++) {
				if (index < numberOfInputs) {
					input[index] = Double.valueOf(parts[index].trim());
				}
				else {
					output[index - numberOfInputs] = Double.valueOf(parts[index].trim());
				}
			}
			return new AnyTenzor[] {factory.newInstanceX(input, input.length), factory.newInstanceX(output, output.length)};
		}
		else {
			final float[]	input = new float[numberOfInputs];
			final float[]	output = new float[parts.length - numberOfInputs];
			
			for(int index = 0; index < parts.length; index++) {
				if (index < numberOfInputs) {
					input[index] = Double.valueOf(parts[index].trim()).floatValue();
				}
				else {
					output[index - numberOfInputs] = Double.valueOf(parts[index].trim()).floatValue();
				}
			}
			return new AnyTenzor[] {factory.newInstance(input, input.length), factory.newInstance(output, output.length)};
		}
	}

	private static void processCaption(final String line) {
		// TODO Auto-generated method stub
		
	}

	
//	
//	public static DataSetManager fromImages(final TenzorFactory factory, final File... imageCatalogs) throws IOException {
//		if (factory == null) {
//			throw new NullPointerException("Factory can't be null");
//		}
//		else if (imageCatalogs == null || imageCatalogs.length == 0 || Utils.checkArrayContent4Nulls(imageCatalogs) >= 0) {
//			throw new IllegalArgumentException("Image catalogs list is null, empty or contains nulls inside"); 
//		}
//		else {
//			return fromImages((in, img)->toTenzor(in, img, factory), imageCatalogs);
//		}
//	}
//
//	public static DataSetManager fromImages(final BiFunction<String, BufferedImage, Tenzor[]> converter, final File... imageCatalogs) throws IOException {
//		if (converter == null) {
//			throw new NullPointerException("Converter can't be null");
//		}
//		else if (imageCatalogs == null || imageCatalogs.length == 0 || Utils.checkArrayContent4Nulls(imageCatalogs) >= 0) {
//			throw new IllegalArgumentException("Image catalogs list is null, empty or contains nulls inside"); 
//		}
//		else {
//			final List<Tenzor>	inputs = new ArrayList<>();
//			final List<Tenzor>	outputs = new ArrayList<>();
//			
//			for(File item : imageCatalogs) {
//				try(final InputStream		is = new FileInputStream(item);
//					final Reader			rdr = new InputStreamReader(is, PureLibSettings.DEFAULT_CONTENT_ENCODING);
//					final BufferedReader	brdr = new BufferedReader(rdr)) {
//					String		line;
//					
//					while ((line = brdr.readLine()) != null) {
//						final String[]		content = line.split(",");
//						
//						if (content.length <= 1) {
//							throw new IOException("Line ["+line+"] must contain at least image file name and at least one float");
//						}
//						else {
//							File	f = new File(content[1].trim());
//							
//							if (!f.isAbsolute()) {
//								f = new File(item.getParentFile(), content[1].trim());
//							}
//							if (!f.exists() || f.isDirectory() || !f.canRead()) {
//								throw new IOException("File ["+f.getAbsolutePath()+"] not exists, is a directory or doesnt' have read access for you");
//							}
//							else {
//								final BufferedImage	img = ImageIO.read(f);
//								final Tenzor[]		temp = converter.apply(line.substring(line.indexOf(',')+1), img);
//								
//								inputs.add(temp[0]);
//								outputs.add(temp[1]);
//							}
//						}
//					}
//				}
//			}
//			return new DataSetManager(inputs.toArray(new Tenzor[inputs.size()]), outputs.toArray(new Tenzor[outputs.size()]));
//		}
//	}
//
//	public static DataSetManager fromImagesX(final BiFunction<String, BufferedImage, XTenzor[]> converter, final File... imageCatalogs) throws IOException {
//		if (converter == null) {
//			throw new NullPointerException("Converter can't be null");
//		}
//		else if (imageCatalogs == null || imageCatalogs.length == 0 || Utils.checkArrayContent4Nulls(imageCatalogs) >= 0) {
//			throw new IllegalArgumentException("Image catalogs list is null, empty or contains nulls inside"); 
//		}
//		else {
//			final List<XTenzor>	inputs = new ArrayList<>();
//			final List<XTenzor>	outputs = new ArrayList<>();
//			
//			for(File item : imageCatalogs) {
//				try(final InputStream		is = new FileInputStream(item);
//					final Reader			rdr = new InputStreamReader(is, PureLibSettings.DEFAULT_CONTENT_ENCODING);
//					final BufferedReader	brdr = new BufferedReader(rdr)) {
//					String		line;
//					
//					while ((line = brdr.readLine()) != null) {
//						final String[]		content = line.split(",");
//						
//						if (content.length <= 1) {
//							throw new IOException("Line ["+line+"] must contain at least image file name and at least one float");
//						}
//						else {
//							File	f = new File(content[1].trim());
//							
//							if (!f.isAbsolute()) {
//								f = new File(item.getParentFile(), content[1].trim());
//							}
//							if (!f.exists() || f.isDirectory() || !f.canRead()) {
//								throw new IOException("File ["+f.getAbsolutePath()+"] not exists, is a directory or doesnt' have read access for you");
//							}
//							else {
//								final BufferedImage	img = ImageIO.read(f);
//								final XTenzor[]		temp = converter.apply(line.substring(line.indexOf(',')+1), img);
//								
//								inputs.add(temp[0]);
//								outputs.add(temp[1]);
//							}
//						}
//					}
//				}
//			}
//			return new DataSetManager(inputs.toArray(new Tenzor[inputs.size()]), outputs.toArray(new Tenzor[outputs.size()]));
//		}
//	}
//	
//	private static Tenzor[] toTenzor(final String source, final int inputs, final TenzorFactory factory) {
//		final String[]	content = source.split(",");
//		
//		if (content.length <= inputs) {
//			throw new IllegalArgumentException("Line ["+source+"] contains less numbers, than required ["+inputs+"]");
//		}
//		else {
//			final float[]	input = new float[content.length];
//			
//			for(int index = 0; index < input.length; index++) {
//				try {
//					input[index] = Float.valueOf(content[index].trim());
//				} catch (NumberFormatException exc) {
//					throw new IllegalArgumentException("Line ["+source+"] - illegal number value at index ["+index+"]: "+exc.getLocalizedMessage());
//				}
//			}
//			return new Tenzor[] {
//					factory.newInstance(Arrays.copyOfRange(input, 0, inputs), inputs),
//					factory.newInstance(Arrays.copyOfRange(input, inputs, input.length - inputs), input.length - inputs)
//			};
//		}
//	}
//
//	private static Tenzor[] toTenzor(final String source, BufferedImage image, final TenzorFactory factory) {
//		final String[]	content = source.split(",");
//		final float[]	output = new float[content.length];
//		final int		width = image.getWidth();
//		final int		height = image.getHeight();
//        final float[]	rgbVector = new float[width * height * 3];
//		
//		for(int index = 0; index < output.length; index++) {
//			try {
//				output[index] = Float.valueOf(content[index].trim());
//			} catch (NumberFormatException exc) {
//				throw new IllegalArgumentException("Line ["+source+"] - illegal number value at index ["+index+"]: "+exc.getLocalizedMessage());
//			}
//		}
//
//        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
//            final BufferedImage 	copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//            
//            copy.getGraphics().drawImage(image, 0, 0, null);
//            image = copy;
//        }
//        final Raster 	raster = image.getRaster();
//        final float[]	pixel = new float[4];
//        final float		koeff = 1 / 255f;
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                 raster.getPixel(x, y, pixel);
//
//                rgbVector[y * width + x] = koeff * pixel[0];
//                rgbVector[width * height + y * width + x] = koeff * pixel[1];
//                rgbVector[2 * width * height + y * width + x] = koeff * pixel[2];
//            }
//        }
//
//		return new Tenzor[] {
//				factory.newInstance(rgbVector, width, height, 3),
//				factory.newInstance(output, output.length)
//		};
//	}
}
