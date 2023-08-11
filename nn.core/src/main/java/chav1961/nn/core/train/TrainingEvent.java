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

package chav1961.nn.core.train;

import java.util.EventObject;

/**
 * This class holds source and type of training event.
 *
 * @author Zoran Sevarac <zoran.sevarac@deepnetts.com>
 */
public final class TrainingEvent extends EventObject {
	private static final long serialVersionUID = 173459921999513034L;
	
    private final Type type;

    public static enum Type {
        STARTED, 
        STOPPED, 
        EPOCH_FINISHED, 
        MINI_BATCH, 
        ITERATION_FINISHED;
    }

    public TrainingEvent(final BackpropagationTrainer source, final Type type) {
    	super(source);
        this.type = type;
    }

    @Override
    public BackpropagationTrainer getSource() {
        return (BackpropagationTrainer)super.getSource();
    }

    public Type getType() {
        return type;
    }
}