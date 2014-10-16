package ru.egslava.synchroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.OverScroller;
import android.widget.RelativeLayout;
import java.util.ArrayList;

/**
 * Created by egslava on 26/09/14.
 * Uses for listening scroll events on some part of screen.
 * Out-of-box it allows to synchronize scrolling of all children.
 *
 * If you want to change virtual size of view, see RangeComputer.
 * If you want to manually code behavior when it scrolls see #listeners and ScrollListener
 */
public class ScrollableComponent extends RelativeLayout {

    private GestureDetector scrollerDetector;
    private OverScroller overScroller;
    private int cachedHorizontalScrollRange;
    private int cachedHorizontalScrollExtent;
    private int cachedVerticalScrollExtent;
    private int cachedVerticalScrollOffset;
    private int cachedVerticalScrollRange;
    private int cachedHorizontalScrollOffset;
    private float scrollDeltaX;
    private float scrollDeltaY;
    private boolean isItStateBetweenOnFlingAndComputeScroll;
    private boolean interceptTouchEvent = true;

    public ScrollableComponent(Context context){
        this(context, null);
    }

    public ScrollableComponent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollableComponent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScrollableComponent, defStyleAttr, 0);

        try {
            initializeScrollbars(a);
            setWillNotDraw(!isHorizontalScrollBarEnabled() && !isVerticalScrollBarEnabled());

            interceptTouchEvent = a.getBoolean(R.styleable.ScrollableComponent_interceptTouchEvent, true);
            int viewId = a.getResourceId(R.styleable.ScrollableComponent_rangeView, 0);
            int viewGroupId = a.getResourceId(R.styleable.ScrollableComponent_rangeViewGroup, 0);
            int scrollWidth = a.getResourceId(R.styleable.ScrollableComponent_scrollWidth, 0);
            int scrollHeight = a.getResourceId(R.styleable.ScrollableComponent_scrollHeight, 0);

            if (viewId != 0) {
                rangeComputer = new ConcreteViewRangeComputer(this, viewId);
            }else if (scrollWidth != 0 || scrollHeight != 0){
                rangeComputer = new StaticSizeRange(scrollWidth, scrollHeight);
            }else if (viewGroupId != 0){
                rangeComputer = new MaxChildSizeRangeComputer(this);
            }

        } finally {
            a.recycle();
        }

        scrollerDetector = new GestureDetector(getContext(), new TouchGestureDetector());
        overScroller = new OverScroller(getContext());

    }

    public RangeComputer getRangeComputer() {
        return rangeComputer;
    }

    public void setRangeComputer(RangeComputer rangeComputer) {
        if (rangeComputer == null) {
            throw new NullPointerException();
        }
        this.rangeComputer = rangeComputer;
    }

    private RangeComputer rangeComputer = new MaxChildSizeRangeComputer(this);
    public ArrayList<ScrollListener> listeners = new ArrayList<ScrollListener>();

    {
        listeners.add(new ScrollerTo());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event) || scrollerDetector.onTouchEvent(event) /*|| true */;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return interceptTouchEvent;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        awakenScrollBars();
        for (ScrollListener listener : listeners) {
            listener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("ScrollableComponent", "onDraw");
    }

    @Override
    public void computeScroll() {

        if (isItStateBetweenOnFlingAndComputeScroll){
            scrollDeltaX = 0;
            scrollDeltaY = 0;
            isItStateBetweenOnFlingAndComputeScroll = false;
        }

        if (scrollDeltaX != 0 || scrollDeltaY != 0){
            overScroller.startScroll(cachedHorizontalScrollOffset, cachedVerticalScrollOffset, (int)scrollDeltaX, (int)scrollDeltaY, 0);
            scrollDeltaX = 0;
            scrollDeltaY = 0;
            isItStateBetweenOnFlingAndComputeScroll = false;
        }

        if (overScroller.computeScrollOffset()) {

            int oldX = cachedHorizontalScrollOffset;
            int oldY = cachedVerticalScrollOffset;

            rangeComputer.computeScroll();

            cachedHorizontalScrollRange     = rangeComputer.computeHorizontalScrollRange();
            cachedHorizontalScrollOffset    = fit(0, overScroller.getCurrX(), cachedHorizontalScrollRange - cachedHorizontalScrollExtent);
            cachedHorizontalScrollExtent    = rangeComputer.computeHorizontalScrollExtent();

            cachedVerticalScrollRange       = rangeComputer.computeVerticalScrollRange();
            cachedVerticalScrollOffset  = fit(0, overScroller.getCurrY(), cachedVerticalScrollRange - cachedVerticalScrollExtent);
            cachedVerticalScrollExtent  = rangeComputer.computeVerticalScrollExtent();


            if (oldX != cachedHorizontalScrollOffset || oldY != cachedVerticalScrollOffset){
                onScrollChanged(cachedHorizontalScrollOffset, cachedVerticalScrollOffset, oldX, oldY);
            }

            if (! awakenScrollBars() ){
                ViewCompat.postInvalidateOnAnimation(this);
            }

        }

    }

    protected int computeHorizontalScrollExtent() {
        return cachedHorizontalScrollExtent;
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        return cachedHorizontalScrollOffset;
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return cachedHorizontalScrollRange;
    }

    @Override
    protected int computeVerticalScrollExtent() {
        return cachedVerticalScrollExtent;
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return cachedVerticalScrollOffset;
    }

    @Override
    protected int computeVerticalScrollRange() {
        return cachedVerticalScrollRange;
    }

    /**
     * @return min <= value <= max
     */
    private int fit(int min, int value, int max){
        int higherThanMin = Math.max(min, value);
        return Math.min(higherThanMin, max);
    }

    class TouchGestureDetector extends GestureDetector.SimpleOnGestureListener{

        @Override
        public boolean onDown(MotionEvent e) {
            overScroller.forceFinished(true);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            scrollBy( distanceX, distanceY);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            isItStateBetweenOnFlingAndComputeScroll = true;
            overScroller.fling(
                    cachedHorizontalScrollOffset, cachedVerticalScrollOffset,
                    (int) -velocityX, (int) -velocityY,
                    0, cachedHorizontalScrollRange,
                    0, cachedVerticalScrollRange);
            postInvalidate();
            return true;
        }
    }

    @Override
    public void scrollTo(int x, int y){

        int oldX = cachedHorizontalScrollOffset;
        int oldY = cachedVerticalScrollOffset;

        scrollBy(x - oldX, oldY - y);

    }

    @Override
    public void scrollBy(int distanceX, int distanceY){
        scrollBy((float) distanceX, (float)distanceY);
    }

    public void scrollBy(float distanceX, float distanceY){
        scrollDeltaX += distanceX;
        scrollDeltaY += distanceY;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.offsetX = cachedHorizontalScrollOffset;
        state.offsetY = cachedVerticalScrollOffset;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if ( ! (state instanceof SavedState) ){
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        scrollTo(savedState.offsetX, savedState.offsetY);
    }
}
