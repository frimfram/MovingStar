package tophatter.demo.com.movingball;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MovingIconView extends View {

    private float mDimension;
    private Drawable mDrawable;

    private int mLeftPositions[];
    private int mRightPositions[];
    private int mTopPositions[];
    private int mBottomPositions[];

    private int mDrawIndex = -1;

    private boolean dragging = false;
    private int prevX;
    private int prevY;

    public double MIN_DRAG_DISTANCE = 60;

    public MovingIconView(Context context) {
        super(context);
        init(null, 0);
    }

    public MovingIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MovingIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MovingIconView, defStyle, 0);

        mDimension = a.getDimension(
                R.styleable.MovingIconView_mivDimension,
                mDimension);

        if (a.hasValue(R.styleable.MovingIconView_mivDrawable)) {
            mDrawable = a.getDrawable(
                    R.styleable.MovingIconView_mivDrawable);
            mDrawable.setCallback(this);

            mDrawIndex = 0;
        }

        a.recycle();
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        int iconWidth = mDrawable.getIntrinsicWidth();
        int iconHeight = mDrawable.getIntrinsicHeight();

        mLeftPositions = new int[] { 0, getWidth()-iconWidth, 0, getWidth()-iconWidth };
        mRightPositions = new int[] { iconWidth, getWidth(), iconWidth, getWidth() };
        mTopPositions = new int[] { 0, 0, getHeight()-iconHeight, getHeight()-iconHeight };
        mBottomPositions = new int[] { iconHeight, iconHeight, getHeight(), getHeight() };
        if(mDrawable != null) {
            mDrawable.setBounds(mLeftPositions[mDrawIndex], mTopPositions[mDrawIndex],
                    mRightPositions[mDrawIndex], mBottomPositions[mDrawIndex]);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDrawable != null) {
            mDrawable.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int posX = (int)event.getX();
        int posY = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Rect iconBounds = mDrawable.getBounds();
                if(iconBounds.contains(posX, posY) ) {
                    prevX = posX;
                    prevY = posY;
                    dragging = true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if(dragging) {
                    positionIcon(event);
                    dragging = false;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if(dragging) {
                    updateIcon(event);
                }
                break;
            }
        }

        return true;
    }

    private void updateIcon(MotionEvent event) {
        int x2 = (int)event.getX();
        int y2 = (int)event.getY();
        final int distY = (int) Math.abs(y2 - prevY);
        final int distX = (int) Math.abs(x2 - prevX);

        if(distY > 10 || distX > 10) {
            int deltaX = x2 - prevX;
            int deltaY = y2 - prevY;
            Rect currentBounds = mDrawable.getBounds();
            if((currentBounds.left+deltaX) >=0 && (currentBounds.right+deltaX)<getWidth() &&
                    (currentBounds.top + deltaY) >= 0 && (currentBounds.bottom + deltaY) <getHeight()) {

                int iconWidth = mDrawable.getIntrinsicWidth();
                int iconHeight = mDrawable.getMinimumHeight();
                Rect iconBounds = mDrawable.copyBounds();
                mDrawable.setBounds((int) (x2 - iconWidth / 2), (int) (y2 - iconHeight / 2), (int) (x2 + iconWidth / 2), (int) (y2 + iconHeight / 2));

                prevX = x2;
                prevY = y2;
                this.invalidate();
            }
        }

    }

    private void positionIcon(MotionEvent event) {
        int x2 = (int)event.getX();
        int y2 = (int)event.getY();

        //original position - can calculate from mDrawIndex
        int x1 = mLeftPositions[mDrawIndex] + mDrawable.getIntrinsicWidth()/2;
        int y1 = mTopPositions[mDrawIndex] +  mDrawable.getMinimumHeight()/2;
        int distY = (int) Math.abs(y2 - y1);
        int distX = (int) Math.abs(x2 - x1);
        double dist = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));

        if(dist > MIN_DRAG_DISTANCE) {
            //position to the nearest corner
            moveIcon();
        }

        mDrawable.setBounds(mLeftPositions[mDrawIndex], mTopPositions[mDrawIndex],
            mRightPositions[mDrawIndex], mBottomPositions[mDrawIndex]);

        this.invalidate();
    }

    private void moveIcon() {
        int nextRandomInt = mDrawIndex;
        while(nextRandomInt == mDrawIndex) {
            nextRandomInt = (int) (Math.random() * 4);
        }
        mDrawIndex = nextRandomInt;
        this.invalidate();
        //this.requestLayout();
    }

    /**
     * Gets the example drawable attribute value.
     * @return The example drawable attribute value.
     */
    public Drawable getMivDrawable() {
        return mDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setMivDrawable(Drawable exampleDrawable) {
        mDrawable = exampleDrawable;
    }
}
