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
import android.widget.TextView;
import android.widget.Toast;

public class Smoothing extends AppCompatActivity {

    ImageView imageViewA, imageViewB, imageViewC, imageViewD;
    SeekBar seekBarA, seekBarB, seekBarC;
    TextView textViewA, textViewB, textViewC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.smoothing_layout);

        imageViewA = findViewById(R.id.imageViewA);
        imageViewB = findViewById(R.id.imageViewB);
        imageViewC = findViewById(R.id.imageViewC);
        imageViewD = findViewById(R.id.imageViewD);
        seekBarA = findViewById(R.id.seekBarA);
        seekBarB = findViewById(R.id.seekBarB);
        seekBarC = findViewById(R.id.seekBarC);
        textViewA = findViewById(R.id.textViewA);
        textViewB = findViewById(R.id.textViewB);
        textViewC = findViewById(R.id.textViewC);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.IntentCode.LOAD_IMAGE);
        }

        initSeekBar(seekBarA, textViewA, 'A');
        initSeekBar(seekBarB, textViewB, 'B');
        initSeekBar(seekBarC, textViewC, 'C');
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
        Bitmap[] result = ImageUtil.getTransformedImage(image, seekBarA.getProgress(), seekBarB.getProgress(), seekBarC.getProgress());

        imageViewB.setImageBitmap(result[0]);
        imageViewD.setImageBitmap(result[1]);
    }

    public void smoothing(View view) {
        Bitmap image = ((BitmapDrawable) imageViewA.getDrawable()).getBitmap();
        Bitmap[] result = ImageUtil.getSmoothingImage(image);

        imageViewB.setImageBitmap(result[0]);
        imageViewD.setImageBitmap(result[1]);
    }

    // private methods

    private void initSeekBar(SeekBar seekBar, final TextView textView, final char c) {
        seekBar.setMax(255);
        seekBar.setProgress(127);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText(String.format("%c : %d", c, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        textView.setText(String.format("%c : %d", c, seekBar.getProgress()));
    }
}
