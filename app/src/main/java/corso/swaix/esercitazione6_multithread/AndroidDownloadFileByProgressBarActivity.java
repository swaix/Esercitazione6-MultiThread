package corso.swaix.esercitazione6_multithread;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class AndroidDownloadFileByProgressBarActivity extends Activity {

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;
    private static final String BLUE = "blue";
    private static final String RED = "red";
    // File url to download
    private static String file_url;
    // button to show progress dialog
    Button btnShowProgress;
    ImageView my_image;
    ProgressBar progressBar;
    TextView percText;
    View progressContainer;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        file_url = getResources().getString(R.string.URL);
        setContentView(R.layout.activity_main_with_progress);

        // show progress bar button
        btnShowProgress = (Button) findViewById(R.id.button);
        // Image view to show image after downloading
        my_image = (ImageView) findViewById(R.id.image_view);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        percText = (TextView) findViewById(R.id.percText);
        progressContainer = findViewById(R.id.progress_container);
        /**
         * Show Progress bar click event
         * */
        btnShowProgress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // starting new Async Task
                new DownloadFileFromURL().execute(file_url);
            }
        });


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


    private File filePath;
    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {


        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            my_image.setVisibility(View.GONE);
            progressContainer.setVisibility(View.VISIBLE);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lenghtOfFile = connection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                filePath = mContext.getFilesDir();
                File f = new File(filePath , "downloadedfile.jpg");
                if (f.isFile()) {
                    percText.setText("COPY FILE AT: " + f.getAbsolutePath());
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(f.getAbsolutePath());

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressBar.setProgress(Integer.parseInt(progress[0]));
            percText.setText(progress[0]);

        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * *
         */
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            // Displaying downloaded image into image view
            // Reading image path from sdcard
            progressContainer.setVisibility(View.GONE);
            my_image.setVisibility(View.VISIBLE);
            File f = new File (filePath,"downloadedfile.jpg");

            // setting downloaded into image view
            my_image.setImageDrawable(Drawable.createFromPath(f.getAbsolutePath()));
            f.delete();
        }

        @Override
        protected void onCancelled() {
            progressContainer.setVisibility(View.VISIBLE);
            percText.setText("ERRORE");
        }
    }
}