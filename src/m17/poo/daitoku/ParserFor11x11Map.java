package m17.poo.daitoku;

import java.util.Map;

public class ParserFor11x11Map {
	//public static synchronized void loadRawHtml(Map<String, String[]> mapdata,String srcOf11x11Map) {
	//ConcurrentHashMapを使ってるからsynchronizedにする必要ない（？）
	public static void loadRawHtml(Map<String, String[]> mapdata,String srcOf11x11Map) {	
		String[] rowobj = srcOf11x11Map.split("onmouseover=");
		for (int i = 1; i<rowobj.length; i++){
			String sepMap = rowobj[i].split("rewrite\\(")[1].split("\\); overOperation")[0];
			String[] mapAry = sepMap.split("\', \'");
			
			String xy = mapAry[3];
			String player = mapAry[1];
			String alliance = mapAry[4];
			boolean isNPC = i==61; //11✕11マップでの中心
			
			String[] value = {player,alliance,String.valueOf(isNPC)};
			
			mapdata.put(xy,value);
		}
	}
}
