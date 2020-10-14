package Foundation.FFS.Filters;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import Foundation.FFS.Filters.Filter.FilterTask;
import Foundation.FFS.Filters.Filter.FilterType;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.UnitChain;

/* Filter3 Describe Queue Sell/Price State 
 * And Is Observer For Queue Between Sell/Price
 * Another Property Is How And Why Queue Making (Analyze Queue Behavior)
 * Add Constant Value To Properties File
 * Use Constant Syntax For Better Optimization
 * Use If elseIf Clause For Better Optimization
 */

@Component
@Scope("prototype")
public class Filter3 extends Filters {
	
	public Filter3(ApplicationContext context,String UniqueItem,ArrayList<WindowItem> window) {
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
		
		// We Need For Observation Just Compare Time(i-1) And Time(i) //
		long sellnumberslast=Integer.valueOf(FirstRecord[2]);
		long buynumberslast=Integer.valueOf(FirstRecord[3]);
		
		long sellnumbersnow=Integer.valueOf(ItemPart[2]);
		long buynumbersnow=Integer.valueOf(ItemPart[3]);
			
		
		String Result="";
		
		if(sellnumberslast > sellnumbersnow) {
			double[] res=CalculateCOEVariationAndGrowthSpeed(2,InRamDBDeep);
			String coeres=(res[0] > 100)?"<h4 class=\"text-danger\">"+"COEVar "+String.valueOf(res[0])+"</h4>":"COEVar "+String.valueOf(res[0]);
			String speed=(res[1] > 50)?"<hr /><h4 class=\"text-danger\">"+"Speed [Queue] "+String.valueOf(res[1])+"</h4>":"<hr/>Speed "+String.valueOf(res[1]);
			
			Result+="SELLER DW "+String.valueOf(sellnumberslast-sellnumbersnow)+" "+coeres+" "+speed+" CSS{p-3@mb-2@bg-danger@text-white}";
		}
		else if (sellnumberslast < sellnumbersnow) {
			double[] res=CalculateCOEVariationAndGrowthSpeed(2,InRamDBDeep);
			String coeres=(res[0] > 100)?"<h4 class=\"text-danger\">"+"COEVar "+String.valueOf(res[0])+"</h4>":"COEVar "+String.valueOf(res[0]);
			String speed=(res[1] > 50)?"<hr /><h4 class=\"text-danger\">"+"Speed [Queue] "+String.valueOf(res[1])+"</h4>":"<hr/>Speed "+String.valueOf(res[1]);

			Result+="SELLER UP "+String.valueOf(sellnumbersnow-sellnumberslast)+" "+coeres+" "+speed+" CSS{p-3@mb-2@bg-success@text-white}";
		}
		else if (buynumberslast > buynumbersnow) {
			double[] res=CalculateCOEVariationAndGrowthSpeed(3,InRamDBDeep);
			String coeres=(res[0] > 100)?"<h4 class=\"text-danger\">"+"COEVar "+String.valueOf(res[0])+"</h4>":"COEVar "+String.valueOf(res[0]);
			String speed=(res[1] > 50)?"<hr /><h4 class=\"text-danger\">"+"Speed [Queue] "+String.valueOf(res[1])+"</h4>":"<hr/>Speed "+String.valueOf(res[1]);

			Result+="BUYER DW "+String.valueOf(buynumberslast-buynumbersnow)+" "+coeres+" "+speed+" CSS{p-3@mb-2@bg-danger@text-white}";
		}
		else if (buynumberslast < buynumbersnow) {
			double[] res=CalculateCOEVariationAndGrowthSpeed(3,InRamDBDeep);
			String coeres=(res[0] > 100)?"<h4 class=\"text-danger\">"+"COEVar "+String.valueOf(res[0])+"</h4>":"COEVar "+String.valueOf(res[0]);
			String speed=(res[1] > 50)?"<hr /><h4 class=\"text-danger\">"+"Speed [Queue] "+String.valueOf(res[1])+"</h4>":"<hr/>Speed "+String.valueOf(res[1]);
			
			Result+="BUYER UP "+String.valueOf(buynumbersnow-buynumberslast)+" "+coeres+" "+speed+" CSS{p-3@mb-2@bg-success@text-white}";
			
			//Need Flow So ADDED//
			int resscore=(res[0] >= 200)?ScoreAssign(id, "COEVar-Buyer","COEVar-Buyer BURST", this.PointX*3,ScoreType.ADDED): // COEVar More Impress If More Than 150 COEVar //
			((res[0] >= 100)?ScoreAssign(id, "COEVar-Buyer","COEVar-Buyer UP", this.PointX*2,ScoreType.ADDED):0); // COEVar More Impress If More Than 150 COEVar //
			
			resscore=(res[1] >= 150)?ScoreAssign(id, "SPEED-Buyer","SPEED-Buyer BURST", this.PointX*3,ScoreType.ADDED): // COEVar More Impress If More Than 150 COEVar //
			(res[1] >= 100)?ScoreAssign(id, "SPEED-Buyer","SPEED-Buyer UP", this.PointX*2,ScoreType.ADDED):0; // COEVar More Impress If More Than 150 COEVar //
		}
		else
			Result+="FIX";
		
		FilterResult.add(Result);
		
	}
	

}
