package Foundation.FFS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import Foundation.FFS.DataLayer.InfluxDBTemplate;
import Foundation.FFS.Filters.Filter;
import Foundation.FFS.Filters.Filter10;
import Foundation.FFS.Filters.Filter5;
import Foundation.FFS.Filters.Filter6;
import Foundation.FFS.Filters.Filter7;
import Foundation.FFS.Filters.Filter8;
import Foundation.FFS.Filters.Filters;
import Foundation.FFS.Services.FFSItemsToFilter;
import Foundation.FFS.Services.FFSScrapper;
import Foundation.FFS.Services.FFSStatusChecker;
import Foundation.FFS.Units.GlobalContainer;
import Foundation.FFS.Units.GraphChain;
import Foundation.FFS.Units.ItemPropertyHolder;
import Foundation.FFS.Units.ScoreChain;
import Foundation.FFS.Units.UnitChain;
import Foundation.FFS.Units.UnitEvent;

@Component
@ComponentScan(basePackages = {"Foundation.FFS","Foundation.FFS.DataLayer","Foundation.FFS.WebSockets",
		"Foundation.FFS.Configure","Foundation.FFS.Services","Foundation.FFS.Filters",
		"Foundation.FFS.Units"})
public class FFSBootStrapper {
	
	@Autowired
	private ApplicationContext applicationContext;

	/* Service Consume Storage */
		// SHARES //
	private HashMap<String, ItemPropertyHolder> CacheBase;
		// SCORES //
	private HashMap<String, ScoreChain> CacheScores;
		// Tunnel //
	private ArrayList<BlockingQueue<UnitEvent>> BrokerTower;
	private ArrayList<Thread> ConsumerThread;
	private ArrayList<FFSItemsToFilter> ListConsumer;
		// InRAMDB //
	private HashMap<String,UnitChain> InRamDBShares;
	private HashMap<String,UnitChain> InRamDBDeeps;
	// Owner->{Shares} //
	private HashMap<String,GraphChain> InRamDBOwners; 
	
	/* Section Storage */
	
	
	
	
	@Autowired
	private GlobalContainer GC;
	
	/* End Of Section Of Cache Ram DB */
	

	private int Size=3500; //Size of Tunnels In BrokerTowers Cell//
	
	@Autowired
	final private InfluxDBTemplate IDBT;
	
	@Autowired
	final private FFSScrapper Scrapper;
	
	@Autowired
	final private FFSStatusChecker SChecker;
	
	public FFSBootStrapper(final InfluxDBTemplate idbt,final FFSScrapper scrapper,final FFSStatusChecker schecker) {
		this.IDBT = idbt;
		this.Scrapper=scrapper;
		this.SChecker=schecker;
		
		this.CacheBase=new HashMap<String, ItemPropertyHolder>();
		this.CacheScores=new HashMap<String,ScoreChain>();
		this.InRamDBShares=new HashMap<String,UnitChain>();
		this.InRamDBDeeps=new HashMap<String,UnitChain>();
		this.InRamDBOwners=new HashMap<String,GraphChain>();
		
		//Implement Properties Topics For BrokerTower//
		//Implement Properties Size For Topics//
		//Implement Properties Numbers Of Topics//
		//Do not Use HashMap For Name Of Topics Just Use Index Easily//
		this.BrokerTower=new ArrayList<BlockingQueue<UnitEvent>>();

		
		this.ListConsumer=new ArrayList<FFSItemsToFilter>();
		
		this.ConsumerThread=new ArrayList<Thread>();
	}
	
	@PostConstruct
	public void init() {
		// Create Two Consumer //
		// Re implement Optional Item //
		for(int i=0;i<=1;i++)
		{
			this.BrokerTower.add(i,new LinkedBlockingQueue<UnitEvent>(Size));
			
			FFSItemsToFilter itemfilter=applicationContext.getBean(FFSItemsToFilter.class);
			itemfilter.setCacheHashMap(CacheBase);
			itemfilter.setInRamDBDeep(InRamDBDeeps);
			itemfilter.setInRamDBShares(InRamDBShares);
			itemfilter.setBrokerTower(this.BrokerTower.get(i));
			itemfilter.setInRamDBOwners(InRamDBOwners);
			this.ListConsumer.add(itemfilter);
		}
	}
	
	public void run() throws InterruptedException{
		
		// Initial Checker //
		SChecker.setCacheHashMap(this.CacheBase);
		SChecker.setBrokerTower(this.BrokerTower);
		SChecker.setInRamDBShares(this.InRamDBShares);
		SChecker.setInRamDBDeep(InRamDBDeeps);
		SChecker.setInRamDBOwners(InRamDBOwners);
		
		// Initial Filter Item And Run Broker //
		
		for (FFSItemsToFilter itemsconsumer : ListConsumer) {
			Thread th=new Thread(itemsconsumer);
			ConsumerThread.add(th);
			th.start();
		}
		
		// First Initial Of Timers //
		Timer OwnerCheckerTimer=null;
		Timer TradeCheckerTimer=null;
		
		while(true) {
			// Sequence Jobs //
			try {
				String Result=Scrapper.ScrapperResult().block();
				
				if(Result != null)
				{
					SChecker.setScrapperResult(Result);
					SChecker.Initial();
					
					long RamDBDeepsCells=0;
					for(int i=0;i<InRamDBDeeps.size();i++)
						RamDBDeepsCells+=((UnitChain)InRamDBDeeps.values().toArray()[i]).getSizeOfTree();
					
					long OwnerDBCells=0;
					for(int i=0;i<InRamDBOwners.size();i++)
						OwnerDBCells+=((GraphChain)InRamDBOwners.values().toArray()[i]).ShareConnection.size();
					
					long RamDBShareCells=0;
					for(int i=0;i<InRamDBShares.size();i++)
						RamDBShareCells+=((UnitChain)InRamDBShares.values().toArray()[i]).getSizeOfTree();
					
					System.out.println("Fixed : "+String.valueOf(SChecker.getStatusFix())+
							" Updated: "+String.valueOf(SChecker.getStatusUpdated())+
							" ScoreCache: "+CacheScores.size()+
							" ShareCache: "+CacheBase.size()+
							" DeepRam: "+InRamDBDeeps.size()+","+RamDBDeepsCells+
							" ShareRam: "+InRamDBShares.size()+","+RamDBShareCells+
							" OwnerRam: "+InRamDBOwners.size()+","+OwnerDBCells
							);
					
				}
				else {System.out.println("Result Scrapper Is NULL");}
				
				
				
				
				/* Section Timer */
				
				if(OwnerCheckerTimer==null) {
					System.out.println("[Owner Timer Phase]");

					OwnerCheckerTimer=new Timer();

					T1OwnerTimer OwnerTask=new T1OwnerTimer();
					//First Call Is 20000 Later//
					//So Please Call One Times In Another Tread And Set Timer//

					Timer FirstRun=new Timer(); FirstRun.schedule(new TimerTask() {

						@Override public void run() { OwnerTask.run(); FirstRun.cancel();} }, 15*1000);// X Second After DBShare Process Successfully

					OwnerCheckerTimer.schedule(OwnerTask, 43200*1000); // Second 12 Hour
				}
				
				if(TradeCheckerTimer==null) {
					System.out.println("[Trade Timer Phase]");

					TradeCheckerTimer=new Timer();

					T2TradeTimer TradeTask=new T2TradeTimer();

					TradeCheckerTimer.schedule(TradeTask,10*1000);// X Second After DBShare Process Successfully

				}
				
				
				
				/* End Timer Section */
				
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
			
			// SET TIMER SERVICE TO 1000 MiliSEC //
			Thread.sleep(1000);
		}
		
		
	}
	
	// Section Timer Inner Class //

	class T2TradeTimer extends TimerTask{
		
		public T2TradeTimer() {
			System.out.println("TradeTimer Settel >>>");
		}
		
		@Override
		public void run() {
			
			//ReDownload All Network Of ShareOwner And Set In Storage//
			ArrayList<Thread> SimplePool=new ArrayList<Thread>();
			
			
			//SNAPSHUT FROM SHARES//
			Set<String> KeyArray=CacheBase.keySet();
			
			
			for (int index=0;index<KeyArray.size();index++) {

				//Check Again For Concurrent Issue//
				if(CacheBase.containsKey(KeyArray.toArray()[index])) {
					
					String item=(String) KeyArray.toArray()[index];

					try {
						
						Filters f5=new Filter5(applicationContext,null,null);
						f5.setIDItem(item); // Need
						f5.setItem(null); // Do Not Need


						SimplePool.add(new Thread(f5));
						SimplePool.get(SimplePool.size()-1).start();

						if(SimplePool.size()==15) {
							for(int k=0;k<SimplePool.size();k++)
								SimplePool.get(k).join(); // Wait All X Thread Closed //

							SimplePool.clear();
						}
						
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
					


				}
				
			}
			
		}
		
	}
	
	class T1OwnerTimer extends TimerTask{
		
		public T1OwnerTimer() {
			System.out.println("OwnerTask Settel >>>");
		}
		
		@Override
		public void run() {
			//ReDownload All Network Of ShareOwner And Set In Storage//

			Set<String> KeyArray=CacheBase.keySet();

			for (int index=0;index<KeyArray.size();index++) {
				
				//Check Again For Concurrent Issue//
				if(CacheBase.containsKey(KeyArray.toArray()[index])) {

					String item=(String) KeyArray.toArray()[index];
					String itemdata=CacheBase.get(item).getItemContent();
					String[] simplecells=itemdata.split(",");

					try {
						
						ArrayList<String> Result=new ArrayList<String>();
						
						/* Download Description (Needed Company ISIN) */
						Filter f_init1=new Filter7(applicationContext,null,null);
						f_init1.run(simplecells[0], itemdata, Result);


						if(Result.size() > 0) {
							String CompanyISIN=Result.get(0); // Company ISIN //

							
							//First Update CacheBase With Company ISIN//***
							CacheBase.get(item).getHolder().put("CISIN",CompanyISIN); // Company ISIN TO LAST Part //
							//*********************************************
							
							
							/* Download Share Owners */
							Filter f_init2=new Filter6(applicationContext,null,null);

							Result.clear();

							f_init2.run(simplecells[0], itemdata, Result);
							
							//Again Check Validate Of Result//
							if(Result.size() == 0)
								continue; //Go To Next Element And This Element Fetch Another Time// // We Retain Any Owner That Was Valid In Our RAM //
							
							
							// Easy Way For Pars Is HTML BASE Parser //
							Map<String,Object> mapedjson=JsonParserFactory.getJsonParser().parseMap(Result.get(0));
							
							//Clear Result To Free SPACE //
							//Some Of Data Was Big//
							Result.clear();
							System.gc();
							
							
							List<Object> items=(List<Object>) mapedjson.get("Owners");
							
							//Again Clear JSON//
							mapedjson=null;
							System.gc();
							
							if(items.size() > 0) {
								
								for (Object object : items) {
									//Owner Item//
									//Value Of JSON Is KEY:VALUE//
									Map<String,String> owner=(Map<String, String>) object;
									
									// **** Add Below Line ****//
									if(InRamDBOwners.containsKey(owner.get("ID"))) {
										
										// Make TimeLine For That Share And Owners //
										UnitChain uc=new UnitChain(owner.get("TL"), true, null);
										
										InRamDBOwners.get(owner.get("ID")).AddShare(simplecells[2],item,Long.parseLong(owner.get("SH")),Float.parseFloat(owner.get("PE")),InRamDBShares.get(item),uc);
										
									}
									else {
										
										// Make TimeLine For That Share And Owners //
										UnitChain uc=new UnitChain(owner.get("TL"), true, null);
										
										//Add Owner//
										InRamDBOwners.put(owner.get("ID"), new GraphChain(owner.get("ID"), owner.get("NM")));
										//Add Share To That's List//
										InRamDBOwners.get(owner.get("ID")).AddShare(simplecells[2],item,Long.parseLong(owner.get("SH")),Float.parseFloat(owner.get("PE")),InRamDBShares.get(item),uc);
									
									
									
									}
									
									
									//Call Filter For Insert Score Per Share Just In Time//
									// If Consume Time For Generate Item So Change This Location To Full Loop Fetch Share //
									Filter f_score=new Filter10(applicationContext,null,null);
									f_score.run(owner.get("ID"), item, null);
									
								}
							}
						}
						else continue;
						
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}


				}
				
			}
		}
		
	}
	
	
	public void InsertItem() {
//		Point p=Point.measurement("Share")
//				.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
//				.addField("name", "x")
//				.addField("cost", 2800)
//				.build();
//		BatchPoints BP=BatchPoints.database(IDBT.getInfluxDBConnectionPropertiesHolder().getDatabase())
//				.points(p)
//				.build();
//		
//		IDBT.WriteInstance(BP);
		
		
		int i;
	}
	
	public HashMap<String, ItemPropertyHolder> getCacheBase() {
		return CacheBase;
	}
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public HashMap<String, UnitChain> getInRamDBShares() {
		return InRamDBShares;
	}

	public HashMap<String, UnitChain> getInRamDBDeeps() {
		return InRamDBDeeps;
	}

	public HashMap<String, GraphChain> getInRamDBOwners() {
		return InRamDBOwners;
	}
	
	public HashMap<String, ScoreChain> getCacheScores() {
		return CacheScores;
	}

	public GlobalContainer getGC() {
		return GC;
	}
}
