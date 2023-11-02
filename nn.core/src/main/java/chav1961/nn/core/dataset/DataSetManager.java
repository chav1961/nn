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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.imageio.ImageIO;

import chav1961.nn.api.interfaces.Tenzor;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.Utils;

public class DataSetManager {
	private final Tenzor[]	inputs;
	private final Tenzor[]	outputs;
	
	protected DataSetManager(final Tenzor[] inputs, final Tenzor[] outputs) {
		this.inputs = inputs;
		this.outputs = outputs;
	}
	
	public int size() {
		return inputs.length;
	}
	
	public DataSetManager[] split(final float percentage) {
		if (percentage <= 0 || percentage >= 1) {
			throw new IllegalArgumentException("Illegal percentage value ["+percentage+"] : must be in 0..1 range (exclusive)"); 
		}
		else {
			final List<Tenzor>	firstInput = new ArrayList<>();
			final List<Tenzor>	firstOutput = new ArrayList<>();
			final List<Tenzor>	secondInput = new ArrayList<>();
			final List<Tenzor>	secondOutput = new ArrayList<>();
			
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
					new DataSetManager(firstInput.toArray(new Tenzor[firstInput.size()]), firstOutput.toArray(new Tenzor[firstOutput.size()])),
					new DataSetManager(secondInput.toArray(new Tenzor[secondInput.size()]), secondOutput.toArray(new Tenzor[secondOutput.size()])),
			};
		}
	}
	
	public void forEach(final BiConsumer<Tenzor, Tenzor> callback) {
		if (callback == null) {
			throw new NullPointerException("Callback can't be null"); 
		}
		else {
			for(int index = 0; index < inputs.length; index++) {
				callback.accept(inputs[index], outputs[index]);
			}
		}
 	}
	
	public static DataSetManager fromCsv(final int numberOfInputs, final Tenzor.TenzorFactory factory, final File... csv) throws IOException {
		if (numberOfInputs <= 0) {
			throw new IllegalArgumentException("Number of inputs ["+numberOfInputs+"] must be greater than 0");
		}
		else if (factory == null) {
			throw new NullPointerException("Tenzor factory can't be null");
		}
		else if (csv == null || csv.length == 0 || Utils.checkArrayContent4Nulls(csv) >= 0) {
			throw new IllegalArgumentException("File list is null, empty or contains nulls inside");
		}
		else {
			return fromCsv(numberOfInputs, (in)->toTenzor(in, numberOfInputs, factory), csv);
		}
	}

	public static DataSetManager fromCsv(final int numberOfInputs, final Function<String, Tenzor[]> converter, final File... csv) throws IOException {
		if (numberOfInputs <= 0) {
			throw new IllegalArgumentException("Number of inputs ["+numberOfInputs+"] must be greater than 0");
		}
		else if (converter == null) {
			throw new NullPointerException("Converter can't be null");
		}
		else if (csv == null || csv.length == 0 || Utils.checkArrayContent4Nulls(csv) >= 0) {
			throw new IllegalArgumentException("File list is null, empty or contains nulls inside");
		}
		else {
			final List<Tenzor>	inputs = new ArrayList<>();
			final List<Tenzor>	outputs = new ArrayList<>();
			
			for(File item : csv) {
				try(final InputStream		is = new FileInputStream(item);
					final Reader			rdr = new InputStreamReader(is, PureLibSettings.DEFAULT_CONTENT_ENCODING);
					final BufferedReader	brdr = new BufferedReader(rdr)) {
					String		line;
					
					while ((line = brdr.readLine()) != null) {
						final Tenzor[]		temp = converter.apply(line);
						
						inputs.add(temp[0]);
						outputs.add(temp[1]);
					}
				}
			}
			return new DataSetManager(inputs.toArray(new Tenzor[inputs.size()]), outputs.toArray(new Tenzor[outputs.size()]));
		}
	}
	
	public static DataSetManager fromImages(final Tenzor.TenzorFactory factory, final File... imageCatalogs) throws IOException {
		if (factory == null) {
			throw new NullPointerException("Factory can't be null");
		}
		else if (imageCatalogs == null || imageCatalogs.length == 0 || Utils.checkArrayContent4Nulls(imageCatalogs) >= 0) {
			throw new IllegalArgumentException("Image catalogs list is null, empty or contains nulls inside"); 
		}
		else {
			return fromImages((in, img)->toTenzor(in, img, factory), imageCatalogs);
		}
	}

	public static DataSetManager fromImages(final BiFunction<String, BufferedImage, Tenzor[]> converter, final File... imageCatalogs) throws IOException {
		if (converter == null) {
			throw new NullPointerException("Converter can't be null");
		}
		else if (imageCatalogs == null || imageCatalogs.length == 0 || Utils.checkArrayContent4Nulls(imageCatalogs) >= 0) {
			throw new IllegalArgumentException("Image catalogs list is null, empty or contains nulls inside"); 
		}
		else {
			final List<Tenzor>	inputs = new ArrayList<>();
			final List<Tenzor>	outputs = new ArrayList<>();
			
			for(File item : imageCatalogs) {
				try(final InputStream		is = new FileInputStream(item);
					final Reader			rdr = new InputStreamReader(is, PureLibSettings.DEFAULT_CONTENT_ENCODING);
					final BufferedReader	brdr = new BufferedReader(rdr)) {
					String		line;
					
					while ((line = brdr.readLine()) != null) {
						final String[]		content = line.split(",");
						
						if (content.length <= 1) {
							throw new IOException("Line ["+line+"] must contain at least image file name and at least one float");
						}
						else {
							File	f = new File(content[1].trim());
							
							if (!f.isAbsolute()) {
								f = new File(item.getParentFile(), content[1].trim());
							}
							if (!f.exists() || f.isDirectory() || !f.canRead()) {
								throw new IOException("File ["+f.getAbsolutePath()+"] not exists, is a directory or doesnt' have read access for you");
							}
							else {
								final BufferedImage	img = ImageIO.read(f);
								final Tenzor[]		temp = converter.apply(line.substring(line.indexOf(',')+1), img);
								
								inputs.add(temp[0]);
								outputs.add(temp[1]);
							}
						}
					}
				}
			}
			return new DataSetManager(inputs.toArray(new Tenzor[inputs.size()]), outputs.toArray(new Tenzor[outputs.size()]));
		}
	}

	private static Tenzor[] toTenzor(final String source, final int inputs, final Tenzor.TenzorFactory factory) {
		final String[]	content = source.split(",");
		
		if (content.length <= inputs) {
			throw new IllegalArgumentException("Line ["+source+"] contains less numbers, than required ["+inputs+"]");
		}
		else {
			final float[]	input = new float[content.length];
			
			for(int index = 0; index < input.length; index++) {
				try {
					input[index] = Float.valueOf(content[index].trim());
				} catch (NumberFormatException exc) {
					throw new IllegalArgumentException("Line ["+source+"] - illegal number value at index ["+index+"]: "+exc.getLocalizedMessage());
				}
			}
			return new Tenzor[] {
					factory.newInstance(Arrays.copyOfRange(input, 0, inputs), inputs),
					factory.newInstance(Arrays.copyOfRange(input, inputs, input.length - inputs), input.length - inputs)
			};
		}
	}

	private static Tenzor[] toTenzor(final String source, BufferedImage image, final Tenzor.TenzorFactory factory) {
		final String[]	content = source.split(",");
		final float[]	output = new float[content.length];
		final int		width = image.getWidth();
		final int		height = image.getHeight();
        final float[]	rgbVector = new float[width * height * 3];
		
		for(int index = 0; index < output.length; index++) {
			try {
				output[index] = Float.valueOf(content[index].trim());
			} catch (NumberFormatException exc) {
				throw new IllegalArgumentException("Line ["+source+"] - illegal number value at index ["+index+"]: "+exc.getLocalizedMessage());
			}
		}

        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            final BufferedImage 	copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            
            copy.getGraphics().drawImage(image, 0, 0, null);
            image = copy;
        }
        final Raster 	raster = image.getRaster();
        final float[]	pixel = new float[4];
        final float		koeff = 1 / 255f;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                 raster.getPixel(x, y, pixel);

                rgbVector[y * width + x] = koeff * pixel[0];
                rgbVector[width * height + y * width + x] = koeff * pixel[1];
                rgbVector[2 * width * height + y * width + x] = koeff * pixel[2];
            }
        }

		return new Tenzor[] {
				factory.newInstance(rgbVector, width, height, 3),
				factory.newInstance(output, output.length)
		};
	}
}
