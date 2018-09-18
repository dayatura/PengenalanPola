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
import android.widget.TextView;
import android.widget.Toast;

public class OCRSevenSegment extends AppCompatActivity {

    ImageView imageView;
    TextView textViewResult, textViewChainCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ocr7s_layout);

        imageView = findViewById(R.id.imageView);
        textViewResult = findViewById(R.id.textViewResult);
        textViewChainCode = findViewById(R.id.textViewChainCode);

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

                    imageView.setImageBitmap(ImageUtil.getBinaryImage(image, 128));
                } else if (requestCode == Constant.IntentCode.OPEN_CAMERA && data.getExtras().get("data") != null) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");

                    imageView.setImageBitmap(ImageUtil.getBinaryImage(image, 128));
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
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        String[] result = ImageUtil.detectNumber2(image);

        textViewResult.setText(result[0]);
        textViewChainCode.setText(result[1]);
    }
}
