package hk.edu.hkbu.comp;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class getIndex_Thr implements Callable{
	String paths = "../SearchEngine/data/";
	String str;
	int i; 
	
	getIndex_Thr(String str,int i){
		this.str=str;
		this.i=i;
	}

	@Override
	public Object call() throws Exception {
		boolean found = false;
		try {
			File file = getFile(paths+i+".txt");
			Scanner myReader = new Scanner(file);
			while(myReader.hasNextLine()&&!found) {
    			String data = myReader.nextLine();
    			if(data.compareTo(str)==0) {
    				return i;
    			}
    		}
    		myReader.close();
		}catch(Exception e) {e.printStackTrace();}
		
		return -1;
	}
	
    public File getFile(String filename) {
    	File file = new File(filename);
    	try {
    	if (!file.exists())
    		file.createNewFile(); 
    	}catch(Exception e) {
        	e.printStackTrace();
        }
    	return file;
    }
}