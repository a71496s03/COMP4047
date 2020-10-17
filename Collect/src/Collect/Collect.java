package Collect;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

import javax.swing.text.html.parser.ParserDelegator;

public class Collect {
	
	int x;
	static int y;
	String currentURL;
	List<String> URLpool;
	List<String> ProcessedURLpool;
	List<String> DeadURLpool;
	static List<String> DomainPool = new ArrayList<String>();
	String blacklistUrls = "blacklist/blacklistUrls.txt";
	String blacklistWord = "blacklist/blacklistWords.txt";
	String generatedFile = "data/ProcessedURLpool.txt";
	URL url; // URL object of current page
	
	public Collect() {
		
		try {
			this.currentURL = "http://www.comp.hkbu.edu.hk";
			url = new URL(currentURL);
			this.x = 10;
			this.y = 100;
			URLpool = new ArrayList<String>();
			ProcessedURLpool = new ArrayList<String>();
			DeadURLpool = new ArrayList<String>();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Collect(String currenturl,int x, int y,List<String> urlpool, List<String> processedpool, List<String> deadpool) {
		
		try {
			this.currentURL = currenturl;
			url = new URL(currentURL);
			this.x = x;
			this.y = y;
			URLpool = urlpool;
			ProcessedURLpool = processedpool;
			DeadURLpool = deadpool;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void action() {
		
		System.out.println("Current URL : "+currentURL);
		URLpool.remove(currentURL);
		ProcessedURLpool.add(currentURL);
		if(extractPage(currentURL)) {
			extractURL(currentURL);
		}
		
		System.out.println("URLpool size : "+URLpool.size());
		System.out.println("URLpool : "+URLpool);
		System.out.println("ProcessedURLpool size : "+ProcessedURLpool.size());
		System.out.println("ProcessedURLpool : "+ProcessedURLpool);
		System.out.println();
		if(ProcessedURLpool.size() < y && URLpool.size() > 0)
			new Collect(URLpool.get(0),x,y,URLpool,ProcessedURLpool, DeadURLpool).action();
	}
	
	public boolean extractPage(String currentURL){
		boolean isVaild = false;
		String content = "";
		String title = "";

		 boolean isSuccessful = false;
	        if (url == null) {
	            return isSuccessful;
	        }
	        BufferedReader in = null;
		try {

			//get content in URL
			in = new BufferedReader(new InputStreamReader(url.openStream()));
            String currentLine;
			
            while ((currentLine = in.readLine()) != null) {
            	if (currentLine.contains("<title>ERROR: The requested URL could not be retrieved</title>")) {
                    System.err.println("Can not collect this page.");
                    return isVaild;
                }
            	// Extract title
                Matcher titleMatcher = Pattern.compile(".*<title>(.*)</title>.*").matcher(currentLine);
                if (title.trim().isEmpty() && titleMatcher.matches()) {
                    title = titleMatcher.group(1);
                }
                content += currentLine;
            }
			in.close();
			
			//filter the invalid content
			String[] words = content.split("[0-9\\W]+");
			ArrayList<String> uniqueWords = new ArrayList<String>();

			for (String w : words) {
				boolean isPass = true;
				w = w.toLowerCase();
				File file = new File(blacklistWord);
				Scanner scanner = new Scanner(file);

					while(scanner.hasNextLine()) {
						String data = scanner.nextLine();
						if(w.contains(data))
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
			filewriter.write(title+"\n");
			String lineSeparator = System.lineSeparator();
			StringBuilder sb = new StringBuilder();
			
			for(String[] row : output) {
				sb.append(Arrays.toString(row))
				.append(lineSeparator);
			}
			filewriter.write(sb.toString());
			filewriter.close();
			
			isVaild = true;
		} catch (IOException e) {
			System.out.println("Error URL in extract Page...");
			System.out.println(e);
			DeadURLpool.add(currentURL);
			content = "<h1>Unable to download the page</h1>" + currentURL;

		}
		return isVaild;
	}
	
	
	public void extractURL(String currentURL) {
		try {
			String[] pool = new String[y];
			
			try {
				URLConnection con = new URL(currentURL).openConnection();
				con.connect();
				con.getInputStream();
				//System.out.println(con.getURL());
				currentURL = con.getURL().toString();
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			pool = getURLs(currentURL).toArray(pool);
			
			List<String> foo = Arrays.asList(pool);
			System.out.println("get Link array :"+ foo);
			
			if(pool != null) {
				for(int i = 0; i < pool.length && pool[i] != null; i++) {
					if (pool[i].contains("#")) {
	                    if (pool[i].equals("#"))
	                        break;
	                    else
	                    	pool[i] = pool[i].split("#")[0];
	                }
					
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
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error URL in extract URL...");
			DeadURLpool.add(currentURL);
		}
	}
	
	private String formatURL(String url) {
		url = url.replaceFirst("http(s{0,1})://", "http://");
        /*if (url.charAt(url.length() - 1) != '/')
            url += "/";*/
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
					String temp = data.split("\\*")[0];
					Matcher matcher = Pattern.compile("http(s{0,1})://"+temp+".*").matcher(url);
	               if(matcher.matches())return true;
				}else {
					String temp = data;
					Matcher matcher = Pattern.compile("http(s{0,1})://"+temp+"(/?)").matcher(url);
	                if(matcher.matches())return true;
				}
				
			}
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
			System.out.println(e);
		}
		return false;
	}
	
	List<String> getURLs(String srcPage) throws IOException {
		URL url = new URL(srcPage);
		InputStreamReader reader = new InputStreamReader(url.openStream());

		ParserDelegator parser = new ParserDelegator();
		ParserCallback callback = new ParserCallback();
		parser.parse(reader, callback, true);
		
		if(srcPage.split("\\?")[0] != null)url = new URL(srcPage.split("\\?")[0]);
		
		for (int i=0; i<callback.urls.size(); i++) {
			String str = callback.urls.get(i);

			if (!isAbsURL(str))
				callback.urls.set(i, toAbsURL(str, url).toString());
		}
		
		return callback.urls;
	}
	boolean isAbsURL(String str) {
		return str.matches("^[a-z0-9]+://.+");
	}
	URL toAbsURL(String str, URL ref) throws MalformedURLException {
		URL url = null;
		String prefix =  ref.getHost();
		
		if (ref.getPort() > -1)
			prefix += ":" + ref.getPort();
		
		if (str.startsWith(ref.getPath())) {
			int len = ref.getPath().length() - ref.getFile().length();
			String tmp = "/" + ref.getPath().substring(0, len) + "/";
			prefix += tmp.replace("//", "/");
		}else {
			prefix += ref.getPath();
		}
		String outputURL = prefix + str;
		//Path normalizedPath = Paths.get(outputURL).normalize();
		outputURL = ref.getProtocol() + "://" + outputURL.replace("//", "/");
		url = new URL(outputURL);
		return url; 
	}

	private String getDomain(String urlString) {
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
    }
	
	
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		Collect collect;
		
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
				collect = new Collect(URLstr,x,y,new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>());
				collect.action();
				break;
			}else {
				System.out.println("Type Error!Please try again.");
			}
		}
		
		
		try {
			new File(collect.generatedFile);
			/*System.out.println("Path:"+System.getProperty("user.dir"));
			for(File fileEntry : file.listFiles()){
				System.out.println(fileEntry.getName());
			}*/
			FileWriter myWriter = new FileWriter(collect.generatedFile);
			myWriter.write(collect.ProcessedURLpool.toString());
		    myWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
