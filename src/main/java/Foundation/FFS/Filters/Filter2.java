package Foundation.FFS.Filters;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.context.ApplicationContext;

import Foundation.FFS.Filters.Filter.FilterTask;
import Foundation.FFS.Filters.Filter.FilterType;
import Foundation.FFS.Filters.Filter.ScoreType;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.UnitChain;

/* Filter2 Describe Capacity And Price Change 
 * And Is Observer For Difference Between Those
 */
public class Filter2 extends Filters {

	public Filter2(ApplicationContext context,String UniqueItem,ArrayList<WindowItem> window) {
		super(context,UniqueItem, window,FilterType.SEQUENTIAL,FilterTask.VIEW_SCORE);
		
		this.PointX=2;
	}

	@Override
	public void run(String id,String item,ArrayList<String> FilterResult) throws Exception {
		setIDItem(id);
		setItem(item);
		
		// Task //
		// Check Last Price Is Up Or Down //
		// * This Item Is At Last Of List Surly PrevItem Called //
		
		String LastItemContent=InRamDBDeep.get(id).getHeadToLastItemPointer().getPrevUnit().getUnitContent();
		String[] FirstRecord=LastItemContent.split(";")[0].split(",");

		String[] ItemPart=this.Item.split(",");
		
		long sellpricelast=Integer.valueOf(FirstRecord[5]);
		long buypricelast=Integer.valueOf(FirstRecord[4]);
		
		long sellpricenow=Integer.valueOf(ItemPart[5]);
		long buypricenow=Integer.valueOf(ItemPart[4]);
		
		long sellcapacitylast=Integer.valueOf(FirstRecord[7]);
		long buycapacitylast=Integer.valueOf(FirstRecord[6]);
		
		long sellcapacitynow=Integer.valueOf(ItemPart[7]);
		long buycapacitynow=Integer.valueOf(ItemPart[6]);
		
		String Result="";
		
		//Price//
		if(sellpricelast > sellpricenow)
			Result+="SELL PRICE DW "+String.valueOf(sellpricelast-sellpricenow+" CSS{p-3@mb-2@bg-danger@text-white}");
		else if (sellpricelast < sellpricenow)
			Result+="SELL PRICE UP "+String.valueOf(sellpricenow-sellpricelast+" CSS{p-3@mb-2@bg-success@text-white}");
		else if (buypricelast > buypricenow)
			Result+="BUY PRICE DW "+String.valueOf(buypricelast-buypricenow+" CSS{p-3@mb-2@bg-danger@text-white}");
		else if (buypricelast < buypricenow) {
			
			int res=ScoreAssign(id, "Price","Price UP", this.PointX*1,ScoreType.ADDED); // INJECT TO SCORE //
			
			Result+="BUY PRICE UP "+String.valueOf(buypricenow-buypricelast+" CSS{p-3@mb-2@bg-success@text-white}");
		}
		else
			Result+="PRICE FIX ";
		
		
		//Capacity//
		if(sellcapacitylast > sellcapacitynow)
			Result+="SELL CAPACITY DW "+String.valueOf(sellcapacitylast-sellcapacitynow+(Result.contains("CSS")?"":" CSS{p-3@mb-2@bg-danger@text-white}"));
		else if (sellcapacitylast < sellcapacitynow)
			Result+="SELL CAPACITY UP "+String.valueOf(sellcapacitynow-sellcapacitylast+(Result.contains("CSS")?"":" CSS{p-3@mb-2@bg-success@text-white}"));
		else if (buycapacitylast > buycapacitynow)
			Result+="BUY CAPACITY DW "+String.valueOf(buycapacitylast-buycapacitynow+(Result.contains("CSS")?"":" CSS{p-3@mb-2@bg-danger@text-white}"));
		else if (buycapacitylast < buycapacitynow) {
			
			int res=ScoreAssign(id, "Capacity","Capacity UP", this.PointX*2,ScoreType.ADDED); // Capacity More Impress //
			
			Result+="BUY CAPACITY UP "+String.valueOf(buycapacitynow-buycapacitylast+(Result.contains("CSS")?"":" CSS{p-3@mb-2@bg-success@text-white}"));
		
		}else
			Result+="CAPACITY FIX ";
		
		FilterResult.add(Result);
		
	}
}
