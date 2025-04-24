package DBMS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import DBMS.FileManager;
import java.util.Calendar;
import java.util.Date;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class DBApp
{
	static int dataPageSize = 2;
	
	public static void createTable(String tableName, String[] columnsNames)
	{
		if (FileManager.loadTable(tableName) != null) {
			System.out.println("This table already exists");
			return;
		}
		Table newTable = new Table(tableName, columnsNames);
		if (!FileManager.storeTable(tableName, newTable)) {
			System.out.println("Cannot store table properly ");
			return;
		}
		System.out.println("Table stored correctly");
	}

	//TRACING HERE BEGIN
	public static String getRecordsTrace(String[] records)
	{
		String result="[";
		for(int i=0;i<records.length;i++) {
			if(i==records.length-1)
				result+=records[i]+"]";
			else
				result+= records[i] + ", ";
		}
		return result;
	}

	public static String getvalueTraces(String[] columns)
	{
		String result="";
		for(String i: columns)
		{
			result+=i+",";
		}
		return result;
	}
	public static String getvalueTraces2(String[] columns)
	{
		if(columns.length>1)
		{
			String result="[";
			for(String i: columns)
			{
				if(i.equals((String)columns[columns.length-1]))
					result+=i+"";
				else result+=i+", ";
			}
			return result+"]";
		}
		else
		{
			String result="[";
			for(String i: columns)
			{
				if(i.equals((String)columns[columns.length-1]))
					result+=i+"";
				else result+=i+", ";
			}
			return result+"]";
		}

	}

	//TRACING HERE END

	public static void insert(String tableName, String[] record) {
		Calendar calendar=Calendar.getInstance();
		long currentTime=calendar.getTimeInMillis();

		Table table = FileManager.loadTable(tableName);
		if (table == null) {
			throw new IllegalArgumentException("Table " + tableName + " does not exist.");
		}

		//Tracing
		ArrayList<String> result=new ArrayList<>();
		for(String i:record)
		{
			result.add(i);
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

			//For Tracing
			long totalExecutionTime=calendar.getTimeInMillis()-currentTime;
			table.addTrace("Inserted:"+getRecordsTrace(record)+", at page number:0, execution time (mil):"+totalExecutionTime);
			FileManager.storeTable(tableName, table); // Save table metadata
			return;
		}

		// Case: Try inserting into the last page
		Integer lastPageNumber = pages.getLast();
		Page lastPage = FileManager.loadTablePage(tableName, lastPageNumber);

		if (lastPage.addRecord(record)) {
			// If successfully added, store back the modified page
			FileManager.storeTablePage(tableName, lastPageNumber, lastPage);
			System.out.println("Record added successfully in current page " + lastPageNumber);

			//Tracing
			long totalExecutionTime=currentTime-calendar.getTimeInMillis();
			table.addTrace("Inserted:"+getRecordsTrace(record)+", at page number:"+lastPageNumber+", execution time (mil):"+totalExecutionTime);
			FileManager.storeTable(tableName, table); // Save table metadata

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

			//Tracing
			long totalExecutionTime=calendar.getTimeInMillis()-currentTime;
			table.addTrace("Inserted:"+getRecordsTrace(record)+", at page number:"+newPageNumber+", execution time (mil):"+totalExecutionTime);
			FileManager.storeTable(tableName, table); // Save table metadata
		}
	}
	
	public static ArrayList<String []> select(String tableName)
	{
		Calendar calendar=Calendar.getInstance();
		long currentTime=calendar.getTimeInMillis();

		Table table = FileManager.loadTable(tableName);
		if (table == null) {
			throw new IllegalArgumentException("Table " + tableName + " does not exist.");
		}

		ArrayList<Integer> pages_numbers = table.getPages();
		ArrayList<String []> result = new ArrayList<>();
        for (Integer pagesNumber : pages_numbers) {
            Page page_data = FileManager.loadTablePage(tableName, pagesNumber);
            ArrayList<String[]> records = page_data.getRecords();
            result.addAll(records);
        }

		//For Tracing
		Page lastPage=FileManager.loadTablePage(tableName,table.getPages().getLast());
		int lastRecordlength=lastPage.getRecords().size();
		int recordsNumbers=((int) table.getPages().getLast()*dataPageSize)+lastRecordlength;

		//Tracing
		int pagesCount=(int) table.getPages().getLast()+1;
		long totalExecutionTime=calendar.getTimeInMillis()-currentTime;
		table.addTrace("Select all pages:"+pagesCount+", records:"+recordsNumbers+", execution time (mil):"+totalExecutionTime);
		FileManager.storeTable(tableName, table); // Save table metadata
		return result;
	}
	
	public static ArrayList<String []> select(String tableName, int pageNumber, int recordNumber)
	{
		Calendar calendar=Calendar.getInstance();
		long currentTime=calendar.getTimeInMillis();
		Table table = FileManager.loadTable(tableName);
		Page page_data = FileManager.loadTablePage(tableName, pageNumber);
		if (table == null || pageNumber>table.getPages().getLast() || page_data.getRecord(recordNumber)==null) {
			throw new IllegalArgumentException("The table doesn't exist or the page number doesn't exist or record");
		}

		ArrayList<Integer> pages_numbers = table.getPages();
		ArrayList<String []> result = new ArrayList<>();
		ArrayList<String[]> records = page_data.getRecords();
		result.add(records.get(recordNumber));

		//For Tracing
		long totalExecutionTime=calendar.getTimeInMillis()-currentTime;
		table.addTrace("Select pointer page:"+(int) pageNumber+", record:"+recordNumber+", total output count:1, execution time (mil):"+totalExecutionTime);
		FileManager.storeTable(tableName, table); // Save table metadata
		return result;
	}

	public static ArrayList<String []> select(String tableName, String[] cols, String[] vals)
	{
		Calendar calendar=Calendar.getInstance();
		long currentTime=calendar.getTimeInMillis();

		Table table = FileManager.loadTable(tableName);
		ArrayList<Integer> column_num = table.getColumnNumber(cols);

		//For Tracing
		int recordNumbersPerPage=0;
		ArrayList<String> traceRecords=new ArrayList<>();
		ArrayList<Integer> recordsPerPage=new ArrayList<>();

		if (table == null) {
			throw new IllegalArgumentException("Table " + tableName + " does not exist.");
		}

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
					//For Tracing
					recordNumbersPerPage++;
					recordsPerPage.add(1);
					//done
					result.add(record);
				}
			}
			//For Tracing
			if(recordsPerPage.size()!=0)
			{
				traceRecords.add("["+(int) pagesNumber+", "+recordsPerPage.size()+"]");
				recordsPerPage.clear();
			}

		}
		//For tracing, ASK here about the condition
		//TODO HERE
		String[] temp=new String[traceRecords.size()];
		for(int i=0;i<traceRecords.size();i++)
		{
			temp[i]=traceRecords.get(i);
		}
		long totalExecutionTime=calendar.getTimeInMillis()-currentTime;
		table.addTrace("Select condition:"+getvalueTraces2(cols)+"->"+getvalueTraces2(vals)+""+", Records per page:"+getvalueTraces2(temp)+", records:"+recordNumbersPerPage+", execution time (mil):"+totalExecutionTime);
		FileManager.storeTable(tableName, table); // Save table metadata
		return result;
	}



	
	public static String getFullTrace(String tableName)
	{
		Table table=FileManager.loadTable(tableName);
		ArrayList<String> temp=table.getAllTraces();
		String result="";
		for(String i: temp)
		{
			result+=i+"\n";
		}
		//For Tracing
		Page lastPage=FileManager.loadTablePage(tableName,table.getPages().getLast());
		int lastRecordlength=lastPage.getRecords().size();
		int recordsNumbers=((int) table.getPages().getLast()*dataPageSize)+lastRecordlength;

		int pagesCount=(int) table.getPages().getLast()+1;
		result+="Pages Count: "+pagesCount+", Records Count: "+recordsNumbers;
		return result;
	}
	
	public static String getLastTrace(String tableName)
	{
		Table table= FileManager.loadTable(tableName);
		if(table==null)
			return "No Table exists";
		return table.getLastTrace();
	}

	public static ArrayList<String []> validateRecords(String tableName){
	}
	public static void recoverRecords(String tableName, ArrayList<String[]> missing){

	}
	public static void createBitMapIndex(String tableName, String colName){

	}
	public static String getValueBits(String tableName, String colName, String value){

	}
	public static ArrayList<String []> selectIndex(String tableName, String[] cols, String[] vals){

	}
	public static void main(String []args) throws IOException
	{
		FileManager.reset();
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

		System.out.println("--------------------------------");
		System.out.println("Output of selecting the output by position:");
		ArrayList<String[]> result2 = select("student", 1, 1);
		for (String[] array : result2) {
			for (String str : array) {
				System.out.print(str + " ");
			}
			System.out.println();
		}

		System.out.println("--------------------------------");
		System.out.println("Output of selecting the output by column condition:");
		ArrayList<String[]> result3 = select("student", new String[]{"gpa"}, new
				String[]{"1.2"});
		for (String[] array : result3) {


			for (String str : array) {
				System.out.print(str + " ");
			}
			System.out.println();
		}
		System.out.println("--------------------------------");
		System.out.println("Full Trace of the table:");
		System.out.println(getFullTrace("student"));
		System.out.println("--------------------------------");
		System.out.println("Last Trace of the table:");
		System.out.println(getLastTrace("student"));
		System.out.println("--------------------------------");
		System.out.println("The trace of the Tables Folder:");
		System.out.println(FileManager.trace());
		FileManager.reset();
		System.out.println("--------------------------------");
		System.out.println("The trace of the Tables Folder after resetting:");
		System.out.println(FileManager.trace());


	}
}
