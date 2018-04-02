public class Data {
    private final int x;
    private final int y;
    private final int iters;

    public Data(int x, int y, int iters) {
        this.x = x;
        this.y = y;
        this.iters = iters;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getIters() {
        return iters;
    }

    @Override
    public String toString() {
        return "Data{" +
                "x=" + x +
                ", y=" + y +
                ", iters=" + iters +
                '}';
    }
}
