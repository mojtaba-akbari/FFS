package Foundation.FFS.Filters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.UnitChain;

/* Filter8 Provide Owner Simple Calculation And Simple Column
 * Basic Of Owners View System
 * For Complex Calculation Reuse And Make Another Filters
 * Filter8 Can Call Any Filter
 */

@Component
@Scope("prototype")
public class Filter8 extends Filters {
	
	
	
	public Filter8(ApplicationContext context,String UniqueItem,ArrayList<WindowItem> window) {
		super(context,UniqueItem, window,FilterType.SEQUENTIAL,FilterTask.VIEW);
		
		this.PointX=3;
	}
	
	private String FindPricePattern(String id,Double Price) {
		if (Price > new Double("10000000000000")) {
			int res=ScoreAssign(id, "Company","Company TRADE-BURST", this.PointX*10,ScoreType.ADDED); // INJECT TO SCORE //
			
			return "class=\"bg-danger h4\""; // 1000B
		}
		else if (Price > new Double("1000000000000")) {
			int res=ScoreAssign(id, "Company","Company TRADE-UP", this.PointX*6,ScoreType.ADDED); // INJECT TO SCORE //
			
			return "class=\"bg-warning h5\""; // 100B
		}
		else if(Price  > new Double("100000000000")) {
			int res=ScoreAssign(id, "Company","Company TRADE", this.PointX*4,ScoreType.ADDED); // INJECT TO SCORE //
			
			return "class=\"text-success h6\""; // 10 B
		}
		else return "";
	}
	
	@Override
	public void run(String id,String item,ArrayList<String> FilterResult) throws Exception {
		setIDItem(id);
		setItem(item);

		// Task //
		// Making All Simple Record Part //
		// And Another Thing Saving Big String => We Should BroadCast Any Element That We Have Calculated With Out Save Big String //
		// I Need Algorithm Sort That I Think Insertion Is So Fast Insert/InTimeSort //
		Document doc=Jsoup.parse("");
		doc.html("");

		//Get SnapShot From RamDB//
		//Insure That Set Do Not Change - Concurrent Storage - We Just Read Data -//
		Set<String> KeyArray=InRamDBOwners.keySet();

		for (int index=0;index<KeyArray.size();index++) {

			if(InRamDBOwners.containsKey(KeyArray.toArray()[index])) {
				// Public For All Shares //
				String fundfoundinshares="";
				
				//Internal Filter Result Storage//
				ArrayList<String> Res=new  ArrayList<String>();
				
				// Fetch Data From SnapShot Of Key//
				GraphChain gc=InRamDBOwners.get(KeyArray.toArray()[index]);
				
				long Countercapacity=0;
				
				String listofshares="<div style=\"max-height: 250px;overflow:auto;\">";

				double totalfoundation=0;

				//SnapShot From Array Of Shares In Owner System - Concurrent Storage - We Just Read Data -//
				Set<String> GCKeySet=gc.ShareConnection.keySet();
				
				for (int j=0;j< GCKeySet.size();j++) {
					//Clear Storage For Filters//
					Res.clear();
					
					String Key=(String) GCKeySet.toArray()[j];
					
					
					
					Countercapacity+=gc.ShareConnection.get(Key).Sharenumbers;

					//Do Not Scare From Working With Pointer :) Best Thing If You Know It Working With High Performance//
					//Owner->Share->InRamDBShare->Content->SnapPrice//
					
					long realtimeprice=Long.parseLong(gc.ShareConnection.get(Key).US.getHeadToLastItemPointer().getUnitContent().split(",")[6]);//Index 6 References To InRamDBShare//
					
					double sharefoundationrealtime=realtimeprice*gc.ShareConnection.get(Key).Sharenumbers;

					totalfoundation+=sharefoundationrealtime;
					
					
					
					
					//Calculation Time line//
					
					String timeline=gc.ShareConnection.get(Key).TimeLine.getUnitContent().replace(",","->"); // Split Just In Time //
					
					//Sorted Array//
					String[] array_reverse=timeline.split(";");
					
					for(int k = 0; k < array_reverse.length / 2; k++)
					{
					    String temp = array_reverse[k];
					    array_reverse[k] = array_reverse[array_reverse.length - k - 1];
					    array_reverse[array_reverse.length - k - 1] = temp;
					}
					
					//Call TimeLine Processor//
					
					Filters f1=new Filter9(getApplicationcontext(),null,null);;
					f1.run(Key, String.join(";",array_reverse)+";"+realtimeprice+";"+gc.ShareConnection.get(Key).Sharename, Res); //Cell 0 Of Res Storage//
					
					fundfoundinshares+=Res.get(0); // Other Shares Behavior For Owner //
					
					timeline=String.join("</p><p>", array_reverse); // Easier Work For Join :) //
					
					timeline="<p>"+timeline+"</p>";
					
					//End TimeLine

					//Danger Text For More Than 10B//
					listofshares+="<p "+FindPricePattern(Key,sharefoundationrealtime)+">"+
							gc.ShareConnection.get(Key).Sharename+" ("+String.valueOf(gc.ShareConnection.get(Key).Sharenumbers)+"*"+String.valueOf(realtimeprice)+")="+
							(new BigDecimal(sharefoundationrealtime).toPlainString())+"</p>"+
							"<div class=\"panel panel-default\" style=\"max-height: 80px;overflow:auto;\">"+timeline+"</div>";
				}

				listofshares+="</div>";
				
				
				// Make Full Item For Show //
				Element eltr=new Element("tr");
				eltr.html("<td id=\""+gc.getOwnerid()+"\">"+
							gc.getOwnerid()+"</td><td>"+
							gc.getOwnername()+
							"</td><td found=\""+new BigDecimal(totalfoundation).toPlainString()+"\"><p>"+new BigDecimal(totalfoundation).toPlainString()+"</p>"
									  /* DIV FOR OTHER EXPLAIN ABOUT SHARES AND OWNER BEHAVIOR */
									+ "<div class=\"panel panel-default\" style=\"max-height: 80px;overflow:auto;\">"+fundfoundinshares+"</div>"
							+"</td><td>"+listofshares+"</td>");

				
				// Now Find Best Index For Element To Add //
				// Algorithm For Insertion Sort - Very Quick In This Scenario Because We Insert And Sorted ALl Item Instance //
				boolean IsAdded=false;
				if(doc.childrenSize()==0) doc.insertChildren(0,eltr);
				else {
					
					for (int i=0;i< doc.childrenSize();i++) {
						double indexfoundation=Double.valueOf(doc.child(i).childNode(2).attr("found"));
						
						if(indexfoundation <= totalfoundation) {
							IsAdded=true;
							doc.insertChildren(i, eltr);
							break;
						}
					}
					
					if(IsAdded==false)	doc.insertChildren(doc.childrenSize(), eltr);
				}
			}
		}
		
		FilterResult.add(doc.outerHtml());
	}
}
