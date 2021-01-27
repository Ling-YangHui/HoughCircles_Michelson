import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Vector;

//@SuppressWarnings("all")
public class Main {
    public static void main(String[] args) {
        int subLevelMid = 20; // 当中央RGB的差距小于这个数值的时候会被认为是灰色系
        int subLevelEdge = 60; // 当边缘RGB的差距小于这个数值的时候会被认为是灰色系
        int edgeLimit = 500; // 当位置距离中心的坐标距离超过这个数值的时候会被认为是边缘
        double declineLevel = 1.6; // 灰色需要进行变暗处理，提取出主要的红色区域特征
        double gray2BWLevel = 0.50; // 二值化的分割阈值
        int minRadius = 80; // 最小的半径允许
        int maxRadius = 200; // 最大的半径允许
        int param2Gradient = 20; // 霍夫变换参数，越小越灵敏

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat src = Imgcodecs.imread("C:\\Users\\YangHui\\Pictures\\test_11.png");
        Mat srcCache = src.clone();
        // 在这里要把相对体现为亮灰色的点变暗，来保证剩余动作的执行
        for (int i = 0; i < src.cols(); i++) {
            for (int j = 0; j < src.rows(); j++) {
                double[] pixel = src.get(j, i);
                int subLevel = Math.abs(i + j - src.cols() / 2 - src.rows() / 2) > edgeLimit ? subLevelEdge : subLevelMid;
                if (Math.abs(pixel[0] - pixel[1]) < subLevel && Math.abs(pixel[0] - pixel[2]) < subLevel && Math.abs(pixel[1] - pixel[2]) < subLevel) {
                    pixel[0] /= declineLevel;
                    pixel[1] /= declineLevel;
                    pixel[2] /= declineLevel;
                }
                srcCache.put(j, i, pixel);
            }
        }
//
        Mat dstCache = SpaceFilter.BGR2BWWeight(srcCache, gray2BWLevel); // 使用滤波器对灰度照片进行黑白化
        Imgcodecs.imwrite("E:\\gray3.png", dstCache);
        Mat circles = new Mat();
        //核心代码
        Imgproc.HoughCircles(dstCache, circles, Imgproc.HOUGH_GRADIENT, 1, 200, 400, param2Gradient, minRadius, maxRadius);
        System.out.println(circles.cols());
        Point graphCenter = new Point(src.cols() / 2.0, src.rows() / 2.0);
        Vector<Double> radiusCache = new Vector<>();
        for (int i = 0; i < circles.cols(); i++) {
            boolean sameCircleRadio = false;
            double[] vCircle = circles.get(0, i);

            Point center = new Point(vCircle[0], vCircle[1]);
            int radius = (int) Math.round(vCircle[2]);
            for (Double r : radiusCache) {
                if (Math.abs(r - vCircle[2]) < 50) {
                    sameCircleRadio = true;
                    break;
                }
            }
            if (sameCircleRadio) {
                continue;
            } else {
                radiusCache.add(vCircle[2]);
            }
            if (Math.pow(graphCenter.x - center.x, 2) + Math.pow(graphCenter.y - center.y, 2) > 24000)
                break;
//            if (vCircle[0] > dst.cols() / 2.0 + 100 || vCircle[0] < dst.cols() / 2.0 - 100)
//                continue;
//            if (vCircle[1] > dst.rows() / 2.0 + 100 || vCircle[1] < dst.rows() / 2.0 - 100)
//                continue;

            // circle center
            Imgproc.circle(src, center, 3, new Scalar(0, 255, 0), -1, 8, 0);
            // circle outline
            Imgproc.circle(src, center, radius, new Scalar(0, 255, 255), 3, 8, 0);
            Imgproc.circle(dstCache, center, 3, new Scalar(0, 255, 0), -1, 8, 0);
            // circle outline
            Imgproc.circle(dstCache, center, radius, new Scalar(0, 255, 255), 3, 8, 0);
        }

        Imgcodecs.imwrite("E:\\gray2.png", src);
        Imgcodecs.imwrite("E:\\gray1.png", dstCache);
    }
}
