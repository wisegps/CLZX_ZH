package com.wise.clzx;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.BaseClass.AllStaticClass;
import com.BaseClass.Config;
import com.BaseClass.GetSystem;
import com.BaseClass.NetThread;
import com.BaseClass.ResolveData;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.Projection;
import com.wise.extend.CarAdapter;
import com.wise.extend.CarLocationOverlay;
import com.wise.extend.CarParkItemizedOverlay;
import com.wise.extend.CarParkOverlay;
import com.wise.extend.MeOverlay;
import com.wise.extend.MoveLocusOverlay;
import com.wise.extend.LineOverlay;
import com.wise.extend.PoiOverlay;
import com.wise.list.XListView;
import com.wise.list.XListView.IXListViewListener;
import com.wise.util.ArrayAdapter;
import com.wise.util.CarInfo;
import com.wise.util.ContacterData;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class AVTActivity extends MapActivity implements IXListViewListener {
	private final String TAG = "AVTActivity";

	private final int GetContacter = 1; // ��ȡ�û�����Ϣ
	private final int GetContacterCar = 2; // ��ȡ�û����µĳ�����Ϣ
	private final int GetRefreshData = 3; // ��ȡˢ������
	private final int UPDATEMAIN = 4; // ��ʱˢ�³�������
	private final int LocausOver = 5; // �켣�ط����
	private final int LocausNow = 6; // ÿ��һ��ʱ�仭һ��ͼ��
	private final int UpdatePwd = 7; // �޸��û�����
	private final int GetPoi = 8; // ����poi
	private final int GetTotal = 9; // ��ȡͳ������
	private final int GetFristLocus = 11; // ��ȡǰ100����¼
	private final int GetNextLocus = 12; // ��ǰһ�β�����ϣ�������һ��

	private final int SearchAddress = 13; // ���������ȡλ��
	// �ؼ�
	ViewFlipper flipper;
	Spinner s_contacter;
	XListView lv_cars;
	AutoCompleteTextView ListAutoComplete, MapAutoComplete;
	ImageView iv_ListClear, iv_MapClear, iv_Map, iv_Me, iv_play, iv_pause,
			iv_stop, iv_map_traffic_set;
	LinearLayout layout_bar; // ���ſ���
	ProgressBar bar;// �켣�طÿ�����
	EditText et_start, et_stop;
	SeekBar sb_speed;
	ProgressDialog Dialog;
	TextView tv_map_change, tv_address, tv_SearchStatistic;
	Button bt_ZoomDown, bt_ZoomUp;
	// ȫ�ֱ���
	List<ContacterData> contacterDatas = new ArrayList<ContacterData>();// �û�����
	List<CarInfo> carinfos; // ���г������ݼ���list
	List<CarInfo> carPath; // �켣�ط�list
	List<Overlay> pathOverlays = new ArrayList<Overlay>(); // �켣ͼ��
	List<Overlay> carOverlays = new ArrayList<Overlay>(); // ����ͼ��
	CarParkItemizedOverlay carParkItemOverlay;// ͣ��ͼ��
	List<String> carNums; // �󶨵��Զ���Ӧ�ؼ�
	CarAdapter carAdapter; // ��������

	String Url;
	String tree_path;
	String number_type = "";
	String auth_code;
	String cust_id;
	String LoginName; // �˺�
	int ShortTime; // ��ʱˢ��ʱ��
	int Text_size; // �����С

	int item; // ������list��λ��
	int PROGRESS = 0; // ��������ʼλ��
	GeoPoint LastPoint; // ��һ����ʵʱ����õ�
	GeoPoint Point; // Ĭ�϶�λ�ĵ�
	GeoPoint myLocation; // ��ǰ����

	LocationClient locationClient; // ��λ
	public MKSearch mMKSearch; // ������ְ

//	public MKSearch mMKSearchAddress; // ������ְ
	BMapManager mBMapMan = null;
	MapView mMapView;
	MapController mMapController;
	MyOverlay myOverlay; // ��ǰλ��
	private List<Overlay> mapOverLays; // ͼ���б�
	View popView; // ���ݴ���

	String startTime;

	String stopTime;

	private static final int PageNumber = 20; // ÿ�μ��ع켣����Ŀ
	int contacter_item = 0; // ѡ��spinner�ڼ���
	int page_total; // ��ǰҳ�ļ�¼���������жϹ켣�Ƿ�������
	int page = 1; // ��ǰҳ�����������ع켣
	boolean isSpinnerChange = true; // spinner���ز�����select�¼�
	/**
	 * ÿ�μ��س�����Ŀ
	 */
	final int Car_Page_Number = 20;
	/**
	 * ��һ�λ�ȡ�ĳ�������
	 */
	int Car_Page_total = 0;
	int Car_Page = 1; // ��ǰҳ��
	String latestTime = ""; // ���³���λ��

	String searchAddress = "";// ����������ҵ�ַ
	/**
	 * �켣�𲽻طŵļ��ʱ��
	 */
	int SENDTIME = 1000;
	/**
	 * ��һ�μ���ҳ�棬��Ҫ������ʱˢ�£�
	 */
	boolean IsFristOnCreate = true;
	/**
	 * �Ƿ��أ�tureʵʱ��أ�����ʾ·��ͼ��falseȡ��ʵʱ���
	 */
	private boolean ISSEARCH = false;
	private boolean IsFristLocus = true;
	/**
	 * false ��ͼ ��true ����
	 */
	boolean IsSatellite = false;
	/**
	 * �Ƿ��ڹ켣�ط��У����٣����ǣ�ֹͣˢ�����ݣ���ˢ������
	 */
	boolean IsLock = false;
	/**
	 * ˢ���������ݣ��˳�ʱ�ر�
	 */
	boolean IsUpdateMain = true;
	/**
	 * ���ƹ켣�ط��߳�
	 */
	boolean ISSTARTBAR = false;
	/**
	 * �����Ƿ��ں�̨
	 */
	boolean isPause = false;

	/**
	 * ʵʱ·����Ϣ
	 * 
	 * 
	 */
	boolean isTraffic = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	private OnClickListener OCL = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_ZoomDown:
				mMapController.zoomOut();
				break;
			case R.id.bt_ZoomUp:
				mMapController.zoomIn();
				break;
			case R.id.iv_Map:// ��ת����ͼҳ
				flipper.setDisplayedChild(1);
				break;
			case R.id.iv_Search:// ��ת���б�ҳ
				flipper.setDisplayedChild(0);
				break;
			case R.id.tv_map_change:// ���Ǻ͵�ͼ�л�
				// if(!IsLock){//����ڹ켣��طò��ܶ�ͼ�괦��
				// ChangeMap();
				// }
				if (IsSatellite) {// ��ͼ
					IsSatellite = false;
					mMapView.setSatellite(false);
					tv_map_change.setText(R.string.Satellite);
				} else {// ����
					IsSatellite = true;
					mMapView.setSatellite(IsSatellite);
					tv_map_change.setText(R.string.Traffic);
				}
				break;
			case R.id.iv_Me:// ��λ����ǰλ��
				try {
					mMKSearch.reverseGeocode(Point);
					mMapController.animateTo(myLocation);
				} catch (Exception e) {
					// û�ж�λ��Ϣ
					Toast.makeText(AVTActivity.this, R.string.Location_wrong,
							Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.iv_ListClear:// ����б�
				ListAutoComplete.setText("");
				break;
			case R.id.iv_MapClear:// �����ͼ
				MapAutoComplete.setText("");
				break;
			case R.id.bt_menu_car:// ���ٹ���
				if (ISSEARCH) {// ȡ������
					ISSEARCH = false;
					System.gc();
					ClearOverlay(pathOverlays);// ɾ���켣��
				} else {// ��ʼ����
					ISSEARCH = true;
					ClearOverlay(carOverlays);// ɾ���������
					ShowAllCar();
				}
				break;
			case R.id.bt_monitor_locus:// �켣�ط�
				popView.setVisibility(View.GONE);
				ISSEARCH = false;// �رո���
				LocausDialog();
				break;
			case R.id.iv_play:// ��ʼ���Ź켣
				iv_play.setEnabled(false);// ���Ű�ť��Ϊ������
				ISSTARTBAR = true;
				new Thread(new startBarThread()).start();
				break;
			case R.id.iv_pause:// ��ͣ�ͼ�������
				if (PROGRESS > 0) {
					// ֹͣ�����߳�
					ISSTARTBAR = false;
				}
				break;
			case R.id.iv_stop:// �˳��켣�ط�
				ISSTARTBAR = false;// ֹͣ�ط��߳�
				// ���ݻظ�����ʼ״̬
				PROGRESS = 0;
				bar.setProgress(0);
				LocusNow(PROGRESS);
				break;
			case R.id.iv_map_traffic_set:// ·����ʾ
				setTraffic();
				break;

			}
		}
	};

	public void setTraffic() {
		new Handler().post(new Runnable() {
			@Override
			public void run() {
				System.gc();
				ImageView img = (ImageView) AVTActivity.this.findViewById(R.id.iv_map_traffic_set);
				if (isTraffic == true) {
					img.setImageResource(R.drawable.main_icon_roadcondition_off);
					isTraffic = false;
					mMapView.setTraffic(isTraffic);
					mMapView.postInvalidateDelayed(100);
					Toast.makeText(AVTActivity.this, "ʵʱ·���ѹر�",
							Toast.LENGTH_SHORT).show();
				} else {
					img.setImageResource(R.drawable.main_icon_roadcondition_on);
					isTraffic = true; 
					mMapView.setTraffic(isTraffic);
					mMapView.postInvalidateDelayed(100);
					Toast.makeText(AVTActivity.this, "ʵʱ·���Ѵ�",
							Toast.LENGTH_SHORT).show();
				}
				
			
			}

		});

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GetContacter:// ��ȡ����Ϣ
				JsonContacter(msg.obj.toString());
				break;
			case GetContacterCar:// ��ȡ���µĳ���
				if (Dialog != null) {
					Dialog.dismiss();
				}
				JsonContacterCar(msg.obj.toString());
				if (Car_Page_total == Car_Page_Number) {
					lv_cars.setPullLoadEnable(true);
				} else {
					lv_cars.setPullLoadEnable(false);
				}
				if (isSpinnerChange) {
					isSpinnerChange = false;
					carAdapter = new CarAdapter(AVTActivity.this, carinfos);
					lv_cars.setAdapter(carAdapter);
					if (carinfos.size() > 0) {
						// ����λ��
						Point = new GeoPoint(
								AllStaticClass.StringToInt(carinfos.get(item)
										.getLat()),
								AllStaticClass.StringToInt(carinfos.get(item)
										.getLon()));
						mMKSearch.reverseGeocode(Point);
						mMapController.animateTo(Point);
					}
				} else {
					carAdapter.notifyDataSetChanged();
					onLoad();
				}

				if (IsFristOnCreate) {
					IsFristOnCreate = false;
					// ����ˢ���߳�
					new Thread(new UpdateMain()).start();
				}
				break;
			case UPDATEMAIN:// ��ʱˢ�����г�����Ϣ
				try {
					String RefreshUrl = Url + "customer/" + cust_id
							+ "/active_gps_data?auth_code=" + auth_code
							+ "&update_time="
							+ URLEncoder.encode(latestTime, "utf-8")
							+ "&mode=all&tree_path="
							+ contacterDatas.get(contacter_item).getTree_path();
					new Thread(new NetThread.GetDataThread(handler, RefreshUrl,
							GetRefreshData)).start();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				break;
			case GetRefreshData: // �����������ݲ���
				jsonRefreshData(msg.obj.toString().trim());
				if (!IsLock) {// ����ڹ켣�طò�ˢ������
					if (popView != null) {
						popView.setVisibility(View.GONE);
					}
					LastPoint = Point;// �����õ�
					System.out.println("ˢ����������");
					carAdapter.notifyDataSetChanged();// ˢ���б�����
					// ��ǰ��λ��
					if (carinfos.size() > 0) {
						Point = new GeoPoint(
								AllStaticClass.StringToInt(carinfos.get(item)
										.getLat()),
								AllStaticClass.StringToInt(carinfos.get(item)
										.getLon()));
						if (ISSEARCH) {
							mapOverLays.add(new LineOverlay(LastPoint, Point));// ���ڸ���״̬
							mMapController.animateTo(Point);
						} else {
							mMapController.animateTo(mMapView.getMapCenter());
						}
						ShowAllCar();
					}
				}
				break;
			case LocausOver:
				// �ط���Ϻ�Ķ���
				iv_play.setEnabled(true);
				iv_pause.setEnabled(true);
				PROGRESS = 0;
				break;
			// �켣�طò���
			case LocausNow:
				LocusNow(msg.arg1);
				break;
			// ��֤���뷵��
			case UpdatePwd:
				if (Dialog != null) {
					Dialog.dismiss();
				}
				String resultPwd = msg.obj.toString();
				if (resultPwd.indexOf("0") > 0) {
					Toast.makeText(AVTActivity.this, R.string.change_pwd_true,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(AVTActivity.this, R.string.change_pwd_false,
							Toast.LENGTH_SHORT).show();
				}
				break;
			case GetPoi:// ��ʾ�ȵ�
				jsonPoiData(msg.obj.toString().trim());
				break;
			case GetTotal:
				// �õ�ͳ����Ϣ
				// try{
				// TotalData totalData = ResolveData.ResolveTotal(theResult);
				// String statistic =
				// getString(R.string.total)+": "+totalData.getToatal()+"          "
				// +getString(R.string.accon)+": "+totalData.getAccOn()+"          "
				// +getString(R.string.accoff)+": "+totalData.getAccOff();
				// tv_statistic.setText(statistic);
				// tv_SearchStatistic.setText(statistic);
				// }catch (Exception e) {
				// e.printStackTrace();
				// System.out.println("ͳ����Ϣ�쳣");
				// }
				break;
			case GetFristLocus:
				jsonLocusData(msg.obj.toString());
				if (Dialog != null) {
					Dialog.dismiss();
				}
				break;
			case GetNextLocus:
				System.out.println("��ʼ����");
				page++;
				String url = Url + "vehicle/"
						+ carinfos.get(item).getObjectID()
						+ "/gps_data2?auth_code=" + auth_code + "&start_time="
						+ URLEncoder.encode(startTime) + "&end_time="
						+ URLEncoder.encode(stopTime) + "&page_no=" + page
						+ "&page_count=" + PageNumber;
				new Thread(new NetThread.GetDataThread(handler, url,
						GetFristLocus)).start();
				break;

			case SearchAddress:

				break;
			}
		}
	};

	/**
	 * �����û�����Ϣ
	 * 
	 * @param str
	 */
	private void JsonContacter(String str) {
		List<String> listContacter = new ArrayList<String>();
		try {
			JSONObject jsonObject = new JSONObject(str);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				ContacterData contacterData = new ContacterData();
				contacterData.setCust_name(json.getString("cust_name"));
				contacterData.setTree_path(json.getString("tree_path"));
				contacterDatas.add(contacterData);
				listContacter.add(json.getString("cust_name"));
			}
			// ��spinner
			android.widget.ArrayAdapter<String> Adapter = new android.widget.ArrayAdapter<String>(
					getApplicationContext(),
					android.R.layout.simple_spinner_item, listContacter);
			Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // ���������б�ķ��
			s_contacter.setAdapter(Adapter);
			s_contacter.setOnItemSelectedListener(onItemSelectedListener);
			// ��ȡ�û��³�������
			carinfos = new ArrayList<CarInfo>();
			carNums = new ArrayList<String>();
			String url = Url + "customer/" + cust_id + "/vehicle?auth_code="
					+ auth_code + "&tree_path="
					+ contacterDatas.get(contacter_item).getTree_path()
					+ "&mode=all&page_no=" + Car_Page + "&page_count="
					+ Car_Page_Number;
			Log.i("AVTActivity", url);

			new Thread(new NetThread.GetDataThread(handler, url,
					GetContacterCar)).start();
			if (listContacter.size() > 1) {
				s_contacter.setVisibility(View.VISIBLE);
			} else {
				s_contacter.setVisibility(View.GONE);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			if (Dialog != null) {
				Dialog.dismiss();
			}
		}
	}

	boolean isSpinnerShow = false;
	// spinner ���ص�����²��ᴥ��onItemSelectedListener
	OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (isSpinnerShow == true) {
				item = 0;
				carinfos = new ArrayList<CarInfo>();
				carNums = new ArrayList<String>();
				isSpinnerChange = true;
				contacter_item = arg2;
				Car_Page = 1;
				Dialog = ProgressDialog.show(AVTActivity.this,
						getString(R.string.serach_pd_title),
						getString(R.string.serach_pd_context), true);
				String url = Url + "customer/" + cust_id
						+ "/vehicle?auth_code=" + auth_code + "&tree_path="
						+ contacterDatas.get(contacter_item).getTree_path()
						+ "&mode=all&&page_no=" + Car_Page + "&page_count="
						+ Car_Page_Number;

				Log.i("AVTActivity", "onItemSelected" + url);

				new Thread(new NetThread.GetDataThread(handler, url,
						GetContacterCar)).start();

			}
			isSpinnerShow = true;
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	/**
	 * ������������
	 * 
	 * @param str
	 */
	private void JsonContacterCar(String str) {
		try {
			Log.i("AVTActivity", "result:" + str);
			JSONObject jsonObject = new JSONObject(str);
			Car_Page_total = Integer
					.valueOf(jsonObject.getString("page_total"));
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			Log.i("AVTActivity", "json:" + jsonArray.length());
			for (int i = 0; i < jsonArray.length(); i++) {
				Log.i("AVTActivity", "json:" + i);
				CarInfo carInfo = new CarInfo();
				JSONObject json = jsonArray.getJSONObject(i);
				
				Log.i("AVTActivity", "json:" + json);
				
				
				
				carInfo.setObj_name(json.getString("obj_name"));
				carInfo.setObjectID(json.getString("obj_id"));
				
				
				if (json.optJSONObject("active_gps_data") == null) {
					carInfo.setLat("0");
					carInfo.setLon("0");
					carInfo.setDirect("0");
					carInfo.setSpeed(0);
					carInfo.setMileage("0");
					carInfo.setRcv_time("");
					carInfo.setMDTStatus("");
					carInfo.setCarStatus(1);
					carInfo.setFuel("0");
				} else {
					
					JSONObject jsonData = json.getJSONObject("active_gps_data");
					
					
					String rcv_time = GetSystem.ChangeTime(
							jsonData.getString("rcv_time"), 0);
					int gps_flag = Integer.valueOf(jsonData
							.getString("gps_flag"));
					int speed = (int) Double.parseDouble(jsonData
							.getString("speed"));
					
					
					carInfo.setLat(getNumString(jsonData,"b_lat"));
					carInfo.setLon(getNumString(jsonData,"b_lon"));
					carInfo.setDirect(jsonData.getString("direct"));
					carInfo.setSpeed(speed);
					carInfo.setRcv_time(rcv_time);
					carInfo.setMileage(jsonData.getString("mileage"));
					carInfo.setFuel(jsonData.getString("fuel"));

					carInfo.setLastStopTime(jsonData
							.getString("last_stop_time"));

					JSONArray jsonArrayStatus = jsonData
							.getJSONArray("uni_status");
					int[] uniStatus = new int[jsonArrayStatus.length()];
					for (int s = 0; s < uniStatus.length; s++) {
						uniStatus[s] = jsonArrayStatus.getInt(s);
					}
					carInfo.setUniStatus(uniStatus);

					JSONArray jsonArrayAlerts = jsonData
							.getJSONArray("uni_alerts");
					String status = ResolveData.getStatusDesc(rcv_time,
							gps_flag, speed,
							ResolveData.getUniStatusDesc(jsonArrayStatus),
							ResolveData.getUniAlertsDesc(jsonArrayAlerts));
					carInfo.setMDTStatus(status);
					carInfo.setCarStatus(ResolveData.getCarStatus(rcv_time,
							jsonArrayAlerts, speed));
					latestTime = GetSystem.LatestTime(latestTime, rcv_time);
				}
				carNums.add(json.getString("obj_name"));
				carinfos.add(carInfo);
			}
			bindData();
			ShowAllCar();
		} catch (Exception e) {
			Log.i("AVTActivity", "e:" +e.getLocalizedMessage());
			e.printStackTrace();
			
		}
	}
	
	public String getNumString(JSONObject jsonData,String key){
		String str = "0";
		try {
			str = jsonData.getString(key);
		} catch (JSONException e) {
			str = "0";
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * ������������
	 * 
	 * @param str
	 */
	private void jsonRefreshData(String str) {
		try {
			JSONArray jsonArray = new JSONArray(str);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				for (int j = 0; j < carinfos.size(); j++) {
					CarInfo carInfo = carinfos.get(j);
					if (json.getString("obj_id").equals(carInfo.getObjectID())) {
						if (json.optJSONObject("active_gps_data") == null) {

						} else {
							JSONObject jsonData = json
									.getJSONObject("active_gps_data");
							String rcv_time = GetSystem.ChangeTime(
									jsonData.getString("rcv_time"), 0);
							int gps_flag = Integer.valueOf(jsonData
									.getString("gps_flag"));
							int speed = (int) Double.parseDouble(jsonData
									.getString("speed"));
							carInfo.setLat(jsonData.getString("b_lat"));
							carInfo.setLon(jsonData.getString("b_lon"));
							carInfo.setDirect(jsonData.getString("direct"));
							carInfo.setSpeed(speed);
							carInfo.setRcv_time(rcv_time);
							carInfo.setMileage(jsonData.getString("mileage"));
							JSONArray jsonArrayStatus = jsonData
									.getJSONArray("uni_status");
							JSONArray jsonArrayAlerts = jsonData
									.getJSONArray("uni_alerts");
							String status = ResolveData.getStatusDesc(rcv_time,
									gps_flag, speed, ResolveData
											.getUniStatusDesc(jsonArrayStatus),
									ResolveData
											.getUniAlertsDesc(jsonArrayAlerts));
							carInfo.setMDTStatus(status);
							latestTime = GetSystem.LatestTime(latestTime,
									rcv_time);
						}
						break;
					}
				}
			}
			carAdapter.notifyDataSetChanged();
			bindData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ʾpoi�ȵ�
	 * 
	 * @param str
	 */
	private void jsonPoiData(String str) {
		try {
			JSONObject jsonObject = new JSONObject(str);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject json = jsonArray.getJSONObject(i);
				GeoPoint point = new GeoPoint(AllStaticClass.StringToInt(json
						.getString("lat")), AllStaticClass.StringToInt(json
						.getString("lon")));
				Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.other);
				PoiOverlay poiOverlay = new PoiOverlay(point, bitmap);
				mapOverLays.add(poiOverlay);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jsonLocusData(String str) {
		try {
			JSONObject jsonObject = new JSONObject(str);
			page_total = Integer.valueOf(jsonObject.getString("page_total"));
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonData = jsonArray.getJSONObject(i);
				CarInfo carInfo = new CarInfo();
				carInfo.setLat(jsonData.getString("b_lat"));
				carInfo.setLon(jsonData.getString("b_lon"));
				carInfo.setDirect(jsonData.getString("direct"));
				carInfo.setMileage(jsonData.getString("mileage"));
				carInfo.setFuel(jsonData.getString("fuel"));
				carInfo.setSpeed((int) Double.parseDouble(jsonData
						.getString("speed")));
				carInfo.setRcv_time(GetSystem.ChangeTime(
						jsonData.getString("rcv_time"), 0));

				carInfo.setGps_time(GetSystem.ChangeTime(
						jsonData.getString("gps_time"), 0));
				carPath.add(carInfo);
				Log.d(TAG, carInfo.toString());
			}
			if (IsFristLocus) {
				if (jsonArray.length() == 0) {
					Toast.makeText(AVTActivity.this,
							R.string.monitor_locus_null, Toast.LENGTH_LONG)
							.show();
				} else {
					IsLock = true;
					Log.d(TAG, "��ʾ����");
					IsFristLocus = false;
					layout_bar.setVisibility(View.VISIBLE);// ��ʾ�켣������
					bar.setMax(Integer.valueOf(jsonObject.getString("total")));
					ClearOverlay(carOverlays);// ɾ���������
					ClearOverlay(pathOverlays);// ɾ���켣

				}
			} else {
				if (jsonArray.length() != 0) {
					// locus();
					ISSTARTBAR = true;
					new Thread(new startBarThread()).start();
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	OnSeekBarChangeListener OSBCL = new OnSeekBarChangeListener() {
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			SENDTIME = 1500 - seekBar.getProgress() * 10;
		}
	};
	MoveLocusOverlay moveLocusOverlay;

	public void addParkOverlay(final int index) {
		if (index < 1) {
			return;
		}

		// �ж���һ���Ƿ�������״̬
		CarInfo lastCarInfo = carPath.get(index - 1);
		boolean runStatus = ResolveData
				.hasStatusRun(lastCarInfo.getUniStatus());
		if (runStatus) {
			// ��һ�����У�����
			return;
		}

		CarInfo carInfo = carPath.get(index);
		// ��һ��Ϩ��,�ж�Ϩ�������
		long duration = GetSystem.getStopDuration(lastCarInfo.getGps_time(),
				carInfo.getGps_time());
		if (duration < 5) {
			// С������Ӳ���
			return;
		}

		GeoPoint lastStopPoint = new GeoPoint(
				AllStaticClass.StringToInt(lastCarInfo.getLat()),
				AllStaticClass.StringToInt(lastCarInfo.getLon()));
		if (carParkItemOverlay == null) {
			Bitmap bitmap = BitmapFactory.decodeResource(
					AVTActivity.this.getResources(), R.drawable.park);
			Drawable drawable = new BitmapDrawable(bitmap);
			carParkItemOverlay = new CarParkItemizedOverlay(AVTActivity.this,
					drawable);
		}

		String snippet = lastCarInfo.getGps_time() + ",," + GetSystem.duration2String(duration*60);

		OverlayItem overLayItem = new OverlayItem(lastStopPoint, "t", snippet);

		carParkItemOverlay.addOverLay(overLayItem);
		mapOverLays.add(carParkItemOverlay);

		// mapOverLays.add(new CarParkOverlay(this,lastStopPoint, bitmap));

	}

	public MapView getMapView() {

		return mMapView;
	}

	/**
	 * �켣ͼ���ƶ�
	 * 
	 * @param index
	 */
	private void LocusNow(int index) {
		if (carPath.size() == 0) {
			return;
		}
		addParkOverlay(index);

		GeoPoint stopPoint = new GeoPoint(AllStaticClass.StringToInt(carPath
				.get(index).getLat()), AllStaticClass.StringToInt(carPath.get(
				index).getLon()));
		if (index != 0) {
			GeoPoint startPoint = new GeoPoint(
					AllStaticClass.StringToInt(carPath.get(index - 1).getLat()),
					AllStaticClass.StringToInt(carPath.get(index - 1).getLon()));
			LineOverlay myOverlay = new LineOverlay(startPoint, stopPoint);
			mapOverLays.add(myOverlay);
			pathOverlays.add(myOverlay);
		}
		// �ƶ�ͼ��
		Log.d(TAG, "index=" + index);
		if (moveLocusOverlay != null) {
			mapOverLays.remove(moveLocusOverlay);
		}
		// GeoPoint stopPoint = new
		// GeoPoint(AllStaticClass.StringToInt(carPath.get(index).getLat()),
		// AllStaticClass.StringToInt(carPath.get(index).getLon()));
		int CarStatus = carPath.get(index).getCarStatus();
		String Direct = carPath.get(index).getDirect();
		Drawable drawable = AllStaticClass.DrawableBimpMap(AVTActivity.this,
				CarStatus, Direct);
		moveLocusOverlay = new MoveLocusOverlay(stopPoint,
				GetSystem.drawableToBitmap(drawable));
		mapOverLays.add(moveLocusOverlay); // ͼ����ӵ�map��ʾ
		mMapController.animateTo(stopPoint); // ��λ
		bar.setProgress(index + 1);
		ShowLocalPop(index);
	}

	private void ShowLocalPop(int i) {
		MapView.LayoutParams geoLP = (MapView.LayoutParams) popView
				.getLayoutParams();
		geoLP.point = new GeoPoint(AllStaticClass.StringToInt(carPath.get(i)
				.getLat()), AllStaticClass.StringToInt(carPath.get(i).getLon()));
		;
		TextView tv_car_id = (TextView) popView.findViewById(R.id.pop_car_id);
		// tv_car_id.setVisibility(View.GONE);
		TextView my_line = (TextView) popView.findViewById(R.id.my_line);
		my_line.setVisibility(View.GONE);
		TextView tv_car_MSTStatus = (TextView) popView
				.findViewById(R.id.pop_car_MSTStatus);
		TextView tv_car_Mileage = (TextView) popView
				.findViewById(R.id.pop_car_Mileage);
		TextView tv_car_fuel = (TextView) popView
				.findViewById(R.id.pop_car_fuel);
		TextView tv_car_GpsTime = (TextView) popView
				.findViewById(R.id.pop_car_GpsTime);
		TextView bt_menu_car = (TextView) popView
				.findViewById(R.id.bt_menu_car);
		bt_menu_car.setVisibility(View.GONE);
		bt_menu_car.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_menu_car.getPaint().setAntiAlias(true);
		bt_menu_car.setOnClickListener(OCL);
		TextView bt_monitor_locus = (TextView) popView
				.findViewById(R.id.bt_monitor_locus);
		bt_monitor_locus.setVisibility(View.GONE);
		bt_monitor_locus.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_monitor_locus.getPaint().setAntiAlias(true);
		bt_monitor_locus.setOnClickListener(OCL);
		int Speed = carPath.get(i).getSpeed();
		String mileage = carPath.get(i).getMileage();
		String Rcv_time = carPath.get(i).getRcv_time();

		tv_car_id.setText(carinfos.get(item).getObj_name());
		tv_car_MSTStatus.setText(carinfos.get(item).getMDTStatus() + " ");
		tv_car_MSTStatus.setVisibility(View.GONE);

		tv_car_Mileage.setText(getString(R.string.car_mileage) + mileage
				+ " km");
		tv_car_fuel.setText(getString(R.string.car_fuel)
				+ carPath.get(i).getFuel() + " L");
		tv_car_GpsTime.setText(Rcv_time);
		mMapView.updateViewLayout(popView, geoLP);
		popView.setVisibility(View.VISIBLE);
	}

	MeOverlay meOverlay;

	/**
	 * ��ǰλ��
	 */
	// private void ShowMeLocation(GeoPoint gp){
	// try{
	// if(meOverlay != null){
	// mapOverLays.remove(meOverlay);
	// }
	// Drawable drawable = getResources().getDrawable(R.drawable.icon_locr);
	// meOverlay = new MeOverlay(gp, GetSystem.drawableToBitmap(drawable),
	// Accuracy);
	// mapOverLays.add(meOverlay);
	// }catch (Exception e) {
	// e.printStackTrace();
	// System.out.println("���˶�λ�쳣");
	// }
	// }
	/**
	 * ��ʱˢ�����߳�
	 * 
	 * @author honesty
	 * 
	 */
	class UpdateMain implements Runnable {
		public void run() {
			while (IsUpdateMain) {
				int updateTime;
				// �ж������ļ��Ƿ��Զ�ˢ��
				SharedPreferences preferences = getSharedPreferences(
						Config.Shared_Preferences, Context.MODE_PRIVATE);
				boolean isRef = preferences.getBoolean("isRef", true);
				if (isRef) {
					updateTime = (preferences.getInt("ShortTime", 30)) * 1000;
				} else {
					updateTime = 180000;
				}
				System.out.println("��ʱˢ��ʱ�䣺" + updateTime);
				try {
					Thread.sleep(updateTime);
					// �ж��Ƿ���������ں�̨
					if (!isPause) {
						Message message = new Message();
						message.what = UPDATEMAIN;
						handler.sendMessage(message);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ��ʾȫ������
	 */
	private void ShowAllCar() {
		ClearOverlay(carOverlays);// ɾ���������
		HideLocaus();
		for (int i = 0; i < carinfos.size(); i++) {
			String Lat = carinfos.get(i).getLat();
			String Lon = carinfos.get(i).getLon();
			int Speed = carinfos.get(i).getSpeed();
			String Direct = carinfos.get(i).getDirect();
			String RegNum = carinfos.get(i).getObj_name();
			int CarStatus = carinfos.get(i).getCarStatus();
			String MSTStatus = carinfos.get(i).getMDTStatus();
			String Mileage = carinfos.get(i).getMileage();
			String gps_time = carinfos.get(i).getRcv_time();
			String fuel = carinfos.get(i).getFuel();

			String snippet = RegNum + ",," + gps_time + ",," + MSTStatus + ",,"
					+ Speed + "km/h" + ",," + Mileage + "km" + ",," + fuel
					+ "L";

			GeoPoint Point = new GeoPoint(AllStaticClass.StringToInt(Lat),
					AllStaticClass.StringToInt(Lon)); // �õ���γ��
			Drawable drawable = AllStaticClass.DrawableBimpMap(
					AVTActivity.this, CarStatus, Direct);
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();
			drawable.setBounds(-w / 2, -h / 2, w / 2, h / 2);
			CarLocationOverlay itemOverLay;
			// �жϵ�ǰ�����Ƿ���٣�Ҫ��������
			if (i == item) {
				itemOverLay = new CarLocationOverlay(drawable,
						AVTActivity.this, RegNum, Point, ISSEARCH, IsSatellite,
						AVTActivity.this, Text_size, i);
			} else {
				itemOverLay = new CarLocationOverlay(drawable,
						AVTActivity.this, RegNum, Point, false, IsSatellite,
						AVTActivity.this, Text_size, i);
			}
			OverlayItem overLayItem = new OverlayItem(Point, RegNum, snippet); // �󶨵���¼�
			overLayItem.setMarker(drawable);
			itemOverLay.addOverLay(overLayItem);
			mapOverLays.add(itemOverLay); // ͼ����ӵ�map��ʾ
			itemOverLay.setOnFocusChangeListener(onFocusChangeListener); // ����ʾ�¼�
			carOverlays.add(itemOverLay);
		}
		Log.d(TAG, "ͼ��������" + mapOverLays.size());
	}

	/**
	 * ���Զ���Ӧ�ؼ�
	 */
	private void bindData() {
		// �����ݵ�AutoCompleteTextView
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, carNums);
		// �б������ؼ�
		ListAutoComplete.setAdapter(adapter);
		ListAutoComplete.setThreshold(0);
		ListAutoComplete.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String Regnum = ListAutoComplete.getText().toString();
				for (int i = 0; i < carinfos.size(); i++) {
					if (carinfos.get(i).getObj_name().equals(Regnum)) {
						// ѡ�ж�Ӧ����
						ChooseCar(i, 1);
						break;
					}
				}
			}
		});
		// ��ͼ�����ؼ�
		MapAutoComplete.setAdapter(adapter);
		MapAutoComplete.setThreshold(0);
		MapAutoComplete.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String Regnum = MapAutoComplete.getText().toString();
				for (int i = 0; i < carinfos.size(); i++) {
					if (carinfos.get(i).getObj_name().equals(Regnum)) {
						ChooseCar(i, 0);
						break;
					}
				}
			}
		});
	}

	/**
	 * �л�����ͼ
	 */
	private void ChangeMap() { // TODO ɾ��
		ClearOverlay(carOverlays);
		ShowAllCar();
	}

	/**
	 * �켣�ط�
	 * 
	 * @author honesty
	 * 
	 */
	class startBarThread implements Runnable {
		public void run() {
			while (ISSTARTBAR) {
				try {
					if (PROGRESS >= carPath.size()) {// �׶λط����
						ISSTARTBAR = false; // ֹͣ�߳�
						Message message = new Message();
						if (page_total == PageNumber) {
							message.what = GetNextLocus; // ����ʣ�µ�����
						} else {
							message.what = LocausOver; // �ط����
						}
						handler.sendMessage(message);
					} else {
						Thread.sleep(SENDTIME);
						if (ISSTARTBAR) {
							Message message = new Message();
							message.what = LocausNow;
							message.arg1 = PROGRESS;
							handler.sendMessage(message);
							PROGRESS++;
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ���ع켣�ط�
	 */
	private void HideLocaus() {
		ISSTARTBAR = false;// �ر��߳�
		layout_bar.setVisibility(View.GONE);// ����
		bar.setProgress(0);
		PROGRESS = 0;
	}

	private void LocausDialog() {
		View viewtime = LayoutInflater.from(AVTActivity.this).inflate(
				R.layout.timedialog, null);
		ImageView iv_start = (ImageView) viewtime.findViewById(R.id.iv_start);
		iv_start.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				timeDialog(1);
			}
		});
		ImageView iv_stop = (ImageView) viewtime.findViewById(R.id.iv_stop);
		iv_stop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				timeDialog(2);
			}
		});
		et_start = (EditText) viewtime.findViewById(R.id.et_start);
		et_start.setInputType(InputType.TYPE_NULL);
		et_stop = (EditText) viewtime.findViewById(R.id.et_stop);
		et_stop.setInputType(InputType.TYPE_NULL);
		AlertDialog.Builder timeBuilder = new AlertDialog.Builder(
				AVTActivity.this);
		timeBuilder.setView(viewtime);
		timeBuilder.setTitle(R.string.car_dialog_title);
		timeBuilder.setPositiveButton(R.string.car_dialog_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if (et_start.getText().toString().equals("")
								|| et_stop.getText().toString().equals("")) {
							Toast.makeText(AVTActivity.this,
									R.string.car_dialog_et_null,
									Toast.LENGTH_SHORT).show();
						} else if (!AllStaticClass.LimitTime(et_start.getText()
								.toString(), et_stop.getText().toString())) {
							Toast.makeText(AVTActivity.this,
									R.string.car_dialog_time_limit,
									Toast.LENGTH_SHORT).show();
						} else {
							Dialog = ProgressDialog.show(AVTActivity.this,
									getString(R.string.serach_pd_title),
									getString(R.string.monitor_locus_load),
									true);
							// ��ѯ�켣ǰ��������
							iv_play.setEnabled(true);
							IsFristLocus = true;
							PROGRESS = 0;
							Car_Page = 1;
							carPath = new ArrayList<CarInfo>();
							String url = Url + "vehicle/"
									+ carinfos.get(item).getObjectID()
									+ "/gps_data2?auth_code=" + auth_code
									+ "&start_time="
									+ URLEncoder.encode(startTime)
									+ "&end_time="
									+ URLEncoder.encode(stopTime) + "&page_no="
									+ page + "&page_count=" + PageNumber;

							Log.i("AVTActivity", "�켣�ط�" + url);
							new Thread(new NetThread.GetDataThread(handler,
									url, GetFristLocus)).start();
						}
					}
				});
		timeBuilder.setNegativeButton(android.R.string.cancel, null);
		timeBuilder.show();
	}

	private void timeDialog(final int i) {
		// TODO �¼�
		View view = LayoutInflater.from(AVTActivity.this).inflate(
				R.layout.time, null);
		final DatePicker dp_start = (DatePicker) view
				.findViewById(R.id.dp_start);
		final TimePicker tp_start = (TimePicker) view
				.findViewById(R.id.tp_start);
		tp_start.setIs24HourView(true);
		if (i == 1) {
			tp_start.setCurrentHour(0);
			tp_start.setCurrentMinute(0);
		} else {
			tp_start.setCurrentHour(23);
			tp_start.setCurrentMinute(59);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(AVTActivity.this);
		builder.setView(view);
		builder.setTitle(R.string.time_choose);
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						System.out.println("i = " + i);
						System.out.println("startTime = " + startTime);
						// �켣�ط�
						if (i == 1) {// ��ʼʱ��
							startTime = AllStaticClass.intToString(dp_start
									.getYear())
									+ "-"
									+ AllStaticClass.intToString(dp_start
											.getMonth() + 1)
									+ "-"
									+ AllStaticClass.intToString(dp_start
											.getDayOfMonth())
									+ " "
									+ AllStaticClass.intToString(tp_start
											.getCurrentHour())
									+ ":"
									+ AllStaticClass.intToString(tp_start
											.getCurrentMinute()) + ":" + "00";

							System.out.println("dp_start = "
									+ dp_start.getDayOfMonth());
							System.out.println("dp_start = "
									+ dp_start.getMonth());
							et_start.setText(startTime);
						} else {
							stopTime = AllStaticClass.intToString(dp_start
									.getYear())
									+ "-"
									+ AllStaticClass.intToString(dp_start
											.getMonth() + 1)
									+ "-"
									+ AllStaticClass.intToString(dp_start
											.getDayOfMonth())
									+ " "
									+ AllStaticClass.intToString(tp_start
											.getCurrentHour())
									+ ":"
									+ AllStaticClass.intToString(tp_start
											.getCurrentMinute()) + ":" + "00";
							et_stop.setText(stopTime);
						}
					}

				});
		builder.setNegativeButton(android.R.string.cancel, null);
		builder.show();
	}

	private OnItemClickListener OICL = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			String Lat = carinfos.get((arg2 - 1)).getLat();
			String Lon = carinfos.get((arg2 - 1)).getLon();
			if(Lat.trim().equals("0") || Lon.trim().equals("0")){
				Toast.makeText(AVTActivity.this, "���޷���ȡλ����Ϣ", Toast.LENGTH_SHORT).show();
				return ;
			}
			
			flipper.setDisplayedChild(1);
			ChooseCar((arg2 - 1), 1);
		}
	};

	/**
	 * �л�����
	 * 
	 * @param arg
	 * @param where
	 *            0����ͼ 1���б�
	 */
	public void ChooseCar(int arg, int where) {
		
		lv_cars.setSelection(arg); // ��λ����Ӧ��
		carAdapter.setSelectItem(arg);
		carAdapter.notifyDataSetInvalidated();
		if (arg != item) {
			item = arg;
			if (ISSEARCH) {// ����ڼ����ֹͣ���
				ISSEARCH = false;
				ClearOverlay(carOverlays);// ɾ���������
				ClearOverlay(pathOverlays);

				ShowAllCar();
			}
			if (IsLock) {// ֹͣ�켣�ط�
				ISSTARTBAR = false;
				IsLock = false;
				ClearOverlay(pathOverlays);
				// ɾ���켣�ط��ܵ�ͼ��
				mapOverLays.remove(moveLocusOverlay);
				popView.setVisibility(View.GONE);
				ShowAllCar();
			}
		}
		// ��λ����ǰ����
		Point = new GeoPoint(AllStaticClass.StringToInt(carinfos.get(arg)
				.getLat()), AllStaticClass.StringToInt(carinfos.get(arg)
				.getLon()));
		mMKSearch.reverseGeocode(Point);
		mMapController.animateTo(Point);
		ShowPop(arg);
	}

	private void ShowPop(int i) {
		MapView.LayoutParams geoLP = (MapView.LayoutParams) popView
				.getLayoutParams();
		geoLP.point = Point;
		TextView tv_car_id = (TextView) popView.findViewById(R.id.pop_car_id);
		tv_car_id.setVisibility(View.VISIBLE);
		TextView my_line = (TextView) popView.findViewById(R.id.my_line);
		my_line.setVisibility(View.VISIBLE);
		TextView tv_car_MSTStatus = (TextView) popView
				.findViewById(R.id.pop_car_MSTStatus);
		TextView tv_car_Mileage = (TextView) popView
				.findViewById(R.id.pop_car_Mileage);
		TextView tv_car_fuel = (TextView) popView
				.findViewById(R.id.pop_car_fuel);
		TextView tv_car_GpsTime = (TextView) popView
				.findViewById(R.id.pop_car_GpsTime);
		TextView bt_menu_car = (TextView) popView
				.findViewById(R.id.bt_menu_car);
		bt_menu_car.setVisibility(View.VISIBLE);
		bt_menu_car.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_menu_car.getPaint().setAntiAlias(true);
		bt_menu_car.setOnClickListener(OCL);
		TextView bt_monitor_locus = (TextView) popView
				.findViewById(R.id.bt_monitor_locus);
		bt_monitor_locus.setVisibility(View.VISIBLE);
		bt_monitor_locus.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_monitor_locus.getPaint().setAntiAlias(true);
		bt_monitor_locus.setOnClickListener(OCL);
		int Speed = carinfos.get(i).getSpeed();
		String RegNum = carinfos.get(i).getObj_name();
		String MSTStatus = carinfos.get(i).getMDTStatus();
		String mileage = carinfos.get(i).getMileage();
		String gps_time = carinfos.get(i).getRcv_time();
		tv_car_id.setText(RegNum);
		tv_car_MSTStatus.setText(MSTStatus + " ");
		tv_car_Mileage.setText(getString(R.string.car_mileage) + mileage
				+ " km");
		tv_car_GpsTime.setText(gps_time);
		tv_car_fuel.setText(getString(R.string.car_fuel)
				+ carinfos.get(i).getFuel() + "L");
		mMapView.updateViewLayout(popView, geoLP);
		popView.setVisibility(View.VISIBLE);
	}

	private final ItemizedOverlay.OnFocusChangeListener onFocusChangeListener = new ItemizedOverlay.OnFocusChangeListener() {
		@SuppressWarnings("rawtypes")
		public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
			if (popView != null) {
				popView.setVisibility(View.GONE);
			}
			if (newFocus != null) {
				MapView.LayoutParams geoLP = (MapView.LayoutParams) popView
						.getLayoutParams();
				geoLP.point = newFocus.getPoint();
				TextView tv_car_id = (TextView) popView
						.findViewById(R.id.pop_car_id);
				TextView tv_car_MSTStatus = (TextView) popView
						.findViewById(R.id.pop_car_MSTStatus);
				TextView tv_car_Mileage = (TextView) popView
						.findViewById(R.id.pop_car_Mileage);
				TextView tv_car_fuel = (TextView) popView
						.findViewById(R.id.pop_car_fuel);
				TextView tv_car_GpsTime = (TextView) popView
						.findViewById(R.id.pop_car_GpsTime);

				TextView bt_menu_car = (TextView) popView
						.findViewById(R.id.bt_menu_car);
				bt_menu_car.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
				bt_menu_car.getPaint().setAntiAlias(true);
				bt_menu_car.setOnClickListener(OCL);
				TextView bt_monitor_locus = (TextView) popView
						.findViewById(R.id.bt_monitor_locus);
				bt_monitor_locus.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
				bt_monitor_locus.getPaint().setAntiAlias(true);
				bt_monitor_locus.setOnClickListener(OCL);

				String[] str = newFocus.getSnippet().split(",,");
				System.out.println(newFocus.getSnippet());
				tv_car_id.setText(str[0]);
				tv_car_GpsTime.setText(str[1]);
				tv_car_MSTStatus.setText(str[2]);
				tv_car_Mileage
						.setText(getString(R.string.car_mileage) + str[4]);
				tv_car_fuel.setText(getString(R.string.car_fuel) + str[5]);
				mMapView.updateViewLayout(popView, geoLP);
				popView.setVisibility(View.VISIBLE);
			}
		}
	};

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, R.string.allcar_config);
		menu.add(0, 2, 0, R.string.bt_updatePwd);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent intent = new Intent(AVTActivity.this, ConfigActivity.class);
			startActivity(intent);
			break;
		case 2:
			ChangePwd();
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	};

	String LoginPws;
	String newpwd;

	/**
	 * �޸�����Ի���
	 */
	private void ChangePwd() {
		View view = LayoutInflater.from(AVTActivity.this).inflate(
				R.layout.changepwd, null);
		final EditText et_oldpwd = (EditText) view
				.findViewById(R.id.oldPassWord_ET);
		final EditText et_newpwd = (EditText) view
				.findViewById(R.id.newPassWord_ET);
		final EditText et_newpwdtwo = (EditText) view
				.findViewById(R.id.newPassWordTwo_ET);
		AlertDialog.Builder bulder = new AlertDialog.Builder(AVTActivity.this);
		bulder.setView(view);
		bulder.setTitle(R.string.changePwd_title);// ���ñ���
		bulder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						LoginPws = et_oldpwd.getText().toString().trim();
						newpwd = et_newpwd.getText().toString().trim();
						String newpwdtoo = et_newpwdtwo.getText().toString()
								.trim();
						if (LoginPws.equals("") || newpwd.equals("")
								|| newpwdtoo.equals("")) {
							Toast.makeText(AVTActivity.this,
									R.string.change_pwd_null,
									Toast.LENGTH_SHORT).show();
							return;
						} else {
							if (newpwd.equals(newpwdtoo)) {
								Dialog = ProgressDialog.show(AVTActivity.this,
										getString(R.string.login),
										getString(R.string.change_pwd_now),
										true);
								String urlString = Url
										+ "customer/user/password?auth_code="
										+ auth_code + "&number_type="
										+ number_type;
								List<NameValuePair> params1 = new ArrayList<NameValuePair>();
								params1.add(new BasicNameValuePair("user_name",
										LoginName));
								params1.add(new BasicNameValuePair(
										"old_password", GetSystem
												.getM5DEndo(LoginPws)));
								params1.add(new BasicNameValuePair(
										"new_password", GetSystem
												.getM5DEndo(newpwd)));
								new Thread(new NetThread.postDataThread(
										handler, urlString, params1, UpdatePwd))
										.start();
							} else {
								Toast.makeText(AVTActivity.this,
										R.string.change_pwd_TwoNewPwd_false,
										Toast.LENGTH_SHORT).show();
								return;
							}
						}
					}
				});
		bulder.setNegativeButton(android.R.string.cancel, null);
		bulder.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ISSEARCH = false; // ֹͣ�߳�
		IsUpdateMain = false;
		ISSTARTBAR = false;
		locationClient.stop();
		System.gc();
	}

	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("onPause");
		isPause = true;
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("onResume");
		isPause = false;
		if (mBMapMan != null) {
			mBMapMan.start();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			System.out.println("IsLock:" + IsLock);
			if (IsLock) {
				ISSTARTBAR = false;
				// ֹͣ�켣�ط�
				IsLock = false;
				ClearOverlay(pathOverlays);
				ClearCarParkOverlay();
				Log.d(TAG, "pathOverlays.size() = " + pathOverlays.size());
				// ɾ���켣�ط��ܵ�ͼ��
				popView.setVisibility(View.GONE);
				ShowAllCar();
				mMapController.animateTo(Point);
				ShowPop(item);
				return false;
			} else {
				AlertDialog.Builder bulder = new AlertDialog.Builder(
						AVTActivity.this);
				bulder.setTitle(R.string.Note);// ���ñ���
				bulder.setMessage(R.string.exit_content);
				bulder.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						});
				bulder.setNegativeButton(android.R.string.cancel, null);
				bulder.show();
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX,
			float velocityY) {
		return false;
	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	private void init() {
		flipper = (ViewFlipper) this.findViewById(R.id.viewFlipper);
		LayoutInflater mLayoutInflater = LayoutInflater.from(AVTActivity.this);
		// ����
		View searchView = mLayoutInflater.inflate(R.layout.search, null);
		flipper.addView(searchView);
		s_contacter = (Spinner) searchView.findViewById(R.id.s_contacter);
		tv_SearchStatistic = (TextView) searchView
				.findViewById(R.id.tv_SearchStatistic);
		iv_ListClear = (ImageView) searchView.findViewById(R.id.iv_ListClear);
		iv_ListClear.setOnClickListener(OCL);
		iv_Map = (ImageView) searchView.findViewById(R.id.iv_Map);
		iv_Map.setOnClickListener(OCL);
		lv_cars = (XListView) findViewById(R.id.lv_cars);
		lv_cars.setOnItemClickListener(OICL);
		lv_cars.setPullLoadEnable(false);
		lv_cars.setPullRefreshEnable(false);
		lv_cars.setXListViewListener(this);
		lv_cars.setOnItemClickListener(OICL);
		ListAutoComplete = (AutoCompleteTextView) searchView
				.findViewById(R.id.et_ListSearch);
		ListAutoComplete.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// autocomolete��������ʾ��հ�ť
				if (s.length() > 0) {
					iv_ListClear.setVisibility(View.VISIBLE);
				} else {
					iv_ListClear.setVisibility(View.GONE);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
		// ��ͼ
		View mapView = mLayoutInflater.inflate(R.layout.map, null);
		flipper.addView(mapView);
		layout_bar = (LinearLayout) mapView.findViewById(R.id.Layout_bar);
		bar = (ProgressBar) mapView.findViewById(R.id.show_bar);
		// ��ת���б�ҳ��
		ImageView iv_Search = (ImageView) mapView.findViewById(R.id.iv_Search);
		iv_Search.setOnClickListener(OCL);
		iv_MapClear = (ImageView) mapView.findViewById(R.id.iv_MapClear);
		iv_MapClear.setOnClickListener(OCL);
		iv_Me = (ImageView) mapView.findViewById(R.id.iv_Me);
		iv_Me.setOnClickListener(OCL);
		bt_ZoomDown = (Button) mapView.findViewById(R.id.bt_ZoomDown);
		bt_ZoomDown.setOnClickListener(OCL);
		bt_ZoomUp = (Button) mapView.findViewById(R.id.bt_ZoomUp);
		bt_ZoomUp.setOnClickListener(OCL);

		iv_map_traffic_set = (ImageView) mapView
				.findViewById(R.id.iv_map_traffic_set);
		iv_map_traffic_set.setTag(false);

		iv_map_traffic_set.setOnClickListener(OCL);
		MapAutoComplete = (AutoCompleteTextView) mapView
				.findViewById(R.id.et_MapSearch);
		MapAutoComplete.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > 0) {
					iv_MapClear.setVisibility(View.VISIBLE);
				} else {
					iv_MapClear.setVisibility(View.GONE);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}

		});
		tv_map_change = (TextView) mapView.findViewById(R.id.tv_map_change);
		tv_address = (TextView) mapView.findViewById(R.id.tv_address);
		tv_map_change.setOnClickListener(OCL);
		iv_play = (ImageView) mapView.findViewById(R.id.iv_play);
		iv_play.setOnClickListener(OCL);
		iv_pause = (ImageView) mapView.findViewById(R.id.iv_pause);
		iv_pause.setOnClickListener(OCL);
		iv_stop = (ImageView) mapView.findViewById(R.id.iv_stop);
		iv_stop.setOnClickListener(OCL);
		sb_speed = (SeekBar) mapView.findViewById(R.id.sb_speed);
		sb_speed.setOnSeekBarChangeListener(OSBCL);
		sb_speed.setProgress(50);
		// Ĭ����ʾ��ͼҳ��
		flipper.setDisplayedChild(1);

		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init("934B1BF7DD618B5E356CA715D473D1BD864048AC", null);
		super.initMapActivity(mBMapMan);
		mMapView = (MapView) findViewById(R.id.MapView);
		mMapView.setBuiltInZoomControls(false); // �����������õ����ſؼ�
		mMapView.setSatellite(IsSatellite);
		mMapView.setTraffic(false);
		// ���������Ŷ���������Ҳ��ʾoverlay,Ĭ��Ϊ������
		mMapView.setDrawOverlayWhenZooming(true);
		mMapController = mMapView.getController(); // �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		mapOverLays = mMapView.getOverlays();
		getDate();
		// ���ҵ�ַ
		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapMan, new MySearhListener());

//		mMKSearchAddress = new MKSearch();
//		mMKSearchAddress.init(mBMapMan, new ParkSearhListener());

		GetMyLocation();
	}

	/**
	 * ��ʼ������
	 */
	private void getDate() {
		Intent intent = getIntent();
		Url = intent.getStringExtra("Url");
		auth_code = intent.getStringExtra("auth_code");
		cust_id = intent.getStringExtra("cust_id");
		LoginName = intent.getStringExtra("LoginName");
		tree_path = intent.getStringExtra("tree_path");
		number_type = intent.getStringExtra("number_type");
		Text_size = intent.getIntExtra("Text_size", 20);
		SharedPreferences preferences = getSharedPreferences(
				Config.Shared_Preferences, Context.MODE_PRIVATE);
		ShortTime = (preferences.getInt("ShortTime", 20)) * 1000;
		// ��ʼ�������ע��ʾ��ʽ
		popView = super.getLayoutInflater().inflate(R.layout.pop, null);
		mMapView.addView(popView, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null, 0, 0,
				MapView.LayoutParams.BOTTOM_CENTER));
		popView.setVisibility(View.GONE);
		Dialog = ProgressDialog.show(AVTActivity.this,
				getString(R.string.serach_pd_title),
				getString(R.string.serach_pd_context), true);
		String url = Url + "customer/" + cust_id + "/customer?auth_code="
				+ auth_code + "&tree_path=" + tree_path
				+ "&page_no=1&page_count=100";
		new Thread(new NetThread.GetDataThread(handler, url, GetContacter))
				.start();
		String poiUrl = Url + "customer/" + cust_id + "/poi?auth_code="
				+ auth_code + "&is_geo=0&page_no=1&page_count=100";
		new Thread(new NetThread.GetDataThread(handler, poiUrl, GetPoi))
				.start();
		Log.d(TAG, "poiUrl=" + poiUrl);
	}

	/**
	 * ɾ����Ӧ��ͼ��
	 * 
	 * @param overlays
	 */
	private void ClearOverlay(List<Overlay> overlays) {
		for (Overlay overlay : overlays) {
			mapOverLays.remove(overlay);
		}
		overlays.clear();
		ClearCarParkOverlay();
		System.gc();
	}

	/**
	 * ɾ����Ӧ��ͼ��
	 * 
	 * @param overlays
	 */
	private void ClearCarParkOverlay() {
		
		
		
		if (carParkItemOverlay != null) {
			carParkItemOverlay.clear();
			mapOverLays.remove(carParkItemOverlay);
			carParkItemOverlay = null;
		}
		mMKSearch.reverseGeocode(Point);
		System.gc();
	}

	public void onRefresh() {
	}

	/**
	 * ��������
	 */
	public void onLoadMore() {// ��������
		if (Car_Page_total == Car_Page_Number) {
			Car_Page++;
			String url = Url + "customer/" + cust_id + "/vehicle?auth_code="
					+ auth_code + "&tree_path="
					+ contacterDatas.get(contacter_item).getTree_path()
					+ "&mode=all&&page_no=" + Car_Page + "&page_count="
					+ Car_Page_Number;
			new Thread(new NetThread.GetDataThread(handler, url,
					GetContacterCar)).start();
			
			Log.i("AVTActivity", "��������");
		}
	}

	/**
	 * �������������������ݺ�ָ�
	 */
	private void onLoad() {
		lv_cars.stopRefresh();
		lv_cars.stopLoadMore();
		lv_cars.setRefreshTime(GetSystem.GetNowTime());
	}

	class MySearhListener implements MKSearchListener {
		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			if (arg0 != null) {
				tv_address.setText(arg0.strAddr);
				
				if (carParkItemOverlay != null) {
					carParkItemOverlay.setAddress(arg0.strAddr);
				}
			}
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
		}

		@Override
		public void onGetRGCShareUrlResult(String arg0, int arg1) {
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		}
	}

	class MyOverlay extends Overlay {
		GeoPoint geoPoint;

		public MyOverlay(GeoPoint geoPoint) {
			this.geoPoint = geoPoint;
		}

		@Override
		public boolean draw(Canvas arg0, MapView mapView, boolean arg2,
				long arg3) {
			super.draw(arg0, mapView, arg2);
			Point point = new Point();
			Projection projection = mapView.getProjection();
			projection.toPixels(geoPoint, point);
			Paint paint = new Paint();
			// ����γ��ת����ʵ����Ļ����
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.icon_locr);
			arg0.drawBitmap(bmp, point.x - bmp.getWidth() / 2,
					point.y - bmp.getHeight() / 2, paint);
			return true;
		}
	}

	private void GetMyLocation() {
		locationClient = new LocationClient(getApplicationContext());
		locationClient.registerLocationListener(new MyLocationListenner());
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(false);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(30000);
		locationClient.setLocOption(option);
		locationClient.start();
	}

	private class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location != null) {
				myLocation = new GeoPoint((int) (location.getLatitude() * 1e6),
						(int) (location.getLongitude() * 1e6));
				if (myOverlay != null) {
					mapOverLays.remove(myOverlay); // �Ƴ�
				}
				myOverlay = new MyOverlay(myLocation);
				mapOverLays.add(myOverlay); // ���
			} else {
				System.out.println("location null");
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
		}
	}

	class ParkSearhListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			if (carParkItemOverlay != null) {
				carParkItemOverlay.setAddress(arg0.strAddr);
			}

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetRGCShareUrlResult(String arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

	}

}