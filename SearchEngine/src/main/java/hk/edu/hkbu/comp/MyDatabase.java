package hk.edu.hkbu.comp;

import java.io.*;
import java.util.*; 
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyDatabase {
	FileWriter myWriter ;
	BufferedWriter bw ;
	AVLTree tree = new AVLTree();
    HashMap<String, String> table = new HashMap<String, String>();  //table.put(url,title)
    private final String paths = "../Collect/data/";
    private final int searchALL = 0;
    private final int searchTitle = 1;
    private final int searchText = 2;
    private final int searchURL = 3;
    
    public void init() {
    	File folder = new File(paths);
		for(File fileEntry : folder.listFiles()){
			//System.out.println(fileEntry.getName());
			if (fileEntry.getName().equals("ProcessedURLpool.txt"))
				continue;
			//System.out.println(fileEntry.getName());
			newWebsite(fileEntry);
		}
    }
	
	public void newWebsite(File file) {
		try {
			Scanner myReader = new Scanner(file);
			String url = myReader.nextLine();
			String title = myReader.nextLine();
			addTitle(url,title);
			while (myReader.hasNextLine()) {//&&count<20
				String data = myReader.nextLine();
				String[] array = data.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
				if (array[1].equals(" ")) 
					continue;
					//System.out.println(count+","+array[1]);
				tree.root=tree.insert(tree.root, url, array[1].replaceAll(" ", ""), Integer.parseInt(array[0]));
			}
			//tmp.output();
			myReader.close();
		} catch (FileNotFoundException e) {
		  System.out.println("An error occurred.");
		  e.printStackTrace();
		}
	}
	
    public void addTitle(String url, String title) {
    	table.put(url,title);
    }

    public String getTitle(String url) {
    	return table.get(url);
    }
    
    public String[][] getTable(){
    	String[][] tmp = new String[table.size()][2];
    	String[] n = table.keySet().toArray(new String[table.size()]);
    	for(int i=0;i<n.length;i++) {
    		tmp[i][0]= n[i];
    		tmp[i][1]= table.get(n[i]);
    	}
    	return tmp;
    }
	
	public String[] findDuplicate(Node[] node){
		String[] tmp;
		tmp=compare(node[0].getAllURL(),node[1].getAllURL());
		for(int i =1;i<node.length;i++)
			tmp=compare(tmp,node[i].getAllURL());
		return tmp;
	}
	
	public String[] compare(String[] n1, String[]n2){
		Set<String> set = new HashSet<>();
		for(String s1:n1)
			for(String s2:n2)
				if(s1.compareTo(s2)==0)
					set.add(s1);
		return set.toArray(new String[0]);
	}
	
	public String[][] combine(String[][] n1, String[][]n2){
		if(n1==null&&n2==null)
			return null;
		else if(n1==null)
			return n2;
		else if(n2==null)
			return n1;
		HashMap<String, String> set = new HashMap<String, String>();
		for(String[] s1:n1)
			set.put(s1[0],s1[1]);
		for(String[] s2:n2)
			set.put(s2[0],s2[1]);
		String[][] tmp = new String[set.size()][2];
    	int count = 0;
    	for(String array :set.keySet()) {
    		tmp[count][0]=array;
    		tmp[count][1]=set.get(array);
    		count++;
    	}
    	return tmp;
	}
	
	public Node findNode(String key) {
	    Node current = tree.root;
	    while (current != null) {
	        if (current.key.compareTo(key)==0) {
	            break;
	        }
	        current = current.key.compareTo(key)<0 ? current.right : current.left;
	    }
	    return current;
	}
	
	public String[][] get(int i, String keyword){
		String[][] str = getTable();
		Vector<String[]> vector = new Vector<String[]>();
		for(String[] s:str)
			if(s[i].contains(keyword))
				vector.add(s);
		if (vector.size()==0)
			return null;
		String[][] t = new String[vector.size()][2];
		for(int n=0;n<vector.size();n++)
			t[n]=vector.get(n);
		return t;
	}
	
	public String[][] search(String keyword, int condition){
		switch(condition) {
			case searchALL:
				return combine(combine(get(0,keyword),get(1,keyword)),search(keyword,2));
			case searchURL:
				return get(0,keyword);
			case searchTitle:
				return get(1,keyword);
			case searchText:
				if (!keyword.matches("\\S+")) 
					return get(keyword.split(" "));
				else
					return get(keyword);
			default:
				System.out.println("Inavailable Searching Condition.");
				return null;
		}
	}
	
	public String[][] get(String keyword){
		Node target = findNode(keyword);
		if (target == null) {
			System.out.println(keyword+" not found");
			return null;
		}
		//System.out.println("size: "+target.map.size());
		String[][] t1 = new String[target.getAllURL().length][2];
		String[] t2 = target.getAllURL();
		for(int i=0; i < t2.length;i++) {
			t1[i][0]=t2[i];
			t1[i][1]=getTitle(t2[i]);
		}
		return t1;
	}
	
	public String[][] get(String[] keyword){
		Node[] node = new Node[keyword.length];
		//System.out.println("search phase");
		boolean match = true;
		for(int i=0; i < keyword.length;i++) {
			node[i]=findNode(keyword[i]);
			if (node[i]==null)
				match = false;
			//System.out.println(node[i].key);
		}
		if(!match) 
			return null;
		//System.out.println(node.length);
		Vector<String[]> tmp = new Vector<String[]>();
		//System.out.println(findDuplicate(node).length);
		for(String key:findDuplicate(node)) {
			Vector<Vector<Integer>> pos=new Vector<Vector<Integer>>();
			for(int i=0; i < node.length;i++) {
				//System.out.println(i);
				pos.add(node[i].getPosition(key));
			}
			//System.out.println("search phase :"+pos.size());
			
			for(int i: pos.get(0)) {
				boolean q = true;
				//System.out.println(i);
				for(int j=1; j < pos.size();j++) {
					q = pos.get(j).contains(i+j);
					//System.out.println(j+":"+(i+j));
					if(!q)
						break;
				}
				if(q) {
					String[] t = {key,getTitle(key)};
					tmp.add(t);
				}
			}
		}
		String[][] output = new String[tmp.size()][2];
		for(int i=0;i<tmp.size();i++) 
			output[i]=tmp.get(i);
		return output;
	}
	
}