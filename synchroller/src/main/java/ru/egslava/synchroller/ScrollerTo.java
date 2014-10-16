package ru.egslava.synchroller;

import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewCompatKitKat;
import android.support.v4.view.ViewGroupCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import it.sephiroth.android.library.widget.AbsHListView;
import it.sephiroth.android.library.widget.HListView;

/**
 * Basic implementation of ScrollListener. Just scrolls all children when user scrolls parent.
 * @see ru.egslava.synchroller.ScrollableComponent#listeners
 */
public class ScrollerTo implements ScrollListener{

    protected static Method trackMotionScroll, hTrackMotionScroll;

    public void onScrollChanged(ScrollableComponent scrollable, int l, int t, int oldl, int oldt){
        for(int i = 0; i < scrollable.getChildCount(); i++){
            View child = scrollable.getChildAt(i);

            if (child instanceof ListView){
                ListView list = (ListView) child;
                scrollBy(list, t - oldt);
            }else if (child instanceof HListView) {
                HListView list = (HListView) child;
                scrollBy(list, l - oldl);
            }else {
                child.scrollTo(l, t);
            }
        }
    }

    void scrollByCompat(AbsListView list, int y){
        try {
            if (trackMotionScroll == null){
                trackMotionScroll = AbsListView.class.getDeclaredMethod("trackMotionScroll", int.class, int.class);
                trackMotionScroll.setAccessible(true);
            }
            trackMotionScroll.invoke(list, -y, -y);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void scrollBy(AbsHListView list, int y){
        try {
            if (trackMotionScroll == null){
                trackMotionScroll = AbsHListView.class.getDeclaredMethod("trackMotionScroll", int.class, int.class);
                trackMotionScroll.setAccessible(true);
            }
            trackMotionScroll.invoke(list, -y, -y);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    void scrollBy(AbsListView list, int y){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            list.scrollListBy(y);
        } else{
            scrollByCompat(list, y);
        }
    }
}
