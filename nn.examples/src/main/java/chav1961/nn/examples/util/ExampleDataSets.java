package chav1961.nn.examples.util;


import java.io.File;
import java.io.IOException;

import chav1961.nn.core.data.ImageSet;
import chav1961.nn.core.data.TabularDataSet;
import chav1961.nn.core.interfaces.MLDataItem;
import chav1961.nn.core.utils.FileIO;

/**
 * TODO: add breast cancer, mnist  and other UCI stuff
 * @author Zoran
 */
public class ExampleDataSets {

    public static TabularDataSet iris() throws IOException {
       // TODO: apply some normalization here, as a param?
       return (TabularDataSet) FileIO.readCsv("./src/test/resources/datasets/iris_data_normalised.txt", 4, 3);
    }

    public static TabularDataSet xor() {
        TabularDataSet dataSet = new TabularDataSet(2, 1);

        MLDataItem item1 = new TabularDataSet.Item(new float[] {0, 0}, new float[] {0});
        dataSet.add(item1);

        MLDataItem item2 = new TabularDataSet.Item(new float[] {0, 1}, new float[] {1});
        dataSet.add(item2);

        MLDataItem item3 = new TabularDataSet.Item(new float[] {1, 0}, new float[] {1});
        dataSet.add(item3);

        MLDataItem item4 = new TabularDataSet.Item(new float[] {1, 1}, new float[] {0});
        dataSet.add(item4);

        return dataSet;
    }


    public static ImageSet mnist() {
        String labelsFile = "./src/test/resources/datasets/mnist/labels.txt";
        String trainingFile = "./src/test/resources/datasets/mnist/train.txt";        
        
        ImageSet imageSet = new ImageSet(28, 28);
        imageSet.setInvertImages(true);
        imageSet.loadLabels(new File(labelsFile));
        imageSet.loadImages(new File(trainingFile), 1000);
        
        return imageSet;
    }
}
