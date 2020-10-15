package Collect;

import javax.swing.text.html.*;
import javax.swing.text.html.HTML.*;
import javax.swing.text.html.HTMLEditorKit.*;
import javax.swing.text.html.parser.*;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.text.*;


class ParserCallback extends HTMLEditorKit.ParserCallback {
	public String content = new String();
	public List<String> urls = new ArrayList<String>();
	int max = Collect.y;

	@Override
	public void handleText(char[] data, int pos) {
		content += " " + new String(data);
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
					if (urls.size() < max && !urls.contains(u))
						urls.add(u);
				}
			}
		}
	}


}
