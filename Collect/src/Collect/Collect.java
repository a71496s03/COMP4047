package Collect;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.*;

import hk.edu.hkbu.comp.MyDatabase;

public class Collect {
	
	int x;
	int y;
	String currentURL;
	List<String> URLpool;
	List<String> TempURLpool;
	List<String> ProcessedURLpool;
	List<String> DeadURLpool;
	static List<String> DomainPool = new ArrayList<String>();
	String blacklistUrls = "blacklist/blacklistUrls.txt";
	String blacklistWord = "blacklist/blacklistWords.txt";
	//String generatedFile = "data/ProcessedURLpool.txt";
	String content = ""; // website content
	URL url; // URL object of current page
	MyDatabase db;
	
	public Collect() {
		
		this.currentURL = "http://www.comp.hkbu.edu.hk";
		this.x = 10;
		this.y = 100;
		URLpool = new ArrayList<String>();
		ProcessedURLpool = new ArrayList<String>();
		DeadURLpool = new ArrayList<String>();
		db = new MyDatabase();
	}
	
	public Collect(String currenturl,int x, int y,List<String> urlpool, List<String> processedpool, List<String> deadpool, MyDatabase db) {
		
		this.currentURL = currenturl;
		this.x = x;
		this.y = y;
		URLpool = urlpool;
		ProcessedURLpool = processedpool;
		DeadURLpool = deadpool;
		this.db = db;
	}

	public void getConection(String currentURL) throws IOException {
		URLConnection con = new URL(currentURL).openConnection();
		String redirect = con.getHeaderField("Location");
		while (redirect != null){
			con = new URL(redirect).openConnection();
			redirect = con.getHeaderField("Location");
		}
		
		//System.out.println("RequestProperties:" + con.getRequestProperties());
		//con.setRequestProperty("User-Agent", "Test");
		con.connect();	
		url = con.getURL();
		BufferedReader bufReader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
		
		String currentLine;
		while((currentLine = bufReader.readLine())!=null) {
			content += currentLine;
			//System.out.println(currentLine);
			/*Matcher Matcher = Pattern.compile(".*URL=(.*)\".*").matcher(content);
            if (Matcher.matches()) {
                String Link = Matcher.group(1);
                getConection(Link);
                break;
            }*/
		}
	}
	
	public void action() {
		
		System.out.println("Current URL : "+currentURL);
		
		try{
			
			 getConection(currentURL);
			
			 if(extractPage(currentURL) && url != null) {
					extractURL(currentURL);
					URLpool.remove(currentURL);
					ProcessedURLpool.add(currentURL);
					
					db.integrate();
			}
			 
		}catch(Exception e) {
			System.out.println("error in action URL");
			DeadURLpool.add(currentURL);
			URLpool.remove(currentURL);
			e.printStackTrace();
		}
		
		//print out status
		System.out.println("URLpool size : "+URLpool.size());
		System.out.println("URLpool : "+URLpool);
		System.out.println("ProcessedURLpool size : "+ProcessedURLpool.size());
		System.out.println("ProcessedURLpool : "+ProcessedURLpool);
		System.out.println();
		
		if(ProcessedURLpool.size() < y && URLpool.size() > 0) // if true, go to next URL;
			new Collect(URLpool.get(0),x,y,URLpool,ProcessedURLpool, DeadURLpool,db).action();
	}
	
	public boolean extractPage(String currentURL){
		boolean isVaild = false;
		String title = "";
		if (url == null) {
	    	return isVaild;
	    }
	    
		try {
			
			// Extract title
            Matcher titleMatcher = Pattern.compile(".*<title>(.*)</title>.*").matcher(content);
            if (title.trim().isEmpty() && titleMatcher.matches()) {
                title = titleMatcher.group(1);
            }
			
                        
            // Extract Content
    		ParserCallback callback = new ParserCallback();
    		String s = callback.loadPlainText(url);
    		TempURLpool = callback.getURLs(url);
    		//System.out.println(TempURLpool);
    		s = s.trim().replaceAll(" {2,}", " ");
    		
			//filter the invalid content
			String[] words = s.split(" ");
			ArrayList<String> uniqueWords = new ArrayList<String>();

			for (String w : words) {
				boolean isPass = true;
				w = w.toLowerCase();
				File file = new File(blacklistWord);
				Scanner scanner = new Scanner(file);

					while(scanner.hasNextLine()) {
						String data = scanner.nextLine().toLowerCase();
						if(w.equals(data))
							{isPass = false;break;}
					}
				if(isPass)
					{uniqueWords.add(w);}
				scanner.close();
			}
			String[][] output = new String[uniqueWords.size()][2];
			for(int i = 0; i < output.length; i++) {
				output[i][0] = Integer.toString(i);
				output[i][1] = uniqueWords.get(i);
			}
			
			//create file
			
			db.web(currentURL,title);
			/*
			String filename = "data/"+getDomain(currentURL)+".txt";
			File file = new File(filename);
			int i = 0;
			while(file.exists()) {
				filename = "data/"+getDomain(currentURL)+"_"+i+".txt";
				file = new File(filename);
				i++;
			}
			FileWriter filewriter = new FileWriter(filename) ;
			filewriter.write(currentURL+"\n");
			filewriter.write(title+"\n");*/
			String lineSeparator = System.lineSeparator();
			StringBuilder sb = new StringBuilder();
			boolean link = false;
			for(String[] row : output) {
				if(row[1].contains("@")) {
					row[1]=row[1].replace("@","");
					link = true;
				}
				db.add(row[1],Integer.parseInt(row[0]),link);
				sb.append(Arrays.toString(row))
				.append(lineSeparator);
			}
			for(String row : TempURLpool) {
				sb.append(row)
				.append(lineSeparator);
			}
			//filewriter.write(sb.toString());
			//filewriter.close();
			
			isVaild = true;
		} catch (IOException e) {
			System.out.println("Error URL in extract Page...");
			//e.printStackTrace();
			DeadURLpool.add(currentURL);

		}
		return isVaild;
	}
	
	
	public void extractURL(String currentURL) {
		String[] pool = new String[y];
				
		try {
			pool = getURLs().toArray(pool);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error in extract URL");
			DeadURLpool.add(currentURL);
		}
		
		List<String> foo = Arrays.asList(pool);
		System.out.println("get Link array :"+ foo);
		
		if(pool != null) {
			for(int i = 0; i < pool.length && pool[i] != null; i++) {
				
				String formatedURL = formatURL(pool[i]);
				
				if(URLpool.size() < x
						&& !ProcessedURLpool.contains(formatedURL)
						&& !URLpool.contains(formatedURL)
						&& !DeadURLpool.contains(formatedURL)
						&& !isBlackList(formatedURL)
						) {
					URLpool.add(formatedURL);
				}
			}
		}
	}
	
	private String formatURL(String url) {
		url = url.replaceFirst("http(s{0,1})://", "https://");
		if (url.charAt(url.length() - 1) == '/')
            url = url.substring(0, url.length() - 1);
        return url;
    }
	
	@SuppressWarnings("resource")
	boolean isBlackList(String url) {
		File file = new File(blacklistUrls);
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			while(scanner.hasNextLine()) {
				String data = scanner.nextLine();
				if(data.toLowerCase().equals(url.toLowerCase()))return true;
				data = data.split("http(s{0,1})://")[1];
				if(data.contains("*")) {
					String temp = data.split("\\*")[0].split("/")[0];
					Matcher matcher = Pattern.compile("http(s{0,1})://"+temp+"(.\\w+)?/.*").matcher(url);
	               if(matcher.matches())return true;
				}else {
					String[] temp = data.split("/");
					data = data.substring(temp[0].length());
					Matcher matcher = Pattern.compile("http(s{0,1})://"+temp[0]+"(.\\w+)?/"+data+"/?").matcher(url);
	                if(matcher.matches())return true;
				}
				
			}
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
			System.out.println(e);
			DeadURLpool.add(currentURL);
		}
		return false;
	}
	
	List<String> getURLs() throws IOException {
		
		List<String> filteredURL = new ArrayList<String>();
		int linkamount = 0;
		
		for(String s : TempURLpool) {
			String s_s = s.split("\\|")[0];
			String u = "";
			if(!isAbsURL(s_s)) {
				u = toAbsURL(s_s, this.url).toString();
			}
			else {
				u = s_s;
			}
			
			if(isAbsURL(u)) {
				filteredURL.add(u);
				linkamount++;
			}
		}
		
		System.out.println("Link amount : "+linkamount);
		
		return filteredURL;
	}
	boolean isAbsURL(String str) {
		return str.matches("^[a-z0-9]+://.+");
	}
	URL toAbsURL(String str, URL ref) throws MalformedURLException {
		URL url = null;
		String prefix =  ref.getHost();
		String outputURL = "";
		
		//System.out.println(str + " | " + prefix + " " + ref.getPath());
		
		if(str.startsWith("/")) {
			outputURL = prefix + str;
		}else if(str.startsWith("?")) {
			outputURL = prefix + ref.getPath() + str;
		}
		
		outputURL = ref.getProtocol() + "://" + outputURL.replace("//", "/");
		url = new URL(outputURL);
		return url; 
	}

	/*private String getDomain(String urlString) {
        if (url == null)
            return null;
        else {
            String domain = urlString.split("/")[2];
            if (domain.contains("?"))
                domain = domain.split("\\?")[0];
            if (!DomainPool.contains(domain))
                DomainPool.add(domain);
            return domain;
        }
    }*/
	
	
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		Collect collect;
		MyDatabase db = new MyDatabase();
		
		while(true) {
			System.out.println("Do you want to run Demo?(yes/no)");
			String isDemo = scanner.nextLine();
			if(isDemo.toLowerCase().equals("yes")) {
				collect = new Collect();
				collect.action();
				break;
			}
			else if(isDemo.toLowerCase().equals("no")){
				System.out.println("Please type the full stating URL: ");
				String URLstr = scanner.nextLine();
				System.out.println("Please type the value of X: ");
				int x = scanner.nextInt();
				System.out.println("Please type the value of Y: ");
				int y = scanner.nextInt();
				collect = new Collect(URLstr,x,y,new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>(),db);
				collect.action();
				break;
			}else {
				System.out.println("Type Error!Please try again.");
			}
		}
		
		System.out.println("\nFinished Collect URL!");
		/*try {
			new File(collect.generatedFile);
			/*System.out.println("Path:"+System.getProperty("user.dir"));
			for(File fileEntry : file.listFiles()){
				System.out.println(fileEntry.getName());
			}
			FileWriter myWriter = new FileWriter(collect.generatedFile);
			myWriter.write(collect.ProcessedURLpool.toString());
		    myWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error in new File");
		}*/
	}
}
