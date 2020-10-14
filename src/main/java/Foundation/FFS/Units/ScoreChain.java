package Foundation.FFS.Units;

import java.util.ArrayList;
import java.util.HashMap;

public class ScoreChain {
	public enum Type{
		SELLQUEUE,
		BUYQUEUE,
		TRADE,
		CLOSE,
		BUSY,
		OPEN
	}
	
	private String ID;
	private String Name;
	private int Point;
	private int Suggestion;
	private HashMap<String,Integer> Reason;
	private Type type;
	
	public ScoreChain(String id,String name,String reason) {
		this.ID=id;
		this.Name=name;
		
		this.Point=1;
		this.Suggestion=0;
		
		Reason=new HashMap<String,Integer>();
		
		this.Reason.put(reason,1);
		
		this.type=Type.OPEN;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public int getPoint() {
		return Point;
	}

	public void setPoint(int point) {
		Point = point;
	}

	public int getSuggestion() {
		return Suggestion;
	}

	public void setSuggestion(int suggestion) {
		Suggestion = suggestion;
	}

	public HashMap<String,Integer> getReason() {
		return Reason;
	}

	public void setReason(HashMap<String,Integer> reason) {
		Reason = reason;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
}
