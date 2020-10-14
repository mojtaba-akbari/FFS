package Foundation.FFS.Services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import org.springframework.stereotype.Service;

import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.ItemPropertyHolder;
import Foundation.FFS.Units.UnitChain;
import Foundation.FFS.Units.UnitEvent;
import Foundation.FFS.Units.UnitEvents;

@Service
public class FFSStatusChecker implements UnitEvents {
	
	// Pointer To Our Storage //
	private HashMap<String,ItemPropertyHolder> CacheHashMap;
	private HashMap<String,UnitChain> InRamDBShares;
	private HashMap<String,UnitChain> InRamDBDeep;
	private HashMap<String,GraphChain> InRamDBOwners;


	private ArrayList<BlockingQueue<UnitEvent>> BrokerTower;


	private String ScrapperResult;
	private int StatusUpdated;
	private int StatusFix;
	
	private int GenerateCounter=0;
	
	public FFSStatusChecker() {
	}
	
	synchronized public void Initial() throws Exception {
		this.StatusFix=0;
		this.StatusUpdated=0;
		
		//First Syntax Filter @ //
		//Second Syntax Filter ;//
		//Third Syntax Filter ,//
		
		String[] Part=ScrapperResult.split("@");
		
		//We Should Rearrange Result Again//
		//Part[2]+Part[3]// //I Do not know what is Part[0] Part[1] Part[4]
		ScrapperResult=Part[2]+Part[3];
		
		String[] itemarray=ScrapperResult.split(";");
		
		for (String fullitem : itemarray) {
		
			String[] indexarray=fullitem.split(","); // IndexArray[0]=> ID
			
			//8 Length Is DeepMarket//
			if(indexarray.length > 8) {
				//Other Element Ignore////Implement Later////
				//SnapTime Part//
				if(CacheHashMap.containsKey(indexarray[0])) {

					if(!fullitem.equals(CacheHashMap.get(indexarray[0]).getItemContent())) {

						this.CacheHashMap.put(indexarray[0], new ItemPropertyHolder(fullitem)); //Updated SNAPSHOT//
						this.setStatusUpdated(this.getStatusUpdated() + 1);
						
						GenerateCounter++;
						
						this.BrokerTower.get(0).add(new UnitEvent(indexarray[0],UnitEvents.Type.SHARE,null)); // We No Need Unique For Share SnapTime //

					}
					else this.setStatusFix(this.getStatusFix() + 1);

				}
				else {
					// I Do Not Send First State :) //
					this.CacheHashMap.put(indexarray[0], new ItemPropertyHolder(fullitem));	
				}

				
				//FullContent Share Part//
				//We Do not Use BroadCast For Chain Of Share//
				if(InRamDBShares.containsKey(indexarray[0])) {
					
					//Is Updated Item Any Valid?//
					//Check Updated With SnapTime//
					//Last Item Checked//
					if(!fullitem.equals(InRamDBShares.get(indexarray[0]).getHeadToLastItemPointer().getUnitContent())) {
						
						// Check Deep Item Has 3 Record Per Time So Add To Time(i) Other Record //
						UnitChain Head=InRamDBShares.get(indexarray[0]);
						UnitChain Last=Head.getHeadToLastItemPointer();
						Last.setPointerToSnapShot(false);
						
						UnitChain NewItem=new UnitChain(fullitem, true, Last); //Last Item Flag Is True//
						Last.setNextUnit(NewItem);
						Head.setHeadToLastItemPointer(NewItem); //Head Updated
						Head.setSizeOfTree(Head.getSizeOfTree()+1); //Head Updated
						
						String UniqueItem=String.valueOf(Head.getSizeOfTree())+"@"+UniqIDgenerator();
						
						NewItem.setUniqeItemID(UniqueItem);
					}
				}
				else {
					
					//Make Root Of ID//
					InRamDBShares.put(indexarray[0], new UnitChain(fullitem,true,null));
					UnitChain Head=InRamDBShares.get(indexarray[0]); // retrive HEAD //
					Head.setHeadToHead(); // Call Operation For Head //
					String UniqueItem=String.valueOf(Head.getSizeOfTree())+"@"+UniqIDgenerator();
					
					Head.setUniqeItemID(UniqueItem);
					
				}
				
				
			}
			else
			{
				//FullContent Deep Part//
				if(InRamDBDeep.containsKey(indexarray[0])) {
					
					// Inject Of Data - Add Name To The End For Simple Task - Our DataSource Is CacheHashMap //
					// 1- Name Of ID //
					/************* UPDATE FULLITEM *************/
					fullitem+=","+CacheHashMap.get(indexarray[0]).getItemContent().split(",")[2];
					
					//Check Item Deep If Equal With Last Item Ignore That//
					//Part 0 Equal With Record Time(i) So If This Record Equal With That//
					//Time(i)=Time(i+1) Ignore That Record//
					//Part 0 Checked That Is Record 1//
					if(InRamDBDeep.get(indexarray[0]).getHeadToLastItemPointer().getUnitContent().split(";")[0].equals(fullitem)) {
						continue;
					}
					
					//Is Updated Item Any Valid?//
					//Check Updated With SnapTime//
					UnitChain Head=InRamDBDeep.get(indexarray[0]);
					UnitChain Last=Head.getHeadToLastItemPointer();
					Last.setPointerToSnapShot(false); //It is Not Last (Now)//
					
					int IsTime_i_Record=Integer.valueOf(fullitem.split(",")[1]); // Part 1 Has Record Number //
					
					//FullItem Is Time(i) Record//
					if(IsTime_i_Record==1) {
						Last.setPointerToSnapShot(false);

						UnitChain NewItem=new UnitChain(fullitem, true, Last); //Last Item Flag Is True//
						Last.setNextUnit(NewItem);// Set Double Link //
						Head.setHeadToLastItemPointer(NewItem); //Head Updated Last Item
						Head.setSizeOfTree(Head.getSizeOfTree()+1); //Head Updated Counter Item

						String UniqueItem=String.valueOf(Head.getSizeOfTree())+"@"+UniqIDgenerator(); // Random With Index Of Tree Is Unique :)

						NewItem.setUniqeItemID(UniqueItem);
						
						
						//Send Deep Update//
						GenerateCounter++;
						
						this.BrokerTower.get(1).add(new UnitEvent(indexarray[0],UnitEvents.Type.DEEP,UniqueItem));
					}
					else if (IsTime_i_Record==2 || IsTime_i_Record==3) {
						//Do Not Send Record 2 And Record 3//
						Last.setUnitContent(Last.getUnitContent()+";"+fullitem);
					}
					
				}
				else {
					
					// Inject Of Data - Add Name To The End For Simple Task - Our DataSource Is CacheHashMap //
					// 1- Name Of ID //
					/************* UPDATE FULLITEM *************/
					fullitem+=","+CacheHashMap.get(indexarray[0]).getItemContent().split(",")[2];
					
					//Make Root Of ID//
					//We Need Unique Item For Find BroadCast Element//
					
					
					InRamDBDeep.put(indexarray[0], new UnitChain(fullitem,true,null));
					UnitChain Head=InRamDBDeep.get(indexarray[0]);
					Head.setHeadToHead();
					
					String UniqueItem=String.valueOf(Head.getSizeOfTree())+"@"+UniqIDgenerator();
					
					Head.setUniqeItemID(UniqueItem);
					
					//Send Deep Update//
					
					GenerateCounter++;
					
					this.BrokerTower.get(1).add(new UnitEvent(indexarray[0],UnitEvents.Type.DEEP,UniqueItem));
				}
			}

		}
		
	}
	
	private String UniqIDgenerator() {
		Random r=new Random();
		long id=r.nextLong()*999999999;
		return String.valueOf(id).replace("-", "");
	}
	
	public HashMap<String,ItemPropertyHolder> getCacheHashMap() {
		return CacheHashMap;
	}

	public void setCacheHashMap(HashMap<String,ItemPropertyHolder> cacheHashMap) {
		CacheHashMap = cacheHashMap;
	}

	public String getScrapperResult() {
		return ScrapperResult;
	}

	public void setScrapperResult(String scrapperResult) {
		ScrapperResult = scrapperResult;
	}

	public int getStatusUpdated() {
		return StatusUpdated;
	}

	public void setStatusUpdated(int statusUpdated) {
		StatusUpdated = statusUpdated;
	}

	public int getStatusFix() {
		return StatusFix;
	}

	public void setStatusFix(int statusFix) {
		StatusFix = statusFix;
	}

	public ArrayList<BlockingQueue<UnitEvent>> getBrokerTower() {
		return BrokerTower;
	}

	public void setBrokerTower(ArrayList<BlockingQueue<UnitEvent>> brokerTower) {
		BrokerTower = brokerTower;
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
	
	public HashMap<String, GraphChain> getInRamDBOwners() {
		return InRamDBOwners;
	}

	public void setInRamDBOwners(HashMap<String, GraphChain> inRamDBOwners) {
		InRamDBOwners = inRamDBOwners;
	}
}
