package hk.edu.hkbu.comp;

import java.io.*;
import java.util.*; 
//import java.nio.file.Files;
//import java.nio.file.Paths;

public class MyDatabase {
	FileWriter myWriter ;
	BufferedWriter bw ;
    private final String paths = "../Collect/data/";
    private final int FileNo = 40;
    private int KeywordNo;
    private HashMap<String, String> map;
    private HashMap<String, Vector<Integer>> content;
    private String infoPath = "../Collect/data/info.txt";
    private String mapPath = "../Collect/data/map.txt";
    private String url;
    private final int searchALL = 0;
    private final int searchTitle = 1;
    private final int searchText = 2;
    private final int searchURL = 3;
    
    public MyDatabase() {
    	try {
    		KeywordNo = 0;
	    	File file = new File(infoPath);
	    	if (file.exists() && !file.isDirectory()) {
	    		Scanner myReader = new Scanner(file);
	    		if(myReader.hasNextInt())
	    			KeywordNo = myReader.nextInt();
	    		myReader.close();
	         } 
	    	FileWriter filewriter = new FileWriter(file);
	    	filewriter.write(KeywordNo+"");
	    	filewriter.close();
	    	file = new File(mapPath);
	    	if (file.exists() && !file.isDirectory()) {
	    		Scanner myReader = new Scanner(file);
	    		while(myReader.hasNextLine()) {
	    			String[] data = myReader.nextLine().split(",");
	    			map.put(data[0], data[1]);
	    		}
	    		myReader.close();
	    	}else file.createNewFile();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public String[][] combine(String[][] n1,String[][]n2) {
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
    
    public String[][] search(String[] arr, int condition) {
    	switch(condition) {
		    case searchALL:
		    	return combine(combine(search(arr,searchTitle),search(arr,searchURL)),search(arr,searchText));
		    case searchTitle:
		    	return search(arr.toString(),searchTitle);
		    case searchText:
		    	boolean empty = false;
		    	int[] n = new int[arr.length];
		    	int i=0;
		    	for(String str:arr) {
		    		int index=getIndex(str);
		    		if(index==-1) {
		    			empty = !empty;
		    			break;
		    		}
		    		n[i++]=index;
		    	}
		    	if(empty)
		    		return null;
		    	try {
		    		Vector<HashMap<String,int[]>> a = new Vector<HashMap<String,int[]>>();  //read content of text file into hashMap
			    	for(int j=0;j<n.length;j++) {
			    		HashMap<String,int[]> tmp=new HashMap<String,int[]>();
			    		File wordfile = getFile(paths+j+"/"+arr[j]+".txt");
			    		Scanner myReader = new Scanner(wordfile);
				    	while(myReader.hasNextLine()) {
				    		String[] data = myReader.nextLine().split("@");
				    		String[] items = data[1].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
				    		int[] results = new int[items.length];
				    		for (int k = 0; k < items.length; k++) 
				    			results[k] = Integer.parseInt(items[i]);
				    		tmp.put(data[0],results);
				    	}
				    	myReader.close();
				    	a.add(tmp);
			    	}
			    	Vector<String> urls=new Vector<String>();   //found same url of n keywords
			    	for(String s:a.get(0).keySet()) {
			    		boolean match = true;
			    		for(int j=1;j<a.size();j++) {
			    			if(!a.get(j).containsKey(s))
			    				match=!match;
			    		}
			    		if(match)
			    			urls.add(s);
			    	}
			    	if(urls.size()==0)
			    		return null;
			    	Vector<String> matchURL=new Vector<String>();   //find url having the continues position keyword
			    	for(String url:urls) {
				    	for(int j:a.get(0).get(url)) {
				    		int found = 1;
				    		for(int k=1;k<a.size();k++) {
				    			for(int o:a.get(k).get(url))
					    			if(o==j)
					    				found++;
				    		}
				    		if(found==a.size())
				    			matchURL.add(url);
				    	}
			    	}
			    	if(matchURL.size()==0)
			    		return null;
			    	else
			    		return vectorTo2Darray(matchURL);
		    	}catch(Exception e) {
		    		e.printStackTrace();
		    	}
		    	return null;
		    case searchURL:
		    	return search(arr.toString(),searchURL);
	    	default:
	    		System.out.println("Invalid Searching Condition");
	    		break;
	    }
    	return null;
    }
    
	public String[][] search(String str, int condition) {
		Vector<String> tmp=new Vector<String>();
    	switch(condition) {
	    	case searchALL:
	    		return combine(combine(search(str,searchTitle),search(str,searchURL)),search(str,searchText));
	    	case searchTitle:
	    		for(String url:map.keySet()) {
	    			String title = map.get(url);
	    			if(title.matches(str))
	    				tmp.add(url);
	    		}
	    		return vectorTo2Darray(tmp);
	    	case searchText:
	    		try {
	    			int index=getIndex(str);
		    		if(index==-1)
			    		return null;
		    		File wordfile = getFile(paths+index+"/"+str+".txt");
		    		Scanner myReader = new Scanner(wordfile);
			    	Vector<String> vec=new Vector<String>();
			    	while(myReader.hasNextLine()) {
			    		String[] data = myReader.nextLine().split("@");
			    		vec.add(data[0]);
			    	}
			    	myReader.close();
			    	return vectorTo2Darray(vec);
	    		}catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    		return null;
	    	case searchURL:
	    		for(String url:map.keySet()) {
	    			if(url.matches(str))
	    				tmp.add(url);
	    		}
	    		return vectorTo2Darray(tmp);
    		default:
    			System.out.println("Invalid Searching Condition");
    			break;
    	}
    	return null;
    }
	
	public int getIndex(String str) {
		int index=-1;
		boolean found = false;
		for(int i=1;i<=FileNo&&!found;i++) {
			try {
				File file = getFile(paths+i+".txt");
				Scanner myReader = new Scanner(file);
	    		while(myReader.hasNextLine()) {
	    			String data = myReader.nextLine();
	    			if(data.compareTo(str)==0) {
	    				index=Integer.valueOf(i);
	    				found=true;
	    				break;
	    			}
	    		}
	    		myReader.close();
			}catch(Exception e) {e.printStackTrace();}
		}
		return index;
	}
    
    public String[][] vectorTo2Darray(Vector<String> vector){
    	String[][] arr=new String[vector.size()][2];
		String[] n = vector.toArray(new String[vector.size()]);
		for(int i=0;i<n.length;i++) {
			arr[i][0]=n[i];
			arr[i][1]=map.get(n[i]);
		}
		return arr;
    }
    
    public void integrate() {
    	content.forEach((word, position) -> {
    		int i = ++KeywordNo%FileNo;
    		try {
	    		File fileInfo = getFile(paths+i+".txt");
		        insert(fileInfo, word);
		        File file = getFile(paths+i+"/"+word+".txt");
		        String str = url+"@"+position.toString();
		        append(file,str);
		        FileWriter filewriter = new FileWriter(infoPath);
		    	filewriter.write(KeywordNo+"");
		    	filewriter.close();
	    	}catch(Exception e) {
	        	e.printStackTrace();
	        }
        });
    }
    
    public File getFile(String filename) {
    	File file = new File(filename);
    	try {
    	if (!file.exists() && file.isDirectory())
    		file.createNewFile(); 
    	}catch(Exception e) {
        	e.printStackTrace();
        }
    	return file;
    }
    
    public void add(String word, int position) {
    	Vector<Integer> tmp;
    	word = word.toLowerCase();
    	if(content.containsKey(word))
    		tmp = content.get(word);
        else 
        	tmp = new Vector<Integer>();
        tmp.add(position);
        content.put(word,tmp);
    }
    
    public void webInfo(String url, String title) {
    	this.url=url;
    	if(map.containsKey(url)) {
    		System.out.println("Overlapping url");
    	}else {
    		map.put(url, title);
    		File file = new File(mapPath);
    		append(file, url+","+title);
    	}
    }
    
    public void append(File file, String str) {
    	try {
    		FileWriter fw = new FileWriter(file,true);
    	  	BufferedWriter bw = new BufferedWriter(fw);
    	  	PrintWriter pw = new PrintWriter(bw);
    	  	pw.println(str);
    	  	pw.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void insert(File file, String str) {
    	try {
    		if (file.exists() && !file.isDirectory()) {
	    		Scanner myReader = new Scanner(file);
	    		Vector<String> vector = new Vector<String>();
	    		String tmp;
	    		boolean inserted = false;
	    		while(myReader.hasNextLine()) {
	    			tmp = myReader.nextLine();
	    			if(tmp.compareTo(str)>0&&!inserted) {
	    				vector.add(str);
	    				inserted=!inserted;
	    			}
	    			vector.add(tmp);
	    		}
	    		new FileWriter(file.getAbsolutePath(), false).close();
	    		vector.forEach((n)->append(file, n));
	    		myReader.close();
	    	}else {
	    		file.createNewFile();
	    		append(file,str);
	    	}
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    /*
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
		keyword= keyword.toLowerCase();
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
				System.out.println("Invailable Searching Condition.");
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
	*/
}