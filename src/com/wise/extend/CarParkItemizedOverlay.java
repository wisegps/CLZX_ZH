package com.wise.extend;

import java.util.ArrayList;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.wise.clzx.AVTActivity;
import com.wise.clzx.R;

public class CarParkItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	ArrayList<OverlayItem> mapOverLays = new ArrayList<OverlayItem>();
	AVTActivity parent;
	View popView;
	MapView mapView;
	
	public Handler handler ;
	
	public CarParkItemizedOverlay(AVTActivity parent,Drawable drawable) {
		super(drawable);
		this.parent = parent;
		mapView = parent.getMapView();
		popView = parent.getLayoutInflater().inflate(R.layout.pop_park,
				null);
		mapView.addView(popView, new MapView.LayoutParams(
				MapView.LayoutParams.WRAP_CONTENT,
				MapView.LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		popView.setVisibility(View.GONE);
		
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		};
	}
	

	@Override
	protected OverlayItem createItem(int i) {
		return mapOverLays.get(i);
	}

	@Override
	public int size() {
		return mapOverLays.size();
	}

	// 点击时触发
	protected boolean onTap(final int index) {
		new Handler().post(new Runnable(){

			@Override
			public void run() {
				MapView.LayoutParams geoLP = (MapView.LayoutParams) popView
						.getLayoutParams();
				OverlayItem item = mapOverLays.get(index);
				TextView tvBeginTime = (TextView) popView.findViewById(R.id.tvBeginTime);
				TextView tvStayTime = (TextView) popView.findViewById(R.id.tvStayTime);
				
				String snippet[] =item.getSnippet().split(",,");
				tvBeginTime.setText("开始时间："+snippet[0]);
				tvStayTime.setText("停留时间："+snippet[1]);
				
				geoLP.point = mapOverLays.get(index).getPoint();
				
				parent.mMKSearch.reverseGeocode(geoLP.point);
				mapView.updateViewLayout(popView, geoLP);
				popView.setVisibility(View.VISIBLE);
				mapView.invalidate();
			}
			
		});
		
		return true;
	}

	
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0, MapView arg1) {
		new Handler().post(new Runnable(){

			@Override
			public void run() {
				if(popView!=null){
					popView.setVisibility(View.GONE);
				}
			}
			
		});
		
		return super.onTouchEvent(arg0, arg1);
	}
	// 添加标记
	public void addOverLay(OverlayItem overLay) {
		mapOverLays.add(overLay);
		this.populate();
	}
	
	
	public void setAddress(String address){
		if(popView!=null && popView.isShown()){
			TextView tvAddressDesc= (TextView) popView.findViewById(R.id.tvAddressDesc);
			tvAddressDesc.setText("位置："+address);
		}
		
	}
	
	public void clear(){
		if(popView!=null){
			popView.setVisibility(View.GONE);
		}
		for(int i =0;i<mapOverLays.size();i++){
			mapOverLays.remove(i);
		}
	}
}
