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
import Foundation.FFS.Filters.Filter.ScoreType;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.UnitChain;

/* Filter10 Provide Connection Between Owner Score Table
 * Basic Of Owners Score System
 * For Complex Calculation Reuse And Make Another Filters
 */

@Component
@Scope("prototype")
public class Filter10 extends Filters {
	
	public Filter10(ApplicationContext context,String UniqueItem,ArrayList<WindowItem> window) {
		super(context,UniqueItem, window,FilterType.SEQUENTIAL,FilterTask.SCORE);
		
		this.PointX=2;
	}
	
	private void FindPricePattern(String id,Double Price) {
		if (Price > new Double("10000000000000")) {
			int res=ScoreAssign(id, "Company","Company TRADE-BURST", this.PointX*5,ScoreType.ADDED); // INJECT TO SCORE //
		}
		else if (Price > new Double("1000000000000")) {
			int res=ScoreAssign(id, "Company","Company TRADE-UP", this.PointX*4,ScoreType.ADDED); // INJECT TO SCORE //
		}
		else if(Price  > new Double("100000000000")) {
			int res=ScoreAssign(id, "Company","Company TRADE", this.PointX*2,ScoreType.ADDED); // INJECT TO SCORE //
			
		}
	}
	
	@Override
	public void run(String id,String item,ArrayList<String> FilterResult) throws Exception {
		setIDItem(id); // Owner Id
		setItem(item); // Owner (Share Id)

		// Task //
		// Fetch Data From SnapShot Of Key//
		GraphChain gc=InRamDBOwners.get(id);


		long Capacity=gc.ShareConnection.get(item).Sharenumbers;



		long realtimeprice=Long.parseLong(gc.ShareConnection.get(item).US.getHeadToLastItemPointer().getUnitContent().split(",")[6]);//Index 6 References To InRamDBShare//

		FindPricePattern(item,new Double(Capacity*realtimeprice));
		
		//Calculation Time line//

		String timeline=gc.ShareConnection.get(item).TimeLine.getUnitContent().replace(",","->"); // Split Just In Time //

		//Sorted Array//
		String[] array_timeline_reverse=timeline.split(";");

		for(int k = 0; k < array_timeline_reverse.length / 2; k++)
		{
			String temp = array_timeline_reverse[k];
			array_timeline_reverse[k] = array_timeline_reverse[array_timeline_reverse.length - k - 1];
			array_timeline_reverse[array_timeline_reverse.length - k - 1] = temp;
		}

		//Call TimeLine Processor//

		//End TimeLine

		//Danger $$$ For More Than 10B//
		//FindPricePattern(Key,sharefoundationrealtime)
		
		
		
		
		
		// Task //
		// Processing Some Stage In TimeLine //
		
		// Time->Capacity Of Shares //
		// This cpTimeLine Sorted In Last Filter Do not need Sorting //
		String[] cpTimeLine=array_timeline_reverse;
		
		// Compare LastDay With 4 Day Before //
		for(int i=0;i<cpTimeLine.length-1;i++) {

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
				
				if(difference>0) {
					int res=foundincreaseiteration[1]>=10 && foundincreaseiteration[0]<=10?ScoreAssign(item, "Company-Power","Company-Power FIRE", this.PointX*8,ScoreType.ADDED):
					foundincreaseiteration[1]>=5 && foundincreaseiteration[0]<=10?ScoreAssign(item, "Company-Power","Company-Power FOCUS", this.PointX*6,ScoreType.ADDED):
					foundincreaseiteration[1]>=2 && foundincreaseiteration[0]<=20?ScoreAssign(item, "Company-Power","Company-Power VISION", this.PointX*4,ScoreType.ADDED):0;
				}

				break; // Find First Change And Break //
				
			}

			if(i==6) break; // Just Last 5 Day Check 1 week Market Work //
		}

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
