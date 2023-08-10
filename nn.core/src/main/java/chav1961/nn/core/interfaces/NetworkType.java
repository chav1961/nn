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

package chav1961.nn.core.interfaces;

/**
 * Neural network architecture types
 * 
 * @author Zoran Sevarac
 */
public enum NetworkType {
    FEEDFORWARD("FEEDFORWARD","feedforward"), 
    CONVOLUTIONAL("CONVOLUTIONAL","convolutional");
    
    private final String name;       
    private final String subscheme;       

    private NetworkType(final String name, final String subscheme) {
        this.name = name;
        this.subscheme = subscheme;
    }    
    
    public String getSubscheme() {
    	return subscheme;
    }
    
    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }
    
    public static NetworkType Of(Class<?> networkClass) {
        if (networkClass.getSimpleName().equals("FeedForwardNetwork")) {
            return FEEDFORWARD;
        } else if (networkClass.getSimpleName().equals("ConvolutionalNetwork")) {
            return CONVOLUTIONAL;
        }

       throw new RuntimeException("Unknown network type!");       
    }

    @Override
    public String toString() {
       return this.name;
    }        
}
