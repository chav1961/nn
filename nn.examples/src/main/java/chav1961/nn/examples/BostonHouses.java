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

import java.io.File;
import java.io.IOException;

import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;

import chav1961.nn.core.eval.Evaluators;
import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.core.interfaces.LossType;
import chav1961.nn.core.utils.FileIO;
import chav1961.nn.standalone.network.FeedForwardNetwork;

/**
 * Minimal example for linear regression using FeedForwardNetwork.
 * Fits a straight line (linear function y=k*x+n) through the data.
 * Uses a single layer with one output and linear activation function, and Mean Squared Error for Loss function.
 * Linear regression can be used to roughly estimate a general trend in data.
 *
 * @author Zoran Sevarac <zoran.sevarac@deepnetts.com>
 */
public class BostonHouses {

    public static void main(String[] args) throws IOException {

            int inputsNum = 1;
            int outputsNum = 1;

            // create neural network using network specific builder
            FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                    .addInputLayer(inputsNum)
                    .addFullyConnectedLayer(3, ActivationType.TANH)
                    .addOutputLayer(outputsNum, ActivationType.LINEAR)
                    .lossFunction(LossType.MEAN_SQUARED_ERROR)
                    .build();
            
            String csvFilename = "./src/test/resources/datasets/bostonsredjen-2kolone.csv";

            // load and create data set from csv file
            DataSet dataSet = FileIO.readCsv(new File(csvFilename).getAbsolutePath() , inputsNum, outputsNum, true, neuralNet.getTensorFactory());
            DataSet[] trainAndTestSet = dataSet.split(0.6);
            
            neuralNet.getTrainer().setMaxError(0.006f);

            neuralNet.train(trainAndTestSet[0]);

            EvaluationMetrics pm = Evaluators.evaluateRegressor(neuralNet, trainAndTestSet[1]);
            System.out.println(pm);

            // perform prediction for some input value
            neuralNet.setInput(neuralNet.getTensorFactory().newInstance(1, 1, new float[] {0.2f}));
            System.out.println("Predicted price of the house is for 8 :" + neuralNet.getOutput()[0]);//*50);
    }

}
