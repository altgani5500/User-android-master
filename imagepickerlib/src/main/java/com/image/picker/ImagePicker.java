package com.image.picker;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;

public class ImagePicker {

    File mFileCaptured;
    Uri mImageCaptureUri;
    ImagePickerListener imagePickerListener;
    Activity context;
    File fileProfilePic = null;
    String tag = "picture";
    File appDir;
    int picWidth = 500;
    int picHeight = 500;
    boolean cropImage = false;
    boolean compressImage = false;
    ProgressBar progressBar;

    public static final String MODE_PIC = "PIC";
    public static final String MODE_CAMERA = "CAMERA";
    public static final String MODE_GALLERY = "GALLERY";
    public static final String MODE_DOCUMENT = "DOCUMENT";
    public static final String MODE_FILE = "FILE";
    public static final String MODE_AUDIO_FILE = "AUDIO_FILE";
    public static final String MODE_VIDEO_FILE = "VIDEO_FILE";

    String mode = MODE_PIC;

    static String storagePermInfoMsg = "App needs this permission to access and store files on your phone's storage. Are you sure you want to deny this permission ?";
    static String storagePermErrorMsg = "Storage permission denied. You can enable permission from settings";

    static String cameraPermInfoMsg = "App needs this permission to capture photos using phone's camera. Are you sure you want to deny this permission ?";
    static String cameraPermErrorMsg = "Camera permission denied. You can enable permission from settings";

    public ImagePicker(Context context) {
        this.context = (Activity) context;
    }

    public ImagePicker setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public ImagePicker setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public ImagePicker setCrop(boolean cropImage) {
        this.cropImage = cropImage;
        return this;
    }

    public ImagePicker setCompress(boolean compressImage) {
        this.compressImage = compressImage;
        return this;
    }

    public ImagePicker setCompressWidthHeight(int picWidth, int picHeight) {
        this.picWidth = picWidth;
        this.picHeight = picHeight;
        return this;
    }

    public ImagePicker setImagePickerListener(ImagePickerListener imagePickerListener) {
        this.imagePickerListener = imagePickerListener;
        return this;
    }

    public ImagePicker pick() {

        if (mode.equals(MODE_PIC)) {
            final CharSequence[] items = {"Capture photo from camera", "Choose photo from gallery"};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(null);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Capture photo from camera")) {
                        pickFromCamera();
                    } else if (items[item].equals("Choose photo from gallery")) {
                        pickFromGallery();
                    }
                }
            });
            builder.show();
        } else if (mode.equals(MODE_CAMERA)) {
            pickFromCamera();
        }else if (mode.equals(MODE_GALLERY)) {
            pickFromGallery();
        }
        else if (mode.equals(MODE_FILE)) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 222);
                return this;
            }
            pickFile();
        } else if (mode.equals(MODE_AUDIO_FILE)) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 222);
                return this;
            }
            pickAudioFile();
        } else if (mode.equals(MODE_DOCUMENT)) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 222);
                return this;
            }
            pickDocument();
        } else if (mode.equals(MODE_VIDEO_FILE)) {
            final CharSequence[] items = {"Capture video from camera", "Choose video from gallery"};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(null);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Capture video from camera")) {
                        pickVideoFromCamera();
                    } else if (items[item].equals("Choose video from gallery")) {
                        pickVideoFromGallery();
                    }
                }
            });
            builder.show();
        }

        return this;
    }

    public ImagePicker onRequestPermissionsResult(final int requestCode, final String[] permissions, int[] grantResults) {
        if (requestCode == 111) {
            if (grantResults != null && grantResults.length == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickFromCamera();
                else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[0])) {
                      AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission required");
                        alertBuilder.setMessage(cameraPermInfoMsg);

                        alertBuilder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{permissions[0]}, requestCode);
                            }
                        });
                        alertBuilder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        if (permissions[0].equals(Manifest.permission.CAMERA))
                            Toast.makeText(context, cameraPermErrorMsg, Toast.LENGTH_LONG).show();
                    }
                }
            } else if (grantResults != null && grantResults.length > 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    pickFromCamera();
                else {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[0])) {
                           AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                            alertBuilder.setCancelable(true);
                            alertBuilder.setTitle("Permission required");
                            alertBuilder.setMessage(cameraPermInfoMsg);

                            alertBuilder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions((Activity) context, new String[]{permissions[0]}, requestCode);
                                }
                            });
                            alertBuilder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert = alertBuilder.create();
                            alert.show();
                        } else {
                            if (permissions[0].equals(Manifest.permission.CAMERA))
                                Toast.makeText(context, cameraPermErrorMsg, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[1])) {
                           AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                            alertBuilder.setCancelable(true);
                            alertBuilder.setTitle("Permission required");
                            alertBuilder.setMessage(storagePermInfoMsg);

                            alertBuilder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions((Activity) context, new String[]{permissions[1]}, requestCode);
                                }
                            });
                            alertBuilder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alert = alertBuilder.create();
                            alert.show();
                        } else {
                            if (grantResults != null && grantResults.length > 1 && permissions[1].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                                Toast.makeText(context, storagePermErrorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        } else if (requestCode == 222) {
            if (grantResults != null && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mode.equals(MODE_PIC)) {
                    pickFromGallery();
                } else if (mode.equals(MODE_FILE)) {
                    pickFile();
                } else if (mode.equals(MODE_AUDIO_FILE)) {
                    pickAudioFile();
                } else if (mode.equals(MODE_DOCUMENT)) {
                    pickDocument();
                }
            } else {
                if (grantResults != null && grantResults.length == 1 && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissions[0])) {
                   AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission required");
                    if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        alertBuilder.setMessage(storagePermInfoMsg);

                    alertBuilder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{permissions[0]}, requestCode);
                        }
                    });
                    alertBuilder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        Toast.makeText(context, storagePermErrorMsg, Toast.LENGTH_LONG).show();
                }
            }
        }

        return this;
    }

    private void showLoading(Context context, RelativeLayout layout) {
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(progressBar, params);
        progressBar.setVisibility(View.VISIBLE);  //To show ProgressBar
        progressBar.setVisibility(View.GONE);
    }


    public void pickFromCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            return;
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            return;
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 111);
            return;
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    String appDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name);
                    appDir = new File(appDirPath);
                    if (!appDir.exists()) {
                        appDir.mkdirs();
                    }

                    mFileCaptured = new File(appDir, tag + ".jpg");

                    mImageCaptureUri = FileProvider.getUriForFile(context,
                            context.getApplicationContext().getPackageName() + ".provider",
                            mFileCaptured);
                }
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                    intent.setClipData(ClipData.newRawUri("", mImageCaptureUri));
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                intent.putExtra("return-data", true);
                context.startActivityForResult(intent, 100);

            } catch (Exception e) {
                Log.e("camera_pick", e.getLocalizedMessage());
            }
        }
    }

    public void pickFromGallery() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            context.startActivityForResult(Intent.createChooser(intent, "Select File"), 200);
    }

    public void pickVideoFromCamera() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            return;
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
            return;
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 111);
            return;
        } else {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 40);
            if (takeVideoIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivityForResult(takeVideoIntent, 700);
            } else {
                //AlertUtil.showToastLong(CreatePostActivity.this, "Unable to open camera.");
            }
        }
    }

    public void pickVideoFromGallery() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 222);
            return;
        }
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        context.startActivityForResult(Intent.createChooser(intent, "Select File"), 800);
    }

    public void pickFile() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        context.startActivityForResult(Intent.createChooser(intent, "Select File"), 300);
    }

    public void pickAudioFile() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        context.startActivityForResult(Intent.createChooser(intent, "Select File"), 300);
    }

    public void pickDocument() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/pdf", "application/msword"};
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }

        context.startActivityForResult(Intent.createChooser(intent, "Select Document"), 400);
    }


    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        //Crop Activity Result
        if (requestCode == 500 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra("uri");
            fileProfilePic = compressToFile(filePath);
            imagePickerListener.onImagePicked(fileProfilePic, tag);
        }
        //Picked Image Activity Result
        else if (requestCode == 600 && resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK) {
                String filePath = data.getStringExtra("filePath");
                fileProfilePic = new File(filePath);
               imagePickerListener.onImagePicked(fileProfilePic, tag);
            }
        }
        //Camera Intent Result
        else if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap bm = null;
            if (cropImage) {
                Intent intent = new Intent(context, CropActivity.class);
                intent.putExtra("uri", mImageCaptureUri);
                intent.putExtra("tag", tag);
                intent.putExtra("picWidth", picWidth);
                intent.putExtra("picHeight", picHeight);
                context.startActivityForResult(intent, 500);
            } else {
                //Go to Preview Activity
                Intent intent = new Intent(context, PickedImageActivity.class);
                intent.putExtra("uri", mImageCaptureUri);
                intent.putExtra("compressImage", compressImage);
                intent.putExtra("tag", tag);
                intent.putExtra("picWidth", picWidth);
                intent.putExtra("picHeight", picHeight);
                context.startActivityForResult(intent, 600);

                   /* if (compressImage) {
                        mFileCaptured = writeToFile(mImageCaptureUri);
                        mFileCaptured = compressToExistingFile(mFileCaptured.getPath(), mFileCaptured);
                        imagePickerListener.onImagePicked(mFileCaptured, tag);
                    } else {
                        mFileCaptured = writeToFile(mImageCaptureUri);
                        imagePickerListener.onImagePicked(mFileCaptured, tag);
                    }*/
            }

        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            if (data != null) {
                if (cropImage) {
                    Intent intent = new Intent(context, CropActivity.class);
                    intent.putExtra("uri", data.getData());
                    intent.putExtra("tag", tag);
                    intent.putExtra("picWidth", picWidth);
                    intent.putExtra("picHeight", picHeight);
                    context.startActivityForResult(intent, 500);
                } else {
                    //Go to Preview Activity
                    Intent intent = new Intent(context, PickedImageActivity.class);
                    intent.putExtra("uri", data.getData());
                    intent.putExtra("compressImage", compressImage);
                    intent.putExtra("tag", tag);
                    intent.putExtra("picWidth", picWidth);
                    intent.putExtra("picHeight", picHeight);
                    context.startActivityForResult(intent, 600);
                }
            }

        } else if ((requestCode == 300 || requestCode == 400 || requestCode == 700 || requestCode == 800) && resultCode == RESULT_OK) {
            if (data != null) {
                try {
                    File selectedFile = null;
                    Uri uri = data.getData();
                  String fileName = queryName(context.getContentResolver(), uri);
                  //String filePath = generatePath(uri, context);

                  /*  String filenameArray[] = filePath.split("\\.");
                    String extension = filenameArray[filenameArray.length - 1];*/
                   //   fileName = tag +".pdf";

                    InputStream input = context.getContentResolver().openInputStream(uri);
                    try {
                        selectedFile = new File(context.getFilesDir(), fileName);
                        OutputStream output = new FileOutputStream(selectedFile);
                        try {
                            byte[] buffer = new byte[4 * 1024]; // or other buffer size
                            int read;
                            while ((read = input.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }
                            output.flush();
                        } finally {
                            output.close();
                        }
                    } finally {
                        input.close();
                    }
                    fileProfilePic = selectedFile;
                    imagePickerListener.onImagePicked(fileProfilePic,fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
    Get File Path from URI returned
     */
    public String generatePath(Uri uri, Context context) {
        String filePath = null;
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat) {
            filePath = generateFromKitkat(uri, context);
        }

        if (filePath != null) {
            return filePath;
        }

        Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.MediaColumns.DATA}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath == null ? uri.getPath() : filePath;
    }

    @TargetApi(19)
    private String generateFromKitkat(Uri uri, Context context) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String wholeID = DocumentsContract.getDocumentId(uri);

            String id = wholeID.split(":")[1];

            String[] column = {MediaStore.Video.Media.DATA};
            String sel = MediaStore.Video.Media._ID + "=?";
            Cursor cursor = null;
            if (context.getContentResolver().getType(uri).contains("audio"))
                cursor = context.getContentResolver().
                        query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{id}, null);
            else {
                cursor = context.getContentResolver().
                        query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{id}, null);
            }

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }

            cursor.close();
        }
        return filePath;
    }

    private File writeToFile(Uri fileUri) {
        File selectedFile = null;
        String fileName = queryName(context.getContentResolver(), fileUri);
        String filenameArray[] = fileName.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];
        fileName = tag + "." + extension;

        try {
            InputStream input = context.getContentResolver().openInputStream(fileUri);
            selectedFile = new File(context.getFilesDir(), fileName);
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

                Bitmap bm = decodeFile(selectedFile.getAbsolutePath(), 800, 800, ScalingLogic.CROP);
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
            Bitmap bm = decodeFile(filePath, picWidth, picHeight, ScalingLogic.CROP);
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
            Bitmap bm = decodeFile(filePath, picWidth, picHeight, ScalingLogic.CROP);
            if (bm != null) {
                bm = fixOrientation(bm, filePath);
                FileOutputStream outputStream;

                selectedFile = new File(context.getFilesDir(), tag + ".jpg");
                outputStream = new FileOutputStream(selectedFile);
                bm.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } else
                return null;
        } catch (Exception e) {
            return null;
        }
        return selectedFile;
    }

    public Bitmap decodeFile(String path, int dstWidth, int dstHeight,
                             ScalingLogic scalingLogic) {
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
                                   ScalingLogic scalingLogic) {
        if (scalingLogic == ScalingLogic.FIT) {
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

    public interface ImagePickerListener {
        void onImagePicked(File imageFile, String tag);
    }
}

