import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Worker implements Callable<List<Data>> {
    private final int begin;
    private final int end;
    private final int width;
    private final int height;
    private final double ZOOM;
    private final int MAX_ITER;
    private double zx, zy, cX, cY, tmp;

    public Worker(int begin, int end, int width, int height, int MAX_ITER, double ZOOM) {
        this.begin = begin;
        this.end = end;
        this.width = width;
        this.height = height;
        this.MAX_ITER = MAX_ITER;
        this.ZOOM = ZOOM;
    }

    @Override
    public List<Data> call() throws Exception {
        List<Data> returnList = new ArrayList<>();
        for(int i = begin; i < end; ++i){
            int x = i/height;
            int y = i%height;
            zx = zy = 0;
            cX = (x - width/2) / ZOOM;
            cY = (y - height/2) / ZOOM;
            int iter = MAX_ITER;
            while (zx * zx + zy * zy < 4 && iter > 0) {
                tmp = zx * zx - zy * zy + cX;
                zy = 2.0 * zx * zy + cY;
                zx = tmp;
                iter--;
            }
            returnList.add(new Data(x, y, iter));
        }
        return returnList;
    }
}
