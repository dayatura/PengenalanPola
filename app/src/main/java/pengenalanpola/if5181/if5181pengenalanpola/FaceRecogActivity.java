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
import java.util.List;

public class FaceRecogActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recog);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.IntentCode.LOAD_IMAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
//        imageView.setImageBitmap(BitmapFactory.decodeFile("/storage/emulated/0/Pictures/LINE/duawaja.jpg"));
        imageView.setImageBitmap(BitmapFactory.decodeFile("/storage/emulated/0/Pictures/LINE/kk.jpg"));
        process(imageView);
//        edgeDetect(imageView);
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
//                    /storage/emulated/0/Pictures/LINE/kk.jpg

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

    public void process(View view) {

        new Thread(new Runnable() {
            public void run() {
                Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                List<String> listName = Face.detectFace(image);
                String[] nameFaces = listName.toArray(new String[listName.size()]);
                String result = "";
                for (String nameFace:nameFaces) result = result + nameFace + " ";
                final Bitmap finalBitmap = image;
                imageView.post(new Runnable() {
                    public void run() {
                        imageView.setImageBitmap(finalBitmap);
                    }
                });
                final String finalResult = result;
                textView.post(new Runnable() {
                    public void run() {
                        textView.setText(finalResult);
                    }
                });

            }
        }).start();

    }

    public void edgeDetect(View view) {

//        Log.d("ID", "button: " + view.getId());
        final int idButton = view.getId();

        new Thread(new Runnable() {
            public void run() {
                Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                double newWidth = 200.0;
                int kernel = 1;
                image = Bitmap.createScaledBitmap(
                        image, (int)newWidth, (int)(image.getHeight()*(newWidth/image.getWidth())),true);

//                image = ImageUtil.getCleanImage(image);
//                image = ImageUtil.getSmoothingImage(image)[0];
                if (idButton == 2131165219) image = ImageUtil.edgeDetection2(image);
                else {
                    if (idButton == 2131165231) kernel = 1;
                    else if (idButton == 2131165229) kernel = 2;
                    else if (idButton == 2131165230) kernel = 3;
                    image = ImageUtil.edgeDetection(image, kernel);
                }

//                image = Thinning.zShuen(image);
                final Bitmap finalBitmap = image;
                imageView.post(new Runnable() {
                    public void run() {
                        imageView.setImageBitmap(finalBitmap);
                    }
                });
            }
        }).start();

    }
}
