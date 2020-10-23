package Foundation.FFS.Filters;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Foundation.FFS.Filters.Filter.FilterTask;
import Foundation.FFS.Filters.Filter.FilterType;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.UnitChain;

/* Filter4 Describe Capacity And Volume Of Buy Or Price
 * And Is Observer Flow Of Foundation
 * Another Property Is How And Why Queue Making (Analyze Flow Behavior)
 * Add Constant Value To Properties File
 * Use Constant Syntax For Better Optimization
 * Use If elseIf Clause For Better Optimization
 */

@Component
@Scope("prototype")
public class Filter4 extends Filters {
	
	private enum TypeTrader{
		PERSONAL,
		COMPANY,
		UNKNOWN
	}
	
	private enum TypeFoundation{
		BIGGEST,
		BIG,
		LARG,
		MID,
		PERSONALITY,
		SMALL
	}
	
	public Filter4(ApplicationContext context,String UniqueItem,ArrayList<WindowItem> window) {
		super(context,UniqueItem, window,FilterType.SEQUENTIAL,FilterTask.VIEW_SCORE);
		
		this.PointX=2;
	}
	
	private double[] BuySellFoundation() {
		String[] ItemPart=this.Item.split(",");
		
		// Foundation=Price*Capacity //
		double bf=Double.valueOf(Double.valueOf(ItemPart[4])*Double.valueOf(ItemPart[6])); //Buy
		double sf=Double.valueOf(Double.valueOf(ItemPart[5])*Double.valueOf(ItemPart[7])); //Sell
		return new double[] {bf,sf};
	}
	
	private String BehaviorFoundation() {
		String[] ItemPart=this.Item.split(",");
		double[] sellitem= {Double.valueOf(ItemPart[5]),Double.valueOf(ItemPart[6]),Double.valueOf(ItemPart[7])};
		double[] buyitem= {Double.valueOf(ItemPart[2]),Double.valueOf(ItemPart[3]),Double.valueOf(ItemPart[4])};
		
		String behavior="";
		// Make A Neural Network And Decision For This Item //
		// Per Parameter Type Should * Metric : COMPANY * 3 , 3 is Metric For Insurance
		// Simple Implement //
		
		int ins=0;
		
		if(sellitem[0] != 0) {
			behavior+="Sell(numbers,capacity)";
			//Numbers
			if(sellitem[0] > 8) //With 5 numbers is real but more than near to unreal
				ins++;
			
			//Capacity
			if(sellitem[2] > 10000) //With 5 numbers is real but more than near to unreal
				ins++;
			
			
			behavior+=(ins==0)?TypeTrader.UNKNOWN.toString():TypeTrader.COMPANY.toString()+"*"+String.valueOf(ins)+" ";
			
			//Add One Line For Readability//
			behavior+="<hr />";
		}
		
		int inb=0;
		
		if(buyitem[0] != 0) {
			behavior+="Buy(numbers,capacity)";
			
			//Numbers
			if(buyitem[0] > 8) //With 5 numbers is real but more than near to unreal
				inb++;
				
			//Capacity
			if(buyitem[2] > 10000) //With 5 numbers is real but more than near to unreal
				inb++;
			
			behavior+=(inb==0)?TypeTrader.UNKNOWN.toString():TypeTrader.COMPANY.toString()+"*"+String.valueOf(ins)+" ";
			
			int resscore=(inb>=2)?ScoreAssign(this.IDItem, "Company","Company BEHAVIOR", this.PointX+1,ScoreType.ADDED):0;
		}
		
		return behavior.equals("")? TypeTrader.UNKNOWN.toString()+"/"+TypeTrader.PERSONAL.toString():behavior;
		
	}
	
	@Override
	public void run(String id,String item,ArrayList<String> FilterResult) throws Exception {
		setIDItem(id);
		setItem(item);
		
		// Task //

		String Result="";

		double[] res=BuySellFoundation();
		String buyfoundation=(res[0] > new Double("3000000000000"))?"<h4 class=\"text-danger\">"+"Buy Found "+String.valueOf(new BigDecimal(res[0]).toPlainString())+"</h4>":"Buy Normal";
		String sellfoundation=(res[1] > new Double("3000000000000"))?"<h4 class=\"text-danger\">"+"Sell Found "+String.valueOf(new BigDecimal(res[1]).toPlainString())+"</h4>":"Sell Normal";
		String behavior=BehaviorFoundation();
		
		Result+=buyfoundation+" <hr /> "+sellfoundation+" <hr /> "+behavior;
		
		//Efect Of Capacity//
		int resscore=(res[0] > new Double("3000000000000"))?ScoreAssign(id, "Buy","Buy BURST", this.PointX*8,ScoreType.ONETIME): // Buyfoundation More Than 100B //
		(res[0] > new Double("2000000000000"))?ScoreAssign(id, "Buy","Buy UP", this.PointX*6,ScoreType.ONETIME):0; // Buyfoundation More Than 10B //
		
		FilterResult.add(Result);
		
	}
	

}
