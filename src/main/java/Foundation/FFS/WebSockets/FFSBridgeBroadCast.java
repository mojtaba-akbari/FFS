package Foundation.FFS.WebSockets;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import Foundation.FFS.Services.FFSViewSchema;
import Foundation.FFS.Units.UnitEvents;

@Controller
public class FFSBridgeBroadCast implements UnitEvents{
	
	final private SimpMessagingTemplate template;
	
	public FFSBridgeBroadCast(SimpMessagingTemplate temp) {
		this.template=temp;
	}
	
	final private String type(String ID,String Event,String Message) {
		return "item<>"+ID+"<>"+Event+"<>"+Message;
	}
	
	public void BroadCast(String Destination,String Message,UnitEvents.Type Type){
		//System.out.println("I Want BroadCast Data"+Message);

		String[] Part=Message.split("<>");
		
		FFSViewSchema view=new FFSViewSchema();
		view.Update(Message.split("<>"),Type);
		
		//Item:ID:EVENT:MSG(msg,filter)//
		template.convertAndSend(Destination, type(Part[0],Type.toString(),view.getPartOfPageSchema()));
	}
}
