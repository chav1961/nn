/**
 *  DeepNetts is pure Java Deep Learning Library with support for Backpropagation
 *  based learning and image recognition.
 *
 *  Copyright (C) 2017  Zoran Sevarac <sevarac@gmail.com>
 *
 *  This file is part of DeepNetts.
 *
 *  DeepNetts is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.package deepnetts.core;
 */

package chav1961.nn.examples;

import javax.visrec.ml.eval.EvaluationMetrics;

import chav1961.nn.core.data.ImageSet;
import chav1961.nn.core.eval.ClassifierEvaluator;
import chav1961.nn.core.eval.ConfusionMatrix;
import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.core.interfaces.LossType;
import chav1961.nn.core.interfaces.OptimizerType;
import chav1961.nn.core.train.BackpropagationTrainer;
import chav1961.nn.standalone.ConvolutionalNetwork;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Cifar10 {

    int imageWidth = 32;
    int imageHeight = 32;

    String labelsFile = "./src/test/resources/datasets/cifar10/labels.txt";
    String trainingFile = "./src/test/resources/datasets/cifar10/train.txt";
    String testFile = "./src/test/resources/datasets/cifar10/test.txt";

    public void run() throws IOException {
        ImageSet imageSet = new ImageSet(imageWidth, imageHeight);
        imageSet.loadLabels(new File(labelsFile));
        imageSet.loadImages(new File(trainingFile), 2000);
        imageSet.setInvertImages(true);
        imageSet.zeroMean();
        imageSet.shuffle();

        int labelsCount = imageSet.getLabelsCount();

        ImageSet[] imageSets = imageSet.split(0.6, 0.4);

        ConvolutionalNetwork neuralNet = ConvolutionalNetwork.builder()
                                        .addInputLayer(imageWidth, imageHeight, 3)
                                        .addConvolutionalLayer(3, 3, 16)
                                        .addMaxPoolingLayer(2, 2, 2)//16
                                        .addConvolutionalLayer(3, 3, 32)
                                        .addMaxPoolingLayer(2, 2, 2)//8
//                                        .addConvolutionalLayer(3, 3, 24)
//                                        .addMaxPoolingLayer(2, 2, 2)
                                     //   .addFullyConnectedLayer(30)
                                        .addFullyConnectedLayer(20)
                                        .addFullyConnectedLayer(10)
                                        .addOutputLayer(labelsCount, ActivationType.SOFTMAX)
                                        .hiddenActivationFunction(ActivationType.TANH)
                                        .lossFunction(LossType.CROSS_ENTROPY)
                                        .build();

        System.err.println("Training neural network");

        BackpropagationTrainer trainer = new BackpropagationTrainer(neuralNet);
        trainer.setLearningRate(0.01f);
        trainer.setMaxError(0.4f);
        trainer.setMomentum(0.9f);
        trainer.setOptimizer(OptimizerType.SGD);
        trainer.train(imageSets[0]);

        // Test trained network
        ClassifierEvaluator evaluator = new ClassifierEvaluator();
        EvaluationMetrics pm = evaluator.evaluate(neuralNet, imageSets[1]);
        System.err.println("------------------------------------------------");
        System.err.println("Classification performance measure"+System.lineSeparator());
        System.err.println("TOTAL AVERAGE");
        System.err.println(evaluator.getTotalAverage());
        System.err.println("By Class");
        Map<String, EvaluationMetrics>  byClass = evaluator.getPerformanceByClass();
        byClass.entrySet().stream().forEach((entry) -> {
        	System.err.println("Class " + entry.getKey() + ":");
        	System.err.println(entry.getValue());
        	System.err.println("----------------");
        });
        
        ConfusionMatrix cm = evaluator.getConfusionMatrix();
        System.err.println(cm.toString());        

    }

    public static void main(String[] args) throws IOException {
            (new Cifar10()).run();
    }
}