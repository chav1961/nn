package chav1961.nn.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.visrec.ml.data.DataSet;
import javax.visrec.ml.eval.EvaluationMetrics;

import chav1961.nn.core.eval.Evaluators;
import chav1961.nn.core.interfaces.ActivationType;
import chav1961.nn.core.interfaces.LossType;
import chav1961.nn.core.interfaces.NeuralNetwork;
import chav1961.nn.core.train.BackpropagationTrainer;
import chav1961.nn.core.utils.FileIO;
import chav1961.nn.standalone.network.FeedForwardNetwork;

/**
 * Iris Classification Problem. An example of multi class classification using neural network.
 * This example is using Softmax activation in output layer and Cross Entropy Loss function.
 * Overfits the iris data set
 *
 * @author Zoran Sevarac <zoran.sevarac@deepnetts.com>
 */
public class QuickStartNew {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        // load data  set from csv file

        // create instance of multi addLayer percetpron using builder
        FeedForwardNetwork neuralNet = FeedForwardNetwork.builder()
                .addInputLayer(4)
                .addFullyConnectedLayer(10, ActivationType.TANH)
                .addOutputLayer(3, ActivationType.SOFTMAX)
                .lossFunction(LossType.CROSS_ENTROPY)
                .randomSeed(123)
                .build();

        DataSet dataSet = FileIO.readJson(new FileInputStream("./src/test/resources/datasets/iris_data_normalised.json"), 4, 3, neuralNet.getTensorFactory());
        dataSet.shuffle();
        
        // create and configure instanceof backpropagation trainer
        BackpropagationTrainer trainer = neuralNet.getTrainer();
        trainer.setMaxError(0.03f);
        trainer.setMaxEpochs(10000);
        trainer.setBatchMode(false);
        trainer.setLearningRate(0.01f);

        // run training
        neuralNet.train(dataSet);
        
        // evaluate trained network
        EvaluationMetrics em = Evaluators.evaluateClassifier(neuralNet, dataSet);
        System.out.println(em);
        
        // todo: explain evaluation results

        // save trained network to file
        FileIO.writeToFile(neuralNet, "myNeuralNet.dnet");
        
        NeuralNetwork loadedNeuralNet = FileIO.createFromFile(new File("myNeuralNet.dnet"));
           
    }
}
