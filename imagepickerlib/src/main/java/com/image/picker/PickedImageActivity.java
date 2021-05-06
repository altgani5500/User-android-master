package com.image.picker;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Techugo on 9/notifications_icon/2018.
 */

public class PickedImageActivity extends AppCompatActivity {
    private ImageView imageView;
    private ProgressBar mProgressView;
    private Uri imageUri;
    private boolean compressImage;
    private TextView tvDone, tvCancel;
    File mFileCaptured;
    private Context context;
    private String tag;
    private int picWidth, picHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picked_image_activity);
        context = this;
        imageView = (ImageView) findViewById(R.id.imageView);
        mProgressView = (ProgressBar) findViewById(R.id.crop_progress);
        tvDone = (TextView) findViewById(R.id.tvDone);
        tvCancel = (TextView) findViewById(R.id.tvCancel);

        imageUri = (Uri) getIntent().getParcelableExtra("uri");
        compressImage = getIntent().getBooleanExtra("compressImage", false);
        tag = getIntent().getStringExtra("tag");
        picWidth = getIntent().getIntExtra("picWidth", 500);
        picHeight = getIntent().getIntExtra("picHeight", 500);

        if (compressImage) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mFileCaptured = writeToFile(imageUri);
                    mFileCaptured = compressToExistingFile(mFileCaptured.getPath(), mFileCaptured);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvDone.setVisibility(View.VISIBLE);
                            tvCancel.setVisibility(View.VISIBLE);
                            mProgressView.setVisibility(View.GONE);
                            imageView.setImageBitmap(BitmapFactory.decodeFile(mFileCaptured.getAbsolutePath()));
                        }
                    });
                }
            }).start();

        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mFileCaptured = writeToFile(imageUri);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvDone.setVisibility(View.VISIBLE);
                            tvCancel.setVisibility(View.VISIBLE);
                            mProgressView.setVisibility(View.GONE);
                            imageView.setImageBitmap(BitmapFactory.decodeFile(mFileCaptured.getAbsolutePath()));
                        }
                    });
                }
            }).start();
        }

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("filePath", mFileCaptured.getAbsolutePath());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private File writeToFile(Uri fileUri) {
        File selectedFile = null;
        String fileName = queryName(context.getContentResolver(), fileUri);
        String filenameArray[] = fileName.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];
        fileName = tag + "." + extension;

        try {
            InputStream input = context.getContentResolver().openInputStream(fileUri);
            selectedFile = new File(context.getCacheDir(), fileName);
            OutputStream output = new FileOutputStream(selectedFile);

            byte[] buffer = new byte[4 * 1024]; // or other buffer size
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
            output.close();
            input.close();

            if (fileName.contains(".jpg") || fileName.contains(".JPG")
                    || fileName.contains(".jpeg") || fileName.contains(".JPEG")
                    || fileName.contains(".png") || fileName.contains(".PNG")) {

                Bitmap bm = decodeFile(selectedFile.getAbsolutePath(), picWidth, picHeight, ImagePicker.ScalingLogic.CROP);
                bm = fixOrientation(bm, selectedFile.getAbsolutePath());
                FileOutputStream outputStream = new FileOutputStream(selectedFile);
                bm.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            return null;
        }
        return selectedFile;
    }

    private File compressToExistingFile(String filePath, File file) {
        try {
            Bitmap bm = decodeFile(filePath, picWidth, picHeight, ImagePicker.ScalingLogic.CROP);
            bm = fixOrientation(bm, filePath);
            FileOutputStream outputStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            return null;
        }
        return file;
    }

    private File compressToFile(String filePath) {
        File selectedFile = null;
        try {
            Bitmap bm = decodeFile(filePath, picWidth, picHeight, ImagePicker.ScalingLogic.CROP);
            bm = fixOrientation(bm, filePath);
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

    public Bitmap decodeFile(String path, int dstWidth, int dstHeight,
                             ImagePicker.ScalingLogic scalingLogic) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth,
                dstHeight, scalingLogic);
        Bitmap unscaledBitmap = BitmapFactory.decodeFile(path, options);
        return unscaledBitmap;
    }

    public enum ScalingLogic {
        CROP, FIT
    }

    public int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight,
                                   ImagePicker.ScalingLogic scalingLogic) {
        if (scalingLogic == ImagePicker.ScalingLogic.FIT) {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return srcWidth / dstWidth;
            } else {
                return srcHeight / dstHeight;
            }
        } else {
            final float srcAspect = (float) srcWidth / (float) srcHeight;
            final float dstAspect = (float) dstWidth / (float) dstHeight;

            if (srcAspect > dstAspect) {
                return srcHeight / dstHeight;
            } else {
                return srcWidth / dstWidth;
            }
        }
    }


    private void deleteDirRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteDirRecursive(child);

        fileOrDirectory.delete();
    }

    private String queryName(ContentResolver resolver, Uri uri) {
        if (uri.getScheme().equalsIgnoreCase("content")) {
            Cursor returnCursor =
                    resolver.query(uri, null, null, null, null);
            assert returnCursor != null;
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();
            return name;
        } else {
            String filenameArray[] = uri.toString().split("\\/");
            String fileName = filenameArray[filenameArray.length - 1];
            return fileName;
        }
    }


    public Bitmap fixOrientation(Bitmap bm, String filePath) {
        Bitmap bitmap = null;
        ExifInterface ei = null;

        try {
            ei = new ExifInterface(filePath);
            if (ei != null) {
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        bitmap = rotateImage(bm, 90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        bitmap = rotateImage(bm, 180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        bitmap = rotateImage(bm, 270);
                        break;
                    case ExifInterface.ORIENTATION_NORMAL:
                        bitmap = bm;
                        break;
                 /*   case ExifInterface.ORIENTATION_UNDEFINED:
                       // if(bm.getWidth()>bm.getHeight())
                        bitmap = rotateImage(bm, 0);
                        break;*/
                    default:
                        bitmap = bm;
                        break;
                }
                if (bitmap != null)
                    return bitmap;
                else
                    return bm;
            } else {
                return bm;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return bm;
        }
    }

    public Bitmap rotateImage(Bitmap source, float angle) {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }


}
