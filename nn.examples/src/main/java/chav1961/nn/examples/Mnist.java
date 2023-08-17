/**
 *  DeepNetts is pure Java Deep Learning Library with support for Backpropagation
 *  based learning and image recognition.
 *
 *  Copyright (C) 2017  Zoran Sevarac <sevarac@gmail.com>
 *
 * This file is part of DeepNetts.
 *
 * DeepNetts is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <https://www.gnu.org/licenses/>.
 */
package chav1961.nn.examples;

import javax.visrec.ml.eval.EvaluationMetrics;

import chav1961.nn.core.data.ImageSet;
import chav1961.nn.core.eval.ClassifierEvaluator;
import chav1961.nn.core.eval.ConfusionMatrix;
import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.core.interfaces.LossType;
import chav1961.nn.core.train.BackpropagationTrainer;
import chav1961.nn.core.utils.FileIO;
import chav1961.nn.standalone.network.ConvolutionalNetwork;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Example of training Convolutional network for MNIST data set. Note: in order
 * to run this example you must download mnist data set and update image paths
 * in train.txt file
 *
 * @author Zoran Sevarac <zoran.sevarac@deepnetts.com>
 */
public class Mnist {

    // input image dimensions
    final int IMAGE_WIDTH = 28;
    final int IMAGE_HEIGHT = 28;

    // data set path and training files
    final String DATA_SET_PATH = "./src/test/resources/datasets/mnist";
    final String LABELS_FILE = DATA_SET_PATH + "/labels.txt";
    final String TRAINING_FILE = DATA_SET_PATH + "/train.txt";

    public void run() throws IOException {

        System.err.println("Training convolutional network with MNIST data set");
        System.err.println("Creating image data set...");

        System.err.println("------------------------------------------------");
        System.err.println("CREATING NEURAL NETWORK");
        System.err.println("------------------------------------------------");

        // create convolutional neural network architecture
        ConvolutionalNetwork neuralNet = ConvolutionalNetwork.builder()
                .addInputLayer(IMAGE_WIDTH, IMAGE_HEIGHT, 3)
                .addConvolutionalLayer(3, 3, 12)
                .addMaxPoolingLayer(2, 2)
                .addFullyConnectedLayer(30)
                .addOutputLayer(labelsCount, ActivationType.SOFTMAX)
                .hiddenActivationFunction(ActivationType.TANH)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(123)
                .build();

        // create a data set from images and labels
        ImageSet imageSet = new ImageSet(IMAGE_WIDTH, IMAGE_HEIGHT, neuralNet.getTensorFactory());
        imageSet.setInvertImages(true);
        imageSet.loadLabels(new File(LABELS_FILE));
        imageSet.loadImages(new File(TRAINING_FILE), 1000);
        //  imageSet.zeroMean();
        imageSet.countByClasses();      
        
        ImageSet[] imageSets = imageSet.split(0.7, 0.3);
        int labelsCount = imageSet.getLabelsCount();

        
        System.err.println(neuralNet);        
        
        // create a trainer and train network
        BackpropagationTrainer trainer = new BackpropagationTrainer(neuralNet);
        trainer.setLearningRate(0.01f)
                .setMaxError(0.05f);
        
        trainer.train(imageSets[0]);

        // Test trained network
        ClassifierEvaluator evaluator = new ClassifierEvaluator();
        evaluator.evaluate(neuralNet, imageSets[1]);
        System.err.println("------------------------------------------------");
        System.err.println("Classification performance measure" + System.lineSeparator());
        System.err.println("TOTAL AVERAGE");
        System.err.println(evaluator.getTotalAverage());
        System.err.println("By Class");
        Map<String, EvaluationMetrics> byClass = evaluator.getPerformanceByClass();
        byClass.entrySet().stream().forEach((entry) -> {
        	System.err.println("Class " + entry.getKey() + ":");
        	System.err.println(entry.getValue());
        	System.err.println("----------------");
        });
        
        System.err.println("CONFUSION MATRIX");
        ConfusionMatrix cm = evaluator.getConfusionMatrix();
        System.err.println(cm);

        // Save network to file
        FileIO.writeToFile(neuralNet, "mnistDemo.dnet");
    }

    public static void main(String[] args) throws IOException {
        (new Mnist()).run();
    }


}
