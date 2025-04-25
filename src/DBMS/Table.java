package DBMS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Table implements Serializable {
    private String tableName;
    private String[] columnsNames;
    private ArrayList<Integer> pages;

    //For the Tracing functions
    private ArrayList<String> traces;

    //For the recovery of records
    private HashMap<String, ArrayList<String []>> originalRecords;

    public Table(String tableName, String[] columnsNames) {
        this.tableName = tableName;
        this.columnsNames = columnsNames;
        this.pages = new ArrayList<>();

        //For the tracing functions
        this.traces = new ArrayList<>();
        this.traces.add("Table created name:" + tableName + ", columnsNames:" + getColumnsTrace(columnsNames));

        //For the recovery of records
        this.originalRecords= new HashMap<>();
    }


    //For the tracing functions
    public String getColumnsTrace(String[] columnsNames) {
        String result = "[";
        for (int i = 0; i < columnsNames.length; i++) {
            if (i == columnsNames.length - 1)
                result += columnsNames[i] + "]";
            else
                result += columnsNames[i] + ", ";
        }
        return result;
    }

    public void addTrace(String newTrace) {
        traces.add(newTrace);
    }

    public String getLastTrace() {
        return traces.getLast();
    }

    public ArrayList<String> getAllTraces() {
        return traces;
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

    public ArrayList<Integer> getColumnNumber(String[] cols) {
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

    //For getting the original Records
    public boolean addOriginalRecords(String [] record, int index) {
        String indexed = Integer.toString(index);
        if (originalRecords.containsKey(indexed)) {
            originalRecords.get(indexed).add(record);
        } else {
            ArrayList<String []> newArray = new ArrayList<>();
            newArray.add(record);
            originalRecords.put(indexed, newArray);
        }
        return true;
    }

    public HashMap<String, ArrayList<String[]>> getOriginalRecords() {
        return originalRecords;
    }
}
