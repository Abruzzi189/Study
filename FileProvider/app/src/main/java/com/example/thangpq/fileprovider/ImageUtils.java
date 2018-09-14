package com.example.thangpq.fileprovider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    public static File tempFileImage;
    // gọi hàm này thì sẽ tạo ra 1 ảnh trắng k có gì
    public static Uri getUriFromImage(Context context)
    {
        //create file temp
        tempFileImage=null;
        try {
            //luu tam ảnh vào thư mục picture
             tempFileImage = File.createTempFile(
                    Calendar.getInstance().getTime().toString(),
                    ".jpg",
                     context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempFileImage.deleteOnExit(); // dùng xogn thì xóa

        Uri uri =null;
        // authority: packageName+ ".provider"
        if(tempFileImage!=null)
        {
            uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName()+".provider",
                    tempFileImage
            );
        }

        return uri;
    }
    public static Bitmap getBitmap()
    {
        Bitmap bitmap = BitmapFactory.decodeFile(tempFileImage.getPath());
        return bitmap;
    }
}
