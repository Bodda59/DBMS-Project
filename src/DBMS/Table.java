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

    @Override
    public String toString() {
        return "Table{name='" + tableName + "', columns=" + Arrays.toString(columnsNames) + ", pages=" + pages + "}";
    }
}
