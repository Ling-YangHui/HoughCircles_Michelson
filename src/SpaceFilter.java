import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class SpaceFilter {
    public static Mat spaceFilter(Mat src, int size, double value) {

        double[][] filterMatrix = new double[size][size];
        double filterSum = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == 0 && j == 0) {
                    filterMatrix[i][j] = value;
                    continue;
                }
                filterMatrix[i][j] = value * Math.abs(Math.sin(value * (i + j)) / value / (i + j));
                filterSum += filterMatrix[i][j] + value;
            }
        }
        Mat mat = src.clone();
        for (int i = 0; i < mat.rows(); i++) {
            for (int j = 0; j < mat.cols(); j++) {
                double[] pixel = src.get(i, j);
                pixel[0] *= value;
                for (int n = 0; n < size; n++) {
                    for (int m = 0; m < size; m++) {
                        if (n == 0 && m == 0)
                            continue;
                        if (i + n < mat.rows() && j + m < mat.cols()) {
                            pixel[0] += src.get(i + n, j + m)[0] * filterMatrix[n][m];
                        }
                        if (i + n < mat.rows() && j - m > 0) {
                            pixel[0] += src.get(i + n, j - m)[0] * filterMatrix[n][m];
                        }
                        if (i - n > 0 && j + m < mat.cols()) {
                            pixel[0] += src.get(i - n, j + m)[0] * filterMatrix[n][m];
                        }
                        if (i - n > 0 && j - m > 0) {
                            pixel[0] += src.get(i - n, j - m)[0] * filterMatrix[n][m];
                        }
                    }
                }
                pixel[0] /= filterSum;
                mat.put(i, j, pixel);
            }
        }
        return mat;
    }

    public static Mat Gray2BW(Mat src, double level) {
        level *= 255;
        Mat mat = src.clone();
        for (int i = 0; i < src.rows(); i++) {
            for (int j = 0; j < src.cols(); j++) {
                if (src.get(i, j)[0] < level) {
                    mat.put(i, j, 0);
                } else {
                    mat.put(i, j, 255);
                }
            }
        }
        return mat;
    }

    public static Mat BGR2BWWeight(Mat src, double level) {
        level *= 255;
        Mat mat = new Mat(src.rows(), src.cols(), CvType.CV_8UC1);
        for (int i = 0; i < src.rows(); i++) {
            for (int j = 0; j < src.cols(); j++) {
                double[] pixel = src.get(i, j); // length=3
                double grayPixel = pixel[0] * 0.1 + pixel[1] * 0.1 + pixel[2] * 0.8;
                if (grayPixel < level) {
                    mat.put(i, j, 0);
                } else {
                    mat.put(i, j, 255);
                }
            }
        }
        return mat;
    }
}
