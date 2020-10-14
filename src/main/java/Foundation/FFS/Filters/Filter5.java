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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import Foundation.FFS.Filters.Filter.FilterTask;
import Foundation.FFS.Filters.Filter.FilterType;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.UnitChain;

/* Filter5 Analyzer For TRADE That Done In Share
 * Run In Thread Mode
 */

@Component
@Scope("prototype")
public class Filter5 extends Filters {
	
	public Filter5(ApplicationContext context,String UniqueItem,ArrayList<WindowItem> window) {
		super(context,UniqueItem, window,FilterType.SEQUENTIAL,FilterTask.SCORE);
		
		this.PointX=3;
	}
	
	private int BigTrade(long capacity,double price) {
		return capacity*price>new Long("8000000000")?3:(capacity*price >new Long("6000000000")?2:(capacity*price>new Long("4000000000")?1:0)); // 600M Trade 400M Trade 200M Trade
	}
	
	private String RecordTradeDownload(String input) throws Exception {
		//Sample Of XML DOC//
		/*<?xml version="1.0" encoding="UTF-8"?>
			<rows>
			<row>
				<cell>1</cell>
				<cell>09:16:32</cell>
				<cell>300</cell>
				<cell>29830.00</cell>
			</row>
			</rows>
		 * 
		 */
		
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		DocumentBuilder documentbuilder=factory.newDocumentBuilder();
		
		ByteArrayInputStream collectionbyte=new ByteArrayInputStream(input.getBytes());
		

		
		Document doc=documentbuilder.parse(collectionbyte);
		
		//Clear Input//
		input=null;
		System.gc();
		
		//Making Table For Result//
		String value="<div style=\"overflow: auto;height:200px;width:100px\">";
		
		//Root Element//
		NodeList root=doc.getElementsByTagName("row");
		
		// We Can Found Follow Of INCREASE WITH THIS CHAIN //
		// First Time > 500 -> Filter INPUT/OUTPUT MID //
		// After Some Times > 700 -> Filter INPUT/OUTPUT UP //
		// At Total Upper Than 400000 Has Total Point //
		int res=root.getLength()>40000?ScoreAssign(this.IDItem,"INPUT/OUTPUT", "INPUT/OUTPUT FIRE", this.PointX*15,ScoreType.ONETIME):
			(root.getLength()>20000?ScoreAssign(this.IDItem, "INPUT/OUTPUT","INPUT/OUTPUT BURST", this.PointX*12,ScoreType.ONETIME):
				root.getLength()>10000?ScoreAssign(this.IDItem, "INPUT/OUTPUT","INPUT/OUTPUT LOT", this.PointX*10,ScoreType.ONETIME):
					root.getLength()>8000?ScoreAssign(this.IDItem, "INPUT/OUTPUT","INPUT/OUTPUT MUCH", this.PointX*4,ScoreType.ONETIME):
						root.getLength()>6000?ScoreAssign(this.IDItem, "INPUT/OUTPUT","INPUT/OUTPUT UPPER", this.PointX*3,ScoreType.ONETIME):
							root.getLength()>3000?ScoreAssign(this.IDItem, "INPUT/OUTPUT","INPUT/OUTPUT UP", this.PointX*2,ScoreType.ONETIME):
								root.getLength()>1000?ScoreAssign(this.IDItem, "INPUT/OUTPUT","INPUT/OUTPUT MID", this.PointX*1,ScoreType.ONETIME):0
				);
		
		
		
		//Every Times We Have A Call From Deep We Run Filter 5 For Checking Trades//
		//So Save Last Element That We Checked And ReCall From That Point//
		//Faster Algorithm And Prohibit From Calculation Again//
		//We Save This Pointer To LastTradeItem In Share Storage//
		//Calculation Variable//
		int lastitemanalyzed=0;
		
		if(this.getCacheHashMap().get(this.IDItem).getHolder().containsKey("LastTradeElementChecked")) 
			lastitemanalyzed=(Integer)this.getCacheHashMap().get(this.IDItem).getHolder().get("LastTradeElementChecked");
		else 
			this.getCacheHashMap().get(this.IDItem).getHolder().put("LastTradeElementChecked", 0);
				
		long CounterTradeInMaxPrice=0;
		int icounter=lastitemanalyzed;
		
		for(;icounter<root.getLength();icounter++)
		{
			Node lr=root.item(icounter);
			NodeList childs = lr.getChildNodes();
			
			//id,time,capacity,price//
			int id=Integer.valueOf(childs.item(1).getTextContent());
			String time=childs.item(3).getTextContent().replace(":", "-");
			double price=0;
			
			if(childs.item(7).getTextContent().contains("."))
				price=Long.valueOf(childs.item(7).getTextContent().substring(0,childs.item(7).getTextContent().indexOf("."))); // Careful . Index In Price //
			else
				price=Long.valueOf(childs.item(7).getTextContent());
			
			long capacity=Long.valueOf(childs.item(5).getTextContent());
			
			
			
			int trade=BigTrade(capacity,price);
			
			//String Style=IsBigTrade?"class=\"text-danger\"":"";
			
			res=trade==3?ScoreAssign(this.IDItem, "TRADE","TRADE BURST", this.PointX*2,ScoreType.ADDED):
				(trade==2?ScoreAssign(this.IDItem, "TRADE","TRADE BIG", this.PointX*1,ScoreType.ADDED):
					(trade==1?ScoreAssign(this.IDItem, "TRADE","TRADE UP", this.PointX*1,ScoreType.ADDED):0)
					); // INJECT TO SCORE //
			
			
			// Calculate Trade Behavior HOPE //
			// HOPE=TRADE*STATUS Price //
			String[] item=this.CacheHashMap.get(this.IDItem).getItemContent().split(",");
			double minprice=Double.valueOf(item[11]);
			double maxprice=Double.valueOf(item[12]);
			double avgpriceday=(minprice+maxprice)/2;
			
			res=trade==3 && price < avgpriceday?ScoreAssign(this.IDItem, "TRADE","TRADE BURST ***HOPE-RES***", this.PointX*3,ScoreType.ADDED):
				(trade==2 && price < avgpriceday?ScoreAssign(this.IDItem, "TRADE","TRADE BIG ***HOPE-RES***", this.PointX*2,ScoreType.ADDED):
					(trade==1 && price < avgpriceday?ScoreAssign(this.IDItem, "TRADE","TRADE UP ***HOPE-RES***", this.PointX*1,ScoreType.ADDED):
						(trade==3 && price==maxprice?ScoreAssign(this.IDItem, "TRADE","TRADE BURST ***HOPE-MAX***", this.PointX*6,ScoreType.ADDED):
							(trade==2 && price==maxprice?ScoreAssign(this.IDItem, "TRADE","TRADE BIG ***HOPE-MAX***", this.PointX*5,ScoreType.ADDED):
								(trade==1 && price==maxprice?ScoreAssign(this.IDItem, "TRADE","TRADE UP ***HOPE-MAX***", this.PointX*4,ScoreType.ADDED):0)
								)
							)
						)
					); // INJECT TO SCORE //
			
			
			if(price==maxprice)	CounterTradeInMaxPrice++;
			
			int maxpriceeffect=(int)(CounterTradeInMaxPrice*100/root.getLength());
			
			res=maxpriceeffect>90 && root.getLength()>2000?ScoreAssign(this.IDItem, "NEXT-DAY","NEXT-DAY=5% BURST", this.PointX*12,ScoreType.ONETIME):
				(maxpriceeffect>70 && root.getLength()>2000?ScoreAssign(this.IDItem, "NEXT-DAY","NEXT-DAY=5% TOP", this.PointX*10,ScoreType.ONETIME):
					(maxpriceeffect>50 && root.getLength()>2000?ScoreAssign(this.IDItem, "NEXT-DAY","NEXT-DAY=5% UP", this.PointX*8,ScoreType.ONETIME):0
							)
					);
			
			//We Just Show 10 Of Items//
			//if(i-root.getLength()<=10)
				//value+="<p "+Style+">"+String.valueOf(id)+"</p><p>time: "+time+"</p><p>Capacity: "+String.valueOf(capacity)+"</p><p>Price: "+String.valueOf(price)+"</p><hr />";
			
		}

		//Update Holder//
		this.getCacheHashMap().get(this.IDItem).getHolder().put("LastTradeElementChecked", icounter);
		
		//value+="<p>Numbers Trade: "+root.getLength()+"</p>"+"</div>";
		
		return value;
	}

	// No Parallel //
	@Override
	public void run(String id,String item,ArrayList<String> FilterResult) throws Exception {
		setIDItem(id);
		setItem(item);

	}
	
	// Parallel //
	@Override
	public void run() {
		// Need 500 for Web DownLoad Data //
		try {
			Thread.sleep(1000);
			
			String downloaddata=DownloadData("http://tsetmc.ir/tsev2/data/TradeDetail.aspx?i="+this.IDItem, "GET","OCTED");
			if(downloaddata != null && downloaddata != "")
				RecordTradeDownload(downloaddata);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Delete Controller THREAD For TRADE //
		this.CacheHashMap.get(IDItem).getHolder().remove("TRADE_RUNNER");
	}

}
