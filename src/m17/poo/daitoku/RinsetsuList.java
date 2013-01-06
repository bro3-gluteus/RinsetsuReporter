package m17.poo.daitoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RinsetsuList {
	private String xy;
	private Map<String,String> userName;
	private Map<String,String> doumeiName;
	
	public void setUser(Map<String,String> map){
		this.userName = map;
	}
	
	public void setDoumei(Map<String,String> map){
		this.doumeiName = map;
	}
	
	public void setXY(String xy){
		this.xy = xy;
	}
	
	public void setInfo(String xy,Map<String,String> user,Map<String,String> doumei){
		this.xy = xy;
		this.userName = user;
		this.doumeiName = doumei;
	}
	
	//座標を受け取って隣接者listを返す
	public List<String> getUser(){
		List<String> list = new ArrayList<String>();
		Pattern p = Pattern.compile("([0-9\\-]+),([0-9\\-]+)");
		Matcher m = p.matcher(xy);
    if (m.find()){
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			for ( int i=-1; i<=1; i++ ) {
			 for ( int j=-1; j<=1; j++ ) {
				 if (i!=0||j!=0){
		       	int thisX = x+i;
		        int thisY = y+j;
	         
	          String thisUser = userName.get("("+thisX+","+thisY+")");
	          if(!thisUser.equals("")) list.add(thisUser);
          }
        }
      }
    }
    //重複除去
    Set<String> set = new HashSet<String>(list);
    List<String> newlist = new ArrayList<String>(set);
		return newlist;
	}
	 
	//座標を受け取って隣接同盟listを返す
	public List<String> getDoumei(){
		List<String> list = new ArrayList<String>();
		
		Pattern p = Pattern.compile("([0-9\\-]+),([0-9\\-]+)");
		Matcher m = p.matcher(xy);
		if (m.find()){
			int x = Integer.parseInt(m.group(1));
			int y = Integer.parseInt(m.group(2));
			for ( int i=-1; i<=1; i++ ) {
				for ( int j=-1; j<=1; j++ ) {
					if (i!=0||j!=0){
						int thisX = x+i;
						int thisY = y+j;
						 
						String thisDoumei = doumeiName.get("("+thisX+","+thisY+")");
						if(!thisDoumei.equals("")) list.add(thisDoumei);
					}
				}
			}
	  }
		return list;
	}
	
}
