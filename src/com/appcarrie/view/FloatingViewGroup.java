package com.appcarrie.view;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.appcarrie.R;
import com.appcarrie.utils.Constants;

public class FloatingViewGroup extends ViewGroup implements OnGestureListener{
	private static final String TAG = FloatingViewGroup.class.getSimpleName();

	
	private static final String TOP_OFFSET = "Top_Offset";
	
	private static final int DIR_DEFAULT = -1;
	private static final int DIR_UP = 0;
	private static final int DIR_DOWN = 1;
	private static final int DIR_LEFT = 2;
	private static final int DIR_RIGHT = 3;
		
	private Context mContext;
	private SharedPreferences mPref;
	private WindowManager windowManager;
	

//	private Animation m_fadein, m_fadeout;
	
	private int	mTopOffset;
	private int mTouchOffset;
	private boolean	mMove = false;
	private int	mDirection = DIR_DEFAULT;
	
	private final Rect mFrame = new Rect();
	
	
	private int screenX;
	private int screenY;
	private int dialogX;
	private int dialogY;
	
	private View mBody;
	
	private final int mBodyId;
	
	private GestureDetector mGestureDetector;
	
	public FloatingViewGroup(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	public FloatingViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		DisplayMetrics dm = new DisplayMetrics();
		windowManager = (WindowManager) mContext.getSystemService(android.content.Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		screenX = dm.widthPixels;
		screenY = dm.heightPixels;
		mPref = mContext.getSharedPreferences(Constants.PREF, 0);
		TypedArray typearray = context.obtainStyledAttributes( attrs, R.styleable.FloatingViewGroup, defStyle, 0);
		
		mBodyId = typearray.getResourceId( R.styleable.FloatingViewGroup_body, 0);
		if ( mBodyId == 0 ) { 
			throw new IllegalArgumentException( "The body attribute is required and must refer to a valid child." ); 
		}
		
		mGestureDetector = new GestureDetector(mContext, this);
		mTopOffset = mPref.getInt( TOP_OFFSET, 0);
		typearray.recycle();

	}
			
	@Override
	protected void onFinishInflate() {
		mBody = findViewById(mBodyId);
		if ( mBody == null ) { throw new IllegalArgumentException( "The body attribute is must refer to an existing child." ); }
		// Important!!!Add a empty listener
		mBody.setOnClickListener(null);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode( widthMeasureSpec );
		int widthSpecSize = MeasureSpec.getSize( widthMeasureSpec );
		
		int heightSpecMode = MeasureSpec.getMode( heightMeasureSpec );
		int heightSpecSize = MeasureSpec.getSize( heightMeasureSpec );
		
		if ( widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED ) { 
			throw new RuntimeException("FloatingView cannot have UNSPECIFIED dimensions" ); 
		}
		
		measureChild( mBody, widthMeasureSpec, heightMeasureSpec);
				
		setMeasuredDimension( widthSpecSize, mBody.getMeasuredHeight());
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {		
		try{
			final View body = mBody;
			
			WindowManager.LayoutParams lp = (WindowManager.LayoutParams) this.getLayoutParams();
			if ( mTopOffset < t) 
				mTopOffset = 0;
			else if ( mTopOffset+body.getMeasuredHeight() > screenY)
				mTopOffset = screenY-body.getMeasuredHeight();
	
			lp.y = mTopOffset;
		
			windowManager.updateViewLayout( this, lp);
			dialogX = lp.x;
			dialogY = lp.y;
			
			body.layout( l, t, r, b);
		}catch(Exception e){
			
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int y = (int)event.getRawY();

		switch ( event.getAction() ) { 
		case MotionEvent.ACTION_MOVE:
			if (mMove && (mDirection == DIR_UP || mDirection == DIR_DOWN )) { 
				mTopOffset = y-mTouchOffset;
//				if ( mCover!=null )
//					mCover.setVisibility(View.VISIBLE);
				requestLayout();
			} 
			break;
		case MotionEvent.ACTION_UP:
			if (mMove)
				mPref.edit().putInt(TOP_OFFSET, mTopOffset).commit();
			mMove = false;
//			if ( mCover!=null )
//				mCover.setVisibility(View.GONE);
			break;
		}
		return true;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		final Rect frame = mFrame;
		
		if ( mMove == false ) {
			int x = (int)event.getX();
			int y = (int)event.getY();
		}
		
		switch ( event.getAction() ) { 
		case MotionEvent.ACTION_DOWN:
			mMove = true;
			mTouchOffset = (int)event.getRawY()-dialogY;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mMove && (mDirection == DIR_UP || mDirection == DIR_DOWN)) { 
				mTopOffset = (int)event.getRawY()-mTouchOffset;
				requestLayout();
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mMove)
				mPref.edit().putInt(TOP_OFFSET, mTopOffset).commit();
			mMove = false;
			break;
		}
		
		return false;
	}
		
	@Override
	public boolean onDown(MotionEvent e) { 
		mDirection = DIR_DEFAULT;
		return false; 
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { 
		mDirection = DIR_DEFAULT;
		return false; 
	}
	@Override
	public void onLongPress(MotionEvent e) {}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if ( mDirection == DIR_DEFAULT) {
			if ( Math.abs(distanceX) > Math.abs(distanceY) ) {
				if ( distanceX > 0 )
					mDirection = DIR_LEFT;
				else
					mDirection = DIR_RIGHT;
			} else {
				if ( distanceY > 0 )
					mDirection = DIR_UP;
				else
					mDirection = DIR_DOWN;			
			}
		}
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {}
	@Override
	public boolean onSingleTapUp(MotionEvent e) { return false; }

}
