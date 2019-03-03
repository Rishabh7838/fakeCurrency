package com.example.rish.detect_currency;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class Classifier {

    private static final String TAG = "Classifier";

    /** Name of the model file stored in Assets. */
    private static final String MODEL_PATH = "converted_model_freeze_v4_2.tflite";

    /** Dimensions of inputs. */
    public static final int DIM_BATCH_SIZE = 1;

    public static final int DIM_CHANNEL_SIZE = 3;

    public static final int DIM_IMG_SIZE_X = 299;
    public static final int DIM_IMG_SIZE_Y = 299;

    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;

    /* Preallocated buffers for storing image data in. */
    private int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];

    /** An instance of the driver class to run model inference with Tensorflow Lite. */
    private Interpreter tflite;

    /** Labels corresponding to the output of the vision model. */
    private List<String> labelList;

    /** A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs. */
    private ByteBuffer imgData = null;

    /** An array to hold inference results, to be feed into Tensorflow Lite as outputs. */
    private float[][] classificationOutput;
    /** multi-stage low pass filter **/


    /** Initializes an {@code ImageClassifier}. */
    Classifier(Activity activity) throws IOException {
        tflite = new Interpreter(loadModelFile(activity));
//        labelList = loadLabelList(activity);
        imgData =
                ByteBuffer.allocateDirect(
                        4*DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_CHANNEL_SIZE);
        imgData.order(ByteOrder.nativeOrder());
        classificationOutput = new float[1][1];
//        labelProbArray = new float[1][labelList.size()];
//        filterLabelProbArray = new float[FILTER_STAGES][labelList.size()];
        Log.d(TAG, "Created a Tensorflow Lite Image Classifier.");
    }

    /** Classifies a frame from the preview stream. */
    float classifyFrame(Bitmap bitmap) {
        if (tflite == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.");
        } else {
            convertBitmapToByteBuffer(bitmap);
            // Here's where the magic happens!!!
            long startTime = SystemClock.uptimeMillis();
            tflite.run(imgData, classificationOutput);
            long endTime = SystemClock.uptimeMillis();
            Log.d(TAG, "Timecost to run model inference: " + Long.toString(endTime - startTime));
            Log.d(TAG, "classifyFrame: "+classificationOutput[0][0]);
        }

        return classificationOutput[0][0];
    }



    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        // Convert the image to floating point.
        int pixel = 0;
        long startTime = SystemClock.uptimeMillis();

//        https://stackoverflow.com/questions/6126439/what-does-0xff-do

        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
////                Red
//                imgData.putFloat(preprocessInput((val >> 16) & 0xFF));
//
////              blue
//                imgData.putFloat(preprocessInput((val >> 16) & 0xFF));
//
////                Green
//                imgData.putFloat(preprocessInput((val >> 16) & 0xFF));
                imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
            }
        }
        long endTime = SystemClock.uptimeMillis();
        Log.d(TAG, "Timecost to put values into ByteBuffer: " + Long.toString(endTime - startTime));
    }

    //    Normalize input (-1 to +1)
    private float preprocessInput(float value){
        value /= 127.5;
        value -= 1;
        return value;
    }

    /** Closes tflite to release resources. */
    public void close() {
        tflite.close();
        tflite = null;
    }
}
