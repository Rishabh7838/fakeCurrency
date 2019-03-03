package com.example.rish.detect_currency;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
//import com.github.clans.fab.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
//import com.example.rish.detect_currency.R;

//import de.hdodenhof.circleimageview.CircleImageView;

public class ImagePreview extends AppCompatActivity {
    //CircleImageView Profile;
    AppCompatImageButton Crop, Rotate, Back;
    CropImageView Image;
    //EditTextMentions Caption;
    FloatingActionButton Send;
    //ImageView emji_btn;

    int AspectX, AspectY;
    int MinX, MinY;
    int ScaleX, ScaleY;
    String Output = "";

    boolean showCaption = false;
    boolean CropMode = true;
    int Rotation;
    //Tagger tagger;
    CoordinatorLayout editTextLayout;
    RelativeLayout Container;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        showCaption = getIntent().getBooleanExtra("Caption", false);
        AspectX = getIntent().getIntExtra("AspectX", 1);
        AspectY = getIntent().getIntExtra("AspectY", 1);
        MinX = getIntent().getIntExtra("MinX", 480);
        MinY = getIntent().getIntExtra("MinY", 270);
        ScaleX = getIntent().getIntExtra("ScaleX", 1920);
        ScaleY = getIntent().getIntExtra("ScaleY", 1080);
        Output = getIntent().getStringExtra("Output");

        editTextLayout = (CoordinatorLayout) findViewById(R.id.newCommentWrap);
        Container = (RelativeLayout) findViewById(R.id.Container);

        if (Output == null) {
            Output = "";
        }

        initialize();

        if (showCaption) {

        } else {

        }

        Glide.with(this)
                .fromUri()
                .asBitmap()
                .load(Uri.parse(getIntent().getStringExtra("ImageUri")))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap image, GlideAnimation<? super Bitmap> glideAnimation) {
                        int newWidth = image.getWidth(), newHeight = image.getHeight();
                        float factor;

                        if (newWidth > ScaleX) {
                            factor = (float) ScaleX / newWidth;
                            newWidth = ScaleX;
                            newHeight *= factor;
                        }
                        if (newHeight > ScaleY) {
                            factor = (float) ScaleY / newHeight;
                            newHeight = ScaleY;
                            newWidth *= factor;
                        }

                        Bitmap resized = getResizedBitmap(image, newHeight, newWidth);

                        findViewById(R.id.progress_overlay).setVisibility(View.GONE);
                        Image.setImageBitmap(resized);
                    }
                });

    }
    void initialize() {
        Back = (AppCompatImageButton) findViewById(R.id.Back);
        Crop = (AppCompatImageButton) findViewById(R.id.Crop);
        Rotate = (AppCompatImageButton) findViewById(R.id.Rotate);
        Image = (CropImageView) findViewById(R.id.Image);
        Send = (FloatingActionButton) findViewById(R.id.Send);



        //Emoji keyboard setup



        Image.setGuidelines(CropImageView.Guidelines.ON);
        Image.setCropShape(CropImageView.CropShape.RECTANGLE);
        Image.setScaleType(CropImageView.ScaleType.FIT_CENTER);
        Image.setShowCropOverlay(true);
        Image.setAutoZoomEnabled(true);
        Image.setAspectRatio(AspectX, AspectY);
        Image.setMinCropResultSize(MinX, MinY);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropMode = !CropMode;
                getCropImage();
            }
        });

        Rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Rotation = (Rotation + 90) % 360;
                Image.setRotatedDegrees(Rotation);
            }
        });


        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.progress_overlay).setVisibility(View.VISIBLE);
                getCropImage();
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        final Intent intent = new Intent();
                        if (Output.isEmpty()) {
                            try {
                                File file = new File(new URI(Output));
                                if (file.exists()) {
                                    file.delete();
                                }

                                saveImage(new File(new URI(Output)), Image.getCroppedImage());
                                intent.putExtra("Uri", Output);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            try {
                                File file = new File(new URI(Output));
                                if (file.exists()) {
                                    file.delete();
                                }

                                saveImage(new File(new URI(Output)), Image.getCroppedImage());
                                intent.putExtra("Uri", Output);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Log.e("ImageHeight",Image.getHeight()+"");
                        Log.e("ImageWidth",Image.getWidth()+"");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        });
        getCropImage();
    }


    @Override
    public void onBackPressed() {
        if (CropMode) {
            CropMode = false;
            Image.setShowCropOverlay(false);
            Image.setGuidelines(CropImageView.Guidelines.OFF);
            Crop.setImageResource(R.drawable.ic_crop_black_24dp);
        } else {
            super.onBackPressed();
        }
    }

    private void getCropImage(){
        if (CropMode) {
            Image.setShowCropOverlay(true);
            Image.setGuidelines(CropImageView.Guidelines.ON);
            Image.setAspectRatio(AspectX, AspectY);
            Drawable drawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_crop_black_24dp);
            drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY));
            Crop.setImageDrawable(drawable);
        } else {
            Image.setShowCropOverlay(false);
            Image.setGuidelines(CropImageView.Guidelines.OFF);
            Crop.setImageResource(R.drawable.ic_crop_black_24dp);
            Image.clearAspectRatio();
            Image.setCropRect(Image.getWholeImageRect());
        }
    }

    public void saveImage(File file, Bitmap image) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }
}
