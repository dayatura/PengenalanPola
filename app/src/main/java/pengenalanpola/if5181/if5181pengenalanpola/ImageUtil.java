package pengenalanpola.if5181.if5181pengenalanpola;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import static android.content.ContentValues.TAG;
import pengenalanpola.if5181.if5181pengenalanpola.VectorUtil;

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

    public static int[] getPixelColor(Bitmap bitmap, int x, int y) {
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

    public static void setPixelColor(Bitmap bitmap, int x, int y, int red, int green, int blue) {
        bitmap.setPixel(x, y, Color.argb(255, red, green, blue));
    }


    public static StringBuffer getSkeletonFeature(Bitmap bitmap, TextView textView) {
        int count;
        int[] border, border2;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = height * width;
        int[] pixels = new int[size];
        int[] pixelsa = new int[size];
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringPredict = new StringBuffer();
//        CharacterRecognition ChaRecog = new CharacterRecognition();

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsa, 0, width, 0, 0, width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((pixels[i + j * width] & 0x000000ff) != 255) {
                    border = Thinning.floodFill(pixels, i, j, width);

                    do {
                        count = Thinning.zhangSuenStep(pixelsa, border[0], border[1], border[2], border[3], width);
                    }
                    while (count != 0);

//                    stringBuffer.append(extractFeature(pixelsa, border[0], border[1], border[2], border[3], width));
                    border2 = getNewBorder(pixelsa, border[0], border[1], border[2], border[3], width);
                    SkeletonFeature sf = extractFeature(pixelsa, border2[0], border2[1], border2[2], border2[3], width);

////// program predict
//                    String className = ChaRecog.predicts(sf);


//                    stringBuffer.append(String.format("Prediksi:%s -> %d,%d,%d,%d,%d,%d,%d,%d,%d,%b,%b,%b,%b,%b,%b,%b,%b,%b\r\n",
//                            className,
//                            sf.endpoints.size(),
//                            sf.epHeading[0],
//                            sf.epHeading[1],
//                            sf.epHeading[2],
//                            sf.epHeading[3],
//                            sf.epHeading[4],
//                            sf.epHeading[5],
//                            sf.epHeading[6],
//                            sf.epHeading[7],
//                            sf.hTop, sf.hMid, sf.hBottom,
//                            sf.vLeft, sf.vMid, sf.vRight,
//                            sf.lTop, sf.lMid, sf.lBottom));
////////////////////////////

                    String epBerposisi = Arrays.deepToString(sf.ep).replace("[","").replace("]","").replace(" ", "");

                    stringBuffer.append(String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%s\r\n",

                            sf.endpoints.size(),
                            sf.intersection,
//                            sf.ep[0][0], sf.ep[0][1], sf.ep[0][2], sf.ep[0][3], sf.ep[0][4], sf.ep[0][5], sf.ep[0][6], sf.ep[0][7], sf.ep[0][8],
//                            sf.ep[1][0], sf.ep[1][1], sf.ep[1][2], sf.ep[1][3], sf.ep[1][4], sf.ep[1][5], sf.ep[1][6], sf.ep[1][7], sf.ep[1][8],
//                            sf.ep[2][0], sf.ep[2][1], sf.ep[2][2], sf.ep[2][3], sf.ep[2][4], sf.ep[2][5], sf.ep[2][6], sf.ep[2][7], sf.ep[2][8],
//                            sf.ep[3][0], sf.ep[3][1], sf.ep[3][2], sf.ep[3][3], sf.ep[3][4], sf.ep[3][5], sf.ep[3][6], sf.ep[3][7], sf.ep[3][8],
//                            sf.ep[4][0], sf.ep[4][1], sf.ep[4][2], sf.ep[4][3], sf.ep[4][4], sf.ep[4][5], sf.ep[4][6], sf.ep[4][7], sf.ep[4][8],
//                            sf.ep[5][0], sf.ep[5][1], sf.ep[5][2], sf.ep[5][3], sf.ep[5][4], sf.ep[5][5], sf.ep[5][6], sf.ep[5][7], sf.ep[5][8],
//                            sf.ep[6][0], sf.ep[6][1], sf.ep[6][2], sf.ep[6][3], sf.ep[6][4], sf.ep[6][5], sf.ep[6][6], sf.ep[6][7], sf.ep[6][8],
//                            sf.ep[7][0], sf.ep[7][1], sf.ep[7][2], sf.ep[7][3], sf.ep[7][4], sf.ep[7][5], sf.ep[7][6], sf.ep[7][7], sf.ep[7][8],

                            sf.hTop, sf.hMid, sf.hBottom,
                            sf.vLeft, sf.vMid, sf.vRight,
                            sf.lTop, sf.lMid, sf.lBottom,
                            epBerposisi));

//                    Log.d(TAG, sf.ep.toString());
                    stringPredict.append(preditHW(sf)+" ");
                }
            }
        }
        String[] fitur = {stringBuffer.toString()};
        FileUtil.write("fiturHW.csv", fitur);


//        textView.setText(stringBuffer);
        textView.setText(stringPredict);

//        return stringBuffer;
        return  stringPredict;

//        return Bitmap.createBitmap(pixelsa, width, height, bitmap.getConfig());
    }

    public static String preditHW(SkeletonFeature sf){

        double[] fitur = {
                sf.endpoints.size(),
                sf.intersection,
                sf.ep[0][0], sf.ep[0][1], sf.ep[0][2], sf.ep[0][3], sf.ep[0][4], sf.ep[0][5], sf.ep[0][6], sf.ep[0][7], sf.ep[0][8],
                sf.ep[1][0], sf.ep[1][1], sf.ep[1][2], sf.ep[1][3], sf.ep[1][4], sf.ep[1][5], sf.ep[1][6], sf.ep[1][7], sf.ep[1][8],
                sf.ep[2][0], sf.ep[2][1], sf.ep[2][2], sf.ep[2][3], sf.ep[2][4], sf.ep[2][5], sf.ep[2][6], sf.ep[2][7], sf.ep[2][8],
                sf.ep[3][0], sf.ep[3][1], sf.ep[3][2], sf.ep[3][3], sf.ep[3][4], sf.ep[3][5], sf.ep[3][6], sf.ep[3][7], sf.ep[3][8],
                sf.ep[4][0], sf.ep[4][1], sf.ep[4][2], sf.ep[4][3], sf.ep[4][4], sf.ep[4][5], sf.ep[4][6], sf.ep[4][7], sf.ep[4][8],
                sf.ep[5][0], sf.ep[5][1], sf.ep[5][2], sf.ep[5][3], sf.ep[5][4], sf.ep[5][5], sf.ep[5][6], sf.ep[5][7], sf.ep[5][8],
                sf.ep[6][0], sf.ep[6][1], sf.ep[6][2], sf.ep[6][3], sf.ep[6][4], sf.ep[6][5], sf.ep[6][6], sf.ep[6][7], sf.ep[6][8],
                sf.ep[7][0], sf.ep[7][1], sf.ep[7][2], sf.ep[7][3], sf.ep[7][4], sf.ep[7][5], sf.ep[7][6], sf.ep[7][7], sf.ep[7][8],
                sf.hTop, sf.hMid, sf.hBottom,
                sf.vLeft, sf.vMid, sf.vRight,
                sf.lTop, sf.lMid, sf.lBottom
        };

//        String[] karakters = FileUtil.load("fiturHW.csv");

        String[] hasil = { /*"A",*/"S","H","R"};
        double[][] characters = {/*{3,26,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},*/
                                 {2,92,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0},
                                 {6,7,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                                 {5,115,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}};

        double probs[] = new double[hasil.length];

        for (int i = 0; i < hasil.length; i++){
            double[] chara = characters[i];
            probs[i] = VectorUtil.similarity(fitur,chara);
            Log.d(TAG, "similiarity: "+probs[i]);
        }


        /// arg max for result


        int maxAt = 0;

        for (int i = 0; i < probs.length; i++) {
            maxAt = probs[i] > probs[maxAt] ? i : maxAt;
        }

        return "\n"+hasil[maxAt] +" (" + probs[maxAt]+")" ;

    }

    public static String getPredictHW(double[] probs){
        String[] hasil = {"A","H","I"};

        int maxAt = 0;

        for (int i = 0; i < probs.length; i++) {
            maxAt = probs[i] > probs[maxAt] ? i : maxAt;
        }

        return hasil[maxAt];
    }

    ///// feature lama

//    public static StringBuffer extractFeature(int[] pixels, int xmin, int ymin, int xmax, int ymax, int width) {
//        int next, i, j, neighbourCount;
//
//        int p = 0;
//        int endCount = 0;
//        int[][] moves = new int[8][8];
//        boolean end = false;
//        Queue<Integer> queue = new LinkedList<>();
//
//        j = ymin;
//        while (p == 0 && j <= ymax) {
//            i = xmin;
//            while (p == 0 && i <= xmax) {
//                if ((pixels[i + j * width] & 0x000000ff) == 0)
//                    p = i + j * width;
//
//                i++;
//            }
//            j++;
//        }
//
//        if (p != 0) {
//            next = p;
//            int before = 2;
//            int temp = 0;
//            while (!end) {
//                int[] neighbours = {
//                        p - width,
//                        p - width + 1,
//                        p + 1,
//                        p + width + 1,
//                        p + width,
//                        p + width - 1,
//                        p - 1,
//                        p - width - 1
//                };
//
//                //Log.i("pixel", "" + p);
//
//                pixels[p] = pixels[p] | 0x0000ff00;
//                neighbourCount = 0;
//
//                for (i = 0; i < 8; i++) {
//                    if ((pixels[neighbours[i]] & 0x000000ff) == 0) {
//                        neighbourCount++;
//
//                        if ((pixels[neighbours[i]] & 0x0000ff00) >> 8 == 0) {
//                            moves[before][i]++;
//                            if (next == p) {
//                                next = neighbours[i];
//                                temp = i;
//                            } else {
//                                queue.offer(neighbours[i]);
//                            }
//                        }
//                    }
//                }
//
//                if (neighbourCount == 1) endCount++;
//
//                if (next != p) {
//                    p = next;
//                    before = temp;
//                } else {
//                    while (!queue.isEmpty() && (pixels[queue.peek()] & 0x0000ffff) != 0) {
//                        queue.poll();
//                    }
//
//                    if (queue.isEmpty()) {
//                        end = true;
//                    } else {
//                        p = queue.poll();
//                        next = p;
//                    }
//                }
//            }
//        }
//
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append(String.format("End Count : %d\r\n", endCount));
//        stringBuffer.append("Moves :\r\n");
//        for (int a = 0; a < 8; a++) {
//            for (int b = 0; b < 8; b++) {
//                stringBuffer.append(String.format("%d %d | %d\r\n", a, b, moves[a][b]));
//            }
//        }
//
//        return stringBuffer;
//    }


    public static SkeletonFeature extractFeature(int[] pixels, int xmin, int ymin, int xmax, int ymax, int width) {

        SkeletonFeature sf = new SkeletonFeature();

        // titik ujung
        List<Integer> endpoints = new ArrayList<>();

        int intersection = 0;

//        Log.d(TAG, "extractFeature: huruf baru");
        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                int p = i + j * width;

                if ((pixels[p] & 0x00ffffff) != 0x00ffffff) {
                    int[] neighbour = {
                            p - width,
                            p - width + 1,
                            p + 1,
                            p + width + 1,
                            p + width,
                            p + width - 1,
                            p - 1,
                            p - width - 1
                    };
                    int black = 0;
                    int index = -1;

                    for (int k = 0; k < neighbour.length; k++) {
                        if ((pixels[neighbour[k]] & 0x00ffffff) != 0x00ffffff) {
                            black++;
                            index = k;
                        }
                    }


                    int heading = (index + 4) % 8;
                    if (black == 1) {
                        sf.epHeading[heading]++;
                        endpoints.add(heading);

                        //titik berarah
                        int gridX = (xmax-xmin)/3 != 0 ? (xmax-xmin)/3 +1 : 1;
                        int gridY = (ymax-ymin)/3 != 0 ? (ymax-ymin)/3 +1 : 1;

                        int position = (1+(((i - xmin) / gridX)) + (((j - ymin) / gridY)) * 3) - 1;

                        sf.ep[heading][position]++;

                    } else if (black > 3) intersection++;

                }
            }
        }


//        Log.d(TAG, "jumlah cross = " + intersection);
        sf.intersection = intersection;
        sf.endpoints = endpoints;

        // garis tegak
        int[] h = new int[ymax - ymin + 1];
        int[] v = new int[xmax - xmin + 1];

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                int p = i + j * width;

                if ((pixels[p] & 0x00ffffff) != 0x00ffffff) {
                    h[j - ymin]++;
                    v[i - xmin]++;
                }
            }
        }

        int[] hsum = new int[3];
        for (int i = 0; i < h.length; i++) {
            if (h[i] > (xmax - xmin + 1) / 2 && h[i] > 1) {
                if (i < (ymax - ymin) * 4 / 10) {
                    hsum[0]++;
                } else if (i < (ymax - ymin) * 6 / 10) {
                    hsum[1]++;
                } else {
                    hsum[2]++;
                }
            }
        }

        int[] vsum = new int[3];
        for (int i = 0; i < v.length; i++) {
            if (v[i] > (ymax - ymin + 1) / 2 && v[i] > 0) {
                if (i < (xmax - xmin) * 4 / 10) {
                    vsum[0]++;
                } else if (i < (xmax - xmin) * 6 / 10) {
                    vsum[1]++;
                } else {
                    vsum[2]++;
                }
            }
        }

        /// fitur dalam boolean
//        sf.hTop = hsum[0] > 0;
//        sf.hMid = hsum[1] > 0;
//        sf.hBottom = hsum[2] > 0;
//        sf.vLeft = vsum[0] > 0;
//        sf.vMid = vsum[1] > 0;
//        sf.vRight = vsum[2] > 0;

        ////fitur dalam int
        sf.hTop = hsum[0];
        sf.hMid = hsum[1];
        sf.hBottom = hsum[2];
        sf.vLeft = vsum[0];
        sf.vMid = vsum[1];
        sf.vRight = vsum[2];

        // lubang
        int[] hole = new int[3];
        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                int p = i + j * width;

                if ((pixels[p] & 0x00ffffff) == 0x00ffffff) {
                    int midpoint = holeFloodFill(pixels, xmin - 1, ymin - 1, xmax + 1, ymax + 1, width, p);

                    if (midpoint / width < (ymax - ymin + 2) * 4 / 10 + (ymin - 1)) {
                        hole[0]++;
                    } else if (midpoint / width < (ymax - ymin + 2) * 6 / 10 + (ymin - 1)) {
                        hole[1]++;
                    } else {
                        hole[2]++;
                    }
                }
            }
        }

        //fitur dalam boolean
//        sf.lTop = hole[0] > 0;
//        sf.lMid = hole[1] - 1 > 0;
//        sf.lBottom = hole[2] > 0;

        //fitur dalam int
        sf.lTop = hole[0];
        sf.lMid = hole[1] - 1;
        sf.lBottom = hole[2];



        return sf;
    }

    public static int[] getNewBorder(int[] pixels, int xmin, int ymin, int xmax, int ymax, int width) {
        int pxmin = (xmax + xmin) / 2;
        int pxmax = (xmax + xmin) / 2;
        int pymin = (ymax + ymin) / 2;
        int pymax = (ymax + ymin) / 2;
        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                int p = i + j * width;
                if ((pixels[p] & 0x00ffffff) == 0x00000000) {
                    if (p % width < pxmin) pxmin = p % width;
                    if (p % width > pxmax) pxmax = p % width;
                    if (p / width < pymin) pymin = p / width;
                    if (p / width > pymax) pymax = p / width;
                }
            }
        }
        return new int[]{pxmin, pymin, pxmax, pymax};
    }

    private static int holeFloodFill(int[] pixels, int xmin, int ymin, int xmax, int ymax, int width, int p) {

        int pxmin = p % width;
        int pxmax = p % width;
        int pymin = p / width;
        int pymax = p / width;
        Queue<Integer> queue = new ArrayDeque<>();

        queue.offer(p);

        while (!queue.isEmpty()) {
            int pt = queue.poll();

            if ((pixels[pt] & 0x00ffffff) == 0x00ffffff
                    && xmin <= (pt % width)
                    && (pt % width) <= xmax
                    && ymin <= (pt / width)
                    && (pt / width) <= ymax) {
                pixels[pt] = (pixels[pt] & 0xff000000);

                if (pt % width < pxmin) pxmin = pt % width;
                if (pt % width > pxmax) pxmax = pt % width;
                if (pt / width < pymin) pymin = pt / width;
                if (pt / width > pymax) pymax = pt / width;

                queue.offer(pt - width);
                queue.offer(pt + 1);
                queue.offer(pt + width);
                queue.offer(pt - 1);
            }
        }

        return (pxmax + pxmin) / 2 + (pymax + pymin) / 2 * width;
    }

    public static Bitmap edgeDetection(Bitmap source) {
        Bitmap result = source.copy(source.getConfig(), true);

        result = ImageUtil.getSmoothingImage(result)[0];
        int height = result.getHeight();
        int width = result.getWidth();

        int treshold = 70;

        int [][][] sobel = {{{1, 0, -1},
                             {2, 0, -2},
                             {1, 0, -1}},

                            {{1, 2, 1},
                              {0, 0, 0},
                              {-1, -2, -1}}};



        int [][][] filter = sobel;

        result = ImageUtil.getGrayscaleImage(result);

        int [][] Gx = filter[0];
        int [][] Gy = filter[1];

        for (int i=0; i<height-2; i++){
            for (int j=0; j<width-2; j++){

                int [][] subImage = new int[3][3];
                int sumX = 0;
                int sumY = 0;
                for (int m = 0; m<3; m++){
                    for (int n = 0; n<3; n++){
                        int pixelColor = ImageUtil.getPixelColor(source,m+j,n+i)[0];
//                        sumX += Math.pow(pixelColor * Gx[m][n],2);
//                        sumY += Math.pow(pixelColor * Gy[m][n],2);
                        sumX += pixelColor * Gx[m][n];
                        sumY += pixelColor * Gy[m][n];

                    }
                }


                int sum = (int) Math.sqrt(Math.pow(sumX,2)+Math.pow(sumY,2));
                sum = sum < 70 ? 0 : sum;
//                sum = sumY;
                ImageUtil.setPixelColor(result,j+1,i+1, sum, sum, sum);
//                int [][] subImage = {{},{},{}};
//                int S1 = VectorUtil.Sum(VectorUtil.cross(Gx,subImage));
            }
        }

        return result;
    }
}