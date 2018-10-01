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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class skeletonActivity extends AppCompatActivity {

    ImageView imgSource, imgSkeleton;
    Button btnloadImage, btnzShuen, btnnewApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skeleton_layout);

        imgSource = findViewById(R.id.imgSource);
        imgSkeleton = findViewById(R.id.imgSkeleton);
        btnloadImage = findViewById(R.id.buttonLoadImage);
        btnzShuen = findViewById(R.id.btnZShuen);
        btnnewApp = findViewById(R.id.btnNewApp);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.IntentCode.LOAD_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && data != null) {
                if (requestCode == Constant.IntentCode.LOAD_IMAGE) {
                    Cursor cursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    cursor.moveToFirst();
                    String imageString = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    cursor.close();
                    imgSource.setImageBitmap(BitmapFactory.decodeFile(imageString));
                } else if (requestCode == Constant.IntentCode.OPEN_CAMERA) {
                    imgSource.setImageBitmap((Bitmap) data.getExtras().get("data"));
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

    public void skeletonByZShuen(View view){


        new Thread(new Runnable() {
            public void run() {
                Bitmap bitmap =((BitmapDrawable) imgSource.getDrawable()).getBitmap();
                bitmap = Thinning.zShuen(bitmap);
                final Bitmap finalBitmap = bitmap;
                imgSkeleton.post(new Runnable() {
                    public void run() {
                        imgSkeleton.setImageBitmap(finalBitmap);
                    }
                });
            }
        }).start();

//        Bitmap bitmap = ((BitmapDrawable) imgSource.getDrawable()).getBitmap();
//        bitmap = Thinning.zShuen(bitmap);
//        imgSkeleton.setImageBitmap(bitmap);

    }

    public void skeletonByMe(View view){
        Bitmap bitmap = ((BitmapDrawable) imgSource.getDrawable()).getBitmap();

        bitmap = Thinning.skeleton(bitmap);

        imgSkeleton.setImageBitmap(bitmap);
    }

}
