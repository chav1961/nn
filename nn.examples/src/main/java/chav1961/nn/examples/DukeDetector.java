/**
 * DeepNetts is pure Java Deep Learning Library with support for Backpropagation
 * based learning and image recognition.
 * <p>
 * Copyright (C) 2017  Zoran Sevarac <sevarac@gmail.com>
 * <p>
 * This file is part of DeepNetts.
 * <p>
 * DeepNetts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.package deepnetts.core;
 */

package chav1961.nn.examples;


import javax.visrec.ml.eval.EvaluationMetrics;

import chav1961.nn.core.data.ImageSet;
import chav1961.nn.core.eval.ClassifierEvaluator;
import chav1961.nn.core.eval.ConfusionMatrix;
import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.core.interfaces.LossType;
import chav1961.nn.core.network.AbstractNeuralNetwork;
import chav1961.nn.core.train.BackpropagationTrainer;
import chav1961.nn.core.utils.FileIO;
import chav1961.nn.examples.util.FileIODebug;
import chav1961.nn.standalone.ConvolutionalNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Convolutional Neural Network that learns to detect Duke images.
 * Example how to create and train convolutional network for image classification.
 *
 * @author Zoran Sevarac <zoran.sevarac@deepnetts.com>
 */
public class DukeDetector {

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
        int imageWidth = 64;
        int imageHeight = 64;
        System.err.println("Creating neural network...");

        ConvolutionalNetwork convNet = ConvolutionalNetwork.builder()
                .addInputLayer(imageWidth, imageHeight, 3)
                .addConvolutionalLayer(3, 3, 3, ActivationType.TANH)
                .addMaxPoolingLayer(2, 2, 2)
                .addFullyConnectedLayer(10, ActivationType.TANH)
                .addOutputLayer(1, ActivationType.SIGMOID)
                .lossFunction(LossType.CROSS_ENTROPY)
                .build();


        String trainingFile = "./src/test/resources/datasets/DukeSet/train.txt";
        String labelsFile = "./src/test/resources/datasets/DukeSet/labels.txt";

        ImageSet imageSet = new ImageSet(imageWidth, imageHeight, convNet.getTensorFactory());

        System.err.println("Loading images...");

        imageSet.loadLabels(new File(labelsFile));
        imageSet.loadImages(new File(trainingFile));
        //imageSet.zeroMean();

        imageSet.setInvertImages(true);
        imageSet.shuffle();

        // ImageSet[] trainAndTestSet = imageSet.split(0.7, 0.3);

        
        convNet.setOutputLabels(imageSet.getTargetColumnsNames());

        System.err.println("Training neural network");

        // create a set of convolutional networks and do training, crossvalidation and performance evaluation
        BackpropagationTrainer trainer = convNet.getTrainer();
        trainer.setMaxError(0.05f)
                .setLearningRate(0.01f);
        trainer.train(imageSet);

        // to save neural network to file on disk
        FileIO.writeToFile(convNet, "DukeDetector.dnet");

        // to load neural network from file use FileIO.createFromFile
        // dukeNet = FileIO.createFromFile("DukeDetector.dnet");
        // to serialize network in json use FileIO.toJson
        // System.out.println(FileIO.toJson(dukeNet));

        // to evaluate recognizer with image set
        ClassifierEvaluator evaluator = new ClassifierEvaluator();
        EvaluationMetrics evalResults = evaluator.evaluate(convNet, imageSet);
        System.out.println(evalResults);

        ConfusionMatrix cm = evaluator.getConfusionMatrix();
        System.out.println(cm);
        
        // load saved network
        AbstractNeuralNetwork loadedNeuralNet = FileIODebug.createFromFile(new File("DukeDetector.dnet"));
        

        // to use recognizer for single image
//        BufferedImage image = ImageIO.read(new File("/home/zoran/datasets/DukeSet/duke/duke7.jpg"));
//        DeepNettsImageClassifier imageClassifier = new DeepNettsImageClassifier(convNet);
//        ClassificationResults<ClassificationResult> results = imageClassifier.classify(image);

        //   System.out.println(results.toString());
    }

}
