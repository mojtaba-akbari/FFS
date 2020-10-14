package Foundation.FFS.Filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import Foundation.FFS.Filters.Filter.FilterTask;
import Foundation.FFS.Filters.Filter.FilterType;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.UnitChain;

/* Filter7 For Download Description Of Items
 */

@Component
@Scope("prototype")
public class Filter7 extends Filters {
	
	public Filter7(ApplicationContext context,String UniqueItem,ArrayList<WindowItem> window) {
		super(context,UniqueItem, window,FilterType.SEQUENTIAL,FilterTask.PROVIDER);
	}
	
	private String GetUniqueNamesAndID(String input,String ISIN) throws Exception {
		//OutPut Of Download Data Is HTML BASE SO I SHOULD REGEX//
		/*
		 * 
		 */
		String value="";
		//System.out.println(ISIN.substring(4,8));
		
		Matcher match=Pattern.compile("IR(..)"+ISIN.substring(4,8)+"(....)").matcher(input);
		String[] Res=new String[2];
		
		int i=0;
		while(match.find()) {
			Res[i]=match.group(0); // 0-Share ISIN 1-Company ISIN //
			i++;
		}
		
		return Res[1]; //Just Return Company ISIN//
	}

	
	@Override
	public void run(String id,String item,ArrayList<String> FilterResult) throws Exception {
		setIDItem(id);
		setItem(item);
		
		// Task //
		
		//This Filter Work With ISIN//
		//Use CacheHashMap Because Maybe Filter Called From Init System//
		String ISIN=CacheHashMap.get(id).getItemContent().split(",")[1];
		
		String Result="";
		
		Thread.sleep(200);// 200 Mili Second Sleep For Trap WAF + Time For Download //

		String downloaddata=DownloadData("http://tsetmc.ir/Loader.aspx?Partree=15131M&i="+id, "GET","HTML");
		
		
		if(downloaddata != null)
			if(downloaddata.contains(id)) {
				Result+=GetUniqueNamesAndID(downloaddata,ISIN);
				FilterResult.add(Result);
			}
		
	}
	

}
