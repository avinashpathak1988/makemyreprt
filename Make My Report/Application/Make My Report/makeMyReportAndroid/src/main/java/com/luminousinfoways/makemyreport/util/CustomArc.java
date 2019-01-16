package com.luminousinfoways.makemyreport.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class CustomArc extends View {
		 
		// CONSTRUCTOR
		public CustomArc(Context context) {
			super(context);
			setFocusable(true);
 
		}
 
		@Override
		protected void onDraw(Canvas canvas) {
 
			canvas.drawColor(Color.CYAN);
			Paint p = new Paint();
			// smooths
			p.setAntiAlias(true);
			p.setColor(Color.RED);
			p.setStyle(Paint.Style.STROKE); 
			p.setStrokeWidth(5);
			// opacity
			//p.setAlpha(0x80); //
 
			RectF rectF = new RectF(50, 20, 100, 80);
			canvas.drawOval(rectF, p);
			p.setColor(Color.BLACK);
			canvas.drawArc (rectF, 90, 45, true, p);
		}
	
}
