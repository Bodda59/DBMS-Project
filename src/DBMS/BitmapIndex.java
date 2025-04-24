package DBMS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class BitmapIndex implements Serializable {
    private HashMap<String, BitSet> index; // maps value -> bitmap
    private ArrayList<String> uniqueValues;
    private int recordCount;               // total number of records in the table

    public void setUniqueValues(ArrayList<String> uniqueValues) {
        this.uniqueValues = uniqueValues;
    }

    public ArrayList<String> getUniqueValues() {
        return this.uniqueValues;
    }

    public BitmapIndex() {
        this.index = new HashMap<>();
        this.uniqueValues=new ArrayList<>();
        this.recordCount = 0;
    }

    public HashMap<String, BitSet> getIndex() {
        return index;
    }

    public void setIndex(HashMap<String, BitSet> index) {
        this.index = index;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
    public void addUnique(String str)
    {
        this.uniqueValues.add(str);
    }
}
