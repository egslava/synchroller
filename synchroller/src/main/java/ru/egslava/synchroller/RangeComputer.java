package ru.egslava.synchroller;

/**
 * The interface is needed to get known how to calculate the virtual (scrollable) size of component.
 * For example, physical component size on screen is 640x480, but we can scroll much more:
 * 2 or 2 screens up and down. So virtual size is 1280x960.
 *
 * So, according to scrollviews, extent - is a physical size of view and extent - is the virtual.
 * @see http://stackoverflow.com/a/16889455/1444191 for more information
 */
public interface RangeComputer{
    /**
     * Calls every time it needs to update scroll position
     */
    void computeScroll();

    /**
     * @return computed (cached) size of total (virtual) area. All processings should be in #computeScroll()
     */
    int computeHorizontalScrollExtent();

    /**
     * @return computed (cached) size of real usually, getMeasuredWidth(). All processings should be in #computeScroll()
     */
    int computeHorizontalScrollRange();

    /**
     * @return computed (cached) size of total (virtual) area. All processings should be in #computeScroll()
     */
    int computeVerticalScrollExtent();

    /**
     * @return computed (cached) size of real usually, getMeasuredHeight(). All processings should be in #computeScroll()
     */
    int computeVerticalScrollRange();

}
