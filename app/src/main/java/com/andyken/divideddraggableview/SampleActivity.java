package com.andyken.divideddraggableview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.andyken.divideddraggableview.DividedDraggableView;
import com.github.andyken.divideddraggableview.sample.R;

import java.util.ArrayList;
import java.util.Random;
/**
 * Created by andyken on 17/2/9.
 */

public class SampleActivity extends Activity {

	private LinearLayout rootView;
	private ArrayList<ImageView> mockViews = new ArrayList<>();
	private Random random = new Random();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		init();
	}

	private void init() {
		initNode();
		initData();
		initView();
	}

	private void initNode() {
		rootView = (LinearLayout) findViewById(R.id.rootView);
	}

	private void initData() {
		for (int i = 0; i < 35; i++) {
			ImageView view = new ImageView(SampleActivity.this);
			view.setImageBitmap(getThumb(String.valueOf(i)));
			mockViews.add(view);
		}
	}

	private void initView(){
		DividedDraggableView dividedDraggableView = new DividedDraggableView(SampleActivity.this);
		dividedDraggableView.setItemCount(mockViews.size());
		for (ImageView imageView : mockViews) {
			dividedDraggableView.addChildView(imageView);
		}
		rootView.addView(dividedDraggableView);
	}

	private Bitmap getThumb(String s) {
		Bitmap bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bmp);
		Paint paint = new Paint();

		paint.setColor(Color.rgb(random.nextInt(128), random.nextInt(128), random.nextInt(128)));
		paint.setTextSize(24);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		canvas.drawRect(new Rect(0, 0, 150, 150), paint);
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText(s, 75, 75, paint);

		return bmp;
	}
}
