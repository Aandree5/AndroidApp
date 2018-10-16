package domains.coventry.andrefmsilva.coventryuniversity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.FileInputStream;


public class ZoomImageActivity extends Activity
{
    String filename;

    //TODO: Check the possibility of passing a bitmap reference to the preaviously loaded bitmap
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_zoom_image);


        ZoomImageView imageView = findViewById(R.id.zoom_imageview);

        try
        {
            filename = getIntent().getStringExtra("filename");
            FileInputStream fInpStream = this.openFileInput(filename);

            Bitmap bmp = BitmapFactory.decodeStream(fInpStream);

            imageView.setImageBitmap(bmp);

            // Close file stream
            fInpStream.close();

        } catch (Exception e)
        {
            Log.e("onCreate", e.getMessage(), e);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (!filename.isEmpty())
            deleteFile(filename);
    }
}
