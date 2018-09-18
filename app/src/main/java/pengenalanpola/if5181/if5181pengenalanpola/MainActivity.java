package pengenalanpola.if5181.if5181pengenalanpola;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void loadHistogram(View view) {
        Intent intent = new Intent(MainActivity.this, Histogram.class);
        startActivity(intent);
    }

    public void loadHistogramKuantisasi(View view) {
        Intent intent = new Intent(MainActivity.this, HistogramKuantisasi.class);
        startActivity(intent);
    }

    public void loadSmoothing(View view) {
        Intent intent = new Intent(MainActivity.this, Smoothing.class);
        startActivity(intent);
    }

    public void loadOCR(View view) {
        Intent intent = new Intent(MainActivity.this, OCR.class);
        startActivity(intent);
    }

    public void loadOCRSevenSegment(View view) {
        Intent intent = new Intent(MainActivity.this, OCRSevenSegment.class);
        startActivity(intent);
    }
}
