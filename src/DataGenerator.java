import java.io.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class DataGenerator {
	static private int fileLength = 5000000;
	static private int stringLength = 10;
	static private String fileName1 = "/tmp/sortedData";
	static private String fileName2 = "/tmp/unsortedData";
	
	public static void main(String[] args) {
		generate(fileName1, true);
		generate(fileName2, false);
	}

	public static void generate(String fileName, boolean isSorted){
		ArrayList<Benchmark> benchmarkList = new ArrayList<Benchmark>();
		for(int i = 0; i < fileLength; i++){
			int key = i;
			int columnA = 1 + (int)(Math.random() * 50000);
			int columnB = 1 + (int)(Math.random() * 50000);
			String filler = generateStr();
			Benchmark bm = new Benchmark(key, columnA, columnB, filler);
			benchmarkList.add(bm);
		}
		
		if(isSorted == false){
			shuffle(benchmarkList);
		}
		
		try {
			PrintWriter writer = new PrintWriter(fileName);
			for(int i = 0; i < fileLength; i++){
				writer.println(benchmarkList.get(i).toString());
			}
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("finished");
		return;
	}
	
	public static void shuffle(ArrayList<Benchmark> bmList){
		Random rand = new Random();
		for(int i = 0; i < fileLength; i++){
			int j = rand.nextInt(fileLength);
			int k = rand.nextInt(fileLength);
			Collections.swap(bmList, j, k);
		}
	}
	
	public static String generateStr(){
		Random rand = new Random();
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < stringLength; i++){
			int num = rand.nextInt(26);
			sb.append((char)('a' + num));
		}
		return sb.toString();
	}
	
	
	
}
