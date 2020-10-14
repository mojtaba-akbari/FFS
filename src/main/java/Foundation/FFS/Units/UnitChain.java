package Foundation.FFS.Units;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class UnitChain {

	private byte[] UnitContent;
	private boolean PointerToSnapShot;
	
	private UnitChain PrevUnit=null;
	private UnitChain NextUnit=null;
	
	
	// For Faster Algorithm :)  We Retain Last Element In Head Node And Other Nodes If you need (Do not Forget It's Just Address PTR)//
	private UnitChain HeadToLastItemPointer=null;
	private int SizeOfTree=0;
	
	private String UniqeItemID;
	
	// Save Element Pointer For BroadCast Search That Help Faster Search //
	private boolean IsBroadCastedItem=false;

	public UnitChain(String Content,boolean PointerToSnapShot,UnitChain PrevUnit) {
		try {
			
			this.setUnitContent(Content);
			this.setPointerToSnapShot(PointerToSnapShot);
			
			this.setPrevUnit(PrevUnit);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private byte[] GZCompress(String Content) throws Exception{
		if (Content == null || Content.length() == 0) return null;
		
		ByteArrayOutputStream stream=new ByteArrayOutputStream();
		GZIPOutputStream gzip=new GZIPOutputStream(stream);
		
		gzip.write(Content.getBytes());
		gzip.close();
		
		return stream.toByteArray();
	}
	
	private String GZDecompress(byte[] Content) throws Exception {
		GZIPInputStream gzip=new GZIPInputStream(new ByteArrayInputStream(Content));
		BufferedReader buff=new BufferedReader(new InputStreamReader(gzip));
		String Outstr="";
		String line;
		
		while ((line=buff.readLine())!=null) {
			Outstr += line;
	    }
	    return Outstr;
	}
	
	public String getUnitContent() throws Exception {
		return GZDecompress(this.UnitContent);
	}

	public void setUnitContent(String unitContent) throws Exception {
		UnitContent = GZCompress(unitContent);
	}

	public boolean isPointerToSnapShot() {
		return PointerToSnapShot;
	}

	public void setPointerToSnapShot(boolean pointerToSnapShot) {
		PointerToSnapShot = pointerToSnapShot;
	}

	public UnitChain getPrevUnit() {
		return PrevUnit;
	}

	public void setPrevUnit(UnitChain prevUnit) {
		PrevUnit = prevUnit;
	}

	public UnitChain getNextUnit() {
		return NextUnit;
	}

	public void setNextUnit(UnitChain nextUnit) {
		NextUnit = nextUnit;
	}

	public UnitChain getHeadToLastItemPointer() {
		return HeadToLastItemPointer;
	}

	public void setHeadToLastItemPointer(UnitChain headToLastItemPointer) {
		HeadToLastItemPointer = headToLastItemPointer;
	}
	
	public int getSizeOfTree() {
		return SizeOfTree;
	}

	public void setSizeOfTree(int sizeOfTree) {
		SizeOfTree = sizeOfTree;
	}
	
	public void setHeadToHead() {
		//PTR TO Last ITEM//
		setHeadToLastItemPointer(this);
		//PTR TO PRV ITEM//
		setPrevUnit(this);
		setSizeOfTree(1);
	}
	
	public String getUniqeItemID() {
		return UniqeItemID;
	}

	public void setUniqeItemID(String uniqeItemID) {
		UniqeItemID = uniqeItemID;
	}

	public boolean getIsBroadCastedItem() {
		return IsBroadCastedItem;
	}

	public void setIsBroadCastedItem(boolean isBroadCastedItem) {
		IsBroadCastedItem = isBroadCastedItem;
	}
}
