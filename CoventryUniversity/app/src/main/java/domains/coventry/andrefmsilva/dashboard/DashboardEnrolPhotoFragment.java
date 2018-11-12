/*:::::::::::::::::::::::::::::::::::::::::::::::::::
 : Copyright 2018 Andre Silva. All rights reserved. :
 :                                                  :
 : Contact: mateussa@uni.coventry.ac.uk             :
 :                                                  :
 : Check my work at,                                :
 : https://github.coventry.ac.uk/mateussa           :
 : https://andrefmsilva.coventry.domains            :
 :                                                  :
 : DashboardEnrolPhotoFragment                      :
 : Last modified 10 Nov 2018                        :
 :::::::::::::::::::::::::::::::::::::::::::::::::::*/

package domains.coventry.andrefmsilva.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Objects;

import domains.coventry.andrefmsilva.views.ZoomImageView;
import domains.coventry.andrefmsilva.coventryuniversity.MainActivity;
import domains.coventry.andrefmsilva.coventryuniversity.R;
import domains.coventry.andrefmsilva.utils.MySQLConnector;

import static android.app.Activity.RESULT_OK;
import static domains.coventry.andrefmsilva.utils.Utils.setToolbarText;
import static domains.coventry.andrefmsilva.utils.Utils.setChildrenEnabled;

public class DashboardEnrolPhotoFragment extends Fragment implements MySQLConnector
{
    ViewGroup enrolcardphotoLayout;
    ZoomImageView imgViewPhoto;
    Button btnCoonfirm;
    String photoPath;
    Bitmap loadedImage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.dashboard_fragment_enrolcardphoto, container, false);

        setToolbarText((AppCompatActivity) Objects.requireNonNull(getActivity()), R.string.enrol_cardphoto, R.string.app_name);

        enrolcardphotoLayout = view.findViewById(R.id.enrolcardphoto_layout);
        imgViewPhoto = view.findViewById(R.id.enrolcardphoto_image);

        btnCoonfirm = view.findViewById(R.id.enrolcardphoto_confirm);

        btnCoonfirm.setOnClickListener(v ->
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            imgViewPhoto.setForeground(null);

            loadedImage = Bitmap.createBitmap(imgViewPhoto.getWidth(), imgViewPhoto.getHeight(), Bitmap.Config.RGB_565);
            Canvas c = new Canvas(loadedImage);
            imgViewPhoto.draw(c);

            imgViewPhoto.setForeground(getResources().getDrawable(R.drawable.ic_photo_guide, getActivity().getTheme()));

            loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();


            HashMap<String, String> requestInfo = new HashMap<>();
            requestInfo.put("type", "register_card_photo");
            requestInfo.put("id", String.valueOf(((MainActivity) Objects.requireNonNull(getActivity())).getUserID()));
            requestInfo.put("photo", Base64.encodeToString(imageBytes, Base64.DEFAULT));

            new connectMySQL(new WeakReference<>(this), FILE_ENROL, requestInfo, "Sending Card Photo", false).execute();
        });

        view.findViewById(R.id.enrolcardphoto_takepicture).setOnClickListener(v -> startTakePicktureActivity());
        view.findViewById(R.id.enrolcardphoto_cancel).setOnClickListener(v -> getActivity().onBackPressed());

        startTakePicktureActivity();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1 && resultCode == RESULT_OK)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

            imgViewPhoto.setImageBitmap(bitmap);
        }
        else
            super.onActivityResult(requestCode, resultCode, data);

        if (imgViewPhoto.getDrawable() == null)
            Objects.requireNonNull(getActivity()).onBackPressed();

        // Only enable button if there is an image to send
        btnCoonfirm.setEnabled(imgViewPhoto.getDrawable() != null);
    }

    @Override
    public void connectionStarted()
    {
        setChildrenEnabled(enrolcardphotoLayout, false);
    }

    @Override
    public void connectionSuccessful(HashMap<String, String> results)
    {
        Toast.makeText(getContext(), "Photo registered successfuly", Toast.LENGTH_SHORT).show();

        FileOutputStream fOutStream = null;
        try
        {
            fOutStream = Objects.requireNonNull(getContext()).openFileOutput("photo.jpg", Context.MODE_PRIVATE);

            loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, fOutStream);
        }
        catch (Exception e)
        {
            Log.e("DashboardLoginFragment", e.getMessage(), e);
        }
        finally
        {
            if (fOutStream != null)
            {
                try
                {
                    fOutStream.close();
                }
                catch (IOException e)
                {
                    Log.e("DashboardLoginFragment", e.getMessage(), e);
                }
            }
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        sharedPreferences.edit().remove("photo").apply();
        sharedPreferences.edit().putString("photo", "photo.jpg").apply();

        Objects.requireNonNull(getActivity()).onBackPressed();
    }

    @Override
    public void connectionUnsuccessful(Boolean canRetry)
    {
        Toast.makeText(getContext(), "Couldn't get data", Toast.LENGTH_SHORT).show();

        // Enable all layout children
        setChildrenEnabled(enrolcardphotoLayout, true);
    }

    private void startTakePicktureActivity()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                photoFile = File.createTempFile("photo", ".jpg", storageDir);

                // Save the file path for use with ACTION_VIEW intents
                photoPath = photoFile.getAbsolutePath();
            }
            catch (IOException e)
            {
                Log.e("takePicture", e.getMessage(), e);
            }

            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, 1);
            }

        }
    }
}
