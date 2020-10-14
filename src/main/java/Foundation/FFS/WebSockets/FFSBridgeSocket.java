package Foundation.FFS.WebSockets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import Foundation.FFS.FFSBootStrapper;
import Foundation.FFS.Services.FFSViewSchema;

@Controller
public class FFSBridgeSocket {
	
	/* Application Request
	 * 
	 */
	
	@Autowired
	FFSBootStrapper strapper;
	
	@MessageMapping("/init") //Where We Wait Server Hand Socket //
	@SendTo("/topic/init") //Where User Wait For Data//
	public String Init(@Payload String msg) {
		System.out.println("***STREAM RUNING***");
		
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
	
	@MessageMapping("/owners") //Where We Wait Server Hand Socket //
	@SendTo("/topic/owners") //Where User Wait For Data//
	public String Owners(@Payload String msg) throws Exception {
		System.out.println("***Owner RUNING***");
		
		FFSViewSchema view=new FFSViewSchema();
		
		//Install Share Table//
		view.setApplicationcontext(strapper.getApplicationContext());
		view.setPTRBootStrapper(strapper);
		//Refresh View//
		
		return view.OwnersItemRowTableSchema();
	}
	
	@MessageMapping("/share") //Where We Wait Server Hand Socket //
	@SendTo("/topic/share") //Where User Wait For Data//
	public String Online_Share(@Payload String msg) {
		return "OK";
	}
	
	@MessageMapping("/deep") //Where We Wait Server Hand Socket //
	@SendTo("/topic/deep") //Where User Wait For Data//
	public String Online_Deep(@Payload String msg) {
		return "OK";
	}
	
	@MessageMapping("/score") //Where We Wait Server Hand Socket //
	@SendTo("/topic/score") //Where User Wait For Data//
	public String Online_Score(@Payload String msg) throws Exception{

		System.out.println("***Score RUNING***");
		
		FFSViewSchema view=new FFSViewSchema();
		
		//Install Share Table//
		view.setApplicationcontext(strapper.getApplicationContext());
		view.setPTRBootStrapper(strapper);
		//Refresh View//
		
		return view.ScoresItemRowTableSchema();
	}
}
