package Foundation.FFS.Services;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import Foundation.FFS.Filters.Filter;
import Foundation.FFS.FFSBootStrapper;
import Foundation.FFS.Filters.*;
import Foundation.FFS.Filters.WindowItem;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.ItemPropertyHolder;
import Foundation.FFS.Units.UnitChain;
import Foundation.FFS.Units.UnitEvents;

@Component
@Scope("prototype")
public class FFSFilters implements UnitEvents {
	
	//Fetch One PTR TO Context//
	@Autowired
	private ApplicationContext appcontext;
	
	@Autowired
	private FFSBootStrapper strapper;
	
	private String Item;
	private String IDItem;
	private String UniqueItem;

	private UnitEvents.Type Event;
	


	//Storage For Filters//
	private ArrayList<Filters> FilterList;
	final private ArrayList<WindowItem> WindowShare;
	final private ArrayList<WindowItem> WindowDeep;

	public FFSFilters() {
		WindowShare=new ArrayList<WindowItem>(); //Save History Just 3000 Item//
		WindowDeep=new ArrayList<WindowItem>(); //Save History Just 700 Item//
	}
	
	public ArrayList<String> PrepairFilters(UnitEvents.Type itemevent) throws Exception {
		
		// Fix Filters Sequential //
		this.FilterList=new ArrayList<Filters>();
		
		if(itemevent.equals(UnitEvents.Type.SHARE)) {
			
			// Should Implement Again For Any Users //
			
			this.FilterList.add(new Filter1(appcontext,this.UniqueItem,WindowDeep));
			// Customize Per User//
			
			ArrayList<String> ResultSet=new ArrayList<String>();

			//System.out.println("Filter Item : "+this.Item);
			
			//Clear Window If Size Reached//
			if(WindowShare.size() > 500)
				WindowShare.clear();

			WindowShare.add(new WindowItem(IDItem, "")); //Item // Do Not Item //
			
			//Result of Per Filter Inserted To ResultSet With : FilterQS=FilterData// 
			for (Filter filter : FilterList) {
				filter.run(IDItem, Item,ResultSet);
			}
			
			this.FilterList.clear();
			
			return ResultSet;
		}
		else if (itemevent.equals(UnitEvents.Type.DEEP)) {
			
			//Re implement For Making Dynamic Filter That Needed//
			//Implement With Just Pointer Not New Item//
			//IoC Spring Can Inject//
			
			
			// Re Implement Added From Options PTR //
			// Add In Index And Set <td> Element In Index Because Easier Algorithm //
			// Seq And Showing Filter
			Filters f1=new Filter1(appcontext,this.UniqueItem,WindowDeep); 
			Filters f2=new Filter2(appcontext,this.UniqueItem,WindowDeep); 
			Filters f3=new Filter3(appcontext,this.UniqueItem,WindowDeep); 
			Filters f4=new Filter4(appcontext,this.UniqueItem,WindowDeep); 

			
			this.FilterList.add(f1);
			this.FilterList.add(f2);
			this.FilterList.add(f3);
			this.FilterList.add(f4);
			
			
			// SECTION //
			//Parallel Filter//
			//ReImplement With Type Parallel Filter//
			
			
			//End

			
			
			//Sequence Filter Runs//
			ArrayList<String> ResultSet=new ArrayList<String>();

			//System.out.println("Filter Item : "+this.Item);
			if(WindowDeep.size() > 500)
				WindowDeep.clear();

			WindowDeep.add(new WindowItem(IDItem, "")); //Item // Do Not Item //
			
			//Result of Per Filter Inserted To ResultSet With : FilterQS=FilterData// 
			
			for (Filter filter : FilterList) {
				filter.run(IDItem, Item,ResultSet);
			}
			
			this.FilterList.clear();
			
			return ResultSet;
			
		}
		
		return null;
	}

	public String getItem() {
		return Item;
	}

	public void setItem(String item) {
		Item = item;
	}

	public String getIDItem() {
		return IDItem;
	}

	public void setIDItem(String iDItem) {
		IDItem = iDItem;
	}
	
	public UnitEvents.Type getEvent() {
		return Event;
	}

	public void setEvent(UnitEvents.Type event) {
		Event = event;
	}
	
	public String getUniqueItem() {
		return UniqueItem;
	}

	public void setUniqueItem(String uniqueItem) {
		UniqueItem = uniqueItem;
	}
}
