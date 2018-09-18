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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;

public class HistogramKuantisasi extends AppCompatActivity {

    ImageView imageViewA, imageViewB, imageViewC, imageViewD;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.histokuanti_layout);

        imageViewA = findViewById(R.id.imageViewA);
        imageViewB = findViewById(R.id.imageViewB);
        imageViewC = findViewById(R.id.imageViewC);
        imageViewD = findViewById(R.id.imageViewD);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.IntentCode.LOAD_IMAGE);
        }
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

                    imageViewA.setImageBitmap(image);
                    imageViewC.setImageBitmap(ImageUtil.getGrayscaleImage(image));
                } else if (requestCode == Constant.IntentCode.OPEN_CAMERA && data.getExtras().get("data") != null) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");

                    imageViewA.setImageBitmap(image);
                    imageViewC.setImageBitmap(ImageUtil.getGrayscaleImage(image));
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

    public void transform(View view) {
        Bitmap image = ((BitmapDrawable) imageViewA.getDrawable()).getBitmap();
        Bitmap[] result = ImageUtil.getTransformedImage(image);

        imageViewB.setImageBitmap(result[0]);
        imageViewD.setImageBitmap(result[1]);
    }
}
