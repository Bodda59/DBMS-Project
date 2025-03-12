package DBMS;


import java.io.Serializable;
import java.util.ArrayList;
import DBMS.DBApp;
public class Page implements Serializable {
    private int Page_Number;
    private ArrayList<String[]> Table_records;

    public Page(int pageNumber) {
        this.Page_Number = pageNumber;
        this.Table_records= new ArrayList<>();
    }

    public int getPageNumber() {
        return Page_Number;
    }

    public ArrayList<String[]> getRecords() {
        return Table_records;
    }

    public boolean addRecord(String[] record) {
        if (Table_records.size() < DBApp.dataPageSize) {
            Table_records.add(record);
            return true;
        }
        return false; // Page is full
    }

    public String[] getRecord(int index) {
        if (index >= 0 && index < Table_records.size()) {
            return Table_records.get(index);
        }
        return null; // Record not found
    }

    @Override
    public String toString() {
        return "Page{pageNumber=" + Page_Number + ", records=" + Table_records.size() + "}";
    }
}
