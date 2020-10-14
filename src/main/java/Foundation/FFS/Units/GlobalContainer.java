package Foundation.FFS.Units;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
public class GlobalContainer {
	// Holder For Retain Global Data //
	
	private byte[] OwnerFullTable;


	public GlobalContainer() {
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
	
	public String getOwnerFullTable() throws Exception {
		return GZDecompress(this.OwnerFullTable);
	}

	public void setOwnerFullTable(String ownerfulltable) throws Exception {
		OwnerFullTable = GZCompress(ownerfulltable);
	}

}
