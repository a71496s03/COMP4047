package hk.edu.hkbu.comp;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask; 


public class MyDatabase{
	FileWriter myWriter ;
	BufferedWriter bw ;
    private final String paths = "../SearchEngine/data/";
    private final int FileNo = 10;
    private int KeywordNo;
    private HashMap<String, String> map = new HashMap<String, String>();
    private HashMap<String, Vector<Integer>> content = new HashMap<String, Vector<Integer>>();
    private HashMap<String, Vector<Integer>> linkcontent = new HashMap<String, Vector<Integer>>();
    private String infoPath = "../SearchEngine/data/info.txt";
    private String mapPath = "../SearchEngine/data/map.txt";
    private String url;
    private final int searchALL = 0;
    private final int searchTitle = 1;
    private final int searchText = 2;
    private final int searchURL = 3;
    private final int searchLink = 4;
    
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
	    			String[] data = myReader.nextLine().split("`");
	    			if(data.length<2)
	    				map.put(data[0]," ");
	    			else
	    				map.put(data[0], data[1]);
	    		}
	    		myReader.close();
	    	}else file.createNewFile();
	    	for(int i=0;i<FileNo;i++) {
	    	File folder = new File(paths+i+"/");
		    if(!folder.exists())
		    	folder.mkdir();
	    	}
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
    
    public String[][] func2(String[] arr, boolean searchlink) throws InterruptedException, ExecutionException{
    	boolean empty = false;
    	int[] n = new int[arr.length];
    	int i=0;
    	for(String str:arr) {
    		str = str.toLowerCase();
    		int index=getIndex(str);
    		if(index==-1) {
    			empty = true;
    			break;
    		}
    		n[i++]=index;
    	}
    	if(empty)
    		return null;
    	try {
    		Vector<HashMap<String,int[]>> a = new Vector<HashMap<String,int[]>>();  //read content of text file into hashMap
	    	for(int j=0;j<arr.length;j++) {
	    		HashMap<String,int[]> tmp=new HashMap<String,int[]>();
	    		File wordfile = getFile(paths+n[j]+"/"+arr[j]+".txt");
	    		Scanner myReader = new Scanner(wordfile);
		    	while(myReader.hasNextLine()) {
		    		String[] data = myReader.nextLine().split("@");		//read line
		    		String[] items;
		    		
		    		if(searchlink)
		    			items = data[2].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(","); //get word position of link content
		    		else
		    			items = data[1].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").split(","); 
		    		int[] results = new int[items.length];	
		    		for (int k = 0; k < items.length; k++) { 
		    			if(items[k].equals("")) {
		    				continue;
		    			}
		    			results[k] = Integer.parseInt(items[k]);			//convert to int array
		    		}
		    		tmp.put(data[0],results);
		    	}
		    	myReader.close();
		    	a.add(tmp);
	    	}
	    	Vector<String> urls=new Vector<String>();   //find same url of n keywords
	    	for(String s:a.get(0).keySet()) {
	    		boolean match = true;
	    		for(int j=1;j<a.size();j++) {
	    			if(!a.get(j).containsKey(s))
	    				match=false;
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
		    		for(int k=1;k<a.size();k++)
		    			for(int o:a.get(k).get(url))
			    			if((o-k)==j) {
			    				found++;
			    			}
		    		if(found==a.size()) {
		    			matchURL.add(url);
		    			break;
		    		}
		    	}
	    	}
	    	if(matchURL.size()==0)
	    		return null;
	    	else
	    		return vectorTo2Darray(reduce(matchURL));
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public Vector<String> reduce(Vector<String> v) {
    	HashMap<String, String> hm = new HashMap<String, String>();
    	for(int i=0;i<v.size();i++) {
    		hm.put(v.get(i),"");
    	}
    	Vector<String> s = new Vector<String>();
    	
    	for(String  p:hm.keySet()){
    		s.add(p);
    	}
    	return s; 
    }
    
    public String[][] search(String[] arr, int condition) throws InterruptedException, ExecutionException {
    	switch(condition) {
		    case searchALL:
		    	return combine(combine(search(arr,searchTitle),search(arr,searchURL)),search(arr,searchText));
		    case searchTitle:
		    	return search(R_str(arr),searchTitle);
		    case searchLink:
		    	return func2(arr,true);
		    case searchText:
		    	return func2(arr,false);
		    case searchURL:
		    	return search(R_str(arr),searchURL);
	    	default:
	    		System.out.println("Invalid Searching Condition");
	    		break;
	    }
    	return null;
    }
    
    public String R_str(String[]str_a) {
    	String sb = str_a[0];
        for(int i = 1; i < str_a.length; i++) {
           sb+=" "+str_a[i];
        }
        return sb;
    }
    
    public String[][] func(String str,boolean searchlink){
    	try {
			int index=getIndex(str);
    		if(index==-1)
	    		return null;
    		File wordfile = getFile(paths+index+"/"+str+".txt");
    		Scanner myReader = new Scanner(wordfile);
	    	Vector<String> vec=new Vector<String>();
	    	while(myReader.hasNextLine()) {
	    		String[] data = myReader.nextLine().split("@");
	    		if(searchlink) 
	    			if(data[2].replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "").equals(""))
	    				continue;
	    		vec.add(data[0]);
	    	}
	    	myReader.close();
	    	return vectorTo2Darray(vec);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
	public String[][] search(String str, int condition) {
		//str = str.toLowerCase();
		Vector<String> tmp=new Vector<String>();
    	switch(condition) {
	    	case searchALL:
	    		return combine(combine(search(str,searchTitle),search(str,searchURL)),search(str,searchText));
	    	case searchTitle:
	    		for(String url:map.keySet()) {
	    			String title = map.get(url);
	    			if(title.contains(str))
	    				tmp.add(url);
	    		}
	    		return vectorTo2Darray(tmp);
	    	case searchLink:
	    		str = str.toLowerCase();
	    		return func(str,true);
	    	case searchText:
	    		str = str.toLowerCase();
	    		return func(str,false);
	    	case searchURL:
	    		for(String url:map.keySet()) {
	    			if(url.contains(str))
	    				tmp.add(url);
	    		}
	    		return vectorTo2Darray(tmp);
    		default:
    			System.out.println("Invalid Searching Condition");
    			break;
    	}
    	return null;
    }
	
	public int getIndex(String str) throws InterruptedException, ExecutionException {
		//boolean found = false;
		str = str.toLowerCase();
		/* old method
		for(int i=0;i<FileNo;i++) {
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
		}
		*/
		FutureTask[] getIndexTasks = new FutureTask[10]; 
		for(int i=0;i<=9;i++) {
			Callable Thr = new getIndex_Thr(str,i);
			getIndexTasks[i] = new FutureTask(Thr);
			
		    Thread t = new Thread(getIndexTasks[i]); 
		    t.start();
		}
		
		for(int i=0;i<=9;i++) {
			if((int)getIndexTasks[i].get()>=0) {
				return (int)getIndexTasks[i].get();
			}
		}
		return -1;
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
    
    public void integrate() throws InterruptedException, ExecutionException {
    	for(String word: content.keySet()) {
    		if(word.compareTo("con")!=0) { 			//"con.txt" file is not allowed to create in Window
	    		int i = getIndex(word);
	    		boolean news = false;
	    		if(i==-1) { 			//not exist
		    		i = (++KeywordNo)%FileNo;
		    		news= true;
	    		}
		    	try {
				    File file = getFile(paths+i+"/"+word+".txt");
				    String str = url+"@"+content.get(word).toString()+"@";
				    if(linkcontent.containsKey(word)) {
				    	str+=linkcontent.get(word).toString();
				    }
				    else {
				    	str+="[]";
				    }
				    append(file,str);
				    FileWriter filewriter = new FileWriter(infoPath);
				    filewriter.write(KeywordNo+"");
				    filewriter.close();
				    if(news) {
					    File fileInfo = getFile(paths+i+".txt");
					    insert(fileInfo, word);
				    }
			    }catch(Exception e) {
			        e.printStackTrace();
			    }
    		}
    	}
    	this.content.clear();
    	this.linkcontent.clear();
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
    
    public void add(String word, int position, boolean link) {
    	Vector<Integer> tmp=new Vector<Integer>();        
    	Vector<Integer> links=new Vector<Integer>();
    	word = word.toLowerCase();        
    	if(link) {
        	if(linkcontent.containsKey(word))
        		links = linkcontent.get(word);
        	links.add(position);
        	linkcontent.put(word,links);
        }
    	
	    if(content.containsKey(word))
	    	tmp = content.get(word);
        tmp.add(position);
        content.put(word,tmp);
    }
    
    public void web(String url, String title) {
    	this.url=url;
	    if(map.containsKey(url)) {
	    	//System.out.println("Overlapping url");
	    }else {
	    	map.put(url, title);
	    	File file = new File(mapPath);
	    	append(file, url+"`"+title);
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
	    	Scanner myReader = new Scanner(file);
	    	Vector<String> vector = new Vector<String>();
	    	String tmp;
	    	boolean inserted = false;
	    	if(!myReader.hasNextLine())
	    		append(file,str);
	    	else {
		    	while(myReader.hasNextLine()) {
		    		tmp = myReader.nextLine();
		    		if(tmp.compareTo(str)>0&&!inserted) {
		    			vector.add(str);
		    			inserted=true;
		    		}
		    		vector.add(tmp);
		    	}
		    	if(!inserted)
		    		vector.add(str);
		    	new FileWriter(file.getAbsolutePath(), false).close();
		    	for(int i = 0 ; i<vector.size();i++) 
		    		append(file,vector.get(i));
	    	}
	    	myReader.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
}