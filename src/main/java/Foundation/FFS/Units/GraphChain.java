package Foundation.FFS.Units;

import java.util.HashMap;

public class GraphChain {
	
	private String Ownername;
	private String Ownerid;
	
	public class SimpleShareData{
		
		public SimpleShareData(String name,String id,long numbers,Float perc,UnitChain us,UnitChain timeline) {
			Sharename=name;
			Shareid=id;
			Sharenumbers=numbers;
			Sharepercentage=perc;
			US=us; // PTR TO SHARE IN RAMDB SHARE //
			TimeLine=timeline; // Time Line Storage // Save Time Line String With Compress Method //
		}
		
		public String Sharename;
		public String Shareid;
		public long Sharenumbers;
		public float Sharepercentage;
		public UnitChain US;
		public UnitChain TimeLine;
	}
	
	public HashMap<String, SimpleShareData> ShareConnection=new HashMap<String, SimpleShareData>();
	

	public GraphChain(String ownerid,String ownername) {
		this.setOwnername(ownername);
		this.setOwnerid(ownerid);
	}
	
	public void AddShare(String name,String id,long numbers,Float perc,UnitChain us,UnitChain timeline) {
		//Do Not Check Validate If Share There Are So Updated//
		ShareConnection.put(id, new SimpleShareData(name,id,numbers,perc,us,timeline));
	}
	
	public SimpleShareData getSSD(String Item) {
		return ShareConnection.get(Item);
	}
	
	public String getOwnername() {
		return Ownername;
	}

	public void setOwnername(String ownername) {
		Ownername = ownername;
	}

	public String getOwnerid() {
		return Ownerid;
	}

	public void setOwnerid(String ownerid) {
		Ownerid = ownerid;
	}

}
