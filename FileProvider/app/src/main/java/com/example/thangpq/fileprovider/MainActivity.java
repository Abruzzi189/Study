package com.example.thangpq.fileprovider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ImageView ivImage;
    Button btnTakePhoto,btnOpenPhoto;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivImage = findViewById(R.id.iv_image);
        btnTakePhoto = findViewById(R.id.btn_takePhoto);
        btnOpenPhoto = findViewById(R.id.btn_openPhoto);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();

            }
        });
        btnOpenPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhoto();
            }
        });
    }

    private void openPhoto() {
        Intent intent = new Intent();

        intent.setType("image/*"); // mở tất cả những thư mục chứa ảnh
        intent.setAction(Intent.ACTION_GET_CONTENT); // đi đến thư mục chọn

        startActivityForResult(intent,2);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // mở ra app chụp ảnh, sau khi chụp xong thfi nó lưu ảnh đó ở trong đường dẫn uri
         uri = ImageUtils.getUriFromImage(this);
        Log.d(TAG, "takePhoto: "+uri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
        {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = ImageUtils.getBitmap();
                ivImage.setImageBitmap(bitmap);
            }
        }else if(requestCode==2)
        {
            if (resultCode == RESULT_OK) {
                   // Picasso.get().load(data.getData()).into(ivImage);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    bitmap = Bitmap.createScaledBitmap(bitmap,500,500,false);
                    ivImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
