import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class PostgresSQLJDBC {
	static private String[] fileNames= {"sortedData", "unsortedData"};
	
	public static void main(String[] args) {
		Connection c = null;
	    try {
	    	Class.forName("org.postgresql.Driver");
	        c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/mydb", 
	        		"postgres", "110500");
	        c.setAutoCommit(false);
	        testPerformance(c);
	        c.close();
	    } catch (Exception e) {
	    	e.printStackTrace();
	        System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
	    }   
	}

	public static void testPerformance(Connection c){
		//first row table is sorted data, second row is unsorted data
		//column number corresponds to physical organization
		int iteration = 10;
		String[][] tables = new String[2][4];
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 4; j++)
				tables[i][j] = "testTable" + Integer.toString(4 * i + j);
		}
		
		//generate query numbers
		int[] nums = new int[iteration];
		Random rand = new Random();
		for(int i = 0; i < iteration; i++){
			nums[i] = rand.nextInt(50000) + 1;
		}
		
//		for(int i = 0; i < iteration; i++)
//			System.out.println(nums[i]);
		
		//test performance
		for(int i = 0; i < 2; i++){
			for(int j = 0; j < 4; j++){
				String tableName = tables[i][j];
				//createTable(c, tableName, j + 1);
				//bulkLoad(c, fileNames[i], tableName, i, j);
				
				//three different queries
				for(int queryOption = 1; queryOption < 4; queryOption++){
					long queryTime = 0;
					for(int k = 0; k < iteration; k++)
						queryTime += query(c, tableName, queryOption, nums[k]);
					queryTime = queryTime / iteration;
					System.out.println("Query" + queryOption + " " + tableName 
							+ " Using time " + queryTime);
				}
			}
		}
		
	} 
	
	public static long query(Connection c, String tableName, int queryOption, int num){
		long startTime = System.nanoTime();
		try {
			Statement stmt = c.createStatement();
			if(queryOption == 1){
				String sql = "SELECT * FROM " + tableName + " WHERE " + 
						tableName + ".columnA = " + Integer.toString(num);
				ResultSet rs = stmt.executeQuery(sql);
				//showResult(rs, tableName, queryOption);
			}
			else if(queryOption == 2){
				String sql = "SELECT * FROM " + tableName + " WHERE " + 
						tableName + ".columnB = " + Integer.toString(num);
				ResultSet rs = stmt.executeQuery(sql);
				//showResult(rs, tableName, queryOption);
			}
			else if(queryOption == 3){
				String sql = "SELECT * FROM " + tableName + " WHERE " + 
						tableName + ".columnA = " + Integer.toString(num) + 
						" AND " + tableName + ".columnB = " + Integer.toString(num);
				ResultSet rs = stmt.executeQuery(sql);
				//showResult(rs, tableName, queryOption);
			}
			else{}
			c.commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long endTime = System.nanoTime();
		//System.out.println("Query successfully " + tableName + " query option is " + queryOption);
		//System.out.println("Using time " + (endTime - startTime));
		return (endTime - startTime);
	}
	
	public static void showResult(ResultSet rs, String tableName, int queryOption) throws SQLException{
		System.out.println("table is " + tableName + " queryOption is " + queryOption);
		while(rs.next()){
			String filler = rs.getString("filler");
			System.out.println(filler);
		}
	}
	
	public static void createTable(Connection c, String tableName, int phyOption){
		long startTime = System.currentTimeMillis();
		try {
			Statement stmt = c.createStatement();
			String sql = "CREATE TABLE " + tableName + " " + 
			"(key INT PRIMARY KEY," + "columnA INT," + "columnB INT," + 
			"filler CHAR(15))";
			stmt.executeUpdate(sql);
			
			//according to parameter phyOption, choose a physical organization
			if(phyOption == 2){
				String indexSql = "CREATE INDEX ON " + tableName + " (columnA)";
				stmt.executeUpdate(indexSql);
			}
			else if(phyOption == 3){
				String indexSql = "CREATE INDEX ON " + tableName + " (columnB)";
				stmt.executeUpdate(indexSql);
			}
			else if(phyOption == 4){
				String indexSql = "CREATE INDEX ON " + tableName + " (columnA)";
				stmt.executeUpdate(indexSql);
				indexSql = "CREATE INDEX ON " + tableName + " (columnB)";
				stmt.executeUpdate(indexSql);
			}
			else{}
			
			c.commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Create table successfully " + tableName + " Using time " + (endTime - startTime));
	}
	
	public static void bulkLoad(Connection c, String fileName, String tableName, int row, int col){
		long startTime = System.currentTimeMillis();
		try {
			Statement stmt = c.createStatement();
//			System.out.println(tableName);
			String sql = "COPY " + tableName + " FROM " +  "'/tmp/" 
					+ fileName + "' (DELIMITER '|')";
			stmt.executeUpdate(sql);
			c.commit();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Load data successfully " + tableName + " row, col " + row 
				+ " " + col + " Using time " + (endTime - startTime));
	}
	
}
