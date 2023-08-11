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
 * this program. If not, see <https://www.gnu.org/licenses/>.package
 * deepnetts.core;
 */

package chav1961.nn.core.train;

import javax.visrec.ml.eval.Evaluator;
import javax.visrec.ml.eval.EvaluationMetrics;
import java.util.ArrayList;
import java.util.List;
import javax.visrec.ml.data.DataSet;
import org.apache.commons.lang3.SerializationUtils;

import chav1961.nn.core.data.TabularDataSet;
import chav1961.nn.core.eval.ClassifierEvaluator;
import chav1961.nn.core.eval.RegresionEvaluator;
import chav1961.nn.core.interfaces.MLDataItem;
import chav1961.nn.core.interfaces.NeuralNetwork;


/**
 * Split data set into k parts of equal sizes (folds)
 * Train with data from k-1 folds(parts), and test with 1 fold, repeat k times each with different test fold.
 *
 * @author Zoran
 */
public class KFoldCrossValidation<E> {
    private int splitsNum; 
    private NeuralNetwork<E> neuralNetwork; 
    private BackpropagationTrainer trainer; 
    private DataSet<MLDataItem> dataSet; 
    private Evaluator<NeuralNetwork<E>, DataSet<? extends MLDataItem>> evaluator;
    private final List<NeuralNetwork<E>> trainedNetworks = new ArrayList<>();

    private KFoldCrossValidation(final int splitsNum, final NeuralNetwork<E> neuralNetwork, final BackpropagationTrainer trainer, final DataSet<MLDataItem> dataSet, final Evaluator<NeuralNetwork<E>, DataSet<? extends MLDataItem>> evaluator) {
		this.splitsNum = splitsNum;
		this.neuralNetwork = neuralNetwork;
		this.trainer = trainer;
		this.dataSet = dataSet;
		this.evaluator = evaluator;
	}

	public EvaluationMetrics runCrossValidation() {
        List<EvaluationMetrics> measures = new ArrayList<>();
        DataSet[] folds = (DataSet[]) dataSet.split(splitsNum);

        for (int testFoldIdx = 0; testFoldIdx < splitsNum; testFoldIdx++) {
            DataSet testSet = folds[testFoldIdx];
            TabularDataSet trainingSet = new TabularDataSet(((TabularDataSet)dataSet).getNumInputs(), ((TabularDataSet)dataSet).getNumOutputs());
            trainingSet.setColumnNames(((TabularDataSet)dataSet).getColumnNames());
            for (int trainFoldIdx = 0; trainFoldIdx < splitsNum; trainFoldIdx++) {
                if (trainFoldIdx == testFoldIdx) continue;
                trainingSet.addAll(folds[trainFoldIdx]);
            }

            // clone the original network each time before training - create a new instace that will be added to trainedNetworks
            NeuralNetwork neuralNet = SerializationUtils.clone(this.neuralNetwork); // ovde bi morao traineru da prosledjuje kloniranu mrezu
            // ova mreza nije ni kreirana
            trainer.train(trainingSet); // napravi da trainer moze da sa istim parametrima pozove novu mrezu!!!!! ovo je problem, trainer zahteva novu instancu neuralNet ovde!!!
            EvaluationMetrics pe = evaluator.evaluate(neuralNet, testSet); // Peturn an instance of PerformanceMeaseure here
            measures.add(pe);
            trainedNetworks.add(neuralNet);
        }
        // get final evaluation results - avg performnce of all test sets - use some static method to get that
        
        if (evaluator instanceof ClassifierEvaluator) {
            return ClassifierEvaluator.averagePerformance(measures);
        } else {
            return RegresionEvaluator.averagePerformance(measures);
        }
        
        
    }

    public List<NeuralNetwork<E>> getTrainedNetworks() {
        return trainedNetworks;
    }

    public static <E> Builder<E> builder() {
        return new Builder<E>();
    }

    public static class Builder<E> {
        private int splitsNum = -1; 
        private NeuralNetwork<E> neuralNetwork = null; 
        private BackpropagationTrainer trainer = null; 
        private DataSet<MLDataItem> dataSet = null; 
        private Evaluator<NeuralNetwork<E>, DataSet<? extends MLDataItem>> evaluator = null;

        public Builder<E> splitsNum(int k) {
           this.splitsNum = k;
           return this;
        }

        public Builder<E> model(NeuralNetwork<E> neuralNet) {
        	this.neuralNetwork = neuralNet;
            return this;
        }

        public Builder<E> trainer(BackpropagationTrainer trainer) {
        	this.trainer = trainer;
            return this;
        }

        public Builder<E> dataSet(DataSet<? extends MLDataItem> dataSet) {
        	this.dataSet = (DataSet<MLDataItem>) dataSet;
            return this;
        }

        public Builder<E> evaluator(Evaluator<NeuralNetwork<E>, DataSet<? extends MLDataItem>> evaluator) {
        	this.evaluator = evaluator;
            return this;
        }

        public KFoldCrossValidation<E> build() {
        	if (splitsNum == -1) {
        		throw new IllegalStateException("Can't build instance: splitsNum parameter is not defined");
        	}
        	else if (neuralNetwork == null) {
        		throw new IllegalStateException("Can't build instance: neuralNetwork parameter is not defined");
        	}
        	else if (trainer == null) {
        		throw new IllegalStateException("Can't build instance: trainer parameter is not defined");
        	}
        	else if (dataSet == null) {
        		throw new IllegalStateException("Can't build instance: dataSet parameter is not defined");
        	}
        	else if (evaluator == null) {
        		throw new IllegalStateException("Can't build instance: evaluator parameter is not defined");
        	}
        	else {
                return new KFoldCrossValidation<E>(splitsNum, neuralNetwork, trainer, dataSet, evaluator);
        	}
        }
    }
}
