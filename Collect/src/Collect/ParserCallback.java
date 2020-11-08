package Collect;

import javax.swing.text.html.*;
import javax.swing.text.html.HTML.*;
import javax.swing.text.html.parser.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.text.*;


class ParserCallback extends HTMLEditorKit.ParserCallback {
	public List<String> urls = new ArrayList<String>();
	public String content = new String();
	public boolean isLink = false;
	public String temp = "";
	int max = Collect.y;

	@Override
	public void handleText(char[] data, int pos) {
		String s = new String(data);
		
		
		if(!s.matches("(.*)\\{(.*)|(.*)\\|(.*)")){
			s = s.replaceAll("[^a-zA-Z0-9]", " ");
			if(isLink) 
				s+="@";
			content += " " + s;
		}
		if(isLink) {
			temp += "|"+s;
		}
	}

	@Override
	public void handleStartTag(Tag tag, MutableAttributeSet attrSet, int pos) 
	{
		if (tag.toString().equals("a")) {

			Enumeration e = attrSet.getAttributeNames();

			while (e.hasMoreElements()) {

				Object aname = e.nextElement();

				if (aname.toString().equals("href")) {
					String u = (String) attrSet.getAttribute(aname);
					if (!urls.contains(u) && !u.contains("lang") && !u.contains("=tc") && !u.contains("=sc")  && !u.contains("&")) {
						temp = u;
						this.isLink = true;
					}
				}
			}
		}
	}

	public void handleEndTag(HTML.Tag tag, int position) {
		if (tag.toString().equals("a")) {
			if(!temp.equals("") && !temp.startsWith("#") && !temp.equals(" ")) {
				//System.out.println(temp);
				urls.add(temp);
			}
			
			temp = "";
			this.isLink = false;
		}
    }
	
	String loadPlainText(URL url) throws IOException {
		ParserCallback callback = new ParserCallback();
		ParserDelegator parser = new ParserDelegator();
		
		InputStreamReader reader = new InputStreamReader(url.openStream(),"UTF-8");
		parser.parse(reader, callback, true);
		
		return callback.content;
	}

	List<String> getURLs(URL url) throws IOException{
		ParserCallback callback = new ParserCallback();
		ParserDelegator parser = new ParserDelegator();
		
		InputStreamReader reader = new InputStreamReader(url.openStream(),"UTF-8");
		parser.parse(reader, callback, true);
		
		return callback.urls;
	}
}
