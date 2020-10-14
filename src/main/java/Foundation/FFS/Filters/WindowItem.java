package Foundation.FFS.Filters;

public class WindowItem {
	private String ID;
	private String Item;
	
	public WindowItem(String id,String item) {
		this.ID=id;
		this.Item=item;
	}

	public String getID() {
		return ID;
	}

	public String getItem() {
		return Item;
	}
}
