package hk.edu.hkbu.comp;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class MyController {
	
	@GetMapping("load")
	@ResponseBody
	String load(@RequestParam(value="query", required=true) String query,@RequestParam(value="type", required=true) int type) {
		
		MyDatabase db = new MyDatabase();
		db.init();
		String[][] tmp=db.search(query, type); //phase matching in the all of the website
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
		
		String result="<a href='http://localhost:8080/index.html'><img src='https://www.flaticon.com/svg/static/icons/svg/25/25694.svg' width='40' height='40'></a>";
		if(tmp!=null) {
			if(tmp.length!=0) { 
				for(String[] array:tmp) { 
					result+= "<a href='"+array[0]+"'>"+array[1]+"</a><br>";
				}
			}
		}
		else {
			result+= "<h1>No match</h1>";
		}
			
		return result;
	}
}