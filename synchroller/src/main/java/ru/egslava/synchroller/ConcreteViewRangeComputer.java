package ru.egslava.synchroller;

import android.view.View;
import android.view.ViewGroup;

/**
 * Clones range and extent of a specific view.
 */
public class ConcreteViewRangeComputer implements RangeComputer {
    private final View[] views;
    private int maxWidth, maxHeight;
    private int horizontalExtent, verticalExtent;
    private final ViewPrivateMethods h = new ViewPrivateMethods();

    private ViewGroup viewGroup;
    private LazyInitialization lazy;

    private class LazyInitialization{
        ViewGroup parent;
        int[] viewIds;

        private LazyInitialization(ViewGroup parent, int... viewIds) {
            this.parent = parent;
            this.viewIds = viewIds;
        }

        void init(){
            viewGroup = this.parent;

            for(int i = 0; i < viewIds.length; i++){
                views[i] = viewGroup.findViewById(viewIds[i]);
            }
        }
    }


    public ConcreteViewRangeComputer(ViewGroup viewGroup, int... viewIds){

        views = new View[viewIds.length];

        lazy = new LazyInitialization(viewGroup, viewIds);
        if (viewGroup.getChildCount() != 0){
            lazy.init();
            lazy = null;
        }
    }

    @Override
    public int computeHorizontalScrollExtent() {
        return horizontalExtent;
    }

    @Override
    public int computeHorizontalScrollRange() {
        return maxWidth;
    }

    @Override
    public int computeVerticalScrollExtent() {
        return verticalExtent;
    }

    @Override
    public int computeVerticalScrollRange() {
        return maxHeight;
    }

    @Override
    public void computeScroll() {
        if (lazy != null){
            lazy.init();
            lazy = null;
        }

        maxWidth = 0;
        maxHeight = 0;
        horizontalExtent = 0;
        verticalExtent = 0;

        int width, height;

        for (View view : views){

            if (view == null)continue;

            h.view = view;
            width = h.computeHorizontalScrollRange();
            height = h.computeVerticalScrollRange();

            if (width > maxWidth){
                maxWidth = width;
                horizontalExtent = h.computeHorizontalScrollExtent();
            }

            if (height > maxHeight){
                maxHeight = height;
                verticalExtent = h.computeVerticalScrollExtent();
            }
        }
    }
}