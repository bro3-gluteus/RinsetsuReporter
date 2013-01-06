package m17.poo.daitoku;

import java.util.List;

import javax.swing.JOptionPane;

/**
 * Mixiアカウント情報のデータクラス
 */
public class MixiAccount {

  //こういうふうにプライベート変数にして他のクラスからの変更を防ぐことを
  //オブジェクト指向プログラミングでは「カプセル化」といいます。
  private String mixiEmail;
  private String mixiPassword;

  /**
   * 引数が与えられなかった時はシステムプロパティで初期化
   * メールアドレスとパスワードは-Dオプションで指定。設定例：
   * http://gyazo.com/e3ee52fb8bdf2206478da397fccbe174
   */
  public MixiAccount() {
		ReadConfig cfg = new ReadConfig();
		
		List<String> email = cfg.getConfigList("mixiEmail");
		List<String> pass = cfg.getConfigList("mixiPassword");
		
	  //パスワードちゃんと入ってるかチェック
		if (email==null || email.size()==0 || 
        pass==null || pass.size()==0 ) {
			JOptionPane.showMessageDialog(null, 
					"mixiログイン設定が入っていません。設定ファイルで設定してください。",
					"設定エラー",
					JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
    }
		
		mixiEmail = email.get(0).trim();
		mixiPassword = pass.get(0).trim();
    //パスワードちゃんと入ってるかチェック
    if (mixiEmail==null || mixiEmail.length()==0 || 
            mixiPassword==null || mixiPassword.length()==0 ) {
			JOptionPane.showMessageDialog(null, 
					"mixiログイン設定が入っていません。設定ファイルで設定してください。",
					"設定エラー",
					JOptionPane.ERROR_MESSAGE
					);
			System.exit(-1);
    }
  }

  /**
   * 引数が与えられた時はその値で初期化
   * @param mixiEmail
   * @param mixiPassword
   */
  public MixiAccount( String mixiEmail, String mixiPassword ) {
    //ローカル変数をインスタンス変数に代入
    this.mixiEmail = mixiEmail;
    this.mixiPassword = mixiPassword;
  }
  
  //Getterメソッド
  public String getMixiEmail() {
    return mixiEmail;
  }

  //Getterメソッド
  public String getMixiPassword() {
    return mixiPassword;
  }
  
}
