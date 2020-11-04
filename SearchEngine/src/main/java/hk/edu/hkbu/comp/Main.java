package hk.edu.hkbu.comp;

import java.io.*;

public class Main {
    private final int searchALL = 0;
    private final int searchTitle = 1;
    private final int searchText = 2;
    private final int searchURL = 3;
    
	public static void main(String[] args){
		MyDatabase db = new MyDatabase();
		File f1 = new File("../Collect/data/b.txt");

        //db.append(f1,"aa");
        db.insert(f1,"2.5");
		/*db.init();
		String[][] tmp=db.search("facebook", 0); //phase matching in the all of the website
		//String[][] tmp=db.search("Huang", 0); //key matching in the all of the website
		if(tmp!=null)
			if(tmp.length!=0) 
				for(String[] array:tmp) 
					System.out.println(array[0]+" | "+array[1]);
			else
				System.out.println("No Matching Target");
		else
			System.out.println("Keyword(s) not exist");
		System.out.println("Done");*/
	}
	
	
}