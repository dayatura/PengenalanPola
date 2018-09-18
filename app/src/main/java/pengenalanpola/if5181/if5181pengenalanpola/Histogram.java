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
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

public class Histogram extends AppCompatActivity {

    ImageView imageView;
    GraphView graphViewRed, graphViewGreen, graphViewBlue, graphViewGrayscale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.histogram_layout);

        imageView = findViewById(R.id.imageView);
        graphViewRed = findViewById(R.id.graphViewRed);
        graphViewGreen = findViewById(R.id.graphViewGreen);
        graphViewBlue = findViewById(R.id.graphViewBlue);
        graphViewGrayscale = findViewById(R.id.graphViewGrayscale);

        graphViewRed.setTitle("Red");
        graphViewGreen.setTitle("Green");
        graphViewBlue.setTitle("Blue");
        graphViewGrayscale.setTitle("Grayscale");

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
                    imageView.setImageBitmap(BitmapFactory.decodeFile(imageString));
                } else if (requestCode == Constant.IntentCode.OPEN_CAMERA) {
                    imageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
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
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        int[][] count = ImageUtil.getPixelCount(bitmap);

        setGraphView(graphViewRed, count[0]);
        setGraphView(graphViewGreen, count[1]);
        setGraphView(graphViewBlue, count[2]);
        setGraphView(graphViewGrayscale, count[3]);
    }

    public void setGraphView(GraphView graphView, int[] data) {
        DataPoint[] dataPoints = new DataPoint[data.length];

        for (int i = 0; i < data.length; i++) {
            dataPoints[i] = new DataPoint(i, data[i]);
        }

        graphView.removeAllSeries();
        graphView.addSeries(new BarGraphSeries(dataPoints));
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(data.length);
    }
}
