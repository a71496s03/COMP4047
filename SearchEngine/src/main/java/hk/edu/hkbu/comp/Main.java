package hk.edu.hkbu.comp;

import java.io.*;

public class Main {
    private final static int searchALL = 0;
    private final static int searchTitle = 1;
    private final static int searchText = 2;
    private final static int searchURL = 3;
    
	public static void main(String[] args){
		MyDatabase db = new MyDatabase();
		
		//File f1 = new File("../SearchEngine/data/");
		//f1.mkdir();
		//db.insert(f1, "aaa");
		//db.init();
		//String[] t ={"necessary","filmstrip_position","right"};
 		//String[][] tmp = db.search(t, searchText);
		//String[][] tmp=db.search("facebook", 0); 
		String[][] tmp=db.search("apple", 0); //key matching in the all of the website
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