package com.awmtea.rimangseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class RimAngSeekBar extends View {

    private Paint linePaint, selectedLinePaint, leftThumbPaint, rightThumbPaint;
    private float thumbDefaultRadius, leftThumbRadius, rightThumbRadius, selectedThumbRadius, leftThumbX, rightThumbX, initialClickedX = 0F;
    private boolean isTherePending = true;
    private SelectedArea selectedArea;
    private float numberOfPoints;
    private int minValue, lowerValue, upperValue;
    private int leftThumbColor, rightThumbColor, selectedLeftThumbColor, selectedRightThumbColor, selectedLineColor, selectedLinePressedColor;
    private OnValueChange onValueChange;

    public RimAngSeekBar(Context context) {
        this(context, null);
    }

    public RimAngSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RimAngSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        Context context = getContext();
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.RimAngSeekBar, 0, 0);

        int maxValue = a.getInteger(R.styleable.RimAngSeekBar_rasb_max_value, 100);
        minValue = a.getInteger(R.styleable.RimAngSeekBar_rasb_min_value, 1);
        numberOfPoints = maxValue - minValue;

        thumbDefaultRadius = a.getDimensionPixelSize(R.styleable.RimAngSeekBar_rasb_thumb_radius, 35);
        selectedThumbRadius = a.getDimensionPixelSize(R.styleable.RimAngSeekBar_rasb_selected_thumb_radius, 40);

        int baseLineColor = a.getColor(R.styleable.RimAngSeekBar_rasb_bg_line_color, ContextCompat.getColor(context, R.color.gray_color));
        leftThumbColor = a.getColor(R.styleable.RimAngSeekBar_rasb_left_thumb_color, ContextCompat.getColor(context, R.color.colorPrimary));
        rightThumbColor = a.getColor(R.styleable.RimAngSeekBar_rasb_right_thumb_color, ContextCompat.getColor(context, R.color.colorPrimary));
        selectedLeftThumbColor = a.getColor(R.styleable.RimAngSeekBar_rasb_selected_left_thumb_color, ContextCompat.getColor(context, R.color.colorPrimaryDark));
        selectedRightThumbColor = a.getColor(R.styleable.RimAngSeekBar_rasb_selected_right_thumb_color, ContextCompat.getColor(context, R.color.colorPrimaryDark));
        selectedLineColor = a.getColor(R.styleable.RimAngSeekBar_rasb_selection_line_color, ContextCompat.getColor(context, R.color.colorPrimary));
        selectedLinePressedColor = a.getColor(R.styleable.RimAngSeekBar_rasb_selection_line_press_color, ContextCompat.getColor(context, R.color.colorPrimaryDark));

        a.recycle();

        linePaint = new Paint();
        linePaint.setColor(baseLineColor);
        selectedLinePaint = new Paint();
        leftThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rightThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        setDefaultValues();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);

        int width, height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = 400;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = 200;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isTherePending) {
            //it is first initialization
            leftThumbX = getLeftThumbX();
            rightThumbX = getRightThumbX();
            isTherePending = false;
        }
        drawBaseLine(canvas);
        drawLeftThumb(canvas);
        drawRightThumb(canvas);
        drawSelection(canvas);
    }

    private void drawBaseLine(Canvas canvas) {
        canvas.drawRect(selectedThumbRadius, getCenterYAxis() - 2.5F, getWidth() - selectedThumbRadius, getCenterYAxis() + 2.5F, linePaint);
    }

    private void drawRightThumb(Canvas canvas) {
        canvas.drawCircle(rightThumbX, getCenterYAxis(), rightThumbRadius, rightThumbPaint);
    }

    private void drawLeftThumb(Canvas canvas) {
        canvas.drawCircle(leftThumbX, getCenterYAxis(), leftThumbRadius, leftThumbPaint);
    }

    private void drawSelection(Canvas canvas) {
        canvas.drawRect(leftThumbX, getCenterYAxis() - 5, rightThumbX, getCenterYAxis() + 5, selectedLinePaint);
    }

    private float getCenterYAxis() {
        return getHeight() / 2F;
    }

    private float getClickedX(MotionEvent event) {
        if (event != null) {
            return event.getX();
        }
        return 0F;
    }

    private void doActionDown(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        float clickedX = getClickedX(event);
        if (clickedX <= leftThumbX + thumbDefaultRadius) {
            setLeftThumbX(clickedX);
            selectedArea = SelectedArea.LEFT_THUMB;

            leftThumbRadius = selectedThumbRadius;
            leftThumbPaint.setColor(selectedLeftThumbColor);
        } else if (clickedX >= rightThumbX - thumbDefaultRadius) {
            setRightThumbX(clickedX);
            selectedArea = SelectedArea.RIGHT_THUMB;

            rightThumbRadius = selectedThumbRadius;
            rightThumbPaint.setColor(selectedRightThumbColor);
        } else {
            selectedArea = SelectedArea.BETWEEN_THUMBS;
            initialClickedX = clickedX;

            leftThumbRadius = selectedThumbRadius;
            rightThumbRadius = selectedThumbRadius;
            leftThumbPaint.setColor(selectedLeftThumbColor);
            rightThumbPaint.setColor(selectedRightThumbColor);
            selectedLinePaint.setColor(selectedLinePressedColor);
        }
        invalidate();
    }

    private void doDragging(MotionEvent event) {
        float draggedX = getClickedX(event);
        if (selectedArea.equals(SelectedArea.LEFT_THUMB)) {
            setLeftThumbX(draggedX);
        } else if (selectedArea.equals(SelectedArea.RIGHT_THUMB)) {
            setRightThumbX(draggedX);
        } else {
            doDoubleThumbDragging(draggedX);
        }
        invalidate();
    }

    private void doDoubleThumbDragging(float draggedX) {
        float swipeDistance;
        if (initialClickedX < draggedX) {
            //user swipe right
            swipeDistance = draggedX - initialClickedX;
            float rightThumbMax = getWidth() - selectedThumbRadius;
            if (rightThumbX + swipeDistance > rightThumbMax) {
                //it reached max.
                swipeDistance = rightThumbMax - rightThumbX;
            }
            leftThumbX += swipeDistance;
            rightThumbX += swipeDistance;
        } else {
            //user swipe left
            swipeDistance = initialClickedX - draggedX;
            if (leftThumbX - swipeDistance < selectedThumbRadius) {
                //it reach min.
                swipeDistance = leftThumbX - selectedThumbRadius;
            }
            leftThumbX -= swipeDistance;
            rightThumbX -= swipeDistance;
        }

        initialClickedX = draggedX;
    }

    public int getLowerValue() {
        return convertToValue(leftThumbX);
    }

    public void setLowerValue(int lowerValue) {
        this.lowerValue = lowerValue;
        if (getWidth() == 0) {
            isTherePending = true;
            return;
        }
        leftThumbX = getLeftThumbX();
        invalidate();
        if (onValueChange != null) {
            onValueChange.onValueChanged(convertToValue(leftThumbX), convertToValue(rightThumbX));
        }
    }

    public int getUpperValue() {
        return convertToValue(rightThumbX);
    }

    public void setUpperValue(int upperValue) {
        this.upperValue = upperValue;
        if (getWidth() == 0) {
            isTherePending = true;
            return;
        }
        rightThumbX = getRightThumbX();
        invalidate();
        if (onValueChange != null) {
            onValueChange.onValueChanged(convertToValue(leftThumbX), convertToValue(rightThumbX));
        }
    }

    public void setOnValueChange(OnValueChange onValueChange) {
        this.onValueChange = onValueChange;
    }

    private float getLeftThumbX() {
        return ((getWidth() / getNumberOfPoints()) * (float) (lowerValue - minValue)) + selectedThumbRadius;
    }

    private void setLeftThumbX(float clickedX) {
        float leftThumbMax = getWidth() - (selectedThumbRadius * 3);
        float leftThumbMin = selectedThumbRadius;
        if (clickedX < leftThumbMin) {
            leftThumbX = leftThumbMin;
        } else if (clickedX > leftThumbMax) {
            leftThumbX = leftThumbMax;
        } else {
            leftThumbX = clickedX;
        }
        if (clickedX > rightThumbX - (selectedThumbRadius * 2)) {
            setRightThumbX(leftThumbX + (selectedThumbRadius * 2));
        }
    }

    private float getRightThumbX() {
        return ((getWidth() / getNumberOfPoints()) * (float) (upperValue - minValue)) + selectedThumbRadius;
    }

    private void setRightThumbX(float clickedX) {
        float rightThumbMax = getWidth() - selectedThumbRadius;
        float rightThumbMin = (selectedThumbRadius * 3);
        if (clickedX < rightThumbMin) {
            rightThumbX = rightThumbMin;
        } else if (clickedX > rightThumbMax) {
            rightThumbX = rightThumbMax;
        } else {
            rightThumbX = clickedX;
        }
        if (clickedX < leftThumbX + (selectedThumbRadius * 2)) {
            setLeftThumbX(rightThumbX - (selectedThumbRadius * 2));
        }
    }

    public float getNumberOfPoints() {
        return numberOfPoints;
    }

    private int convertToValue(float xValue) {
        float perPointWidth = (getWidth() - (selectedThumbRadius * 2)) / getNumberOfPoints();
        float calculatedValue = (xValue - selectedThumbRadius) / perPointWidth;
        return (int) (calculatedValue + minValue);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int action = event.getAction();
        switch (action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                doActionDown(event);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                doDragging(event);
                break;
            }
            case MotionEvent.ACTION_UP: {
                setDefaultValues();
                invalidate();
                break;
            }
        }
        if (onValueChange != null) {
            onValueChange.onValueChanged(getLowerValue(), getUpperValue());
        }
        performClick();
        return true;
    }

    private void setDefaultValues() {
        leftThumbRadius = thumbDefaultRadius;
        rightThumbRadius = thumbDefaultRadius;
        selectedLinePaint.setColor(selectedLineColor);
        leftThumbPaint.setColor(leftThumbColor);
        rightThumbPaint.setColor(rightThumbColor);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public enum SelectedArea {
        LEFT_THUMB,
        RIGHT_THUMB,
        BETWEEN_THUMBS
    }

    public interface OnValueChange {
        void onValueChanged(int lower, int upper);
    }
}
