package ru.egslava.synchroller;

import android.view.View;
import android.view.ViewGroup;

/**
 * A basic implementation of RangeComputer.
 *
 * Clones a real and virtual sizes from the most broad/high children.
 * For example we have 2 ScrollView inside Synchroscroller: ScrollView (Hor) and HorizontalScrollView (Ver)
 * So as the Hor is more broad than Ver so it will use Hor to produce horizontal <b>range</b> and <b>extent</b>.
 * As the Ver is higher than Hor so it will use Ver to produce vertical <b>range</b> and <b>extent</b>.
 */
public class MaxChildSizeRangeComputer implements RangeComputer {
        private final ViewGroup viewGroup;
        private int maxWidth, maxHeight;
        private int horizontalExtent, verticalExtent;
        private final ViewPrivateMethods h = new ViewPrivateMethods();

        public MaxChildSizeRangeComputer(ViewGroup viewGroup){
            this.viewGroup = viewGroup;
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
            maxWidth = 0;
            maxHeight = 0;
            horizontalExtent = 0;
            verticalExtent = 0;

            for(int i = 0; i < viewGroup.getChildCount(); i++){
                View child = viewGroup.getChildAt(i);

                if (child == null) continue;

                h.view = child;
                int width = h.computeHorizontalScrollRange();
                int height = h.computeVerticalScrollRange();

                if (width > maxWidth){
                    horizontalExtent = h.computeHorizontalScrollExtent();
                    maxWidth = width;
                }

                if (height > maxHeight){
                    verticalExtent  = h.computeVerticalScrollExtent();
                    maxHeight = height;
                }
            }
        }
    }