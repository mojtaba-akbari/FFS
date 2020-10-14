package Foundation.FFS.Filters;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import Foundation.FFS.Filters.Filter.FilterTask;
import Foundation.FFS.Filters.Filter.FilterType;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.UnitChain;

/* Filter6 Scrap Owner Items And Shares
 */

@Component
@Scope("prototype")
public class Filter6 extends Filters {
	
	public Filter6(ApplicationContext context,String UniqueItem,ArrayList<WindowItem> window) {
		super(context,UniqueItem, window,FilterType.SEQUENTIAL,FilterTask.PROVIDER);
	}
	
	private String GraphSharesOwn(String input,String CompanyISIN) throws Exception {
		//OutPut Of Download Data Is HTML BASE SO I SHOULD REGEX//
		/*
		 * Save Data With JSON Format
		 */
		String value="";
		value+="{\"Owners\":[";
		
		//Parse With JSOUP HTML Document//
		Document doc=Jsoup.parse(input);
		Elements indexshareholder=doc.getElementsByClass("sh");
		
		for(int i=0;i<=indexshareholder.size()-1;i++) {
			
			Element isinandinternalid=indexshareholder.get(i); // <tr>
			Element name=isinandinternalid.child(0);//<td>
			// Personal Or Company Is Here //
			// Use ISIN And INTERNAL ID For Hashing //
			Element numbers=isinandinternalid.child(1).childrenSize()>=1?isinandinternalid.child(1).child(0):isinandinternalid.child(1);//<td><div>
			Element percentage=isinandinternalid.child(2);//<td>
			Element growth=isinandinternalid.child(3);//<Td>
			
			Matcher match=Pattern.compile("ii\\.ShowShareHolder\\((.*?)\\)").matcher(isinandinternalid.attr("onclick"));
			
			while(match.find()) {
				String mgroup=match.group(1).replace("'", "");
				String[] listmgroup=mgroup.split(",");
				
				System.out.println(listmgroup[0]+","+listmgroup[1]);
				
				//Download TimeLine//
				String shareholderdata=DownloadData("http://tsetmc.ir/tsev2/data/ShareHolder.aspx?i="+listmgroup[0]+","+listmgroup[1], "GET", "HTML");
				String timeline=shareholderdata.split("#")[0]; // ; Splitter // And Next Splitter For Time,ShareNumber //
				
					
						
				if(listmgroup.length > 1) {
					
					value+="{\"ID\":\""+(numbers.hasAttr("title")?name.text().replace("\n", "").replace("\t", " ").hashCode():(listmgroup[0]+listmgroup[1]).hashCode())+"\","+
							"\"NM\":\""+name.text().replace("\n", "").replace("\t", " ")+"\","+
							"\"SH\":\""+(numbers.hasAttr("title")?numbers.attr("title").replace(",", ""):numbers.text().replace(",", ""))+"\","+
							"\"PE\":\""+percentage.text()+"\","+
							"\"TL\":\""+timeline+"\""+
							"},";
					
					
				}
			}
			
		}
		
		value=value.contains(",")?value.replaceFirst(".$", ""):value;
		
		value+="]}";
		
		
		//System.out.println(value);
		return value;
		
	}

	
	@Override
	public void run(String id,String item,ArrayList<String> FilterResult) throws Exception {
		setIDItem(id);
		setItem(item);
		
		// Task //
		
		//This Filter Work With ISIN//
		//Use CacheHashMap Because Maybe Filter Called From Init System//
		//Get Company ISIN FROM Holder//
		String CompanyISIN=(String) CacheHashMap.get(id).getHolder().get("CISIN");
		
		String Result="";
		
		//Loop For Getting Data//
		
		String downloaddata=null;
		
		Thread.sleep(200);// 200 Mili Second For Escape From WAF+Time Download //
		
		downloaddata=DownloadData("http://tsetmc.ir/Loader.aspx?Partree=15131T&c="+CompanyISIN, "GET","HTML");
		
		if(downloaddata != null) {
			Result+=GraphSharesOwn(downloaddata,CompanyISIN);
			FilterResult.add(Result);
		}
		
	}
	

}
