package com.wise.extend;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
/**
 * »»µ„Õº±Í
 * @author Ã∆∑…
 *
 */
public class PoiOverlay extends Overlay{
	GeoPoint geoPoint;
	Bitmap bitmap;
	public PoiOverlay(GeoPoint geoPoint,Bitmap bitmap){
		this.geoPoint = geoPoint;
		this.bitmap = bitmap;
	}
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,long when) {
		super.draw(canvas, mapView, shadow);
		Paint paint = new Paint();
        Point myScreenCoords = new Point();
        mapView.getProjection().toPixels(geoPoint, myScreenCoords);
        canvas.drawBitmap(bitmap, (myScreenCoords.x - bitmap.getWidth()/2), (myScreenCoords.y - bitmap.getHeight()/2), paint);
		return true;
	}
}