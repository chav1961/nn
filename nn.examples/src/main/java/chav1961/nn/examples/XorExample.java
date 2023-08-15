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

import javax.visrec.ml.data.DataSet;

import chav1961.nn.core.data.TabularDataSet;
import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.core.interfaces.LossType;
import chav1961.nn.core.train.BackpropagationTrainer;
import chav1961.nn.examples.util.ExampleDataSets;
import chav1961.nn.standalone.FeedForwardNetwork;

/**
 * Solve XOR problem to confirm that backpropagation is working, and that it can
 * solve the simplest nonlinear problem.
 *
 * @author Zoran Sevarac <zoran.sevarac@deepnetts.com>
 */
public class XorExample {

    public static void main(String[] args) {


        FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                .addInputLayer(2)
                .addFullyConnectedLayer(3, ActivationType.TANH)
                .addOutputLayer(1, ActivationType.SIGMOID)
                .lossFunction(LossType.MEAN_SQUARED_ERROR)
//                .randomSeed(123)
                .build();

        TabularDataSet dataSet = ExampleDataSets.xor(neuralNet.getTensorFactory());
        dataSet.setColumnNames(new String[] {"input1", "input2", "output"});
        
//        neuralNet.getTrainer().setLearningRate(0.9f);
//        neuralNet.setOutputLabels("output");
        
//        neuralNet.train(dataSet);

        BackpropagationTrainer trainer = new BackpropagationTrainer(neuralNet);
        trainer.setMaxError(0.01f);
        trainer.setLearningRate(0.9f);
        trainer.train(dataSet);
        System.err.println("Completed");
    }



}