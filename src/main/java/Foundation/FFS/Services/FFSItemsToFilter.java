package Foundation.FFS.Services;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.ItemPropertyHolder;
import Foundation.FFS.Units.UnitChain;
import Foundation.FFS.Units.UnitEvent;
import Foundation.FFS.Units.UnitEvents;
import Foundation.FFS.WebSockets.FFSBridgeBroadCast;

@Component
@Scope("prototype")
public class FFSItemsToFilter implements Runnable , UnitEvents {
	// We Should Get One PTR To Application Context //
	
	@Autowired
	private ApplicationContext appcontext;
	
	// PTR TO STORAGE //
	private HashMap<String,ItemPropertyHolder> CacheHashMap;
	private HashMap<String,UnitChain> InRamDBShares;
	private HashMap<String,UnitChain> InRamDBDeep;
	private HashMap<String,GraphChain> InRamDBOwners;

	private BlockingQueue<UnitEvent> BrokerTower;
	
	private long ConsumeCounter=0;
	
	@Autowired
	private FFSFilters filters;
	
	@Autowired
	FFSBridgeBroadCast terminalbroadcast;
	
	public FFSItemsToFilter() {
	}
	
	synchronized public void CheckerItemEvent() throws InterruptedException,Exception {

		while(true) {
			
			ConsumeCounter++;
			
			UnitEvent Event=BrokerTower.take();
			
			// Extract Item //
			if(Event.getType().equals(UnitEvents.Type.SHARE)) {
				
				filters.setIDItem(Event.getID());
				filters.setEvent(Event.getType());
				filters.setUniqueItem(Event.getUniqeItemID()); //Need For Tracking//
				
				// CALCULATION CONCURRENT PROBLEM //
				filters.setItem(CacheHashMap.get(Event.getID()).getItemContent());
				
				// Filter Item //
				//System.out.println("I Consume Item : "+filters.getItem());
				//Return Result Filter That Added//
				ArrayList<String> Result=filters.PrepairFilters(filters.getEvent());
				// ID<>EVENT<>MESSAGE<>FILTERS
				String ComplexData=filters.getIDItem()+"<>"+filters.getEvent().name()+"<>"+filters.getItem()+"<>";
				
				//Result Of Any Filters Added To String With Delimiter ://
				for (String string : Result) {
					ComplexData+=string+"<>";
				}
				
				// Re Implement Calling Filter //
				
				terminalbroadcast.BroadCast("/topic/share", ComplexData,Event.getType());
			}
			else if (Event.getType().equals(UnitEvents.Type.DEEP)) {
				
				filters.setIDItem(Event.getID());
				filters.setEvent(Event.getType());
				filters.setUniqueItem(Event.getUniqeItemID()); //Unique Item For Same Item That Just Has Difference In Time Need For Tracking//
				
				int Snapshot=Integer.valueOf(Event.getUniqeItemID().split("@")[0]);
				
				UnitChain uc=InRamDBDeep.get(Event.getID());

				//Find Item That Should BroadCast Maybe Just Once Time Execute//
				
				if (uc == null) {
					System.out.println("FIND ITEM DEEEP NOT IN RAM : "+Event.getUniqeItemID()+" RAM DEEP SIZE: "+InRamDBDeep.size());
					continue;
				}
				
				
				//Careful DeadLock While Loop//
				// Find From Index //
				while(Snapshot>1) {
					uc=uc.getNextUnit();
					Snapshot--;
				}
				
				// Find From Element //
				while(!uc.getUniqeItemID().equals(Event.getUniqeItemID()))
					uc=uc.getNextUnit();
				
				try {
					
					//Check Deep Item Has 3 Record//
					//Just Now Send Time(i) First Record To View//
					//Re implement Again For Demonstrate 3 Record//
					filters.setItem(uc.getUnitContent().split(";")[0]);
					
					//Return Result Filter That Added//
					ArrayList<String> Result=filters.PrepairFilters(filters.getEvent());
					
					// ID:EVENT:MESSAGE:FILTERS
					String ComplexData=filters.getIDItem()+"<>"+filters.getEvent().name()+"<>"+filters.getItem()+"<>";
					
					for (String string : Result) {
						ComplexData+=string+"<>";
					}
					// Re Implement Calling Filter //
					
					
					
					terminalbroadcast.BroadCast("/topic/deep", ComplexData,Event.getType());
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public HashMap<String,ItemPropertyHolder> getCacheHashMap() {
		return CacheHashMap;
	}

	public void setCacheHashMap(HashMap<String,ItemPropertyHolder> cacheHashMap) {
		CacheHashMap = cacheHashMap;
	}

	@Override
	public void run() {
		try {
			CheckerItemEvent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public void setBrokerTower(BlockingQueue<UnitEvent> brokerTower) {
		BrokerTower = brokerTower;
	}
	
	public HashMap<String, GraphChain> getInRamDBOwners() {
		return InRamDBOwners;
	}

	public void setInRamDBOwners(HashMap<String, GraphChain> inRamDBOwners) {
		InRamDBOwners = inRamDBOwners;
	}
}
