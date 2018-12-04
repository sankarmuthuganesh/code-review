package RealTime.CheckTrial;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.keyvalue.MultiKey;

public class FIlel {
public static void main(String[] args) {
	
	
	
	Map<MultiKey,String> detailMap=new HashMap<>();
	MultiKey kk=new MultiKey("indu",23);
	detailMap.put(kk,"88");
	MultiKey kfk=new MultiKey("durga",24,2,34,5);
	detailMap.put(kfk,"88");
	//detailMap.put("vijay","88");
	MultiKey kkfsd=new MultiKey("indu",23);
	System.out.println(detailMap.get(kkfsd));
	
//	detailMap.entrySet().stream().forEach(kkd ->{
//		kkd.getKey().g;
//		
//	});
}
}
