public class Benchmark {
	private int key;
	private int columnA;
	private int columnB;
	private String filler;
	
	public Benchmark(int key, int columnA, int columnB, String filler){
		this.key = key;
		this.columnA = columnA;
		this.columnB = columnB;
		this.filler = filler;
	}
	
	public String toString(){
		return Integer.toString(key) + "|" + Integer.toString(columnA) + 
				"|" + Integer.toString(columnB) + "|" + filler;
	}
	
}
