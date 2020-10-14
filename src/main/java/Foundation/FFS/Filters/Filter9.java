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

import Foundation.FFS.Filters.Filter.FilterTask;
import Foundation.FFS.Filters.Filter.FilterType;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.UnitChain;

/* Filter8 Provide Owner Simple Calculation And Simple Column
 * Basic Of Owners View System
 * For Complex Calculation Reuse And Make Another Filters
 * Filter8 Can Call Any Filter
 */

@Component
@Scope("prototype")
public class Filter9 extends Filters {

	public Filter9(ApplicationContext context,String UniqueItem,ArrayList<WindowItem> window) {
		super(context,UniqueItem, window,FilterType.SEQUENTIAL,FilterTask.VIEW);
		
		this.PointX=2;
	}
	
	@Override
	public void run(String id,String item,ArrayList<String> FilterResult) throws Exception {
		setIDItem(id);
		setItem(item);

		// Task //
		// Processing Some Stage In TimeLine //
		
		// STAGE-1 //
		// Any Updated Should Warned //
		
		// Time->Capacity Of Shares //
		// This cpTimeLine Sorted In Last Filter Do not need Sorting //
		String[] cpTimeLine=this.Item.split(";");
		
		String name=cpTimeLine[cpTimeLine.length-1];
		double RealTimePrice=Double.valueOf(cpTimeLine[cpTimeLine.length-2]); // In Last Item We Save RealTime Price //
		
		// Compare LastDay With 4 Day Before //
		boolean flagclose=false;
		for(int i=0;i<=cpTimeLine.length-4;i++) {

			//Step By Step Check//
			if(!cpTimeLine[i].split("->")[1].equals(cpTimeLine[i+1].split("->")[1])) {
				//Time Reversed// Time(i+1)->Time(i)
				//Found Difference// Found(i)-Found(i+1) 1- i<i+1 we have decreased found state 2- i>i+1 we have increased found state
				//Rate Of Increase Power//
				long fi=Long.valueOf(cpTimeLine[i].split("->")[1]);
				long fi1=Long.valueOf(cpTimeLine[i+1].split("->")[1]);

				long difference=fi-fi1;

				//If Positive Calculate Increase Power//
				//Calculate How Many Days Need For Next Iteration Increase//
				long[] foundincreaseiteration=difference>0?FindIncreaseDayIteration(i,cpTimeLine):null;
				
				FilterResult.add(0,"<p class=\"text-danger\">Fund Change : ( Date "+cpTimeLine[i+1].split("->")[0]+"->"+cpTimeLine[i].split("->")[0]+") ("+name+")"+
						(difference)+
						"</p>"+
						(difference>0?"<p class=\""+(foundincreaseiteration[1]>5?"bg-success":"")+"\">Avg-Increase-Power: "+String.valueOf(foundincreaseiteration[1])+"%</p>" +
						"<p class=\""+(foundincreaseiteration[0]<10 && foundincreaseiteration[0]!= 0?"bg-success":"")+"\">Avg-Iteration-Day: "+String.valueOf(foundincreaseiteration[0])+"-days</p>":"") // Add Syntax With lazy Syntax //
						+"<hr />"
						);

				flagclose=true;

				break; // Find First Change And Break //
				
			}

			if(i==5) break; // Just Last 5 Day Check 1 week Market Work //
		}

		if(flagclose==false)
			FilterResult.add(0, "");
		
	}
	
	private long[] FindIncreaseDayIteration(int i,String[] cptimeline) {
		int cpStep=i;
		int iteration=0;
		long avgpower=0;
		int occure=0; // We Have 1 in Stock //
		
		i++; // Go Next //

		
		for(;i<=cptimeline.length-4;i++) {
			
			if(!cptimeline[i].split("->")[1].equals(cptimeline[i+1].split("->")[1])) {
				
				long fi=Long.valueOf(cptimeline[i].split("->")[1]);
				long fiplus=Long.valueOf(cptimeline[i+1].split("->")[1]);
				
				long difference=fi-fiplus;
				
				
				if(difference > 0) // Positive
				{
					avgpower=avgpower+(difference*100)/fiplus;
					
					
					cpStep=i-cpStep;
					iteration=iteration+cpStep;
					cpStep=i;
					
					occure++;
				}
				
			}
			
		}
		
		return new long[] {occure>0?iteration/occure:0,occure>0?avgpower/occure:0};
	}
}
