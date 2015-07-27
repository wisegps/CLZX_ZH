package com.wise.clzx;

import org.json.JSONArray;
import org.json.JSONObject;
import com.BaseClass.Config;
import com.BaseClass.GetSystem;
import com.BaseClass.NetThread;
import com.BaseClass.UpdateManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity{	
	private final String TAG = "LoginActivity";
	private final int Login = 1; 	
	private final int Update = 2;
	
	EditText et_name,et_pwd;
	TextView tv_update;
	CheckBox cb_isSavePwd;
	
	/*ȫ�ֱ���*/
	ProgressDialog Dialog = null;    //progress
	String LoginName ;               //�û���
	String LoginPws ;                //����
	boolean LoginNote;               //�Ƿ񱣴�����
	double Version;   //��ǰ�汾
	String VersonUrl; //����·��
	String logs;   //������Ϣ
	int Text_size; //�����С
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		init();
		getDM();
		getSp();
		isUpdate();
	}	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Login:
				LoginData(msg);
				break;
			case Update:
				UpdateData(msg);
				break;
			}
		}
	};
	
	OnClickListener OCL = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_login:
				//��½�¼�
				LoginName = et_name.getText().toString();
				LoginPws = et_pwd.getText().toString();
				if (LoginName.equals("")|| LoginPws.equals("")) {
					Toast.makeText(LoginActivity.this, R.string.Login_null, Toast.LENGTH_LONG).show();
				}else{
					String url = Config.url + "login?username=" + LoginName + "&password=" + GetSystem.getM5DEndo(LoginPws) +"&mac=" + GetSystem.getMacAddress(LoginActivity.this);
					Dialog = ProgressDialog.show(LoginActivity.this,getString(R.string.login),getString(R.string.login_pd_context),true);
					new Thread(new NetThread.GetDataThread(handler, url, Login)).start();
				}
				break;
			case R.id.tv_update:
				UpdateManager mUpdateManager = new UpdateManager(LoginActivity.this,VersonUrl,logs,Version);
			    mUpdateManager.checkUpdateInfo();
				break;
			}
		}
	};
	
	private void LoginData(Message msg){
    	if(Dialog != null){
			Dialog.dismiss();
		}
		try {
			JSONObject jsonObject = new JSONObject(msg.obj.toString());
			if(jsonObject.opt("auth_code") == null){
				String status_code = jsonObject.getString("status_code");
				if(status_code.equals("5")){
					Toast.makeText(getApplicationContext(), R.string.accout_bind_phone, Toast.LENGTH_LONG).show();
				}else if(status_code.equals("2") || status_code.equals("1")){
					Toast.makeText(getApplicationContext(), R.string.login_id_wrong, Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(), R.string.login_id_wrong, Toast.LENGTH_LONG).show();
				}
			}else{
				String auth_code = jsonObject.getString("auth_code");
				String cust_id = jsonObject.getString("cust_id");
				String tree_path = jsonObject.getString("tree_path");
				String number_type = jsonObject.getString("number_type");
				String Url = "http://"+jsonObject.getString("host")+":"+jsonObject.getString("port")+"/";
				//����������Ϣ
				SharedPreferences preferences = getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString("LoginName", LoginName);
				editor.putString("LoginPws", LoginPws);
				editor.putBoolean("LoginNote", LoginNote);					
				editor.commit();
				Intent intent = new Intent(LoginActivity.this, AVTActivity.class);
				intent.putExtra("auth_code", auth_code);
				intent.putExtra("cust_id", cust_id);
				intent.putExtra("number_type", number_type);
				intent.putExtra("Url", Url);
				intent.putExtra("tree_path", tree_path);
				intent.putExtra("Text_size", Text_size);
				intent.putExtra("LoginName", LoginName);
				startActivity(intent);
				finish();
			}				
		}catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * ��ʼ������
	 */
	private void getSp(){ 
		Version = Double.valueOf(GetSystem.GetVersion(getApplicationContext(), Config.PackString));	
		//��ȡsharedPreferences������Ϣ
		SharedPreferences preferences = getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
		String LoginName = preferences.getString("LoginName", "");
		String LoginPws = preferences.getString("LoginPws", "");
		LoginNote = preferences.getBoolean("LoginNote", true);
		VersonUrl = preferences.getString("VersonUrl", "");
		logs = preferences.getString("logs", "");
		et_name.setText(LoginName);
		et_pwd.setText(LoginPws);
		cb_isSavePwd.setChecked(LoginNote);
	}
	/**
	 * ��ʼ���ؼ�
	 */
	private void init() {
		et_name = (EditText) findViewById(R.id.et_account);
		et_pwd = (EditText) findViewById(R.id.et_password);
		cb_isSavePwd = (CheckBox) findViewById(R.id.NotePsw);
		cb_isSavePwd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox)v;
				LoginNote = cb.isChecked();
			}
		});
		Button bt_login = (Button) findViewById(R.id.bt_login);
		bt_login.setOnClickListener(OCL);
		tv_update = (TextView)findViewById(R.id.tv_update);
		tv_update.setOnClickListener(OCL);
	}	
		
	/**
	 * ���ݷֱ��ʣ����������С
	 */
	private void getDM(){
		 DisplayMetrics dm = new DisplayMetrics();  
	     getWindowManager().getDefaultDisplay().getMetrics(dm);
	     int dpi = dm.densityDpi;
		 if(dpi <= 120){
			 Text_size = 8;
			 return;
	     }else if(dpi <= 160){
	    	 Text_size = 12;
	    	 return;
	     }else if(dpi <= 240){
	    	 Text_size = 20;
	    	 return;
	     }else{
	    	 Text_size = 24;
	    	 return;
	     }
	}
	/**
	 * �Ƿ��ȡ������Ϣ,�����sd�������
	 */
	private void isUpdate(){
		if(isSdCardExist()){
			try {
				//�õ�ϵͳ�İ汾
				JSONArray jsonArray = new JSONArray(logs);
				for (int i = 0; i < jsonArray.length(); i++) {
					 double logVersion = Double.valueOf(jsonArray.getJSONObject(i).getString("version"));
					 if(logVersion > Version){
						 tv_update.setVisibility(View.VISIBLE);
						 break;
					 }
				}
			} catch (Exception e) {
				Log.d(TAG, "����������Ϣ����");
			}
			String url = Config.url + Config.UpdateUrl;
			new Thread(new NetThread.GetDataThread(handler, url, Update)).start();
		}else{
			Toast.makeText(LoginActivity.this, R.string.SD_NOTFIND, Toast.LENGTH_LONG).show();
		}
	}
	/**
	 * �ж�sd���Ƿ����
	 * @return
	 */
	private boolean isSdCardExist(){
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	/**
     * ��ȡ���½ӿ���Ϣ
     * @param msg
     */
    private void UpdateData(Message msg){
    	try {
			JSONObject jsonObject = new JSONObject(msg.obj.toString());
			SharedPreferences preferences = getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putString("Verson", jsonObject.getString("version"));
			editor.putString("VersonUrl", jsonObject.getString("app_path"));
			editor.putString("logs", jsonObject.getString("logs"));
			editor.commit();
			
			VersonUrl = jsonObject.getString("app_path");
			logs = jsonObject.getString("logs");			
			if(isSdCardExist()){
				try {
					//�õ�ϵͳ�İ汾
					JSONArray jsonArray = new JSONArray(logs);
					for (int i = 0; i < jsonArray.length(); i++) {
						 double logVersion = Double.valueOf(jsonArray.getJSONObject(i).getString("version"));
						 if(logVersion > Version){
							 tv_update.setVisibility(View.VISIBLE);
							 break;
						 }
					}
				} catch (Exception e) {
					Log.d(TAG, "����������Ϣ����");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}