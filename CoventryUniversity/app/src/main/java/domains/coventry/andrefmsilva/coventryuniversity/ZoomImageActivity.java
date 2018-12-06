/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : ZoomImageActivity.java                           :
 : Last modified 06 Dec 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.coventryuniversity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

import domains.coventry.andrefmsilva.views.ZoomImageView;


public class ZoomImageActivity extends AppCompatActivity
{
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

        ZoomImageView zoomImageView = findViewById(R.id.zoom_imageview);

        // Only run after the view is created, for the center function of the zoomedimagview have the view's correct size
        zoomImageView.post(() ->
        {
            FileInputStream fInpStream = null;

            try
            {
                // Getting the file name from the previous activity
                fileName = getIntent().getStringExtra("fileName");
                fInpStream = openFileInput(fileName);

                zoomImageView.setImageBitmap(BitmapFactory.decodeStream(fInpStream));
            }
            catch (Exception e)
            {
                Log.e("onCreate", e.getMessage(), e);
            }
            finally
            {
                if (fInpStream != null)
                {
                    try
                    {
                        fInpStream.close();
                    }
                    catch (IOException e)
                    {
                        Log.e("onCreate.close", e.getMessage(), e);
                    }
                }
            }
        });


    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (!fileName.isEmpty())
            deleteFile(fileName);
    }
}
