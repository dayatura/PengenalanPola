package pengenalanpola.if5181.if5181pengenalanpola;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class Face {

    public static Bitmap detectFace(Bitmap image) {
        Bitmap result = image.copy(image.getConfig(), true);

//        result = ImageUtil.getGrayscaleImage(result);
        result = ImageUtil.getSmoothingImage(result)[0];

        int[][] facePosition = getFacePosition(result);
        result = ImageUtil.edgeDetection(result);
        result = drawBoundary(result, facePosition);

        return result;
    }

    private static Bitmap drawBoundary(Bitmap result, int[][] facePosition) {
        int x, y;

        //atas
        y = facePosition[0][1];
        for(int i = facePosition[0][0]; i <= facePosition[1][0]; i++){
            x = i;
            ImageUtil.setPixelColor(result, x, y, 255, 0, 0);
        }
        //bawah
        y = facePosition[1][1];
        for(int i = facePosition[0][0]; i <= facePosition[1][0]; i++){
            x = i;
            ImageUtil.setPixelColor(result, x, y, 255, 0, 0);
        }
        //kiri
        x = facePosition[0][0];
        for(int i = facePosition[0][1]; i <= facePosition[1][1]; i++){
            y = i;
            ImageUtil.setPixelColor(result, x, y, 255, 0, 0);
        }
        //kanan
        x = facePosition[1][0];
        for(int i = facePosition[0][1]; i <= facePosition[1][1]; i++){
            y = i;
            ImageUtil.setPixelColor(result, x, y, 255, 0, 0);
        }

        return result;
    }

    private static int[][] getFacePosition(Bitmap result) {
        int height = result.getHeight();
        int width = result.getWidth();

        int[][] position = {{135,194},{327,442}};
        position = new int[][]{{width / 3, height / 3}, {2 * width / 3, 2 * height / 3}};
        Log.d(TAG, position[0][0] + " " +position[0][1] + " " +position[1][0] + " " +position[1][1] );
        int colorRange[]= {0,0,0};
        for (int i = position[0][0]; i<position[1][0];i++){
            for (int j=position[0][1]; j<position[1][1];j++){
                int color [] = ImageUtil.getPixelColor(result,i,j);
                colorRange[0]+= color[0];
                colorRange[1]+= color[1];
                colorRange[2]+= color[2];
            }
        }
        int numPixels= (position[1][0] - position[0][0]) * (position[1][1] - position[0][1]);
        colorRange[0] /= numPixels; //176
        colorRange[1] /= numPixels; //135
        colorRange[2] /= numPixels; //126

        for (int i=0;i<width;i++){
            for (int j=0; j<height; j++){
                int color[] = ImageUtil.getPixelColor(result,i,j);
                if (    color[0] < colorRange[0]+10 && color[0] > colorRange[0]-10 &&
                        color[1] < colorRange[1]+10 && color[1] > colorRange[2]-10 &&
                        color[2] < colorRange[2]+10 && color[2] > colorRange[2]-10){
                    if ( i < position[0][0] ) position[0][0] = i;
                    if ( i > position[1][0] ) position[1][0] = i;
                    if ( j < position[0][1] ) position[0][1] = j;
                    if ( j > position[1][1] ) position[1][1] = j;
                }

            }
        }

//        Log.d(TAG, position[0][0] + " " +position[0][1] + " " +position[1][0] + " " +position[1][1] );
//        Log.d(TAG, colorRange[0] + " " +colorRange[1] + " " +colorRange[2] );
        return position;
    }


}
