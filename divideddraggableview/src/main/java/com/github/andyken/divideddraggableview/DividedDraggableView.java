package com.github.andyken.divideddraggableview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Created by hzfengyuexin on 17/2/10.
 */

public class DividedDraggableView extends ScrollView {

	private RelativeLayout rootView;
	private AttributeSet attributeSet;
	private int bgColor, gapColor, textInGapColor, groupBgColor;
	private String textInGap;
	private int itemCount;
	private int yPadding;//the y-axis padding of the item
	private int rowHeight, itemWidth, itemHeight, colCount;
	private boolean usingGroup = false;
	private int groupGap, groupLineCount, groupItemCount;
	private DividedDraggableViewCore dividedDraggableViewCore;

	private static final int DEFAULT_ROW_HEIGHT = 60;
	private static final int DEFAULT_ITEM_WIDTH = 50;
	private static final int DEFAULT_ITEM_HEIGHT = 55;
	private static final int DEFAULT_Y_PADDING = 20;
	private static final boolean DEFAULT_USING_GROUP = false;//using group for default/默认是否使用group
	private static final int DEFAULT_GROUP_ITEM_COUNT = 10;
	private static final int DEFAULT_GROUP_GAP = 35;//default group gap is 35dp/35dp默认group之间的高度
	private static final int DEFAULT_BG_COLOR = Color.parseColor("#00ffffff");
	private static final int DEFAULT_GAP_COLOR = Color.parseColor("#f8f8f8");
	private static final int DEFAULT_TEXT_IN_GAP_COLOR = Color.parseColor("#999999");
	private static final int DEFAULT_GROUP_BG_COLOR = Color.parseColor("#ffffff");
	private static final String DEFAULT_TEXT_IN_GAP = "page %d";
	private static final int DEFAULT_GROUP_LINE_COUNT = 2;

	public DividedDraggableView(Context context) {
		super(context);
		init();
	}

	public DividedDraggableView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.attributeSet = attributeSet;
		init();
	}

	private void init() {
		initView();
		initAttributes();
		initEventListener();
	}

	private void initAttributes() {
		TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.DividedDraggableView);
		try {
			rowHeight = (int) typedArray.getDimension(R.styleable.DividedDraggableView_rowHeight, dp2px(DEFAULT_ROW_HEIGHT));
			itemWidth = (int) typedArray.getDimension(R.styleable.DividedDraggableView_itemWidth, dp2px(DEFAULT_ITEM_WIDTH));
			itemHeight = (int) typedArray.getDimension(R.styleable.DividedDraggableView_itemHeight, dp2px(DEFAULT_ITEM_HEIGHT));
			yPadding = (int) typedArray.getDimension(R.styleable.DividedDraggableView_yPadding, dp2px(DEFAULT_Y_PADDING));//20dp
			usingGroup = typedArray.getBoolean(R.styleable.DividedDraggableView_usingGroup, DEFAULT_USING_GROUP);
			groupGap = (int) typedArray.getDimension(R.styleable.DividedDraggableView_groupGap, dp2px(DEFAULT_GROUP_GAP));//35dp
			groupLineCount = typedArray.getInteger(R.styleable.DividedDraggableView_groupLineCount, DEFAULT_GROUP_LINE_COUNT);
			groupItemCount = typedArray.getInteger(R.styleable.DividedDraggableView_groupItemCount, DEFAULT_GROUP_ITEM_COUNT);
			colCount = groupItemCount / groupLineCount;
			bgColor = typedArray.getColor(R.styleable.DividedDraggableView_bgColor, DEFAULT_BG_COLOR);
			gapColor = typedArray.getColor(R.styleable.DividedDraggableView_gapColor, DEFAULT_GAP_COLOR);
			textInGapColor = typedArray.getColor(R.styleable.DividedDraggableView_bgColor, DEFAULT_TEXT_IN_GAP_COLOR);
			groupBgColor = typedArray.getColor(R.styleable.DividedDraggableView_groupBgColor, DEFAULT_GROUP_BG_COLOR);
			textInGap = typedArray.getString(R.styleable.DividedDraggableView_textInGap);
			if (TextUtils.isEmpty(textInGap)) {
				textInGap = DEFAULT_TEXT_IN_GAP;;
			}
		} finally {
			typedArray.recycle();
		}
	}

	private void initView(){
		LinearLayout.LayoutParams scrollViewParam = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		setLayoutParams(scrollViewParam);
		setFillViewport(true);

		LinearLayout linearLayout = new LinearLayout(getContext());
		ScrollView.LayoutParams params = new ScrollView.LayoutParams(
				ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT);
		linearLayout.setLayoutParams(params);
		linearLayout.setOrientation(LinearLayout.VERTICAL);

		rootView = new RelativeLayout(getContext());
		linearLayout.addView(rootView);
		super.addView(linearLayout);

		dividedDraggableViewCore = new DividedDraggableViewCore(getContext());
	}


	private void initEventListener() {
		//解决和scrollview的上下滚动冲突问题
		dividedDraggableViewCore.setActionMoveListener(new DividedDraggableViewCore.ActionListener<MotionEvent>() {
			@Override
			public void onAction(MotionEvent motionEvent) {
				requestDisallowInterceptTouchEvent(true);
				int scrollDistance = getScrollY();
				//motionEvent.getY 为其在scrollView中的子view的高度 而不是距离屏幕的高度 getRawY为距离屏幕左上角的高度 注意scrollView中的子view比屏幕的高度要大
				int y = Math.round(motionEvent.getY());
				int translatedY = y - scrollDistance;
				int threshold = 50;
				// scrollview 向下移动 将上面的内容显示出来
				if (translatedY < threshold) {
					scrollBy(0, -30);
				}
				// scrollview 向上移动 将下面的内容显示出来
				if (translatedY + threshold > getHeight()) {
					// make a scroll down by 30 px
					scrollBy(0, 30);
				}
			}
		});

		dividedDraggableViewCore.setActionUpListener(new DividedDraggableViewCore.ActionListener<MotionEvent>() {
			@Override
			public void onAction(MotionEvent motionEvent) {
				requestDisallowInterceptTouchEvent(false);
			}
		});
	}

	private void initDraggableView(){
		int groupCount = 0;
		int rowCount = 0;
		if (itemCount > 0) {
			groupCount = (int) Math.ceil((double) itemCount / groupItemCount);
			rowCount = (int) Math.ceil((double) itemCount / colCount);
		}
		//每个分组区域高度为 每行高度*行数 + 每行间隔高度*（行数+1) 这里 两行存在三个每行间隔高度
		int groupHeight = rowHeight * DEFAULT_GROUP_LINE_COUNT + yPadding * (DEFAULT_GROUP_LINE_COUNT + 1);
		int pageLineHeight = groupGap;//“第一页 第二页” 所在行的line的高度
		ArrayList<Integer> topMarginArray = new ArrayList<>();//“第一页 第二页” 所在行距离顶部的margin集合
		//add 分组间隔区域
		if (groupCount > 0) {
			for (int i = 0; i < groupCount; i++) {
				LinearLayout linearLayout = getPageLineLayout();
				TextView textView = getTextViewInGapLayout();
				textView.setText(String.format(textInGap, i + 1));
				linearLayout.addView(textView);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, pageLineHeight);
				//每个间隔分组区域 距离顶部的距离 i为分组间隔区域个数
				//分组间隔区域个数*间隔区域高度 + 分组间隔区域个数*（分组中的行数*行高+（分组中的行数+1)*行间隔）两行存在三个行间隔
				int topMargin = i * groupGap + (i * (DEFAULT_GROUP_LINE_COUNT * rowHeight + (DEFAULT_GROUP_LINE_COUNT + 1) * yPadding));
				topMarginArray.add(topMargin);
				params.setMargins(0, topMargin, 0, 0);
//				params.addRule(RelativeLayout.BELOW, R.id.categorySort_actionBar);
				linearLayout.setLayoutParams(params);
				rootView.addView(linearLayout);
			}
		}

		//由于可拖动区域是透明的 这里需要绘制其白底
		for (int topMargin : topMarginArray) {
			LinearLayout groupLayout = getGroupLayout();
			RelativeLayout.LayoutParams groupLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, groupHeight);
//			pageLineGapParams.addRule(RelativeLayout.BELOW, R.id.categorySort_actionBar);
			groupLayoutParams.setMargins(0, topMargin + groupGap, 0, 0);
			groupLayout.setLayoutParams(groupLayoutParams);
			rootView.addView(groupLayout);
		}
		//add 可拖动区域
		//可拖动区域高度为 间隔分组的高度*分组个数 +该高度下面的一个padding*分组个数 +每行高度*行数 + 每行间隔高度*行数
//		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//				rowheight * rowCount + groupGap + rowPadding * groupCount + rowCount * rowPadding + groupCount * groupGap);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				groupGap * groupCount + yPadding * groupCount + rowHeight * rowCount + yPadding * rowCount);
//		layoutParams.addRule(RelativeLayout.BELOW, R.id.categorySort_actionBar);
		dividedDraggableViewCore.setLayoutParams(layoutParams);
		dividedDraggableViewCore.setItemHeight(rowHeight);
		dividedDraggableViewCore.setItemWidth(itemWidth);
		dividedDraggableViewCore.setColCount(colCount);
		dividedDraggableViewCore.setyPadding(yPadding);
		dividedDraggableViewCore.setGroupGap(groupGap);
		dividedDraggableViewCore.setUsingGroup(true);
		dividedDraggableViewCore.setGroupLineCount(groupLineCount);
		dividedDraggableViewCore.setBackgroundColor(bgColor);
		rootView.addView(dividedDraggableViewCore);
	}

	/**
	 * should init first
	 * @param itemCount
	 */
	public void setItemCount(int itemCount){
		this.itemCount = itemCount;
		initDraggableView();
	}

	public void addChildView(View child) {
		if (dividedDraggableViewCore != null) {
			dividedDraggableViewCore.addView(child);
		}
	}

	public LinearLayout getPageLineLayout() {
		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setBackgroundColor(gapColor);
		linearLayout.setGravity(Gravity.CENTER_VERTICAL);
		return linearLayout;
	}

	public TextView getTextViewInGapLayout() {
		TextView textView = new TextView(getContext());
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, dp2px(13));
		textView.setTextColor(textInGapColor);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(dp2px(15), 0, 0, 0);
		textView.setLayoutParams(layoutParams);
		return textView;
	}

	public LinearLayout getGroupLayout() {
		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setBackgroundColor(groupBgColor);
		return linearLayout;
	}

	/**
	 * dp转pixel
	 */
	public int dp2px(double dp) {
		float density = getContext().getResources().getDisplayMetrics().density;
		return (int) (dp * density + 0.5);
	}
}
