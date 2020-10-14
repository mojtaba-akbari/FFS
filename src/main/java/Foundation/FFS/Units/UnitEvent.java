package Foundation.FFS.Units;

public class UnitEvent implements UnitEvents {
	
	private String ID;
	private String UniqeItemID;
	Type Type;
	
	public UnitEvent(String id,Type type,String Uniq) {
		this.ID=id;
		this.Type=type;
		this.UniqeItemID=Uniq;
	}
	
	public Type getType() {
		return Type;
	}
	public void setType(Type type) {
		this.Type = type;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}

	public String getUniqeItemID() {
		return UniqeItemID;
	}

	public void setUniqeItemID(String uniqeItemID) {
		UniqeItemID = uniqeItemID;
	}
	
}
