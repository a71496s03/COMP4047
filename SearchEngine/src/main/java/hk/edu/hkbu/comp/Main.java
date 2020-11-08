package hk.edu.hkbu.comp;

import java.io.*;
import java.util.concurrent.ExecutionException;

public class Main {
    private final int searchALL = 0;
    private final int searchTitle = 1;
    private final int searchText = 2;
    private final int searchURL = 3;
    private final int searchLink = 4;
    
	public static void main(String[] args) throws InterruptedException, ExecutionException{
		MyDatabase db = new MyDatabase();
		System.out.println(db.getIndex("mphil"));
		System.out.println(db.getIndex("and"));
		System.out.println(db.getIndex("phd"));
		System.out.println(db.getIndex("degrees"));
		//File f1 = new File("../SearchEngine/data/");
		//f1.mkdir();
		//db.insert(f1, "aaa");
		//db.init();
		//String[] t ={"necessary","filmstrip_position","right"};
 		//String[][] tmp = db.search(t, searchText);
		//String[][] tmp=db.search("facebook", 0); 
		/*int i=0;
		String[] m={"a","b","c","d","e","f","g","h","i","j","k","l","m","n"};
		for(String s:m) {
			db.add(i++, s);
		} 
		db.integrate();*/
		String[][] tmp=db.search("a", 0); //key matching in the all of the website
		//System.out.println(db.getIndex("huang"));
		if(tmp!=null)
			if(tmp.length!=0) 
				for(String[] array:tmp) 
					System.out.println(array[0]+" | "+array[1]);
			else
				System.out.println("No Matching Target");
		else
			System.out.println("Keyword(s) not exist");
		System.out.println("Done");
	}
	
	
}