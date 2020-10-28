package Foundation.FFS.Filters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import Foundation.FFS.FFSBootStrapper;
import Foundation.FFS.Properties.FiltersProperties;
import Foundation.FFS.Services.FFSScrapper;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.ItemPropertyHolder;
import Foundation.FFS.Units.UnitChain;
import Foundation.FFS.Units.UnitEvents;
import Foundation.FFS.WebSockets.FFSBridgeBroadCast;
import Foundation.FFS.Units.ScoreChain;

/*
 * LOCATION FOR DOC OF FILTER
 */

// RE IMPELEMENT ALGORITHM FOR BETTER STATE //

@Component
@Scope("proptype")
@EnableConfigurationProperties(FiltersProperties.class)
public class Filters implements Filter,Runnable {
	
	
	private ApplicationContext applicationcontext;
	
	// Terminal For Broadcast //
	private FFSBridgeBroadCast terminalbroadcast;
	
	public int PointX; // POINT MULTIPELECTION //
	
	public String IDItem;
	public String UniqueItem;
	public String Item;
	public ArrayList<WindowItem> Window;
	public UnitChain PTRtoitem=null;

	//PTR To Storage//


	public HashMap<String,ItemPropertyHolder> CacheHashMap;
	public HashMap<String,ScoreChain> ScoreChain;
	public HashMap<String,UnitChain> InRamDBShares;
	public HashMap<String,UnitChain> InRamDBDeep;
	public HashMap<String,GraphChain> InRamDBOwners;
	
	
	//TYPE FILTER//
	final private FilterType filtertype;
	final private FilterTask filtertasktype;
	
	public Filters(ApplicationContext context,String UniqueItem,ArrayList<WindowItem> window,FilterType filtertype,FilterTask filtertasktype) {
		this.applicationcontext=context;
		this.Window=window;
		
		// GET PTR //
		FFSBootStrapper bs=context.getBean(FFSBootStrapper.class);
		
		this.CacheHashMap=bs.getCacheBase();
		this.ScoreChain=bs.getCacheScores();
		this.InRamDBDeep=bs.getInRamDBDeeps();
		this.InRamDBShares=bs.getInRamDBShares();
		this.InRamDBOwners=bs.getInRamDBOwners();
		
		// BroadCast Terminal //
		terminalbroadcast=context.getBean(FFSBridgeBroadCast.class);
		
		this.UniqueItem=UniqueItem;
		
		this.filtertype=filtertype;
		this.filtertasktype=filtertasktype;
		
	}
	
	public double[] CalculateCOEVariationAndGrowthSpeed(int indexelement,HashMap<String, UnitChain> Storage) throws Exception{
		//TAKE SNAPSHOT FROM SIZE BECAUSE MAYBE CALCULATION IS LONG AND NEW ITEM ADDED//
		//Find Element//
		ArrayList<Double> array_element=new ArrayList<Double>();
		
		UnitChain Elem=Storage.get(IDItem); //Head//
		
		//Cache All Parameter That Needed//
		while(Elem.getUniqeItemID()!=UniqueItem)
		{
			String value=Elem.getUnitContent().split(",")[indexelement];
			array_element.add(Double.valueOf(value));
			Elem=Elem.getNextUnit();
		}
		
		//Now We Have Element So Save In Filter And Insert In Cache Storage//
		setPTRtoitem(Elem);
		
		//DEV Calculation//
		double sumofelement=0;
		
		for(int i=0;i<=array_element.size()-1;i++)
			sumofelement=+array_element.get(i);
		
		double averageofelement=Math.floor(sumofelement/array_element.size());
		
		double variance=0;
		for(int i=0;i<=array_element.size()-1;i++)
			variance=+Math.pow(array_element.get(i)-averageofelement,2);
		
		variance=Math.floor((variance/array_element.size()));
		
		double devcoe=Math.floor(variance/averageofelement)*100;
		
		//SPEED OF GROWTH//
		double avgspeed=0;
		for(int i=0;i<array_element.size()-1;i++)
			avgspeed+=Math.abs(array_element.get(i)-array_element.get(i+1)); // Add Difference
		
		avgspeed=avgspeed/(array_element.size()-1);
		
		return new double[]{devcoe,avgspeed};
	}
	
	// YOU CAN INJECT DIRECT HTML IN DATA //
	// YOU CAN INJECT CSS STYLE IN DATA //
	
	/* Delimiter : Per Filter :
	 * 				Element In Filter Result [Space]
	 * 				Element In CSS Style @ => space
	 * CSS INSERT TO <td> element So For Any Of Element Between This You Should Write Your Own
	 * At Last Run Should Have OUT PUT PER <td>OUTPUT</td>
	 */
	
	//Some Of Filter Calculation Need Just One Times Calculation So Retain It In Public Cache And Recall It With Cache ID//
	
	//Run Without Signiture Thread//
	@Override
	public void run(String id, String item, ArrayList<String> FilterResult) throws Exception {
		// TODO Auto-generated method stub
	}
	
	// With This Function Download Any Data That You Needed Per Item And Not Save In System //
	@Override
	public String DownloadData(String URL,String Method,String DataType) {
		try {

			FFSScrapper scrapper=applicationcontext.getBean(FFSScrapper.class);
			scrapper.FFSScrapperNewInstance(URL, Method, 20*1024*1024); // fix Size // More Buff To 10MB
			// Data Type OCTED-STREAM OR HTML/TEXT //
			String result=(DataType.equals("HTML"))?scrapper.ScrapperResultTextHtmlData(12000).block():scrapper.ScrapperResult().block();
			return result;
		}
		catch(Exception ex) {
			//If You Need Disable This printstack For Timeout Response //
			ex.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Filter If Clause : (PointX=? * Fix Metric For Filter=?) = R -----> If Continue: Point Should Increase So
	 *  :) Flow Of Data Should Sense
	 *  Any Sensitive Data Can Sense With More Increased
	 *  I Think Work With Upper Formula Has Very Flexible
	 */
	@Override
	public int ScoreAssign(String Id,String GroupName,String Reason, int Point,ScoreType scoretype) {
		//Re Implement Return 1 With Successful Or Failed
		class Update{
			public int act() {
				ScoreChain item=ScoreChain.get(Id);
				
				
				if(scoretype.equals(ScoreType.ADDED)) {
					
					if(item.getReason().containsKey(Reason)) { // Check With Reason Name //

						item.setPoint(item.getPoint()+Point); // Item Score //

						int r=item.getReason().get(Reason);
						
						
						item.getReason().put(Reason, r+Point); // Update Times Of Occurrence Of Reason //
					}
					else {
						item.setPoint(item.getPoint()+Point); // Item Score //
						item.getReason().put(Reason, Point);
					}

				}
				else if (scoretype.equals(ScoreType.ONETIME)) {
					
					Set<String> keySet=item.getReason().keySet();
					
					for(int i=0;i<keySet.size();i++) {
						if(String.valueOf(keySet.toArray()[i]).contains(GroupName)) {
							//Decrease Point//
							//Delete Last Family Reason//
							item.setPoint(item.getPoint()-item.getReason().get(keySet.toArray()[i]));
							item.getReason().remove(keySet.toArray()[i]);
						}
					}
					
					item.setPoint(item.getPoint()+Point); // Item Score //
					item.getReason().put(Reason, Point); // Add New Family Item //
					
				}

				
				return 1;
			}
		}
		
		class Insert{
			public int act() {
				String name=CacheHashMap.get(Id).getItemContent().split(",")[2];
				ScoreChain.put(Id, new ScoreChain(Id,name,Reason));
				return 1;
			}
		}
		
		int succ=this.ScoreChain.containsKey(Id)? 
				new Update().act():
					new Insert().act();
		
		
		//Make Item From Updated Item//
		ScoreChain item=ScoreChain.get(Id);
		String ListOfReason="<div style=\"width:150px;max-height: 100px;overflow:auto;\">";
		
		
		// Get All Reason And Point // 
		HashMap<String, Integer> Key=ScoreChain.get(Id).getReason(); 
		
		for (Entry<String, Integer> entry : Key.entrySet()) {
			ListOfReason+=entry.getKey()+"("+entry.getValue()+")"+"<hr />";
		}
		
		ListOfReason+="</div>";
		
		
		String strItem=item.getID()+","+item.getName()+","+item.getPoint()+","+ListOfReason+","+item.getType().name()+", Score > 500";
		
		// BroadCast //
		// Just BroadCast Items With Point More Than 500 //
		if(item.getPoint() >= 500)
			terminalbroadcast.BroadCast("/topic/score", Id+"<>SCORE<>"+strItem, UnitEvents.Type.SCORE);
		
		return succ;
	}
	
	public HashMap<String, GraphChain> getInRamDBOwners() {
		return InRamDBOwners;
	}

	public void setInRamDBOwners(HashMap<String, GraphChain> inRamDBOwners) {
		InRamDBOwners = inRamDBOwners;
	}

	public String getIDItem() {
		return IDItem;
	}

	public void setIDItem(String iDItem) {
		IDItem = iDItem;
	}

	public String getItem() {
		return Item;
	}

	public void setItem(String item) {
		Item = item;
	}
	
	public HashMap<String, ItemPropertyHolder> getCacheHashMap() {
		return CacheHashMap;
	}

	public void setCacheHashMap(HashMap<String, ItemPropertyHolder> cacheHashMap) {
		CacheHashMap = cacheHashMap;
	}

	public String getUniqueItem() {
		return UniqueItem;
	}


	public void setUniqueItem(String uniqueItem) {
		UniqueItem = uniqueItem;
	}


	public ArrayList<WindowItem> getWindow() {
		return Window;
	}


	public void setWindow(ArrayList<WindowItem> window) {
		Window = window;
	}


	public HashMap<String, UnitChain> getInRamDBShares() {
		return InRamDBShares;
	}

	public void setInRamDBShares(HashMap<String, UnitChain> inRamDBShares) {
		InRamDBShares = inRamDBShares;
	}

	public HashMap<String, UnitChain> getInRamDBDeep() {
		return InRamDBDeep;
	}

	public void setInRamDBDeep(HashMap<String, UnitChain> inRamDBDeep) {
		InRamDBDeep = inRamDBDeep;
	}

	
	public UnitChain getPTRtoitem() {
		return PTRtoitem;
	}


	public void setPTRtoitem(UnitChain pTRtoitem) {
		PTRtoitem = pTRtoitem;
	}

	public ApplicationContext getApplicationcontext() {
		return applicationcontext;
	}

	public void setApplicationcontext(ApplicationContext applicationcontext) {
		this.applicationcontext = applicationcontext;
	}

	//Run With Thread Sign//
	//Some Of Filter Run In Thread Mode For Background Analyzer//
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}
