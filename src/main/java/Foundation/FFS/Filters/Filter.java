package Foundation.FFS.Filters;

import java.util.ArrayList;
import java.util.HashMap;

import Foundation.FFS.Units.UnitChain;
import reactor.core.publisher.Mono;

public interface Filter {
	public enum FilterType{
		REALTIME,
		UNREALTIME,
		SEQUENTIAL,
		PARRALEL
	}
	public enum FilterTask{
		PROVIDER,
		VIEW,
		SCORE,
		VIEW_SCORE
	}
	public enum ScoreType{
		ONETIME,
		ADDED
	}
	public void run(String id,String item,ArrayList<String> FilterResult) throws Exception ;
	public double[] CalculateCOEVariationAndGrowthSpeed(int indexelement,HashMap<String, UnitChain> Storage) throws Exception;
	public String DownloadData(String URL,String Method,String DataType) throws Exception;
	public int ScoreAssign(String Id,String GroupName,String Reason,int Point,ScoreType scoretype);
}
