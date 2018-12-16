package pengenalanpola.if5181.if5181pengenalanpola;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import static android.content.ContentValues.TAG;

public class Face {


    public static List<String> detectFace(Bitmap image) {
        Bitmap result = image.copy(image.getConfig(), true);

        List<String> nameFace = new ArrayList<>();
        int[][] dayatFaceCP = {{15,28},{15,43},{32,28},{32,43},{53,28},{53,43},{66,28},{66,43},{51,67},{36,67},{58,79},{37,79},{47,79},{47,88}};

        double newWidth = 200.0;
        result = Bitmap.createScaledBitmap(
                result, (int)newWidth, (int)(result.getHeight()*(newWidth/result.getWidth())),true);

//        result = ImageUtil.getGrayscaleImage(result);

//        Log.d(TAG, "detectFace: getSmoothImage");
//        result = ImageUtil.getSmoothingImage(result)[0];

        List<int[][]> facePositions = getFacePositionMulti(result);

        //prewit atau sobel
//        result = ImageUtil.edgeDetection(result);

        //gausian
//        result = ImageUtil.edgeDetection2(result);

        for (int[][]facePosition:facePositions
             ) {

//            Log.d(TAG, "detectFace: face posisiton " + facePosition[0][0] + " " + facePosition[0][1]);
            result = drawBoundary(result, facePosition);
            List<int[]> controlPoint = getFaceAtributes(result, facePosition);

            for (int i=0; i<controlPoint.size();i++) {
                controlPoint.set(i, new int[]{controlPoint.get(i)[0] - facePosition[0][0], controlPoint.get(i)[1] - facePosition[0][1]});
                ImageUtil.setPixelColor(result,controlPoint.get(i)[0],controlPoint.get(i)[1],255,0,255);
//                Log.d(TAG, "detectFace: control point " +i + " loc -> " + (controlPoint.get(i)[0] ) + " " + (controlPoint.get(i)[1] ) + " -------" );
//                Log.d(TAG, "detectFace: " + (controlPoint.get(i)[0] - facePosition[0][0]) + " " + (controlPoint.get(i)[1] - facePosition[0][1]) );

            }

            nameFace.add(recognizeFace(controlPoint, dayatFaceCP));

//  List<int[][]> faceAtributesPositions = getFaceAtributes(result, facePosition);

//            //kalau mau munculin boundary
//            for (int[][]faceAtributesPosition:faceAtributesPositions){
//                result = drawBoundary(result,faceAtributesPosition);
//            }
//            ImageUtil.setPixelColor(result,facePosition[0][0],facePosition[0][1],0,255,0);
        }

        return nameFace;
    }

    private static String recognizeFace(List<int[]> controlPoint, int[][] dayatFaceCP) {
        String name = "Unknown";

        int[][] conPoint = controlPoint.toArray(new int[controlPoint.size()][controlPoint.get(0).length]);

        int similiarity = 99999;

        if (conPoint.length == dayatFaceCP.length) {
            similiarity = 0;
            for (int i = 0; i < conPoint.length; i++) {
                Log.d(TAG, "cp " + i + " " + conPoint[i][0] + " " + dayatFaceCP[i][0] + " " + conPoint[i][1] + " " + dayatFaceCP[i][1]);
                int x = conPoint[i][0] - dayatFaceCP[i][0];
                int y = conPoint[i][1] - dayatFaceCP[i][1];
                similiarity += x + y;
            }
        }
//        Log.d(TAG, "recognizeFace: similiarity: "+ similiarity);
        if (similiarity < 100) name = "Hidayaturrahman";

        return name;
    }

    private static List<int[]> getFaceAtributes(Bitmap image, int[][] facePosition) {
//        List<int[][]> faceAtributes = new ArrayList<>();
//
//        //eyes
//        int[][][]eyesPoss = detectEyes(image,facePosition);
//        if (eyesPoss != null) {
//            for (int[][]eyesPos:eyesPoss
//                    ) {
//                faceAtributes.add(eyesPos);
//            }
//        }
//
//        //nose
//        faceAtributes.add(detectNose(image, facePosition));
//
//        //lips
//        faceAtributes.add(detectLips(image, facePosition));
//          return faceAtributes;

        List<int[]> controlPoint = new ArrayList<>();
        int[][] eyes = detectEyes(image,facePosition);
        int[][] noses = detectNose(image,facePosition);
        int[][] lips = detectLips(image, facePosition);

        if (eyes != null)
            for (int[]eye:eyes) controlPoint.add(eye);
        if (noses!= null)
            for (int[]nose:noses) controlPoint.add(nose);
        if (lips!= null)
            for (int[]lip:lips) controlPoint.add(lip);



        return controlPoint;
    }

    private static int[][] detectLips(Bitmap image, int[][] facePosition) {
        int lipsTreshold = 110;

        List<Integer> posX = new ArrayList<>();
        List<Integer> posY = new ArrayList<>();

        for(int x = facePosition[0][0]+(facePosition[1][0]-facePosition[0][0])/4; x < facePosition[1][0] - (facePosition[1][0]-facePosition[0][0])/4; x++){
            for (int y = (int)(facePosition[1][1]-(facePosition[1][1]-facePosition[0][1])/2.1); y < facePosition[1][1]-(facePosition[1][1]-facePosition[0][1])/3.2; y++){
                int[] pixelColor = ImageUtil.getPixelColor(image,x,y);


                //titik prediksi
                if (pixelColor[0] > 150
                        && pixelColor[1] < 110
                        && pixelColor[2] > 100){
//                    ImageUtil.setPixelColor(image,x,y,0,255,255);
                    posX.add(x);
                    posY.add(y);
                }

                //boundary
//                if (pixelColor[0] < lipsTreshold
//                        && pixelColor[1] < lipsTreshold
//                        && pixelColor[2] < lipsTreshold){
//                    ImageUtil.setPixelColor(image,x,y,0,255,255);
//                    posX.add(x);
//                    posY.add(y);
//                }

            }
        }
        if (!(posX.isEmpty() && posY.isEmpty())){
            int maxX = Collections.max(posX);
            int minX = Collections.min(posX);
            int maxY = Collections.max(posY);
            int minY = Collections.min(posY);

            //control point
            ImageUtil.setPixelColor(image,maxX,minY + (maxY-minY)/2,0,255,0);
            ImageUtil.setPixelColor(image,minX,minY + (maxY-minY)/2,0,255,0);
            ImageUtil.setPixelColor(image,minX + (maxX-minX)/2,minY + (maxY-minY)/2,0,255,0);
            ImageUtil.setPixelColor(image,minX + (maxX-minX)/2, maxY,0,255,0);

            return new int[][] {{maxX,      minY + (maxY-minY)/2},
                    {minX,                  minY + (maxY-minY)/2},
                    {minX + (maxX-minX)/2,  minY + (maxY-minY)/2},
                    {minX + (maxX-minX)/2,  maxY}};

            //area
//            return new int[][] {{minX, minY},{maxX,maxY}};
        }else return null;
    }

    private static int[][] detectNose(Bitmap image, int[][] facePosition) {

        List<Integer> posX = new ArrayList<>();
        List<Integer> posY = new ArrayList<>();

        int eyesTreshold = 130;

        for(int x = facePosition[0][0]+(facePosition[1][0]-facePosition[0][0])/3; x < facePosition[1][0] - (facePosition[1][0]-facePosition[0][0])/3; x++){
            for (int y = (int)(facePosition[0][1] + (facePosition[1][1]-facePosition[0][1])/2.5); y < facePosition[1][1]-(facePosition[1][1]-facePosition[0][1])/2.1; y++){
                int[] pixelColor = ImageUtil.getPixelColor(image,x,y);
//                Log.d(TAG, "getFaceAtributes: pixel color"+pixelColor[0]+" "+pixelColor[1]+" "+ pixelColor[2]);
                if (pixelColor[0] < eyesTreshold
                        && pixelColor[1] < eyesTreshold
                        && pixelColor[2] < eyesTreshold){
//                    ImageUtil.setPixelColor(image,x,y,255,9,255);
                    posX.add(x);
                    posY.add(y);
                }
            }
        }
        if (!(posX.isEmpty() && posY.isEmpty())){
            int maxX = Collections.max(posX);
            int minX = Collections.min(posX);
            int maxY = Collections.max(posY);
            int minY = Collections.min(posY);


            //control point
            ImageUtil.setPixelColor(image,maxX,minY + (maxY-minY)/2,0,255,0);
            ImageUtil.setPixelColor(image,minX,minY + (maxY-minY)/2,0,255,0);

            return new int[][] {{maxX,minY + (maxY-minY)/2},
                    {minX,minY + (maxY-minY)/2}};

            //area
//            return new int[][] {{minX, minY},{maxX,maxY}};
        }else return null;

    }

    private static int[][] detectEyes(Bitmap image, int[][] facePosition) {

        List<Integer> posX = new ArrayList<>();
        List<Integer> posY = new ArrayList<>();

        int eyesTreshold = 100;

        for(int x = facePosition[0][0]+(facePosition[1][0]-facePosition[0][0])/7; x < facePosition[1][0] - (facePosition[1][0]-facePosition[0][0])/6; x++){
            for (int y = facePosition[0][1] + (facePosition[1][1]-facePosition[0][1])/6; y < facePosition[1][1]-(facePosition[1][1]-facePosition[0][1])*3/5; y++){

                int[] pixelColor = ImageUtil.getPixelColor(image,x,y);
//                Log.d(TAG, "getFaceAtributes: pixel color"+pixelColor[0]+" "+pixelColor[1]+" "+ pixelColor[2]);
                if (pixelColor[0] < eyesTreshold
                        && pixelColor[1] < eyesTreshold
                        && pixelColor[2] < eyesTreshold){

//                    ImageUtil.setPixelColor(image,x,y,0,255,0);
                    posX.add(x);
                    posY.add(y);
                }
            }
        }

        if (!(posX.isEmpty() && posY.isEmpty())){
            int maxX = Collections.max(posX);
                int minX = Collections.min(posX);
                int maxY = Collections.max(posY);
                int minY = Collections.min(posY);
                int leftX = minX;
                int rightX = maxX;


                for (int newX :
                        posX) {
                    if (newX > leftX && newX < (maxX + minX) / 2) leftX = newX;
                    if (newX < rightX && newX > (maxX + minX) / 2) rightX = newX;
                }

            //control point
                for (int[][] eyes: new int[][][]{{{minX, minY},{leftX, maxY}},{{rightX,minY},{maxX,maxY}}}) {
                    ImageUtil.setPixelColor(image, eyes[0][0], eyes[0][1], 0, 255, 0);
                    ImageUtil.setPixelColor(image, eyes[0][0], eyes[1][1], 0, 255, 0);
                    ImageUtil.setPixelColor(image, eyes[1][0], eyes[0][1], 0, 255, 0);
                    ImageUtil.setPixelColor(image, eyes[1][0], eyes[1][1], 0, 255, 0);
                }

                return new int[][]{{minX,minY},{minX,maxY},{leftX,minY},{leftX,maxY},
                        {rightX,minY},{rightX,maxY},{maxX,minY},{maxX,maxY}};


//                return new int[][][] {{{minX, minY},{leftX, maxY}},{{rightX,minY},{maxX,maxY}}};
        }else
            return null;

    }



    private static Bitmap drawBoundary(Bitmap result, int[][] position) {
        int x, y;

//        for (int[][]facePosition:facePositions
//             ) {

            //atas
            y = position[0][1];
            for(int i = position[0][0]; i <= position[1][0]; i++){
                x = i;
                ImageUtil.setPixelColor(result, x, y, 255, 0, 0);
            }
            //bawah
            y = position[1][1];
            for(int i = position[0][0]; i <= position[1][0]; i++){
                x = i;
                ImageUtil.setPixelColor(result, x, y, 255, 0, 0);
            }
            //kiri
            x = position[0][0];
            for(int i = position[0][1]; i <= position[1][1]; i++){
                y = i;
                ImageUtil.setPixelColor(result, x, y, 255, 0, 0);
            }
            //kanan
            x = position[1][0];
            for(int i = position[0][1]; i <= position[1][1]; i++){
                y = i;
                ImageUtil.setPixelColor(result, x, y, 255, 0, 0);
            }

//        }
        return result;
    }



    private static List<int[][]> getFacePosition(Bitmap result) {
        int height = result.getHeight();
        int width = result.getWidth();

        List<int[][]> positions = new ArrayList<>();
        int [][] faceCandidate = new int[height][width];
        int colorRange[]= {0,0,0};


//        int[][] position = {{135,194},{327,442}};
        int[][] position = {{width,height},{0,0}};

        /// use for recalculate in certain area

//        position = new int[][]{{width / 3, height / 3}, {2 * width / 3, 2 * height / 3}};
//        Log.d(TAG, position[0][0] + " " +position[0][1] + " " +position[1][0] + " " +position[1][1] );
//        for (int i = position[0][0]; i<position[1][0];i++){
//            for (int j=position[0][1]; j<position[1][1];j++){
//                int color [] = ImageUtil.getPixelColor(result,i,j);
//                colorRange[0]+= color[0];
//                colorRange[1]+= color[1];
//                colorRange[2]+= color[2];
//            }
//        }
//        int numPixels= (position[1][0] - position[0][0]) * (position[1][1] - position[0][1]);
//        colorRange[0] /= numPixels; //176
//        colorRange[1] /= numPixels; //135
//        colorRange[2] /= numPixels; //126


        ///by color
        colorRange[0] = 176;
        colorRange[1] = 135;
        colorRange[2] = 126;

        for (int i=0;i<width;i++){
            for (int j=0; j<height; j++){
                int color[] = ImageUtil.getPixelColor(result,i,j);
                if (    color[0] < colorRange[0]+10 && color[0] > colorRange[0]-10 &&
                        color[1] < colorRange[1]+10 && color[1] > colorRange[2]-10 &&
                        color[2] < colorRange[2]+10 && color[2] > colorRange[2]-10){
                    faceCandidate[j][i] = 1;
                    ImageUtil.setPixelColor(result,i,j+1,0,255,0);
                    if ( i < position[0][0] ) position[0][0] = i;
                    if ( i > position[1][0] ) position[1][0] = i;
                    if ( j < position[0][1] ) position[0][1] = j;
                    if ( j > position[1][1] ) position[1][1] = j;
                }else faceCandidate[j][i] = 0;

            }
        }


        /////TBD
        int[][] positionX = {{width,height},{0,0}};
        for (int i=position[0][0];i<position[1][0]/2;i++){
            for (int j=position[0][1]; j<position[1][1]; j++){
                int color[] = ImageUtil.getPixelColor(result,i,j);
                if (    color[0] < colorRange[0]+10 && color[0] > colorRange[0]-10 &&
                        color[1] < colorRange[1]+10 && color[1] > colorRange[2]-10 &&
                        color[2] < colorRange[2]+10 && color[2] > colorRange[2]-10){

//                    ImageUtil.setPixelColor(result,i,j,0,255,0);
                    if ( i < positionX[0][0] ) positionX[0][0] = i;
                    if ( i > positionX[1][0] ) positionX[1][0] = i;
                    if ( j < positionX[0][1] ) positionX[0][1] = j;
                    if ( j > positionX[1][1] ) positionX[1][1] = j;
                }

            }
        }
        Log.d(TAG, "FacePosition: " + position[0][0] + " " +position[0][1] + " " +position[1][0] + " " +position[1][1]);
        positions.add(positionX);

        positionX = new int[][]{{width, height}, {0, 0}};
        for (int i=position[1][0]/2;i<position[1][0];i++){
            for (int j=position[1][1]/2; j<position[1][1]; j++){
                int color[] = ImageUtil.getPixelColor(result,i,j);
                if (    color[0] < colorRange[0]+10 && color[0] > colorRange[0]-10 &&
                        color[1] < colorRange[1]+10 && color[1] > colorRange[2]-10 &&
                        color[2] < colorRange[2]+10 && color[2] > colorRange[2]-10){

//                    ImageUtil.setPixelColor(result,i,j,0,255,0);
                    if ( i < positionX[0][0] ) positionX[0][0] = i;
                    if ( i > positionX[1][0] ) positionX[1][0] = i;
                    if ( j < positionX[0][1] ) positionX[0][1] = j;
                    if ( j > positionX[1][1] ) positionX[1][1] = j;
                }

            }
        }
        Log.d(TAG, "FacePosition: " + position[0][0] + " " +position[0][1] + " " +position[1][0] + " " +position[1][1]);








        //multi face
//        int[][] area = null;
//        for (int i=1;i<height-1;i++){
//            for (int j=1; j<width-1; j++){
//                area=floodFill(faceCandidate, i, j, height, width);
//            }
//        }
//        positions.add(area);

        positions.add(positionX);
//        Log.d(TAG, position[0][0] + " " +position[0][1] + " " +position[1][0] + " " +position[1][1] );
//        Log.d(TAG, colorRange[0] + " " +colorRange[1] + " " +colorRange[2] );
        return positions;
    }

    private static int[][] floodFill(int[][] faceCandidate, int y, int x, int height, int width) {

        int pxmin = x;
        int pxmax = x;
        int pymin = y;
        int pymax = y;
        Queue<int[]> queue = new ArrayDeque<>();

        queue.offer(new int[]{x, y});

        while (!queue.isEmpty()) {
            int []pt = queue.poll();
            x = pt[0];
            y = pt[1];

            if (faceCandidate[y][x] != 0
                    && 0 < x
                    && x < width - 1
                    && 0 < y
                        && y < height - 1) {

                faceCandidate[y][x] = 0;

                if (x < pxmin) pxmin = x;
                if (x > pxmax) pxmax = x;
                if (y < pymin) pymin = y;
                if (y > pymax) pymax = y;
//
                queue.offer(new int[]{x-1, y});
                queue.offer(new int[]{x+1, y});
                queue.offer(new int[]{x, y-1});
                queue.offer(new int[]{x, y+1});

//                if(y-1 >= 0)        queue.offer(new int[]{x, y-1});
//                if(y+1 <= height)   queue.offer(new int[]{x, y+1});
//                if(x-1 >= 0)        queue.offer(new int[]{x+1, y});
//                if(x+1 <= width)    queue.offer(new int[]{x-1, y});
            }
        }

//        if(y-1 >= 0)        queue.offer(new int[]{y-1, x});
//        if(y+1 <= height)   queue.offer(new int[]{y+1, x});
//        if(x-1 >= 0)        queue.offer(new int[]{y, x-1});
//        if(x+1 <= width)    queue.offer(new int[]{y, x+1});

        return new int[][]{{pxmin,pymin},{pxmax,pymax}};





//        int[][] position = {{x,y},{x,y}};
//
//
//        if (faceCandidate[y][x] != 0) {
//            faceCandidate[y][x] = 0;
//
//
//
//            if(x-1 >= 0)        position =  floodFill(faceCandidate, y, x-1, height, width);
//            if (x < position[0][0]) position[0][0] = x;
//
//            if(x+1 <= width)    position =  floodFill(faceCandidate, y, x+1, height, width);
//            if (x > position[1][0]) position[1][0] = x;
//
//            if(y-1 >= 0)        position =  floodFill(faceCandidate, y-1, x, height, width);
//            if (y < position[0][1]) position[0][1] = y;
//
//            if(y+1 <= height)   position =  floodFill(faceCandidate, y+1, x, height, width);
//            if (y > position[1][1]) position[1][1] = y;
//
//
//        }
//
//        return position;
    }


    private static List<int[][]> getFacePositionMulti(Bitmap result) {
        int height = result.getHeight();
        int width = result.getWidth();
        int [][] faceCandidate = new int[height][width];

        List<int[][]> positions = new ArrayList<>();

        for (int i=0;i<width;i++){
            for (int j=0; j<height; j++){
                int color[] = ImageUtil.getPixelColor(result,i,j);
                if ( isSkinColor(color)){
//                    detectEyes(result,i,j);
//                    ImageUtil.setPixelColor(result,i,j,0,0,255);
                    faceCandidate[j][i] = 1;
                }
                else faceCandidate[j][i] = 0;
            }
        }

        for (int i=0;i<width;i++){
            for (int j=0; j<height; j++){
                if ( faceCandidate[j][i] == 1){
                    int[][] position = floodFill(faceCandidate,j,i,height,width);
//                    ImageUtil.setPixelColor(result,i,j,0,i%255,0);
//                    ImageUtil.setPixelColor(result,i,j,0,255,0);
                    int numFacePixels = (position[1][0] - position[0][0])*(position[1][1] - position[0][1]);
                    int dense = 10;
                    int facePixelsTreshold = height*width/((4*3)*dense);

                    if(numFacePixels > facePixelsTreshold){
                        positions.add(position);
                    }

//        Log.d(TAG, position[0][0] + " " + position[0][1]+ " "+position[1][0]+ " "+position[1][0]);
                }
            }
        }

//        Log.d(TAG, position[0][0] + " " + position[0][1]+ " "+position[1][0]+ " "+position[1][0]);
        return positions;
    }




    private static boolean isSkinColor(int[] color) {

        double red = (double) color[0];
        double green = (double) color[1];
        double blue = (double) color[2];

        double y =((0.257*red) + (0.504*green) + (0.098*blue) + 16);
        double cb = (-(0.148*red) - (0.291*green)+ (0.439*blue) + 128);
        double cr = ((0.439*red) - (0.368*green) - (0.071*blue) + 128);


//        double r = color[0] / ( color[0] + color[1] + color[2] );
//        double g = color[1] / ( color[0] + color[1] + color[2] );
//
//        double upper = -1.36*Math.pow(r,2) + 1.0743*r + 0.2;
//        double bottom = -0.776*Math.pow(r,2) + 0.5601*r + 0.18;
//        double white = Math.pow(r-0.33,2) + Math.pow(g-0.33,2);

        if (cb > 105 && cb < 135 && cr > 140 && cr < 165){
            return true;
        } else {
            return false;
        }
    }
}
