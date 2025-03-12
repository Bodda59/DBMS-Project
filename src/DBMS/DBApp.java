package DBMS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import DBMS.FileManager;

import org.junit.Test;

public class DBApp
{
	static int dataPageSize = 20;
	
	public static void createTable(String tableName, String[] columnsNames)
	{
		if (FileManager.loadTable(tableName) != null) {
			System.out.println("This table already exists");
			return;
		}
		Table newTable = new Table(tableName, columnsNames);
		if (!FileManager.storeTable(tableName, newTable)) {
			System.out.println("Cannot store table proparly ");
			return;
		}
		System.out.println("Table stored correctly");
	}

	public static void insert(String tableName, String[] record) {
		Table table = FileManager.loadTable(tableName);
		if (table == null) {
			throw new IllegalArgumentException("Table " + tableName + " does not exist.");
		}

		ArrayList<Integer> pages = table.getPages();
		int sizeOfPages = pages.size();

		// Case: No pages exist, create the first page
		if (sizeOfPages == 0) {
			Page page = new Page(0);
			page.addRecord(record);
			table.addPage(0);
			FileManager.storeTablePage(tableName, 0, page);
			FileManager.storeTable(tableName, table); // Save table metadata
			System.out.println("Table first had no pages, created page 0 and added record.");
			return;
		}

		// Case: Try inserting into the last page
		Integer lastPageNumber = pages.getLast();
		Page lastPage = FileManager.loadTablePage(tableName, lastPageNumber);

		if (lastPage.addRecord(record)) {
			// If successfully added, store back the modified page
			FileManager.storeTablePage(tableName, lastPageNumber, lastPage);
			System.out.println("Record added successfully in current page " + lastPageNumber);
		} else {
			// If page is full, create a new page
			int newPageNumber = lastPageNumber + 1;
			Page newPage = new Page(newPageNumber);
			newPage.addRecord(record);
			table.addPage(newPageNumber);

			// Store new page and update table
			FileManager.storeTablePage(tableName, newPageNumber, newPage);
			FileManager.storeTable(tableName, table); // Save table metadata
			System.out.println("New page " + newPageNumber + " created for adding record.");
		}
	}
	
	public static ArrayList<String []> select(String tableName)
	{
		Table table = FileManager.loadTable(tableName);
		ArrayList<Integer> pages_numbers = table.getPages();
		ArrayList<String []> result = new ArrayList<>();
        for (Integer pagesNumber : pages_numbers) {
            Page page_data = FileManager.loadTablePage(tableName, pagesNumber);
            ArrayList<String[]> records = page_data.getRecords();
            result.addAll(records);
        }
		return result;
	}
	
	public static ArrayList<String []> select(String tableName, int pageNumber, int recordNumber)
	{
		Table table = FileManager.loadTable(tableName);
		ArrayList<Integer> pages_numbers = table.getPages();
		ArrayList<String []> result = new ArrayList<>();
		Page page_data = FileManager.loadTablePage(tableName, pageNumber);
		ArrayList<String[]> records = page_data.getRecords();
		result.add(records.get(recordNumber-1));
		return result;
	}

	public static ArrayList<String []> select(String tableName, String[] cols, String[] vals)
	{
		Table table = FileManager.loadTable(tableName);
		String [] column_names = table.getColumnsNames();
		ArrayList<Integer> column_num = getColumnNumber(cols, column_names);
		ArrayList<Integer> pages_numbers = table.getPages();
		ArrayList<String []> result = new ArrayList<>();
		for (Integer pagesNumber : pages_numbers) {
			Page page_data = FileManager.loadTablePage(tableName, pagesNumber);
			ArrayList<String[]> records = page_data.getRecords();
			for (String[] record : records) {
				boolean isRecordValid = true;

				for (int i = 0; i < column_num.size(); i++) {
					int colIndex = column_num.get(i);
					if (!record[colIndex].equals(vals[i])) {
						isRecordValid = false;
						break;
					}
				}

				if (isRecordValid) {
					result.add(record);
				}
			}
		}
		return result;
	}
	public static ArrayList<Integer> getColumnNumber(String[] cols, String[] column_names) {
		ArrayList<Integer> res = new ArrayList<>();
		for (String col : cols) {
			boolean found = false;
			for (int j = 0; j < column_names.length; j++) {
				if (col.equals(column_names[j])) {
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
	
	public static String getFullTrace(String tableName)
	{
		
		return "";
	}
	
	public static String getLastTrace(String tableName)
	{
		
		return "";
	}
	
	
	public static void main(String []args) throws IOException
	{

		String[] cols = {"id","name","major","semester","gpa"};
		createTable("student", cols);
		String[] r1 = {"1", "stud1", "CS", "5", "0.9"};
		insert("student", r1);
		String[] r2 = {"2", "stud2", "BI", "7", "1.2"};
		insert("student", r2);
		String[] r3 = {"3", "stud3", "CS", "2", "2.4"};
		insert("student", r3);
		String[] r4 = {"4", "stud4", "DMET", "9", "1.2"};
		insert("student", r4);
		String[] r5 = {"5", "stud5", "BI", "4", "3.5"};
		insert("student", r5);
		System.out.println("Output of selecting the whole table content:");
		ArrayList<String[]> result1 = select("student");
		for (String[] array : result1) {
			for (String str : array) {
				System.out.print(str + " ");
			}
			System.out.println();
		}
		System.out.println(" -------------------------------");
				System.out.println("Output of selecting the output by position:");
		ArrayList<String[]> result2 = select("student", 0, 3);
		for (String[] array : result2) {
			for (String str : array) {
				System.out.print(str + " ");
			}
			System.out.println();
		}
		System.out.println(" --------------------------------");
		System.out.println("Output of selecting the output by column condition:");
		ArrayList<String[]> result3 = select("student", new String[]{"gpa"}, new String[]{"1.2"});
		for (String[] array : result3) {
			for (String str : array) {
				System.out.print(str + " ");
			}
			System.out.println();
		}

		//FileManager.reset();
	}
}
