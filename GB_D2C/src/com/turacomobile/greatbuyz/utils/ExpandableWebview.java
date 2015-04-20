package com.turacomobile.greatbuyz.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class ExpandableWebview extends WebView {

    public ExpandableWebview(Context context) {
        super(context);
    }

    public ExpandableWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableWebview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }          
}