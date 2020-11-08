package hk.edu.hkbu.comp;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class MyController {
	
	@GetMapping("load")
	@ResponseBody
	String load(@RequestParam(value="query", required=true) String query,@RequestParam(value="type", required=true) int type) throws InterruptedException, ExecutionException {
		
		MyDatabase db = new MyDatabase();
		//db.init();
		
		String[][] tmp;
		
		try {
			query = java.net.URLDecoder.decode(query, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
		    // not going to happen - value came from JDK's own StandardCharsets
		}
		
		System.out.println(query);
		if(query.contains(" ")) {
			String[] arr_query = query.split(" ");
			tmp=db.search(arr_query, type); 
		}
		else {
			tmp=db.search(query, type); //phase matching in the all of the website
		}
		
		//String[][] tmp=db.search("Huang", 0); //key matching in the all of the website
		if(tmp!=null)
			if(tmp.length!=0) 
				for(String[] array:tmp) 
					System.out.println(array[0]+" | "+array[1]);
			else
				System.out.println("No Matching Target");
		else
			System.out.println("Keyword(s) not exist");
		System.out.println("Done");
		
		
		
		String result="<div>\r\n" + 
				"	<h1>My Search Engine</h1>\r\n" + 
				"	<form action=\"load\" method=\"GET\">\r\n" + 
				"		<p>\r\n" + 
				"			Search: <input type=\"text\" name=\"query\"/>\r\n" + 
				"			<input type=\"radio\" id=\"all\" name=\"type\" value=0 checked=\"checked\">\r\n" + 
				"			<label for=\"all\">all</label>\r\n" + 
				"			\r\n" + 
				"			<input type=\"radio\" id=\"title\" name=\"type\" value=1>\r\n" + 
				"			<label for=\"title\">title</label>\r\n" + 
				"			\r\n" + 
				"			<input type=\"radio\" id=\"text\" name=\"type\" value=2>\r\n" + 
				"			<label for=\"text\">text</label>\r\n" + 
				"			\r\n" + 
				"			<input type=\"radio\" id=\"url\" name=\"type\" value=3>\r\n" + 
				"			<label for=\"url\">url</label>\r\n" + 
				"			\r\n" + 
				"			<input type=\"radio\" id=\"links\" name=\"type\" value=4>\r\n" + 
				"			<label for=\"links\">links</label>\r\n" + 
				"			<input type=\"submit\"/>\r\n" + 
				"		</p>\r\n" + 
				"	</form>	\r\n" + 
				"</div>"
				+ "<br>";
		if(tmp!=null) {
			if(tmp.length!=0) { 
				for(String[] array:tmp) { 
					result+= "<a href='"+array[0]+"' style='font-size:30px; font-weight:bold;'>"+array[1]+"</a><br>";
				}
			}
			else {
				result+= "<h1>No match</h1>";
			}
		}
		else {
			result+= "<h1>No match</h1>";
		}
			
		return result;
	}
}