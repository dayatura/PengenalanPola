package pengenalanpola.if5181.if5181pengenalanpola;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class ImageUtil {

    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;
    public static final int GRAYSCALE = 3;

    public static Bitmap getBinaryImage(Bitmap bitmap, int threshold) {
        Bitmap result = bitmap.copy(bitmap.getConfig(), true);
        int[] color;

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);

                if (color[GRAYSCALE] < threshold) {
                    setPixelColor(result, i, j, 0, 0, 0);
                } else {
                    setPixelColor(result, i, j, 255, 255, 255);
                }
            }
        }

        return result;
    }

    public static Bitmap getGrayscaleImage(Bitmap bitmap) {
        Bitmap result = bitmap.copy(bitmap.getConfig(), true);
        int[] color;

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                setPixelColor(result, i, j, color[GRAYSCALE], color[GRAYSCALE], color[GRAYSCALE]);
            }
        }

        return result;
    }

    public static Bitmap[] getTransformedImage(Bitmap bitmap) {
        Bitmap resultA = bitmap.copy(bitmap.getConfig(), true);
        Bitmap resultB = bitmap.copy(bitmap.getConfig(), true);
        int sum = bitmap.getWidth() * bitmap.getHeight();
        int[] color;
        int[][] count = getPixelCount(bitmap);
        int[][] lookup = new int[4][256];

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 4; j++) {
                if (i > 0)
                    count[j][i] = count[j][i] + count[j][i - 1];

                lookup[j][i] = count[j][i] * 255 / sum;
            }
        }

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                setPixelColor(resultA, i, j, lookup[RED][color[RED]], lookup[GREEN][color[GREEN]], lookup[BLUE][color[BLUE]]);
                setPixelColor(resultB, i, j, lookup[GRAYSCALE][color[GRAYSCALE]], lookup[GRAYSCALE][color[GRAYSCALE]], lookup[GRAYSCALE][color[GRAYSCALE]]);
            }
        }

        return new Bitmap[]{resultA, resultB};
    }

    public static Bitmap[] getTransformedImage(Bitmap bitmap, int a, int b, int c) {
        Bitmap resultA = bitmap.copy(bitmap.getConfig(), true);
        Bitmap resultB = bitmap.copy(bitmap.getConfig(), true);
        int[] color;
        int[] count = new int[256];
        int[] lookup = new int[256];

        for (int i = 0; i < 256; i++) {
            if (i < b)
                count[i] = a + i * (255 - a) / b;
            else if (i > b)
                count[i] = c + (255 - i) * (255 - c) / (255 - b);
            else
                count[i] = 255;
        }

        for (int i = 1; i < 256; i++) {
            count[i] = count[i] + count[i - 1];
        }

        for (int i = 0; i < 256; i++) {
            lookup[i] = count[i] * 255 / count[255];
        }

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                setPixelColor(resultA, i, j, lookup[color[RED]], lookup[color[GREEN]], lookup[color[BLUE]]);
                setPixelColor(resultB, i, j, lookup[color[GRAYSCALE]], lookup[color[GRAYSCALE]], lookup[color[GRAYSCALE]]);
            }
        }

        return new Bitmap[]{resultA, resultB};
    }

    public static Bitmap[] getSmoothingImage(Bitmap bitmap) {
        Bitmap resultA = bitmap.copy(bitmap.getConfig(), true);
        Bitmap resultB = bitmap.copy(bitmap.getConfig(), true);
        int count;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] color;
        int[] sum = new int[4];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                count = 0;
                sum[RED] = sum[GREEN] = sum[BLUE] = sum[GRAYSCALE] = 0;
                for (int a = -1; a <= 1; a++) {
                    for (int b = -1; b <= 1; b++) {
                        if (0 <= i + a && i + a < width && 0 <= j + b && j + b < height) {
                            color = getPixelColor(bitmap, i + a, j + b);
                            sum[RED] = sum[RED] + color[RED];
                            sum[GREEN] = sum[GREEN] + color[GREEN];
                            sum[BLUE] = sum[BLUE] + color[BLUE];
                            sum[GRAYSCALE] = sum[GRAYSCALE] + color[GRAYSCALE];
                            count++;
                        }
                    }
                }
                setPixelColor(resultA, i, j, sum[RED] / count, sum[GREEN] / count, sum[BLUE] / count);
                setPixelColor(resultB, i, j, sum[GRAYSCALE] / count, sum[GRAYSCALE] / count, sum[GRAYSCALE] / count);
            }
        }

        return new Bitmap[]{resultA, resultB};
    }

    public static String[] detectNumber(Bitmap bitmap) {
        Bitmap image = bitmap.copy(bitmap.getConfig(), true);
        int[] color;
        String result = "";
        String chains = "";

        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                color = getPixelColor(image, i, j);

                if (color[GRAYSCALE] == 0) {
                    String chain = getChainCode(image, i, j);
                    chains = chains + chain + "\n\n";
                    result = result + translate(chain) + "\t";
                    Log.i("chain", chain);
                    Log.i("num", "" + translate(chain));
                    floodFill(image, i, j);
                }
            }
        }

        //Bitmap result = bitmap.copy(bitmap.getConfig(), true);

        return new String[] {result, chains};
    }

    public static String[] detectNumber2(Bitmap bitmap) {

        int[] color;
        String chain;

        Bitmap image = bitmap.copy(bitmap.getConfig(), true);
        StringBuilder result = new StringBuilder();
        StringBuilder chains = new StringBuilder();

        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                color = getPixelColor(image, i, j);

                if (color[GRAYSCALE] == 0) {
                    chain = getChainCode(image, i, j);
                    chains.append(String.format("%s\n\n", chain));
                    result.append(String.format("%d \t", ChainCodeUtil.translate(chain)));

                    floodFill(image, i, j);
                }
            }
        }

        return new String[]{result.toString(), chains.toString()};
    }

    public static int[][] getPixelCount(Bitmap bitmap) {
        int[] color;
        int[][] count = new int[4][256];

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);
                count[RED][color[RED]]++;
                count[GREEN][color[GREEN]]++;
                count[BLUE][color[BLUE]]++;
                count[GRAYSCALE][color[GRAYSCALE]]++;
            }
        }

        return count;
    }

    // private methods
    private static double getVectorLength(double[] vector) {
        double sum = 0;

        for (int i = 0; i < 8; i++) {
            sum = sum + vector[i] * vector[i];
        }

        return Math.sqrt(sum);
    }

    private static int translate(String chain) {
        double[][] ratio = {
                {0.250, 0.075, 0.098, 0.075, 0.250, 0.075, 0.098, 0.075},
                {0.329, 0.079, 0.074, 0.000, 0.361, 0.053, 0.095, 0.005},
                {0.108, 0.161, 0.172, 0.044, 0.134, 0.112, 0.243, 0.022},
                {0.143, 0.098, 0.158, 0.105, 0.132, 0.098, 0.169, 0.094},
                {0.186, 0.146, 0.090, 0.005, 0.328, 0.005, 0.232, 0.005},
                {0.161, 0.060, 0.218, 0.067, 0.147, 0.070, 0.211, 0.063},
                {0.189, 0.086, 0.133, 0.103, 0.163, 0.086, 0.159, 0.077},
                {0.211, 0.091, 0.201, 0.000, 0.201, 0.105, 0.183, 0.004},
                {0.175, 0.095, 0.132, 0.101, 0.164, 0.101, 0.132, 0.095},
                {0.168, 0.090, 0.147, 0.086, 0.181, 0.086, 0.142, 0.095}
        };
        double max = 0;
        double[] sum = new double[8];
        int number = 0;

        for (int i = 0; i < chain.length(); i++) {
            sum[Character.getNumericValue(chain.charAt(i))]++;
        }

        for (int i = 0; i < 8; i++) {
            sum[i] = sum[i] / chain.length();
        }

        for (int i = 0; i < 10; i++) {
            double res = 0;
            for (int j = 0; j < 8; j++) {
                res = res + ratio[i][j] * sum[j];
            }
            res = res / getVectorLength(ratio[i]) / getVectorLength(sum);
            if (res > max) {
                max = res;
                number = i;
            }
        }

        return number;
    }

    private static int[] getPixelColor(Bitmap bitmap, int x, int y) {
        int pixel, red, green, blue, grayscale;

        pixel = bitmap.getPixel(x, y);
        red = Color.red(pixel);
        green = Color.green(pixel);
        blue = Color.blue(pixel);
        grayscale = (red + green + blue) / 3;

        return new int[]{red, green, blue, grayscale};
    }

    private static int[] getNextPixel(Bitmap bitmap, int x, int y, int source) {
        int a, b, target = source;
        int[][] points = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};

        do {
            target = (target + 1) % 8;
            a = x + points[target][0];
            b = y + points[target][1];
        }
        while (getPixelColor(bitmap, a, b)[GRAYSCALE] == 255);

        //Log.i("loc", String.format("%d %d %d", a, b, target));

        return new int[]{a, b, target};
    }

    private static String getChainCode(Bitmap bitmap, int x, int y) {
        int a = x;
        int b = y;
        int[] next;
        int source = 6;
        String chain = "";

        do {
            next = getNextPixel(bitmap, a, b, source);
            a = next[0];
            b = next[1];
            source = (next[2] + 4) % 8;
            chain = chain + next[2];
        }
        while (!(a == x && b == y));

        return chain;
    }

    private static void floodFill(Bitmap bitmap, int x, int y) {
        int[] color = getPixelColor(bitmap, x, y);

        if (color[GRAYSCALE] != 255) {
            setPixelColor(bitmap, x, y, 255, 255, 255);
            floodFill(bitmap, x - 1, y);
            floodFill(bitmap, x + 1, y);
            floodFill(bitmap, x, y - 1);
            floodFill(bitmap, x, y + 1);
        }
    }

    private static void setPixelColor(Bitmap bitmap, int x, int y, int red, int green, int blue) {
        bitmap.setPixel(x, y, Color.argb(255, red, green, blue));
    }
}
