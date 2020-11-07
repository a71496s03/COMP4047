package hk.edu.hkbu.comp;

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
		
		
		
		String result="<iframe src='http://localhost:8080/index.html' width='100%' style='border:none;'></iframe><br>";
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