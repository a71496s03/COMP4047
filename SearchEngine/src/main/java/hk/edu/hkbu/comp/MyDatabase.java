package hk.edu.hkbu.comp;

import java.io.*;
import java.util.*; 
import java.nio.file.Files;
import java.nio.file.Paths;

public class MyDatabase {
	FileWriter myWriter ;
	BufferedWriter bw ;
	AVLTree tree = new AVLTree();
	
	public void newWebsite(File file) {
		try {
			Scanner myReader = new Scanner(file);
			String url = myReader.nextLine();
			String title = myReader.nextLine();
			while (myReader.hasNextLine()) {//&&count<20
				String data = myReader.nextLine();
				String[] array = data.replaceAll("\\[", "").replaceAll("\\]", "").split(",");
				if (array[1].equals(" ")) 
					continue;
					//System.out.println(count+","+array[1]);
				tree.root=tree.insert(tree.root, url,title, array[1].replaceAll(" ", ""), Integer.parseInt(array[0]));
			}
			//tmp.output();
			myReader.close();
		} catch (FileNotFoundException e) {
		  System.out.println("An error occurred.");
		  e.printStackTrace();
		}
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
				if(s1.compareTo(s2)>0)
					set.add(s1);
		return set.toArray(new String[0]);
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
	
	public String[][] search(String keyword){
		Node target = findNode(keyword);
		if (target == null) {
			System.out.println(keyword+" not found");
			return null;
		}
		//System.out.println("size: "+target.map.size());
		return target.getTable();
	}
	
	public String[][] search(String[] keyword){
		Node[] node = new Node[keyword.length];
		for(int i=0; i < keyword.length;i++) {
			node[i]=findNode(keyword[i]);
			//System.out.println(node[i].key);
		}
		String[][]tmp = new String[findDuplicate(node).length][2];
		int count=0;
		for(String key:findDuplicate(node)) {
			Vector<Vector<Integer>> pos=new Vector<Vector<Integer>>();
			//System.out.println(key[0]+"."+key[1]);
			//System.out.println(key);
			for(int i=0; i < node.length;i++) {
				//System.out.println(i);
				pos.add(node[i].getPosition(key));
			}
			
			boolean q = true;
			for(int i: pos.get(0))
				for(int j=1; j < keyword.length;j++) 
					q = pos.get(j).contains(i+j);
			if(q) {
				String[] t = {key,node[0].getTitle(key)};
				tmp[count]=t;
			}
			count++;
		}
		return tmp;
	}
	
}