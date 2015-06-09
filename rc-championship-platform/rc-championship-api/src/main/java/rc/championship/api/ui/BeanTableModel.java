package rc.championship.api.ui;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.openide.util.Exceptions;

/**
 * A table model where each row represents one instance of a Java bean. When the
 * user edits a cell the model is updated.
 *
 * @author Lennart Schedin
 *
 * @param <M> The type of model
 */
@SuppressWarnings("serial")
public class BeanTableModel<M> extends AbstractTableModel {

    private final List<M> rows = new ArrayList<>();
    private final List<BeanColumn> columns = new ArrayList<>();
    private final Class<?> beanClass;

    public BeanTableModel(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public void addColumn(String columnGUIName, String beanAttribute,
            EditMode editable) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(beanAttribute,
                    beanClass);
            columns.add(new BeanColumn(columnGUIName, editable, descriptor));
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void addColumn(String columnGUIName, String beanAttribute) {
        addColumn(columnGUIName, beanAttribute, EditMode.NON_EDITABLE);
    }

    public void addRow(M row) {
        addRows(Arrays.asList(row));

    }

    public void addRows(List<M> rows) {
        int first = this.rows.isEmpty() ? 0 : this.rows.size() - 1;
        rows.stream().forEach((row) -> {
            this.rows.add(row);
        });

        fireTableRowsInserted(first, this.rows.size() - 1);

        fireTableDataChanged();
    }

    
    public List<M> getRows() {
        return rows;
    }

    public void clearRows() {
        int size = rows.size();
        rows.clear();
        for (int i = 0; i < size; i++) {
            fireTableRowsDeleted(i, i);
        }
    }

    public void removeRow(M row) {
        int rowIndex = rows.indexOf(row);
        if (rows.remove(row)) {
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    public M getRow(int rowIndex) {
        return rows.get(rowIndex);
    }
    
    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        BeanColumn column = columns.get(columnIndex);
        M row = rows.get(rowIndex);

        Object result = null;
        try {
            result = column.descriptor.getReadMethod().invoke(row);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        M row = rows.get(rowIndex);
        BeanColumn column = columns.get(columnIndex);

        try {
            column.descriptor.getWriteMethod().invoke(row, value);
            fireTableCellUpdated(rowIndex, columnIndex);
            
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        BeanColumn column = columns.get(columnIndex);
        Class<?> returnType = column.descriptor.getReadMethod().getReturnType();
        return returnType;
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column).columnGUIName;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).editable == EditMode.EDITABLE;
    }


    public enum EditMode {

        NON_EDITABLE,
        EDITABLE;
    }

    /**
     * One column in the table
     */
    private static class BeanColumn {

        private String columnGUIName;
        private EditMode editable;
        private PropertyDescriptor descriptor;

        public BeanColumn(String columnGUIName, EditMode editable,
                PropertyDescriptor descriptor) {
            this.columnGUIName = columnGUIName;
            this.editable = editable;
            this.descriptor = descriptor;
        }
    }
}
