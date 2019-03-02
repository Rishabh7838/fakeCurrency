package com.example.rish.detect_currency;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rish.detect_currency.Modal.QueryResponse;
import com.example.rish.detect_currency.services.RetrtofitInstance;
import com.example.rish.detect_currency.services.SendPhotoService;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private static final int GALLERY =1,CAMERA = 0,PREVIEW=2 ;
    private Button uploadButton;
    private Uri uri = null;
    private PhotoDialog photoDialog;
    private ImageView noteImage;
    private String newCoverPic;
    private String[] Permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private Intent intent =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            intent = getIntent();
        uploadButton = findViewById(R.id.UploadButton);
        noteImage = findViewById(R.id.NoteImage);

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
    public void ClickImageFromCamera(){
        Intent CamIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        File file = new File(Environment.getExternalStorageDirectory(),
//                "file" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        File file = null;
        String imageFileName = "IMG_" + String.valueOf(System.currentTimeMillis()) + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            file = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        }catch (IOException e){}

        if(file!=null) {
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

                } else { }
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
                    if(data!=null) {
                        Uri resultUri = Uri.parse(data.getStringExtra("Uri"));
                        uri = resultUri;
//                        Glide.with(MainActivity.this).from(uri.toString()).into(noteImage);
                        noteImage.setImageURI(uri);
                       // newCoverPic = resultUri.toString();
                        //String caption = data.getStringExtra("Caption");
                        //String profile = userDetailsModel.ProfilePic;
                        //String username = userDetailsModel.UserName;
                       /* Bundle bundle = data.getExtras();
                        Bitmap image = bundle.getParcelable("Image");
                        String caption = bundle.getString("Caption");
                       */
                        Bitmap image = null;
                        try {
                            image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                            File file = new File(resultUri.getPath());
                            if(intent.hasExtra("Activity")){
                                Toast.makeText(this, "Thanks for your valuable contribution", Toast.LENGTH_SHORT).show();
                            }

                            else {
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
                                        Toast.makeText(MainActivity.this, "value = "+response.body().getValue(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<QueryResponse> call, Throwable t) {
                                        Log.e("sorry", "babes = " + t.getMessage());
                                    }
                                });

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    //    SaveData(image, caption, profile, username);
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
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
