package com.wise.extend;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
/**
 * πÏº£ªÿ∑√Õº±Í
 * @author Ã∆∑…
 *
 */
public class MoveLocusOverlay extends Overlay{
	GeoPoint geoPoint;
	Bitmap bitmap;
	public MoveLocusOverlay(GeoPoint geoPoint,Bitmap bitmap){
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
