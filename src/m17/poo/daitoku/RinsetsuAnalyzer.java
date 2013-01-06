package m17.poo.daitoku;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RinsetsuAnalyzer {
	
	private List<String> npcList = new ArrayList<String>(1600);
	private Map<String,String> userName = new HashMap<String,String>(258134);
	private Map<String,String> doumeiName = new HashMap<String,String>(258134);
	
	public RinsetsuAnalyzer(Map<String,String[]> mapData){
		
		for(Map.Entry<String, String[]> e : mapData.entrySet()) {
			//NPCリストを抽出
			if (Boolean.valueOf(e.getValue()[2])){
	    	npcList.add(e.getKey());
	    }
			userName.put(e.getKey(), e.getValue()[0]); //座標：所有者のMapを作っておく
			doumeiName.put(e.getKey(), e.getValue()[1]);//座標：同盟のMapを作っておく
		}
	}
	
	public Map<String,String[]> getOutput(ReadConfig cfg){
		
		Map<String,String[]> output = new HashMap<String, String[]>();
		
		//全てのNPC座標から情報を抽出
		for (int index=0;index<npcList.size();index++){
			RinsetsuList rl = new RinsetsuList();
			rl.setInfo(npcList.get(index),userName, doumeiName);
			List<String> rinsetsuUser = rl.getUser();//隣接者のリスト（重複なし）
			List<String> rinsetsuDoumei = rl.getDoumei();//隣接同盟のリスト（重複あり←包囲してるかを判定するため）
			
    	//重複なしの同盟リストも作成
    	Set<String> set = new HashSet<String>(rinsetsuDoumei);
      List<String> uRinsetsuDoumei = new ArrayList<String>(set);
           
      //設定ファイルから判定材料
      List<String> honbuNameList = cfg.getConfigList("本部");
      String honbuName = null;
      if (honbuNameList.size()>0){
      	honbuName = honbuNameList.get(0).trim();
      }else{
      	System.out.println("設定ファイルに本部名が記載されていません");
      	//System.exit(0);
      }
      List<String> shibuName = cfg.getConfigList("支部");
      
      //攻略判定 所属同盟が本部なら○、支部なら△、それ以外なら×、"-"なら未
      String syoyu = doumeiName.get(npcList.get(index));
      String kouryaku="×";
      if (syoyu.equals(honbuName)) kouryaku = "○";
      for (int i=0;i<shibuName.size();i++){
      	if (syoyu.equals(shibuName.get(i).trim())) kouryaku = "△";
      }
      if (syoyu.equals("-")) kouryaku = "未";

      //競合状況の取得;
      String kyougou = "隣接なし";
      int honbu = 0;
    	int sibu=0;

    	for (String doumei :rinsetsuDoumei){
    		if (doumei.equals(honbuName)) honbu++;
        for (int i=0;i<shibuName.size();i++){
        	if (doumei.equals(shibuName.get(i).trim())) sibu++;
        }
    	}
    	int hoka = rinsetsuDoumei.size()-honbu-sibu;
    	
    	if (honbu+sibu==8) kyougou="包囲";
    	else if (honbu>0&&hoka==0) kyougou="独占";
    	else if (honbu>0&&hoka>0) kyougou="競合";
    	else if (honbu==0&&sibu>0&&hoka==0) kyougou="独占・要移籍";
    	else if (honbu==0&&sibu>0&&hoka>0) kyougou="競合・要移籍";
    	else if (honbu==0&&sibu==0&&hoka<8) kyougou="他同盟隣接・空き地あり";
    	else if (honbu==0&&sibu==0&&hoka==8) kyougou="他同盟隣接・空き地なし";
    	
    	//output用に整形
    	String outputUser = LISTtoString(rinsetsuUser);
    	String outputDoumei = LISTtoString(uRinsetsuDoumei);
    	
    	String[] outputList = {syoyu,outputUser, outputDoumei, kyougou, kouryaku};
    	output.put("'"+npcList.get(index), outputList);//key:座標, value:String[] {所有同盟,隣接者,隣接同盟,競合状態,攻略状況}
		}
		return output;
	}
	
	//Listを受け取ってカンマ区切りのStringを返す
	private String LISTtoString(List<String> list){
  	StringBuilder sb = new StringBuilder();
  	for (String item:list){
  		sb.append(item+",");
  	}
  	if (sb.length()>0) sb.deleteCharAt(sb.length() - 1);
  	String s = new String(sb);
		return s;
	}
	
}