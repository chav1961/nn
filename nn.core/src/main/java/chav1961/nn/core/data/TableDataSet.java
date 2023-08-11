package chav1961.nn.core.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.visrec.ml.data.Column;
import javax.visrec.ml.data.DataSet;

import chav1961.nn.core.interfaces.MLDataItem;
import chav1961.nn.core.interfaces.Tensor;
import chav1961.nn.core.interfaces.TensorFactory;
import chav1961.purelib.concurrent.LightWeightListenerList;

public class TableDataSet<E extends MLDataItem> implements TableModel, DataSet<E> {
	private static final String[]	EMPTY_CONTENT = new String[0]; 
	
	private final LightWeightListenerList<TableModelListener>	listeners = new LightWeightListenerList<>(TableModelListener.class);
	private final List<Column>		columns = new ArrayList<>();
	private final List<MLDataItem>	content = new ArrayList<>();
	
	public TableDataSet() {
	}

	public TableDataSet(final List<Column> columns, final List<E> content) {
		if (columns == null) {
			throw new NullPointerException("Columns list can't be null"); 
		}
		else if (content == null) {
			throw new NullPointerException("Content list can't be null"); 
		}
		else {
			this.columns.addAll(columns);
			this.content.addAll(content);
		}
	}
	
	public TableDataSet(final ColumnsAndContent cc, final int numberOfInputs, final int numberOfOutputs, final TensorFactory factory) {
		if (cc == null) {
			throw new NullPointerException("Column and content instance can't be null"); 
		}
		else if (numberOfInputs <= 0 || numberOfInputs > cc.columns.length) {
			throw new IllegalArgumentException("Number of inputs ["+numberOfInputs+"] out of range 0.."+cc.columns.length); 
		}
		else if (numberOfOutputs <= 0 || numberOfOutputs > cc.columns.length) {
			throw new IllegalArgumentException("Number of outputs ["+numberOfOutputs+"] out of range 0.."+cc.columns.length); 
		}
		else if (numberOfInputs + numberOfOutputs > cc.columns.length) {
			throw new IllegalArgumentException("Number of inputs ["+numberOfInputs+"] plus number of outputs ["+numberOfOutputs+"] is greater than number of columns ["+cc.columns.length+"]"); 
		}
		else if (factory == null) {
			throw new NullPointerException("Tensor factory can't be null"); 
		}
		else {
			int	count = 0;
			
			for(String item : cc.columns) {
				columns.add(new TableColumn(item, count >= numberOfInputs));
				count++;
			}
			for(float[] item : cc.content) {
				content.add(new Item(factory, Arrays.copyOfRange(item, 0, numberOfInputs), Arrays.copyOfRange(item, numberOfInputs, numberOfInputs+numberOfOutputs)));
			}
		}
	}
	
	@Override
	public List<E> getItems() {
		return (List<E>) Collections.unmodifiableList(content);
	}

	@Override
	public TableDataSet<E>[] split(final double... parts) {
        if (parts == null || parts.length == 0) {
            throw new IllegalArgumentException("Number of split parts can't be null or empty");
        }
        else {
	        double partsSum = 0;
	        
	        for (int index = 0; index < parts.length; index++) {
	            if (parts[index] <= 0) {
	                throw new IllegalArgumentException("Value of the part ["+parts[index]+"] at index ["+index+"] cannot be zero or negative!");
	            }
	            else {
	                partsSum += parts[index];
	            }
	        }
	
	        if (partsSum > 1) {
	            throw new IllegalArgumentException("Sum of parts cannot be larger than 1!");
	        }
	        else {
		        final TableDataSet<E>[]	result = new TableDataSet[parts.length];
		        int 	itemIdx = 0;
		
//		        this.shuffle(); 
		        for (int part = 0; part < parts.length; part++) {
		            final int 		itemsCount = (int) (size() * parts[part]);
		        	final List<E>	sublist = new ArrayList<>();
		        	
		            for (int index = 0; index < itemsCount; index++) {
		                sublist.add((E) content.get(itemIdx));
		                itemIdx++;
		            }
		            result[part] = new TableDataSet<E>(getColumns(), sublist);
		        }
		
		        return result;
	        }
        }
	}

	@Override
	public void setColumns(final List<Column> columns) {
		if (columns == null || columns.isEmpty()) {
			throw new IllegalArgumentException("Columns list to set can't be null or empty");
		}
		else {
			this.columns.clear();
			this.columns.addAll(columns);
			fireChanges(new TableModelEvent(this, TableModelEvent.HEADER_ROW, TableModelEvent.HEADER_ROW, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
		}
	}

	@Override
	public List<Column> getColumns() {
		return Collections.unmodifiableList(columns);
	}

	@Override
	public String[] getTargetColumnsNames() {
		int	count = 0;
		
		for(Column col : columns) {
			if (col.isTarget()) {
				count++;
			}
		}
		
		if (count == 0) {
			return EMPTY_CONTENT;
		}
		else {
			final String[]	result = new String[count];
					
			count = 0;
			for(Column col : columns) {
				if (col.isTarget()) {
					result[count++] = col.getName();
				}
			}
			return result;
		}
	}

	@Override
    public TableDataSet<E> add(final E item) {
		if (item == null) {
			throw new NullPointerException("Item to add can't be null");
		}
		else {
			final int	size = content.size();
			
			content.add(item);
			fireChanges(new TableModelEvent(this, size, content.size(), TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	        return this;
		}
    }

	@Override
    public TableDataSet<E> addAll(final DataSet<E> dataSet) {
		if (dataSet == null) {
			throw new NullPointerException("Item to add can't be null");
		}
		else {
			final int	size = content.size();
	        
			getItems().addAll(dataSet.getItems());
			fireChanges(new TableModelEvent(this, size, content.size(), TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	        return this;
		}
    }

    /**
     * Clear items of the {@link DataSet}
     */
	@Override
    public void clear() {
		final int	size = content.size();
		
        content.clear();
		fireChanges(new TableModelEvent(this, 0, size-1, TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
    }
 
    @Override
    public void shuffle() {
        Collections.shuffle(content);
		fireChanges(new TableModelEvent(this, 0, content.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    @Override
    public void shuffle(final Random rnd) {
    	if (rnd == null) {
    		throw new NullPointerException("Random to shuffle can't be null");
    	}
    	else {
            Collections.shuffle(content, rnd);
    		fireChanges(new TableModelEvent(this, 0, content.size() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    	}
    }
               
	@Override
	public int getRowCount() {
		return content.size();
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getColumnName(final int columnIndex) {
		return columns.get(columnIndex).getName();
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return Float.class;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return false;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		return content.get(rowIndex).getInput().get(columnIndex);
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
	}

	@Override
	public void addTableModelListener(final TableModelListener l) {
		if (l == null) {
			throw new NullPointerException("Listener to add cna't be null");
		}
		else {
			listeners.addListener(l);
		}
	}

	@Override
	public void removeTableModelListener(final TableModelListener l) {
		if (l == null) {
			throw new NullPointerException("Listener to remove cna't be null");
		}
		else {
			listeners.removeListener(l);
		}
	}

	private void fireChanges(final TableModelEvent tableModelEvent) {
		listeners.fireEvent((l)->l.tableChanged(tableModelEvent));
	}

	private static class TableColumn extends Column {
		public TableColumn(final String name, boolean isTarget) {
			super(name, Column.Type.DECIMAL, isTarget);
		}

		@Override
		public String toString() {
			return "TableColumn [getName()=" + getName() + ", getType()=" + getType() + ", isTarget()=" + isTarget() + "]";
		}
	}
	
    private static class Item implements MLDataItem {
        private final Tensor input; // network input
        private final Tensor targetOutput; // for classifiers target can be index, int 

        public Item(final TensorFactory factory, final float[] in, final float[] targetOutput) {
            this.input = factory.newInstance(in);
            this.targetOutput = factory.newInstance(targetOutput);
        }

        @Override
        public Tensor getInput() {
            return input;
        }

        @Override
        public Tensor getTargetOutput() {
            return targetOutput;
        }

        @Override
        public String toString() {
            return "BasicDataSetItem{" + "input=" + input + ", targetOutput=" + targetOutput + '}';
        }
    }
}
