import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JFrame;

public class Mandelbrot extends JFrame {

    //private final int MAX_ITER = 570;
    private final double ZOOM = 150;
    private BufferedImage I;
    private double zx, zy, cX, cY, tmp;

    public Mandelbrot(int workersCount, ExecutorService service, int x, int y, int MAX_ITER) {
        super("Mandelbrot Set");
        setBounds(100, 100, x, y);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //ExecutorService service = Executors.newFixedThreadPool(workersCount);
        Set<Future<List<Data>>> futures = new HashSet<>();
        I = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
//        for (int y = 0; y < getHeight(); y++) {
//            for (int x = 0; x < getWidth(); x++) {
//                zx = zy = 0;
//                cX = (x - 400) / ZOOM;
//                cY = (y - 300) / ZOOM;
//                int iter = MAX_ITER;
//                while (zx * zx + zy * zy < 4 && iter > 0) {
//                    tmp = zx * zx - zy * zy + cX;
//                    zy = 2.0 * zx * zy + cY;
//                    zx = tmp;
//                    iter--;
//                }
//                I.setRGB(x, y, iter | (iter << 8));
//            }
//        }
        int N = getWidth()*getHeight();
        int a = N/workersCount;
        for(int i =0; i < workersCount-1; ++i){
            futures.add(service.submit(new Worker(i*a, (i+1)*a, getWidth(), getHeight(), MAX_ITER, ZOOM)));
        }
        futures.add(service.submit(new Worker((workersCount-1)*a, workersCount*a + N%workersCount, x, y, MAX_ITER, ZOOM)));
        try {
            for (Future<List<Data>> f : futures) {
                List<Data> dataList = f.get();
                for(Data d : dataList) {

                    //System.out.println(d);
                    try {
                        I.setRGB(d.getX(), d.getY(), d.getIters() | (d.getIters() << (int) Math.ceil(Math.log(MAX_ITER) / Math.log(2))));
                    }
                    catch(ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (InterruptedException | ExecutionException e){
            e.printStackTrace();
        }

        service.shutdownNow();
        //I.setRGB(x, y, iter | (iter << 8));
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(I, 0, 0, this);
    }

    public static void main(String[] args) {
        int threadsCount = 4;
        int MAX_ITERS = 1000;
        List<Long> times;
        for(int i = 1; i <= 5; i++){
            for(int j = 1; j <= 10; j++){
                times = new ArrayList<>();
                for(int k = 0; k < 10; k++){
                    long start = System.currentTimeMillis();
                    new Mandelbrot(threadsCount*i, Executors.newFixedThreadPool(threadsCount*j), 800, 600, MAX_ITERS*i);
                    times.add(System.currentTimeMillis()-start);
                }
                double avg_1 = 0;
                for(Long time : times){
                    avg_1+=time;
                }
                avg_1/=times.size();
                double sum = 0;
                for(Long time:times){
                    sum+=Math.pow(time-avg_1,2);
                }
                double s_1 = Math.sqrt((1.0/(times.size()-1))*sum);
                times = new ArrayList<>();
                for(int k = 0; k < 10; k++){
                    long start = System.currentTimeMillis();
                    new Mandelbrot(threadsCount*i, Executors.newScheduledThreadPool(threadsCount*j), 800, 600, MAX_ITERS*i);
                    times.add(System.currentTimeMillis()-start);
                }
                double avg_2 = 0;
                for(Long time : times){
                    avg_2+=time;
                }
                avg_2/=times.size();
                sum = 0;
                for(Long time:times){
                    sum+=Math.pow(time-avg_2,2);
                }
                double s_2 = Math.sqrt((1.0/(times.size()-1))*sum);
                System.out.println("Threads: " + threadsCount*j+" max iterations: " + MAX_ITERS*i);
                System.out.println("FixedThreadPool average time: "+ avg_1 + " standard deviation " + s_1);
                System.out.println("ScheduledThreadPool average time: "+ avg_2+ " standard deviation " + s_2);
                System.out.println();

                Runtime.getRuntime().gc();

            }
            System.out.println("--------------------------------------");
        }
        //new Mandelbrot(threadsCount, Executors.newFixedThreadPool(threadsCount), 800, 600).setVisible(true);
        //new Mandelbrot(threadsCount, Executors.newCachedThreadPool(), 1000, 800, MAX_ITERS).setVisible(true);

    }


}