package Foundation.FFS.Units;

import java.util.HashMap;

public class ItemPropertyHolder {
	private String ItemContent;
	private HashMap<String,Object> Holder;
	
	public ItemPropertyHolder(String Item){
		setItemContent(Item);
		this.Holder=new HashMap<String, Object>();
	}
	
	
	
	public String getItemContent() {
		return ItemContent;
	}
	public void setItemContent(String itemContent) {
		ItemContent = itemContent;
	}
	public synchronized HashMap<String,Object> getHolder() {
		return Holder;
	}
}
