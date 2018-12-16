package pengenalanpola.if5181.if5181pengenalanpola;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStreamWriter;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class FourierActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourier);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.IntentCode.LOAD_IMAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        imageView.setImageBitmap(BitmapFactory.decodeFile("/storage/emulated/0/Download/images.jpg"));
//        LowPassFilter(imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && data != null) {
                if (requestCode == Constant.IntentCode.LOAD_IMAGE && data.getData() != null) {
                    Cursor cursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);

                    if (cursor == null)
                        return;

                    cursor.moveToFirst();
                    String imageString = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    cursor.close();

                    Bitmap image = BitmapFactory.decodeFile(imageString);
//                    Log.d("FILENAME", "onActivityResult: "+imageString);
//                    /storage/emulated/0/Download/images.jpg

                    imageView.setImageBitmap(image);
                } else if (requestCode == Constant.IntentCode.OPEN_CAMERA && data.getExtras().get("data") != null) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");

                    imageView.setImageBitmap(image);

                } else if (requestCode == 3) {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("confDayat.txt", MODE_PRIVATE));
                    outputStreamWriter.write("test");
                    outputStreamWriter.close();
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, String.format("Error : %s", e.getMessage()), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void loadImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constant.IntentCode.LOAD_IMAGE);
    }

    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constant.IntentCode.OPEN_CAMERA);
    }

    public void HighPassFilter(View view) {


//        new Thread(new Runnable() {
//            public void run() {
                Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                image = ImageUtil.squarePadding(image);

                double[] imgSpacDomain = ImageUtil.getImgSpacDomain(image);

                double[] imgSpacDomain1 = new double[imgSpacDomain.length/2];
                double[] imgSpacDomain2 = new double[imgSpacDomain.length/2];

                double[] imgFreqDomain1 = new double[imgSpacDomain.length/2];
                double[] imgFreqDomain2 = new double[imgSpacDomain.length/2];


                for (int i=0; i< imgSpacDomain.length-2; i+=2){
                    imgSpacDomain1[i/2]=imgSpacDomain[i];
                    imgSpacDomain2[i/2]=imgSpacDomain[i+1];
                }

                double[] imgFreqDomain = FFTBase.fft(imgSpacDomain1, imgSpacDomain2, true);

//                imgFreqDomain = ImageUtil.normalize(imgFreqDomain);



                /// coba lakukan operasi frequensi disini

                // high pass filtering

//                double max = imgFreqDomain[0];
//                double min = imgFreqDomain[0];
//                for (int i=0; i<imgFreqDomain.length; i++){
//                    if (min > imgFreqDomain[i]) min = imgFreqDomain[i];
//                    if (max < imgFreqDomain[i]) max = imgFreqDomain[i];
//                }
//                double threshold = (max - min) / 2;
//
//                Log.d("BARU", "TH>>>>>>>>>: " +threshold+ " " + min+ " "+ max);
//
//                for (int i=0; i<imgFreqDomain.length; i++){
//                    if (imgFreqDomain[i] <  ) imgFreqDomain[i] = imgFreqDomain[i]*-1;
//                }


        int side = (int) Math.sqrt(imgFreqDomain.length);

        for (int i = 0+1; i < side-1; i++) {
            for (int j = 0+1; j < side-1; j++) {
                if (imgFreqDomain[i+j*side] < imgFreqDomain[side/2 + side*side/2]) continue;
                else imgFreqDomain[i+j*side] = 60;
            }
        }





//                imgFreqDomain = ImageUtil.normalize(imgFreqDomain);

                image = ImageUtil.createBitmapFromArray(imgFreqDomain);

                for (int i=0; i< imgSpacDomain.length; i+=2){
                    imgFreqDomain1[i/2]=imgFreqDomain[i];
                    imgFreqDomain2[i/2]=imgFreqDomain[i+1];
                }

//        image = ImageUtil.createBitmapFromArray(imgFreqDomain);

                imgSpacDomain = FFTBase.fft(imgFreqDomain1, imgFreqDomain2, false);

                image = ImageUtil.createBitmapFromArray(imgSpacDomain);
//        Log.d("HFHFH", "process: jalan");


        imageView.setImageBitmap(image);

//                final Bitmap finalBitmap = image;
//                imageView.post(new Runnable() {
//                    public void run() {
//                        imageView.setImageBitmap(finalBitmap);
//                    }
//                });
//            }
//        }).start();

    }

    public void inverse(View view) {


//        new Thread(new Runnable() {
//            public void run() {
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        image = ImageUtil.squarePadding(image);

        double[] imgFreqDomain = ImageUtil.getImgSpacDomain(image);

        double[] imgFreqDomain1 = new double[imgFreqDomain.length/2];
        double[] imgFreqDomain2 = new double[imgFreqDomain.length/2];

        for (int i=0; i< imgFreqDomain.length; i+=2){
            imgFreqDomain1[i/2]=imgFreqDomain[i];
            imgFreqDomain2[i/2]=imgFreqDomain[i+1];
        }

        double[] imgSpacDomain = FFTBase.fft(imgFreqDomain1, imgFreqDomain2, false);

//        imgSpacDomain = ImageUtil.normalize(imgSpacDomain);

        image = ImageUtil.createBitmapFromArray(imgSpacDomain);

//                imgFreqDomain = ImageUtil.normalize(imgFreqDomain);

//                image = ImageUtil.createBitmapFromArray(imgFreqDomain);



        imageView.setImageBitmap(image);

//                final Bitmap finalBitmap = image;
//                imageView.post(new Runnable() {
//                    public void run() {
//                        imageView.setImageBitmap(finalBitmap);
//                    }
//                });
//            }
//        }).start();

    }


    public void LowPassFilter(View view) {


//        new Thread(new Runnable() {
//            public void run() {
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        image = ImageUtil.squarePadding(image);

        double[] imgSpacDomain = ImageUtil.getImgSpacDomain(image);

        double[] imgSpacDomain1 = new double[imgSpacDomain.length/2];
        double[] imgSpacDomain2 = new double[imgSpacDomain.length/2];

        double[] imgFreqDomain1 = new double[imgSpacDomain.length/2];
        double[] imgFreqDomain2 = new double[imgSpacDomain.length/2];


        for (int i=0; i< imgSpacDomain.length-2; i+=2){
            imgSpacDomain1[i/2]=imgSpacDomain[i];
            imgSpacDomain2[i/2]=imgSpacDomain[i+1];
        }

        double[] imgFreqDomain = FFTBase.fft(imgSpacDomain1, imgSpacDomain2, true);

//                imgFreqDomain = ImageUtil.normalize(imgFreqDomain);



        /// coba lakukan operasi frequensi disini

        // high pass filtering

//                double max = imgFreqDomain[0];
//                double min = imgFreqDomain[0];
//                for (int i=0; i<imgFreqDomain.length; i++){
//                    if (min > imgFreqDomain[i]) min = imgFreqDomain[i];
//                    if (max < imgFreqDomain[i]) max = imgFreqDomain[i];
//                }
//                double threshold = (max - min) / 2;
//
//                Log.d("BARU", "TH>>>>>>>>>: " +threshold+ " " + min+ " "+ max);
//
//                for (int i=0; i<imgFreqDomain.length; i++){
//                    if (imgFreqDomain[i] <  ) imgFreqDomain[i] = imgFreqDomain[i]*-1;
//                }


        int side = (int) Math.sqrt(imgFreqDomain.length);

        for (int i = 0+1; i < side-1; i++) {
            for (int j = 0+1; j < side-1; j++) {
                if (imgFreqDomain[i+j*side] > imgFreqDomain[side/2 + side*side/2]) continue;
                else imgFreqDomain[i+j*side] = 50;
            }
        }





//                imgFreqDomain = ImageUtil.normalize(imgFreqDomain);

        image = ImageUtil.createBitmapFromArray(imgFreqDomain);

        for (int i=0; i< imgSpacDomain.length; i+=2){
            imgFreqDomain1[i/2]=imgFreqDomain[i];
            imgFreqDomain2[i/2]=imgFreqDomain[i+1];
        }

//        image = ImageUtil.createBitmapFromArray(imgFreqDomain);

        imgSpacDomain = FFTBase.fft(imgFreqDomain1, imgFreqDomain2, false);

        image = ImageUtil.createBitmapFromArray(imgSpacDomain);
//        Log.d("HFHFH", "process: jalan");


        imageView.setImageBitmap(image);

//                final Bitmap finalBitmap = image;
//                imageView.post(new Runnable() {
//                    public void run() {
//                        imageView.setImageBitmap(finalBitmap);
//                    }
//                });
//            }
//        }).start();

    }
}
