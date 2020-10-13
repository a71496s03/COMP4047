package hk.edu.hkbu.comp;

import java.io.*;

public class Main {
	public static void main(String[] args){
		MyDatabase db = new MyDatabase();
		File folder = new File("../Collect/data/");
		for(File fileEntry : folder.listFiles()){
			//System.out.println(fileEntry.getName());
			if (fileEntry.getName().equals("ProcessedURLpool.txt"))
				continue;
			//System.out.println(fileEntry.getName());
			db.newWebsite(fileEntry);
		}
		//String[][] tmp=db.search("content");
		String[][] tmp=db.search(new String[]{"compatible","content","ie"});
		if(tmp!=null)
			for(String[] array:tmp) {
				System.out.println(array[0]+" | "+array[1]);
			}
	}
	
	
}