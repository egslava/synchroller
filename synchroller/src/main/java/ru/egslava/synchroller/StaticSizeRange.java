package ru.egslava.synchroller;

/**
 * Just manual declaration of virtual component width and height
 */
public class StaticSizeRange implements RangeComputer{
    private final int width;
    private final int height;

    StaticSizeRange(int width, int height){
        this.width = width;
        this.height = height;
    }

    @Override
    public void computeScroll() {}

    @Override
    public int computeHorizontalScrollExtent() {
        return 0;
    }

    @Override
    public int computeHorizontalScrollRange() {
        return width;
    }

    @Override
    public int computeVerticalScrollExtent() {
        return 0;
    }

    @Override
    public int computeVerticalScrollRange() {
        return height;
    }

}