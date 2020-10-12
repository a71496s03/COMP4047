package hk.edu.hkbu.comp;

import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyDatabase {
	FileWriter myWriter ;
	BufferedWriter bw ;
	AVLTree tree = new AVLTree();
	
	public void newWebsite(String path) {
		try {
			File file = new File(path);
			Scanner myReader = new Scanner(file);
			String url = myReader.nextLine();
			String title = myReader.nextLine();
			//int count=0;
			while (myReader.hasNextLine()) {//&&count<20
				String data = myReader.nextLine();
				String[] array = data.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
				if (!array[1].equals(" ")) {
					//System.out.println(count+","+array[1]);
					tree.root=tree.insert(tree.root, url,title, array[1], Integer.parseInt(array[0]));
				}
				//count++;
			}
			//tmp.output();
			myReader.close();
		} catch (FileNotFoundException e) {
		  System.out.println("An error occurred.");
		  e.printStackTrace();
		}
	}
	
	public 
}