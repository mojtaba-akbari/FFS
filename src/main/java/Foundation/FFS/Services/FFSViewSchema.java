package Foundation.FFS.Services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import Foundation.FFS.FFSBootStrapper;
import Foundation.FFS.Filters.Filter;
import Foundation.FFS.Filters.Filter6;
import Foundation.FFS.Filters.Filter7;
import Foundation.FFS.Filters.Filter8;
import Foundation.FFS.Filters.Filters;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.ScoreChain;
import Foundation.FFS.Units.UnitEvents;

@Component
@Scope("prototype")
public class FFSViewSchema implements UnitEvents{
	
	private ApplicationContext applicationcontext;
	private FFSBootStrapper PTRBootStrapper;
	

	private String ShareRowSchema;
	private String DeepRowSchema;
	private String OwnerRowSchema;
	private String ScoreRowSchema;
	
	private String EmptyShareSchema;
	private String EmptyDeepSchema;
	private String EmptyOwnerSchema;
	private String EmptyScoreSchema;

	private UnitEvents.Type implementtype;


	public FFSViewSchema() {
		// A Table With 
		this.setShareRowSchema("<tr>"
						+ "<td>ID</td><td>ISIN</td><td>FitName</td><td>FullName</td><td>Date</td><td>First Price</td><td>Last Price</td><td>Last Sell Price</td>"
						//+"<td>Number of Trades</td><td>Capacity of Trades</td><td>Value of Trades</td>"
						+"<td>Min-Price Day</td><td>Max-Price Day</td><td>Last-Day Price</td>"
						//+"<td>EPS</td><td>Number Of Share</td><td>Capacity of Share</td>"
						//+"<td>Min-Price</td><td>Max-Price</td>"
						+"<td>FILTER-Occures</td><td>FILTER-GRAPH</td>"
						+"</tr>");
		
		
		this.setDeepRowSchema("<tr>"
				+ "<td>ID</td><td>Name</td><td>Buy-Numbers</td><td>Buy-Price</td><td>Buy-Capacity</td><td>Sell-Numbers</td><td>Sell-Price</td><td>Sell-Capacity</td>"
				+"<td>FILTER-Occures</td><td>FILTER-Price/Capacity(Observer)</td><td>FILTER-Seller/Buyer(Observer)</td><td>FILTER-Behavior</td>"
				+"</tr>");

		this.setOwnerRowSchema("<tr>"
				+ "<td>ID(NotValid)</td><td>Owner-Name</td><td>Market-Found</td><td>List-Shares</td>"
				+"</tr>");
		
		
		this.setScoreRowSchema("<tr>"+
							"<td>ID</td><td>Name</td><td>Point</td><td>Reason</td><td>Status</td><td>Liked</td>"
							+"</tr>");
		
		this.EmptyShareSchema=ShareRowSchema;
		this.EmptyDeepSchema=DeepRowSchema;
		this.EmptyOwnerSchema=OwnerRowSchema;
		this.EmptyScoreSchema=ScoreRowSchema;
		
	}

	private String[] Share_Structure1(String[] simplecells) {
			if(simplecells.length > 8) {
				
				String[] rigthcells= {simplecells[0],simplecells[1],simplecells[2],simplecells[3],simplecells[4],simplecells[5],
				simplecells[6],simplecells[7],simplecells[11],simplecells[12],simplecells[13]
				};
				
				return rigthcells;
			}
			
			return null;
	}
	private String[] Deep_Structure1(String[] simplecells) {
		String[] rigthcells= {simplecells[0],simplecells[8],simplecells[3],simplecells[4],simplecells[6],simplecells[2],simplecells[5],
				simplecells[7]
		};

		return rigthcells;
	}
	public String OwnersItemRowTableSchema() throws Exception{
		
		ArrayList<String> FilterResult=new  ArrayList<String>();
		
		Filter f1=new Filter8(applicationcontext, null, null);
		f1.run(null, null, FilterResult);
		
		String Network="tbowners<>"+FilterResult.get(0);
		
		//Return//
		return Network;
	}
	public String ScoresItemRowTableSchema() throws Exception{

		//Make Item From Updated Item//
		String Scores="";
		
		Set<String> KeyArray=this.PTRBootStrapper.getCacheScores().keySet();
		
		for (int index=0;index<KeyArray.size();index++) {
			
			//Check Again For Concurrent Issue//
			if(this.PTRBootStrapper.getCacheScores().containsKey(KeyArray.toArray()[index])) {
				String id=(String) KeyArray.toArray()[index];
				ScoreChain item=this.PTRBootStrapper.getCacheScores().get(id);
				
				String ListOfReason="<div style=\"width:150px;max-height: 100px;overflow:auto;\">";


				// Get All Reason And Point // 
				HashMap<String, Integer> Key=item.getReason(); 

				for (Entry<String, Integer> entry : Key.entrySet()) {
					ListOfReason+=entry.getKey()+"("+entry.getValue()+")"+"<hr />";
				}

				ListOfReason+="</div>";

				// BroadCast //
				// Just BroadCast Items With Point More Than 500 //
				if(item.getPoint() >= 500)
					Scores+="<tr id=\"score"+item.getID()+"\"><td>"+item.getID()+"</td><td>"+item.getName()+"</td><td class=\"sortable\">"+item.getPoint()+"</td><td>"+ListOfReason+"</td><td>"+item.getType().name()+"</td><td>Score > 500</td></tr>";
			}
		}
		
		//Re factor Like All Item//
		return "items<><>SCORE<>"+Scores;
	}
	private void SetItemRowTableSchema() throws Exception {
		//Retain ALl Schema That Needed For Setup//
		
		//For Concurrent Task And Reference Problem//
		
		//ReDownload All Network Of ShareOwner And Show//
		Set<String> KeyArray=this.PTRBootStrapper.getCacheBase().keySet();
		
		for (int index=0;index<KeyArray.size();index++) {
			
			//Check Again For Concurrent Issue//
			if(this.PTRBootStrapper.getCacheBase().containsKey(KeyArray.toArray()[index])) {
				String item=(String) KeyArray.toArray()[index];
				String[] simplecells=this.PTRBootStrapper.getCacheBase().get(item).getItemContent().split(",");

				simplecells=Share_Structure1(simplecells);

				if(simplecells != null) {
					
					this.ShareRowSchema+="<tr id=\"share"+simplecells[0]+"\">";

					for (int i=0;i<=simplecells.length-1;i++) {
						this.ShareRowSchema+="<td>"+simplecells[i]+"</td>";
					}
					
					this.ShareRowSchema+="</tr>";
				}
			}
		}
		
		
	}
	
	private void UpdateItemRowTableSchema(String[] Row,UnitEvents.Type MType) {
		// Format Of Data Is //
		// ID:ITEM:FILTER1:FILTER2:FILTER3... //
		String ID=Row[0];
		String Type=Row[1];
		String Item=Row[2];
		
		// Add Items To Result //
		switch(MType) {
		case DEEP:
			String[] simplecells_deep=Deep_Structure1(Item.split(","));
			
			//Deep Inserted Stack Base In DOM HTML//
			if(simplecells_deep != null)
				for (int i=0;i<=simplecells_deep.length-1;i++)
					this.DeepRowSchema+="<td>"+simplecells_deep[i]+"</td>";

			break;
		case SHARE:
			String[] simplecells_share=Share_Structure1(Item.split(","));
			
			//Share Inserted ID Base In DOM HTML//
			if(simplecells_share != null)
				for (int i=0;i<=simplecells_share.length-1;i++)
					this.ShareRowSchema+="<td>"+simplecells_share[i]+"</td>";
			break;
		
		case SCORE:
			String[] simplecells_score=Item.split(",");
			
			//Share Inserted ID Base In DOM HTML//
			if(simplecells_score != null)
				for (int i=0;i<=simplecells_score.length-1;i++)
					this.ScoreRowSchema+="<td>"+simplecells_score[i]+"</td>";
			break;
		}
		

		//Filter Added To Result//
		//*********************//
		//Result Of Filters Delimiter ://
		//Result Of Inside Of Filter Delimiter SPACE//
		//Result Of CSS Inside Of TD @//
		for(int i=3;i<Row.length;i++)
			switch(MType) {
			case DEEP:
				this.DeepRowSchema+="<td "+CSSmaker(Row[i])+">"+ClearAddonceElement(Row[i])+"</td>";
				break;
			case SHARE:
				this.ShareRowSchema+="<td "+CSSmaker(Row[i])+">"+ClearAddonceElement(Row[i])+"</td>";
				break;
			}
		
	}
	
	// GET DATA WITH POINTER //
	private String CSSmaker(String input) {
		// Platform For Process CSS Items //
		// CSS Always is latest node //
		
		if(input.contains("CSS")) {
			String css=input.substring(input.indexOf("CSS{"),input.length());
			css=css.replace("CSS{","");
			css=css.replace("}", "");
			css=css.replace("@", " "); //Delimiter CSS Class SPACE-WE USEs SPACE In ANOTHER FOR DELIMTER OF ITEM//
			
			return "class=\""+css+"\"";
		}
		
		return "";
	}
	
	//CLEAR ADDED ELEMENTS//
	private String ClearAddonceElement(String input) {
		// Platform For Process CSS Items //
		// CSS Always is latest node //
		
		if(input.contains("CSS")) {
			
			return input.replaceAll("CSS\\{(.*)}", "");
		}
		
		return input;
	}
	
	public void Clear() {
		this.ShareRowSchema=EmptyShareSchema;
		this.DeepRowSchema=EmptyDeepSchema;
	}
	public void Refresh() throws Exception{
		Clear();
		SetItemRowTableSchema();
	}
	public void Update(String[] Row,UnitEvents.Type Type) {
		
		this.ShareRowSchema="";
		this.DeepRowSchema="";
		this.ScoreRowSchema="";
		
		setImplementtype(Type);
		UpdateItemRowTableSchema(Row,Type);
	
	}
	
	public String getPartOfPageSchema() {
		if(getImplementtype().equals(UnitEvents.Type.SHARE))
			return ShareRowSchema;
		else if(getImplementtype().equals(UnitEvents.Type.DEEP))
			return DeepRowSchema;
		else if(getImplementtype().equals(UnitEvents.Type.SCORE))
			return ScoreRowSchema;
		else return null;
	}

	public String getDeepRowSchema() {
		return DeepRowSchema;
	}

	public void setDeepRowSchema(String deepRowSchema) {
		DeepRowSchema = deepRowSchema;
	}
	
	public UnitEvents.Type getImplementtype() {
		return implementtype;
	}

	public void setImplementtype(UnitEvents.Type implementtype) {
		this.implementtype = implementtype;
	}
	
	public String getShareRowSchema() {
		return ShareRowSchema;
	}

	public void setShareRowSchema(String shareRowSchema) {
		ShareRowSchema = shareRowSchema;
	}


	public ApplicationContext getApplicationcontext() {
		return applicationcontext;
	}


	public void setApplicationcontext(ApplicationContext applicationcontext) {
		this.applicationcontext = applicationcontext;
	}
	
	public String getOwnerRowSchema() {
		return OwnerRowSchema;
	}

	public void setOwnerRowSchema(String ownerRowSchema) {
		OwnerRowSchema = ownerRowSchema;
	}
	
	public String getScoreRowSchema() {
		return ScoreRowSchema;
	}

	public void setScoreRowSchema(String scoreRowSchema) {
		ScoreRowSchema = scoreRowSchema;
	}
	
	public FFSBootStrapper getPTRBootStrapper() {
		return PTRBootStrapper;
	}

	public void setPTRBootStrapper(FFSBootStrapper pTRBootStrapper) {
		PTRBootStrapper = pTRBootStrapper;
	}
}
