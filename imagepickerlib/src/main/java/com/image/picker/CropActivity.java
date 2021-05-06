package com.image.picker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Techugo on 9/notifications_icon/2018.
 */

public class CropActivity extends AppCompatActivity {
    private CropImageView mCropImageView;
    private ProgressBar mProgressView;
    private Uri mCropImageUri;
    private TextView tvDone, tvCancel;
    private String tag;
    private int picWidth, picHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_activity);
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        mProgressView = (ProgressBar) findViewById(R.id.crop_progress);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvCancel = (TextView) findViewById(R.id.tvCancel);

        mCropImageUri = (Uri) getIntent().getParcelableExtra("uri");
        tag = getIntent().getStringExtra("tag");
        picWidth = getIntent().getIntExtra("picWidth", 500);
        picHeight = getIntent().getIntExtra("picHeight", 500);

        mCropImageView.setImageUriAsync(mCropImageUri);
        mCropImageView.setOnSetImageUriCompleteListener(new CropImageView.OnSetImageUriCompleteListener() {
            @Override
            public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
                tvDone.setVisibility(View.VISIBLE);
                mProgressView.setVisibility(View.GONE);
                mCropImageView.setVisibility(View.VISIBLE);
            }
        });

        mCropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
            @Override
            public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                File file = writeToFile(CropActivity.this, result.getBitmap());
                Intent intent = new Intent();
                intent.putExtra("uri", file.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCropImageView.getCroppedImageAsync(picWidth, picHeight, CropImageView.RequestSizeOptions.RESIZE_FIT);
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private  File writeToFile(Context context, Bitmap bitmap) {
        File selectedFile = null;
        try {
            Bitmap bm = bitmap;
            FileOutputStream outputStream;

            selectedFile = new File(context.getCacheDir(), tag + ".jpg");
            outputStream = new FileOutputStream(selectedFile);
            bm.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            return null;
        }
        return selectedFile;
    }

}
