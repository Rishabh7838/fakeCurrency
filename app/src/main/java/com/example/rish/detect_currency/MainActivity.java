package com.example.rish.detect_currency;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rish.detect_currency.Modal.QueryResponse;
import com.example.rish.detect_currency.Modal.RegistrationResponse;
import com.example.rish.detect_currency.services.RetrtofitInstance;
import com.example.rish.detect_currency.services.SendPhotoService;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;

import java.io.File;
import java.io.IOException;
import java.security.Permissions;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MainActivity extends AppCompatActivity {
    private static final int GALLERY = 1, CAMERA = 0, PREVIEW = 2;
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button uploadButton;
    private Uri uri = null;
    private PhotoDialog photoDialog;
    private TextView rTextView;
    private ImageView noteImage;
    private Button offlineButton;
    private ProgressBar progressBar;
    private String newCoverPic;
    private String[] Permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private Intent intent = null;
    private Classifier classifier;
    private static final String SHOWCASE_ID = "seq1";
    private static final String SHOWCASE_ID2 = "seq2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            classifier = new Classifier(this);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onCreate: Failed to load model!");
            Toast.makeText(this, "Failed to load Model!", Toast.LENGTH_SHORT).show();
//            finish();
        }
        intent = getIntent();
        uploadButton = findViewById(R.id.UploadButton);
        offlineButton = findViewById(R.id.offline_button);
        noteImage = findViewById(R.id.NoteImage);
        progressBar = findViewById(R.id.progressBar);
        rTextView = findViewById(R.id.textview);

        tips();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (uri != null) {
                    uploadImage(uri);
                }
            }
        });

        offlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (uri != null) {
                    classifyImage(uri);
                }
            }
        });



        ActivityCompat.requestPermissions(MainActivity.this, Permissions, 1);
        noteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoDialog = new PhotoDialog(MainActivity.this);
                photoDialog.setOnCameraClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClickImageFromCamera();
                    }
                });
                photoDialog.setOnGalleryClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GetImageFromGallery();
                    }
                });
                photoDialog.show();
            }
        });
    }

    public void ClickImageFromCamera() {
        Intent CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = null;
        String imageFileName = "IMG_" + String.valueOf(System.currentTimeMillis()) + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            file = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
        }

        if (file != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.rish.detect_currency.provider", file);
            getSharedPreferences("Temp", MODE_PRIVATE).edit().putString("Uri", photoURI.toString()).apply();
            CamIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            CamIntent.putExtra("return-data", false);

            CamIntent.putExtra("outputX", 960);
            CamIntent.putExtra("outputY", 540);
            CamIntent.putExtra("aspectX", 16);
            CamIntent.putExtra("aspectY", 9);
            CamIntent.putExtra("scale", true);
            startActivityForResult(CamIntent, CAMERA);
        }
    }


    public void GetImageFromGallery() {
        Intent GalIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(GalIntent, "Select Image From Gallery"), GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("PermissionsGranted", Integer.toString(grantResults.length));
                    // permission granted
                    //viewHolder.Camera.callOnClick();

                } else {
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY:
                    if (data != null) {
                        try {
                            uri = data.getData();
                            ImageCropFunction();

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                    break;
                case CAMERA:
                    uri = Uri.parse(getSharedPreferences("Temp", MODE_PRIVATE).getString("Uri", ""));
                    ImageCropFunction();
                    break;
                case PREVIEW:
                    if (data != null) {
                        final Uri resultUri = Uri.parse(data.getStringExtra("Uri"));
                        uri = resultUri;
//                        Glide.with(MainActivity.this).from(uri.toString()).into(noteImage);
                        noteImage.setImageURI(uri);
                        uploadButton.setVisibility(View.VISIBLE);
                        offlineButton.setVisibility(View.VISIBLE);
                        tips2();
                        if(intent.hasExtra("Activity")){
                            offlineButton.setVisibility(View.GONE);
                            uploadButton.setText("Upload Image");
                        }
                    }
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        }
        //else super.onActivityResult(requestCode, resultCode, data);
    }

    public void ImageCropFunction() {
        Intent intent = new Intent(this, ImagePreview.class);
        intent.putExtra("ImageUri", uri.toString());
        intent.putExtra("Caption", true);

        newCoverPic = "Cover" + String.valueOf(System.currentTimeMillis()) + ".png";
        intent.putExtra("AspectX", 16);
        intent.putExtra("AspectY", 9);
        intent.putExtra("MinX", 960);
        intent.putExtra("MinY", 540);
        intent.putExtra("Output", Uri.fromFile(new File(getFilesDir(), newCoverPic)).toString());


        startActivityForResult(intent, PREVIEW);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login: {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void dialog(boolean msg) {

        if(msg)
            new LovelyInfoDialog(this)
                    .setTopColorRes(R.color.light_blue_900)
                    .setIcon(R.drawable.ic_valid_24dp)
                    .setMessage("VALID")
                    .show();
        else
            new LovelyInfoDialog(this)
                    .setTopColorRes(R.color.red_900)
                    .setIcon(R.drawable.ic_fake_24dp)
                    .setMessage("COUNTERFEIT")
                    .show();

    }

    private void uploadImage(Uri resultUri) {
        Log.d(TAG, "uploadImage:");
        uploadButton.setEnabled(false);
        Bitmap image = null;
        try {
            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
            File file = new File(resultUri.getPath());

            if (intent.hasExtra("Activity")) {

                MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

                SendPhotoService sendPhotoService = RetrtofitInstance.getService();
                Call<RegistrationResponse> call = sendPhotoService.uploadphoto(filePart);
                call.enqueue(new Callback<RegistrationResponse>() {
                    @Override
                    public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                        Log.e("Hello", "badiya");
                        progressBar.setVisibility(View.INVISIBLE);
                        uploadButton.setEnabled(true);
                        String output = response.body().getIsSuccessfull();
                        if(output.equals("true")) {
                            Toast.makeText(MainActivity.this, "successfully uploaded", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        }else
                            Toast.makeText(MainActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                        progressBar.setVisibility(View.INVISIBLE);
                        uploadButton.setEnabled(true);
                        Log.d(TAG, "onFailure:");
                        Log.e("sorry", "babes= " + t.getMessage());
                        Toast.makeText(MainActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                    }
                });




                Toast.makeText(this, "Thanks for your valuable contribution", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "uploadImage: uploading");
                MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

                SendPhotoService sendPhotoService = RetrtofitInstance.getService();
//                            Call<String> call = sendPhotoService.detectNote("");
//                            call.enqueue(new Callback<String>() {
//                                             @Override
//                                             public void onResponse(Call<String> call, Response<String> response) {
//                                                 Log.e("Hello","babes");
//                                             }
//
//                                             @Override
//                                             public void onFailure(Call<String> call, Throwable t) {
//                                                 Log.e("sorry","babes = "+t.getMessage());
//                                             }
//                                         });
                Call<QueryResponse> call = sendPhotoService.detectNote(filePart);
                call.enqueue(new Callback<QueryResponse>() {
                    @Override
                    public void onResponse(Call<QueryResponse> call, Response<QueryResponse> response) {
                        Log.e("Hello", "badiya");
                        progressBar.setVisibility(View.INVISIBLE);
                        uploadButton.setEnabled(true);
                        rTextView.setVisibility(View.VISIBLE);
                        String output = response.body().getValue();
                        Toast.makeText(MainActivity.this, "value = " + output, Toast.LENGTH_SHORT).show();
                        if(Float.parseFloat(output)>0.65f)
                            dialog(true);
                        else
                            dialog(false);
                    }

                    @Override
                    public void onFailure(Call<QueryResponse> call, Throwable t) {
                        progressBar.setVisibility(View.INVISIBLE);
                        uploadButton.setEnabled(true);
                        Log.d(TAG, "onFailure:");
                        Log.e("sorry", "babes= " + t.getMessage());
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void classifyImage(Uri uri) {
        offlineButton.setEnabled(false);
        new AsyncTask<Uri, Void, Float>() {
            float prediction = 0;

            @Override
            protected Float doInBackground(Uri... uris) {

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                            uris[0]);

                    bitmap = scaleAndAddWhiteBorder(bitmap);

                    prediction = classifier.classifyFrame(bitmap);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Prediction=" + prediction, Toast.LENGTH_SHORT).show();
                            if(prediction>0.65f)
                                dialog(true);
                            else
                                dialog(false);
//                            dialog((prediction * 100) + " % real.");
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "doInBackground: Can't predict as failed to convert string to uri!");
                }
                return prediction;
            }

            @Override
            protected void onPostExecute(Float aFloat) {
                offlineButton.setEnabled(true);
                progressBar.setVisibility(View.INVISIBLE);
                rTextView.setVisibility(View.VISIBLE);
            }
        }.execute(uri);
    }

    private Bitmap scaleAndAddWhiteBorder(Bitmap bmp) {
        int height = bmp.getHeight();
        int width = bmp.getWidth();
        int biggerSide = height > width ? height : width;

        Bitmap bmpWithBorder = Bitmap.createBitmap(biggerSide, biggerSide, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.WHITE);

        canvas.drawBitmap(bmp, 0f, biggerSide / 2f - height / 2f, null);

        bmpWithBorder = Bitmap.createScaledBitmap(bmpWithBorder, Classifier.DIM_IMG_SIZE_X,
                Classifier.DIM_IMG_SIZE_Y, false);
        return bmpWithBorder;
    }

    private void tips() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(1000);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem(noteImage, "Click here to upload image from gallery or camera.", "GOT IT");

        sequence.start();
    }

    private void tips2() {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(1000);
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID2);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem(uploadButton, "Click here to check using latest algorithm online (slower)", "GOT IT");

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(offlineButton)
                        .setDismissText("GOT IT")
                        .setContentText("Click here to check using offline algorithm (faster)")
                        .build()
        );
        sequence.start();
    }

    @Override
    public void onBackPressed() { }
}


