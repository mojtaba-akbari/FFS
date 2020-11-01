package Foundation.FFS.WebServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import Foundation.FFS.FFSBootStrapper;
import Foundation.FFS.Services.FFSViewSchema;

@RestController
@RequestMapping("/rgw")
public class UserGateWay {

	/* Application Request
	 * 
	 */
	
	@Autowired
	FFSBootStrapper strapper;
	
	@RequestMapping("/scores/")
	public String FetchScores() {
		return "GateWay";
	}
	
	@RequestMapping("/init")
	@ResponseBody
	public String Init() {
		System.out.println("***Init RUNING***");
		
		FFSViewSchema view=new FFSViewSchema();
		
		//Install Share Table//
		view.setApplicationcontext(strapper.getApplicationContext());
		view.setPTRBootStrapper(strapper);
		//Refresh View//
		
		try {
			view.Refresh();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "tbshares<>"+view.getShareRowSchema()+"<>tbdeeps<>"+view.getDeepRowSchema()+"<>tbowners<>"+view.getOwnerRowSchema()+"<>tbscores<>"+view.getScoreRowSchema();
	}
	
	@RequestMapping("/owners") //Where User Wait For Data//
	@ResponseBody
	public String Owners() throws Exception {
		System.out.println("***Owner RUNING***");
		
		FFSViewSchema view=new FFSViewSchema();
		
		//Install Share Table//
		view.setApplicationcontext(strapper.getApplicationContext());
		view.setPTRBootStrapper(strapper);
		//Refresh View//
		
		return view.OwnersItemRowTableSchema();
	}
	
	@RequestMapping("/scores") //Where User Wait For Data//
	@ResponseBody
	public String Online_Score() throws Exception{

		System.out.println("***Score RUNING***");
		
		FFSViewSchema view=new FFSViewSchema();
		
		//Install Share Table//
		view.setApplicationcontext(strapper.getApplicationContext());
		view.setPTRBootStrapper(strapper);
		//Refresh View//
		
		return view.ScoresItemRowTableSchema();
	}
}
