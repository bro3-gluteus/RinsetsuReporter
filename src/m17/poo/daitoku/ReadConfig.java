package m17.poo.daitoku;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.swing.JOptionPane;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class ReadConfig {
	
	private Multimap<String, String> configMap = ArrayListMultimap.create();
	
	public ReadConfig(){
		String fileName = "config.txt";
	  BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF-8"));
		  String str;
		  try {
				while((str = br.readLine()) != null){
					if (str.length()==0) continue;//空白行対策
					if (str.substring(0,2)=="\\/\\/") continue; //コメント行飛ばし
					String[] line = str.split(":");
					if(line.length<2) continue;//値を入れてなかった時の対策
				  String key = line[0];
				  String value = line[1];
				  configMap.put(key, value);
				}
			} catch (IOException e) {
				System.out.println(e);
				System.out.println("設定ファイルを読み取れません");
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println(e);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, 
					"設定ファイルが見つかりません。"+fileName+"があるか確認してください。",
					"FileNotFoundException",
					JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
		} 
	}
	
	public List<String> getConfigList(String key){
		List<String> value =  (List<String>) configMap.get("<"+key+">");
		if (key!="支部"&&value.size()==0){
			JOptionPane.showMessageDialog(null, 
					"設定ファイルで <"+key+"> の値を設定してください。\nシステムを終了します。",
					"設定エラー",
					JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
		}
		return value;
	}
}
