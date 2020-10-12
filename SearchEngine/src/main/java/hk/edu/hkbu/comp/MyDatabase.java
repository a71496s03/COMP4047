package hk.edu.hkbu.comp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyDatabase {
	File file;
	FileWriter myWriter ;
	BufferedWriter bw ;
	MyWebsite web;
	
	public MyDatabase(String filename){
		try {
			file = new File(filename); 
			if(file.createNewFile())
				System.out.println("Created File.");
			else
				System.out.println("Opened File.");
			myWriter = new FileWriter(file,true);
			bw = new BufferedWriter(myWriter);
		}catch(IOException e){
			e.printStackTrace();
		}
	} 
	
	public void newWebsite(String url) {
		
	}
	
}