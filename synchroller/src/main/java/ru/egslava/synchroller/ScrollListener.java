package ru.egslava.synchroller;

/**
 * @see ru.egslava.synchroller.ScrollableComponent#listeners
 */
public interface ScrollListener{
    void onScrollChanged(ScrollableComponent scrollable, int l, int t, int oldl, int oldt);
}