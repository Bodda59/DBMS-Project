package DBMS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

import DBMS.FileManager;

import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static DBMS.FileManager.*;

public class DBApp
{
	static int dataPageSize = 2;
	
	public static void createTable(String tableName, String[] columnsNames)
	{
		if (loadTable(tableName) != null) {
			System.out.println("This table already exists");
			return;
		}
		Table newTable = new Table(tableName, columnsNames);
		if (!storeTable(tableName, newTable)) {
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

/*	public static void insert(String tableName, String[] record) {
		Calendar calendar=Calendar.getInstance();
		long currentTime=calendar.getTimeInMillis();

		Table table = loadTable(tableName);
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
			storeTablePage(tableName, 0, page);
			storeTable(tableName, table); // Save table metadata
			System.out.println("Table first had no pages, created page 0 and added record.");

			//For Tracing
			long totalExecutionTime=calendar.getTimeInMillis()-currentTime;
			table.addTrace("Inserted:"+getRecordsTrace(record)+", at page number:0, execution time (mil):"+totalExecutionTime);
			storeTable(tableName, table); // Save table metadata
			updateBitmapIndexes(tableName, record);
			return;
		}

		// Case: Try inserting into the last page
		Integer lastPageNumber = pages.getLast();
		Page lastPage = loadTablePage(tableName, lastPageNumber);

		if (lastPage.addRecord(record)) {
			// If successfully added, store back the modified page
			storeTablePage(tableName, lastPageNumber, lastPage);
			System.out.println("Record added successfully in current page " + lastPageNumber);

			//Tracing
			long totalExecutionTime=currentTime-calendar.getTimeInMillis();
			table.addTrace("Inserted:"+getRecordsTrace(record)+", at page number:"+lastPageNumber+", execution time (mil):"+totalExecutionTime);
			storeTable(tableName, table); // Save table metadata

		} else {
			// If page is full, create a new page
			int newPageNumber = lastPageNumber + 1;
			Page newPage = new Page(newPageNumber);
			newPage.addRecord(record);
			table.addPage(newPageNumber);

			// Store new page and update table
			storeTablePage(tableName, newPageNumber, newPage);
			storeTable(tableName, table); // Save table metadata
			System.out.println("New page " + newPageNumber + " created for adding record.");

			//Tracing
			long totalExecutionTime=calendar.getTimeInMillis()-currentTime;
			table.addTrace("Inserted:"+getRecordsTrace(record)+", at page number:"+newPageNumber+", execution time (mil):"+totalExecutionTime);
			storeTable(tableName, table);// Save table metadata
			updateBitmapIndexes(tableName, record);
		}
	}*/
	public static void insert(String tableName, String[] record) {
		Calendar calendar = Calendar.getInstance();
		long currentTime = calendar.getTimeInMillis();

		Table table = loadTable(tableName);
		if (table == null) {
			throw new IllegalArgumentException("Table " + tableName + " does not exist.");
		}

		ArrayList<String> result = new ArrayList<>();
		for (String i : record) {
			result.add(i);
		}

		ArrayList<Integer> pages = table.getPages();
		int sizeOfPages = pages.size();

		if (sizeOfPages == 0) {
			Page page = new Page(0);
			page.addRecord(record);
			table.addPage(0);
			storeTablePage(tableName, 0, page);
			storeTable(tableName, table);

			long totalExecutionTime = calendar.getTimeInMillis() - currentTime;
			table.addTrace("Inserted:" + getRecordsTrace(record) + ", at page number:0, execution time (mil):" + totalExecutionTime);

			// ✅ Update bitmap indexes
			updateBitmapIndexesIfPresent(tableName, record);

			//TODO DID SOMETHING HERE
			table.addOriginalRecords(record,0);
			//TODO FINISHED THAT SOMETHING

			storeTable(tableName, table);

			return;
		}

		Integer lastPageNumber = pages.getLast();
		Page lastPage = loadTablePage(tableName, lastPageNumber);

		if (lastPage.addRecord(record)) {
			storeTablePage(tableName, lastPageNumber, lastPage);

			long totalExecutionTime = calendar.getTimeInMillis() - currentTime;
			table.addTrace("Inserted:" + getRecordsTrace(record) + ", at page number:" + lastPageNumber + ", execution time (mil):" + totalExecutionTime);

			// ✅ Update bitmap indexes
			updateBitmapIndexesIfPresent(tableName, record);

			//TODO DID SOMETHING HERE
			table.addOriginalRecords(record,lastPageNumber);
			//TODO FINISHED THAT SOMETHING

			storeTable(tableName, table);


		} else {
			int newPageNumber = lastPageNumber + 1;
			Page newPage = new Page(newPageNumber);
			newPage.addRecord(record);
			table.addPage(newPageNumber);

			storeTablePage(tableName, newPageNumber, newPage);
			storeTable(tableName, table);

			long totalExecutionTime = calendar.getTimeInMillis() - currentTime;
			table.addTrace("Inserted:" + getRecordsTrace(record) + ", at page number:" + newPageNumber + ", execution time (mil):" + totalExecutionTime);

			// ✅ Update bitmap indexes
			updateBitmapIndexesIfPresent(tableName, record);
			//TODO DID SOMETHING HERE
			table.addOriginalRecords(record,newPageNumber);
			//TODO FINISHED THAT SOMETHING

			storeTable(tableName, table);


		}
	}
	private static void updateBitmapIndexesIfPresent(String tableName, String[] record) {
		Table table = loadTable(tableName);
		String[] columnNames = table.getColumnsNames();

		boolean hasAnyIndex = false;

		// First pass: check if any index exists
		for (String colName : columnNames) {
			if (loadTableIndex(tableName, colName) != null) {
				hasAnyIndex = true;
				break;
			}
		}

		if (!hasAnyIndex) return; // 🚫 No bitmap index exists, skip

		// Second pass: update each bitmap index
		for (int i = 0; i < columnNames.length; i++) {
			String colName = columnNames[i];
			BitmapIndex bitmapIndex = loadTableIndex(tableName, colName);
			if (bitmapIndex == null) continue;

			HashMap<String, BitSet> indexMap = bitmapIndex.getIndex();
			String valueInRecord = record[i];

			for (Map.Entry<String, BitSet> entry : indexMap.entrySet()) {
				BitSet bitset = entry.getValue();
				if (entry.getKey().equals(valueInRecord)) {
					bitset.set(bitmapIndex.getRecordCount(), true);  // 1 for match
				} else {
					bitset.set(bitmapIndex.getRecordCount(), false); // 0 otherwise
				}
			}

			bitmapIndex.setRecordCount(bitmapIndex.getRecordCount() + 1);
			storeTableIndex(tableName, colName, bitmapIndex);
		}
	}
	
	public static ArrayList<String []> select(String tableName)
	{
		Calendar calendar=Calendar.getInstance();
		long currentTime=calendar.getTimeInMillis();

		Table table = loadTable(tableName);
		if (table == null) {
			throw new IllegalArgumentException("Table " + tableName + " does not exist.");
		}

		ArrayList<Integer> pages_numbers = table.getPages();
		ArrayList<String []> result = new ArrayList<>();
        for (Integer pagesNumber : pages_numbers) {
            Page page_data = loadTablePage(tableName, pagesNumber);
            ArrayList<String[]> records = page_data.getRecords();
            result.addAll(records);
        }

		//For Tracing
		Page lastPage= loadTablePage(tableName,table.getPages().getLast());
		int lastRecordlength=lastPage.getRecords().size();
		int recordsNumbers=((int) table.getPages().getLast()*dataPageSize)+lastRecordlength;

		//Tracing
		int pagesCount=(int) table.getPages().getLast()+1;
		long totalExecutionTime=calendar.getTimeInMillis()-currentTime;
		table.addTrace("Select all pages:"+pagesCount+", records:"+recordsNumbers+", execution time (mil):"+totalExecutionTime);
		storeTable(tableName, table); // Save table metadata
		return result;
	}
	
	public static ArrayList<String []> select(String tableName, int pageNumber, int recordNumber)
	{
		Calendar calendar=Calendar.getInstance();
		long currentTime=calendar.getTimeInMillis();
		Table table = loadTable(tableName);
		Page page_data = loadTablePage(tableName, pageNumber);
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
		storeTable(tableName, table); // Save table metadata
		return result;
	}

	public static ArrayList<String []> select(String tableName, String[] cols, String[] vals)
	{
		Calendar calendar=Calendar.getInstance();
		long currentTime=calendar.getTimeInMillis();

		Table table = loadTable(tableName);
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
			Page page_data = loadTablePage(tableName, pagesNumber);
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
		storeTable(tableName, table); // Save table metadata
		return result;
	}



	
	public static String getFullTrace(String tableName)
	{
		Table table= loadTable(tableName);
		ArrayList<String> temp=table.getAllTraces();
		String result="";
		for(String i: temp)
		{
			result+=i+"\n";
		}
		//For Tracing
		Page lastPage= loadTablePage(tableName,table.getPages().getLast());
		int lastRecordlength=lastPage.getRecords().size();
		int recordsNumbers=((int) table.getPages().getLast()*dataPageSize)+lastRecordlength;

		int pagesCount=(int) table.getPages().getLast()+1;

		String temps="[";
		for(int i=0;i<table.getIndexedColumns().size();i++)
		{
			if(i==table.getIndexedColumns().size()-1)
			{
				temps+=table.getIndexedColumns().get(i);
			}else{
				temps+=table.getIndexedColumns().get(i)+", ";
			}
		}
		temps+="]";
		result+="Pages Count: "+pagesCount+", Records Count: "+recordsNumbers+", Indexed Columns: "+temps;
		return result;
	}
	
	public static String getLastTrace(String tableName)
	{
		Table table= loadTable(tableName);
		if(table==null)
			return "No Table exists";
		return table.getLastTrace();
	}

	public static ArrayList<String []> validateRecords(String tableName){
		Table table= FileManager.loadTable(tableName);
		int pagesNumbers= table.getPages().size();
		HashMap<String, ArrayList<String[]>> recordsMap= table.getOriginalRecords();

		ArrayList<String[]> result=new ArrayList<>();
		for(int i=0;i<pagesNumbers;i++)
		{
			Page page= FileManager.loadTablePage(tableName,i);
			if(page==null)
			{
				ArrayList <String[]> temp=  recordsMap.get(Integer.toString(i));
				if (temp == null) {
					System.out.println("No records found in originalRecords for page Number: " + i);
				}else
				{
					for(int j=0;j<temp.size();j++)
					{
						result.add(temp.get(j));
					}
				}

			}
		}
		table.addTrace("Validating records: "+result.size()+" records missing.");
		storeTable(tableName,table);
		return result;
	}

	public static void recoverRecords(String tableName, ArrayList<String[]> missing){
		int recordsCount=0;
		ArrayList<String> pagesrecords= new ArrayList<>();
		Table table= FileManager.loadTable(tableName);
		int pagesNumbers= table.getPages().size();
		HashMap<String, ArrayList<String[]>> recordsMap= table.getOriginalRecords();

		for(int i=0;i<pagesNumbers;i++)
		{

			Page page= FileManager.loadTablePage(tableName,i);
			if(page==null)
			{
				ArrayList <String[]> temp=  recordsMap.get(Integer.toString(i));
				if (temp == null) {
					System.out.println("No records found in originalRecords for page Number: " + i);
				}else
				{
					Page newPage= new Page(i);
					for(int j=0;j<temp.size();j++)
					{
						newPage.addRecord(temp.get(j));
						recordsCount++;
					}
					pagesrecords.add(""+i);
					storeTablePage(tableName,i,newPage);
				}

			}
			storeTable(tableName,table);
		}
		String temps="[";
		for(int i=0;i<pagesrecords.size();i++)
		{
			if(i==pagesrecords.size()-1)
			{
				temps+=pagesrecords.get(i)+"]";
			}
			else
			{
				temps+=pagesrecords.get(i)+", ";
			}

		}


		table.addTrace("Recovering "+recordsCount+" in pages: "+temps+".");
		storeTable(tableName,table);
	}
	public static void createBitMapIndex(String tableName, String colName) {
		long startTime = System.currentTimeMillis();
		Table table = loadTable(tableName);
		String[] columns = table.getColumnsNames();
		int columnIndex = -1;

		// Find the column index
		for (int i = 0; i < columns.length; i++) {
			if (colName.equals(columns[i])) {
				columnIndex = i;
				break;
			}
		}

		if (columnIndex == -1) return; // Column not found

		BitmapIndex bitmapIndex = new BitmapIndex();
		HashMap<String, BitSet> indexMap = new HashMap<>();
		ArrayList<String> uniqueValues = new ArrayList<>();
		ArrayList<Integer> pagesNumbers = table.getPages();
		int globalRowIndex = 0;

		// First Pass: Get all unique values and initialize BitSets
		for (int pageNum : pagesNumbers) {
			Page page = loadTablePage(tableName, pageNum);
			ArrayList<String[]> rows = page.getRecords();

			for (String[] row : rows) {
				String value = row[columnIndex];
				if (!indexMap.containsKey(value)) {
					indexMap.put(value, new BitSet());
					uniqueValues.add(value);
				}
			}
		}

		// Second Pass: Set bits in the BitSets
		for (int pageNum : pagesNumbers) {
			Page page = loadTablePage(tableName, pageNum);
			ArrayList<String[]> rows = page.getRecords();

			for (String[] row : rows) {
				String value = row[columnIndex];
				BitSet bitset = indexMap.get(value);
				bitset.set(globalRowIndex); // Set 1 for this row index
				globalRowIndex++;
			}
		}

		// Prepare the BitmapIndex object
		bitmapIndex.setIndex(indexMap);
		bitmapIndex.setRecordCount(globalRowIndex); // Total rows processed
		bitmapIndex.setUniqueValues(uniqueValues);
		table.addIndexedColumns(colName);
		long endTime = System.currentTimeMillis();
		long totalExecutionTime=endTime-startTime;
		table.addTrace("Index created for column: "+colName+", execution time (mil):"+totalExecutionTime);
		storeTable(tableName,table);
		storeTableIndex(tableName, colName, bitmapIndex);
	}
	public static String getValueBits(String tableName, String colName, String value) {
		BitmapIndex bitmapIndex = loadTableIndex(tableName, colName);
		HashMap<String, BitSet> indexMap = bitmapIndex.getIndex();

		BitSet bits = indexMap.get(value);
		if (bits == null) {
			return null;
		}

		int totalBits = bitmapIndex.getRecordCount(); // total number of rows
		StringBuilder bitString = new StringBuilder();

		for (int i = 0; i < totalBits; i++) {
			bitString.append(bits.get(i) ? '1' : '0');
		}

		return bitString.toString();
	}
	public static ArrayList<String []> selectIndex(String tableName, String[] cols, String[] vals)
    {
		ArrayList<String> indexedColumn = new ArrayList<>();
		long startTime = System.currentTimeMillis();
		ArrayList<String []> result = new ArrayList<>();
		ArrayList<String> notIndex = new ArrayList<>();
		ArrayList<String> newVals = new ArrayList<>();
		BitSet andResult = new BitSet();
		for(int i=0;i<cols.length;i++)
		{
			BitmapIndex b = loadTableIndex(tableName,cols[i]);
			if(b==null)
			{
				notIndex.add(cols[i]);
				newVals.add(vals[i]);
			}
			else {
				indexedColumn.add(cols[i]);
				if(andResult.isEmpty()){
					andResult=b.getIndex().get(vals[i]);
				}
				else{
					andResult = andBitSets(andResult,b.getIndex().get(vals[i]));
				}
			}
		}
		if(notIndex.size()==cols.length)
		{
			result =  select(tableName,cols,vals);
		}
		else if(notIndex.isEmpty())
		{
			Table table = loadTable(tableName);
			ArrayList<Integer> pageNum = table.getPages();
			for(int i=0;i<pageNum.size();i++)
			{
				Page pageData = loadTablePage(tableName,pageNum.get(i));
				ArrayList<String[]> records = pageData.getRecords();
				for(int j=0;j<records.size();j++)
				{
					if(andResult.get(j + (dataPageSize*i)))
					{
						result.add(records.get(j));
					}
				}
			}
		}
		else{
			result = selectIndexHelp(tableName,andResult,notIndex,newVals);
		}
		long endTime = System.currentTimeMillis();
		long totalExecutionTime = endTime - startTime;
		ArrayList<String> column = new ArrayList<>();
		ArrayList<String> values =new ArrayList<>();
		for(int i=0;i<cols.length;i++)
		{
			column.add(cols[i]);
			values.add(vals[i]);
		}
		Table tableHelp = loadTable(tableName);
		if(indexedColumn.size()==cols.length)
		{
			tableHelp.addTrace("Select index condition:"+column.toString()+"->"+values.toString()+", "+"Indexed columns: "+indexedColumn.toString()+", "+"Indexed selection count: 1"+", "+"Final count: 1, execution time (mil):"+totalExecutionTime);
		}
		else if(notIndex.size()==cols.length)
		{
			tableHelp.addTrace("Select index condition:"+column.toString()+"->"+values.toString()+", "+"Non Indexed: "+notIndex.toString()+", "+"Indexed selection count: 1"+", "+"Final count: 1, execution time (mil):"+totalExecutionTime);
		}
		else
		{
			tableHelp.addTrace("Select index condition:"+column.toString()+"->"+values.toString()+", "+"Indexed columns: "+indexedColumn.toString()+", "+"Indexed selection count: 1"+", "+"Non Indexed: "+notIndex.toString()+", "+"Final count: 1, execution time (mil):"+totalExecutionTime);
		}
		storeTable(tableName,tableHelp);
		return result;
	}
	public static ArrayList<String[]> selectIndexHelp(String tableName, BitSet andResult, ArrayList<String> cols, ArrayList<String> vals) {
		ArrayList<String[]> result = new ArrayList<>();
		Table table = loadTable(tableName);
		ArrayList<Integer> pageNum = table.getPages();
		ArrayList<Integer> colIndex = getPositionsInSecondArray(cols, table.getColumnsNames());

		for (int i = 0; i < pageNum.size(); i++) {
			Page pageData = loadTablePage(tableName, pageNum.get(i));
			ArrayList<String[]> records = pageData.getRecords();

			for (int j = 0; j < records.size(); j++) {
				if (andResult.get(j + (dataPageSize * i))) {
					boolean allMatch = true;

					for (int k = 0; k < cols.size(); k++) {
						if (!records.get(j)[colIndex.get(k)].equals(vals.get(k))) {
							allMatch = false;
							break;
						}
					}

					if (allMatch) {
						result.add(records.get(j));
					}
				}
			}
		}
		return result;
	}
	public static BitSet andBitSets(BitSet a, BitSet b) {
		int maxLength = Math.max(a.length(), b.length());

		BitSet paddedA = (BitSet) a.clone();
		BitSet paddedB = (BitSet) b.clone();

		paddedA.set(maxLength); // ensure the BitSet reaches the same length
		paddedA.clear(maxLength); // remove the last added bit
		paddedB.set(maxLength);
		paddedB.clear(maxLength);

		paddedA.and(paddedB);
		return paddedA;
	}

	public static ArrayList<Integer> getPositionsInSecondArray(ArrayList<String> first, String[] second) {
		ArrayList<Integer> positions = new ArrayList<>();

		for (String target : first) {
			boolean found = false;
			for (int i = 0; i < second.length; i++) {
				if (second[i].equals(target)) {
					positions.add(i);
					found = true;
					break;
				}
			}
			if (!found) {
				throw new IllegalArgumentException("Element '" + target + "' not found in second array.");
			}
		}

		return positions;
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

        String[] r4 = {"4", "stud4", "CS", "9", "1.2"};
        insert("student", r4);

        String[] r5 = {"5", "stud5", "BI", "4", "3.5"};
        insert("student", r5);

        //////// This is the code used to delete pages from the table
        System.out.println("File Manager trace before deleting pages: "
                +FileManager.trace());
                String path =
                        FileManager.class.getResource("FileManager.class").toString();
        File directory = new File(path.substring(6,path.length()-17) +
                File.separator
                + "Tables//student" + File.separator);
        File[] contents = directory.listFiles();
        int[] pageDel = {0,2};
        for(int i=0;i<pageDel.length;i++)
        {
            contents[pageDel[i]].delete();
        }
////////End of deleting pages code
        System.out.println("File Manager trace after deleting pages: "
                +FileManager.trace());
                ArrayList<String[]> tr = validateRecords("student");
        System.out.println("Missing records count: "+tr.size());
        recoverRecords("student", tr);
        System.out.println("--------------------------------");
        System.out.println("Recovering the missing records.");
        tr = validateRecords("student");
        System.out.println("Missing record count: "+tr.size());
        System.out.println("File Manager trace after recovering missing records: "
                +FileManager.trace());
                System.out.println("--------------------------------");
        System.out.println("Full trace of the table: ");
        System.out.println(getFullTrace("student"));
        FileManager.reset();

	}
	/*public static void main(String []args) throws IOException
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
		createBitMapIndex("student", "gpa");
		createBitMapIndex("student", "major");
		System.out.println("Bitmap of the value of CS from the major index: "+getValueBits("student", "major", "CS"));
		System.out.println("Bitmap of the value of 1.2 from the gpa index: "+getValueBits("student", "gpa", "1.2"));
		String[] r4 = {"4", "stud4", "CS", "9", "1.2"};
		insert("student", r4);
		String[] r5 = {"5", "stud5", "BI", "4", "3.5"};
		insert("student", r5);
		System.out.println("After new insertions:");
		System.out.println("Bitmap of the value of CS from the major index: "+getValueBits("student", "major", "CS"));
		System.out.println("Bitmap of the value of 1.2 from the gpa index: "+getValueBits("student", "gpa", "1.2"));
		System.out.println("Output of selection using index when all columns of the select conditions are indexed:");
		ArrayList<String[]> result1 = selectIndex("student", new String[]
				{"major","gpa"}, new String[] {"CS","1.2"});
		for (String[] array : result1) {
			for (String str : array) {
				System.out.print(str + " ");
			}
			System.out.println();
		}
		System.out.println("Last trace of the table: "+getLastTrace("student"));
		System.out.println(" --------------------------------");
		System.out.println("Output of selection using index when only one columnof the columns of the select conditions are indexed:");
		ArrayList<String[]> result2 = selectIndex("student", new String[]
				{"major","semester"}, new String[] {"CS","5"});
		for (String[] array : result2) {
			for (String str : array) {
				System.out.print(str + " ");
			}
			System.out.println();
		}
		System.out.println("Last trace of the table: "+getLastTrace("student"));
		System.out.println(" --------------------------------");
		System.out.println("Output of selection using index when some of the columns of the select conditions are indexed:");
		ArrayList<String[]> result3 = selectIndex("student", new String[]
				{"major","semester","gpa" }, new String[] {"CS","5", "0.9"});
		for (String[] array : result3) {
			for (String str : array) {
				System.out.print(str + " ");
			}
			System.out.println();
		}
		System.out.println("Last trace of the table: "+getLastTrace("student"));
		System.out.println(" --------------------------------");
		System.out.println("Full Trace of the table:");
		System.out.println(getFullTrace("student"));
		System.out.println(" --------------------------------");
		System.out.println("The trace of the Tables Folder:");
		System.out.println(FileManager.trace());
	}*/
}
