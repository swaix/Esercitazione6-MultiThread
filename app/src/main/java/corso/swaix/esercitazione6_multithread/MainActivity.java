package corso.swaix.esercitazione6_multithread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static final String BLUE = "blue";
    private static final String RED = "red";
    private static String file_url;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        file_url = getResources().getString(R.string.URL);
        mImageView = (ImageView) findViewById(R.id.image_view);
        Button red = (Button) findViewById(R.id.button_red);
        Button blue = (Button) findViewById(R.id.button_blu);
        final View container = findViewById(R.id.container);

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor(container, RED);
            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColor(container, BLUE);
            }
        });

    }

    public void changeColor(View container, String color) {
        switch (color) {
            case BLUE: {
                container.setBackgroundColor(getResources().getColor(R.color.blue));
            }
            break;
            case RED: {
                container.setBackgroundColor(getResources().getColor(R.color.red));
            }
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        new DownloadImageTask().execute(file_url);
    }

    private Bitmap loadImageFromNetwork(String url) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            return loadImageFromNetwork(urls[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected void onPostExecute(final Bitmap result) {
            //Do something with bitmap eg:
            mImageView.post(new Runnable() {
                public void run() {
                    mImageView.setImageBitmap(result);
                }
            });
        }
    }
}
