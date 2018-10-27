package domains.coventry.andrefmsilva.utils;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

import domains.coventry.andrefmsilva.CustomViews.ZoomImageView;
import domains.coventry.andrefmsilva.coventryuniversity.R;


public class ZoomImageActivity extends Activity
{
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);

        ZoomImageView imageView = findViewById(R.id.zoom_imageview);
        FileInputStream fInpStream = null;

        try
        {
            // Getting the file name from the previous activity
            fileName = getIntent().getStringExtra("fileName");
            fInpStream = this.openFileInput(fileName);

            imageView.setImageBitmap(BitmapFactory.decodeStream(fInpStream));
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
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (!fileName.isEmpty())
            deleteFile(fileName);
    }
}
