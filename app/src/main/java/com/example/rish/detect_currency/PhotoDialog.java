package com.example.rish.detect_currency;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

/**
 * Created by ${Tushar} on 05/08/17.
 */

public class PhotoDialog {
    private View.OnClickListener Camera, Gallery;
    private Dialog dialog;
    public  PhotoDialog(final Context context) {
        dialog=new Dialog(context);
        dialog.setContentView(R.layout.layout_photo_dialog);
        dialog.setCancelable(true);
        if( dialog.getWindow()!=null)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);



        dialog.findViewById(R.id.Camera).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(Camera!=null)
                    Camera.onClick(view);
                //sendRequest();
                if(dialog.isShowing())
                    dismiss();

            }
        });
        dialog.findViewById(R.id.Gallery).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(Gallery!=null)
                    Gallery.onClick(view);

                if(dialog.isShowing())
                    dismiss();
            }
        });
    }
    public void setOnCameraClickListener(View.OnClickListener listener)
    {
        Camera=listener;
    }

    public void setOnGalleryClickListener(View.OnClickListener listener)
    {
        Gallery=listener;
    }

    public void show()
    {
        dialog.show();
    }

    public void dismiss()
    {
        dialog.dismiss();
    }

    public Dialog getDialog()
    {
        return dialog;
    }
}
