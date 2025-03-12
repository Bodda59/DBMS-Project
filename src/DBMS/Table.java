package DBMS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Table implements Serializable {
    private String tableName;
    private String[] columnsNames;
    private ArrayList<Integer> pages;

    public Table(String tableName, String[] columnsNames) {
        this.tableName = tableName;
        this.columnsNames = columnsNames;
        this.pages = new ArrayList<>();
    }

    public String getTableName() {
        return tableName;
    }

    public String[] getColumnsNames() {
        return columnsNames;
    }

    public ArrayList<Integer> getPages() {
        return pages;
    }

    public void addPage(int pageNumber) {
        pages.add(pageNumber);
    }

    public  ArrayList<Integer> getColumnNumber(String[] cols) {
        ArrayList<Integer> res = new ArrayList<>();
        for (String col : cols) {
            boolean found = false;
            for (int j = 0; j < columnsNames.length; j++) {
                if (col.equals(columnsNames[j])) {
                    res.add(j);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException("Column " + col + " does not exist in table.");
            }
        }
        return res;
    }
}
