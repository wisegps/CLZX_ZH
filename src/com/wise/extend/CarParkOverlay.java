package com.wise.extend;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import com.wise.clzx.AVTActivity;
import com.wise.clzx.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

/**
 * Í£³µÍ¼±ê
 * 
 * @author c
 * 
 */
public class CarParkOverlay extends Overlay {
	AVTActivity parent;
	GeoPoint geoPoint;
	Bitmap bitmap;
	View popView;
	

	public CarParkOverlay(AVTActivity parent, GeoPoint geoPoint, Bitmap bitmap) {
		this.parent = parent;
		this.geoPoint = geoPoint;
		this.bitmap = bitmap;
	}

	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		super.draw(canvas, mapView, shadow);
		Paint paint = new Paint();
		Point myScreenCoords = new Point();
		mapView.getProjection().toPixels(geoPoint, myScreenCoords);
		canvas.drawBitmap(bitmap, (myScreenCoords.x - bitmap.getWidth() / 2),
				(myScreenCoords.y - bitmap.getHeight()), paint);
		return true;
	}

	@Override
	public boolean onTap(GeoPoint geo, MapView mapView) {
		
		if(popView!=null && popView.isShown()){
			mapView.removeView(popView);
		}
//		int la0 = geo.getLatitudeE6();
//		int la1 =  geoPoint.getLatitudeE6();
//		int lo0 = geo.getLongitudeE6();
//		int lo1 = geoPoint.getLongitudeE6();
//		int w = bitmap.getHeight();
//		if( la0<la1+w  &&  la0>la1-w && lo0<lo1+w && lo0 >lo1-w ){
			popView = parent.getLayoutInflater().inflate(R.layout.pop_park,
					null);
			mapView.addView(popView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, null,
					MapView.LayoutParams.BOTTOM_CENTER));
			popView.setVisibility(View.GONE);
			MapView.LayoutParams geoLP = (MapView.LayoutParams) popView
					.getLayoutParams();
			geoLP.point = geoPoint;
			mapView.updateViewLayout(popView, geoLP);
			popView.setVisibility(View.VISIBLE);
			mapView.invalidate();
//	/	}
		return super.onTap(geo, mapView);

	}

}