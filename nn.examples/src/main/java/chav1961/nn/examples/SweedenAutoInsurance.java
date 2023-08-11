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

import java.io.IOException;
import java.util.Arrays;

import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;

import chav1961.nn.core.eval.Evaluators;
import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.core.interfaces.LossType;
import chav1961.nn.core.train.BackpropagationTrainer;
import chav1961.nn.core.utils.FileIO;
import chav1961.nn.standalone.FeedForwardNetwork;


/**
 * Minimal example for simple linear regression using FeedForwardNetwork.
 * Fits a straight line through the data.
 * Uses a single addLayer with one output and linear activation function, and Mean Squared Error for Loss function.
 * You can use linear regression to roughly estimate a global trend in data.
 *
 * TODO: dont print accuracy for regression problems!
 *
 * predicting the total payment for all claims in thousands of Swedish Kronor, given the total number of claims.
 *
 * @author Zoran Sevarac
 */
public class SweedenAutoInsurance {

    public static void main(String[] args) throws IOException {

        String datasetFile = "./src/test/resources/datasets/SweedenAutoInsurance.csv";
        int inputsNum = 1;
        int outputsNum = 1;

        FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                .addInputLayer(1)
                .addOutputLayer(1, ActivationType.LINEAR)
                .lossFunction(LossType.MEAN_SQUARED_ERROR)
                .build();

        DataSet dataSet = FileIO.readCsv(datasetFile, inputsNum, outputsNum, neuralNet.getTensorFactory());
        DataSet[] trainAndTestSet = dataSet.split(0.7, 0.3);

        
        BackpropagationTrainer trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.01f)
               .setMaxEpochs(100)
               .setLearningRate(0.1f);

        neuralNet.train(trainAndTestSet[0]);

       EvaluationMetrics em = Evaluators.evaluateRegressor(neuralNet, trainAndTestSet[1]);
       System.out.println(em);

       // use model for prediction
       float[] predictedOutput = neuralNet.predict(new float[]{0.153225806f});
       System.out.println(Arrays.toString(predictedOutput));

    }

}
