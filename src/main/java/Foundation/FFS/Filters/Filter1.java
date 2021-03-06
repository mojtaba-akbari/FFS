package Foundation.FFS.Filters;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.context.ApplicationContext;

import Foundation.FFS.Filters.Filter.FilterTask;
import Foundation.FFS.Filters.Filter.FilterType;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.UnitChain;

/* Filter1 Calculate Occurrence Of Item In specific Time Window 
 * We Can Change Size Of Window
 */

public class Filter1 extends Filters {
	
	
	public Filter1(ApplicationContext context,String UniqueItem,ArrayList<WindowItem> window) {
		super(context,UniqueItem, window,FilterType.SEQUENTIAL,FilterTask.VIEW_SCORE);
		
		this.PointX=1; // Multiplication //
	}

	@Override
	public void run(String id,String item,ArrayList<String> FilterResult) {
		setIDItem(id);
		setItem(item);
		
		// Task //
		int occurence=0;
		for(int index=0;index<Window.size();index++)
			if(Window.get(index).getID().equals(id)) occurence++;
		
		
		// Occurrence > 4 : (Point=1 * Fix Metric For Filter=1) = 1 -----> If Continue This Increase :) Flow Of Data Should Sense //
		int res=occurence>=4?ScoreAssign(id, "Occurrence","Occurrence UP", this.PointX*1,ScoreType.ADDED):0; // INJECT TO SCORE //
		
		FilterResult.add("Occurr "+String.valueOf(occurence));
	}
}
