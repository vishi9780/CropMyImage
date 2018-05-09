package com.example.think360user.cropmyimage;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.cropmyimage2.AppController;
import com.example.cropmyimage2.CropImageView;
import com.example.cropmyimage2.callback.CropCallback;
import com.example.cropmyimage2.callback.LoadCallback;
import com.example.cropmyimage2.callback.SaveCallback;
import com.example.cropmyimage2.util.Logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CAMERA = 1;
    private static final int GALLERY = 2;
    private static final String PROGRESS_DIALOG = "ProgressDialog";
    private static final String KEY_FRAME_RECT = "FrameRect";
    private static final String KEY_SOURCE_URI = "SourceUri";
    public static final String TAG=MainActivity.class.getSimpleName();
    private CropImageView mCropView;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private RectF mFrameRect = null;
    private Uri mSourceUri = null;
    private Bitmap bitmap;

    ImageButton iv_buttonDone,iv_buttonRotateRight,iv_buttonRotateLeft,iv_buttonclose;
    Button buttonShowCircleButCropAsSquare,buttonCircle,buttonFree,buttonCustom,button16_9,button9_16,button4_3
    ,button3_4,button1_1,buttonFitImage,buttonPickImage,btn_crop;
    ImageView iv_isCrop;
    LinearLayout ll_isCrop;
    boolean isCrop=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppController.constant.checkAndRequestPermissions(MainActivity.this);
        findIDS();
    }

    //finding id of views here
    private void findIDS() {
        mCropView=(CropImageView)findViewById(R.id.cropImageView);
        iv_buttonRotateLeft=(ImageButton)findViewById(R.id.iv_buttonRotateLeft);
        iv_buttonclose=(ImageButton)findViewById(R.id.iv_buttonclose);
        iv_buttonRotateRight=(ImageButton)findViewById(R.id.iv_buttonRotateRight);
        iv_buttonDone=(ImageButton)findViewById(R.id.iv_buttonDone);
        iv_isCrop=(ImageView)findViewById(R.id.iv_isCrop);
        ll_isCrop=(LinearLayout)findViewById(R.id.ll_isCrop);

        buttonShowCircleButCropAsSquare=(Button) findViewById(R.id.buttonShowCircleButCropAsSquare);
        btn_crop=(Button) findViewById(R.id.btn_crop);
        buttonCircle=(Button) findViewById(R.id.buttonCircle);
        buttonFree=(Button) findViewById(R.id.buttonFree);
        buttonCustom=(Button) findViewById(R.id.buttonCustom);
        button16_9=(Button) findViewById(R.id.button16_9);
        button9_16=(Button) findViewById(R.id.button9_16);
        button4_3=(Button) findViewById(R.id.button4_3);
        button3_4=(Button) findViewById(R.id.button3_4);
        button1_1=(Button) findViewById(R.id.button1_1);
        buttonFitImage=(Button) findViewById(R.id.buttonFitImage);
        buttonPickImage=(Button) findViewById(R.id.buttonPickImage);
                iv_buttonDone.setOnClickListener(this);
                iv_buttonRotateRight.setOnClickListener(this);
                iv_buttonclose.setOnClickListener(this);
                iv_buttonRotateLeft.setOnClickListener(this);


                btn_crop.setOnClickListener(this);
                buttonShowCircleButCropAsSquare.setOnClickListener(this);
                buttonCircle.setOnClickListener(this);
                buttonFree.setOnClickListener(this);
                buttonCustom.setOnClickListener(this);
                button16_9.setOnClickListener(this);
                button9_16.setOnClickListener(this);
                button4_3.setOnClickListener(this);
                button3_4.setOnClickListener(this);
                button1_1.setOnClickListener(this);
                buttonFitImage.setOnClickListener(this);
                buttonPickImage.setOnClickListener(this);
    }
    //alert camera and gallery
    private void alertDialog(Context c) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());
                    capureImage();
                } else if (options[item].equals("Choose from Gallery")) {
                    galleryImage();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void capureImage() {
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Folder/";
        File newdir = new File(dir);
        newdir.mkdirs();
        String file = dir + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".jpg";


        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
        }

        mSourceUri = Uri.fromFile(newfile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mSourceUri);

        startActivityForResult(cameraIntent, CAMERA);
    }

    private void galleryImage() {
        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GALLERY:
                if (null != data) {
                    mSourceUri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mSourceUri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCropView
                            .load(mSourceUri)
                            .initialFrameRect(mFrameRect)
                            .useThumbnail(true)
                            .execute(mLoadCallback);
                }

                break;
            case CAMERA:
                if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
                    Log.e(TAG, "onActivityResult: "+mSourceUri );
                    mCropView
                            .load(mSourceUri)
                            .initialFrameRect(mFrameRect)
                            .useThumbnail(true)
                            .execute(mLoadCallback);

                }
                break;
            default:
                break;
        }
    }

    // Callbacks ///////////////////////////////////////////////////////////////////////////////////
    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override public void onSuccess() {
            Log.e(TAG, "onSuccess:57>>"+"" );
            if (isCrop){
                mCropView.setCropMode(CropImageView.CropMode.SQUARE);
                mCropView.crop(mSourceUri).execute(mCropCallback);
                ll_isCrop.setVisibility(View.GONE);
                iv_isCrop.setVisibility(View.VISIBLE);

            }

        }

        @Override public void onError(Throwable e) {
            Log.e(TAG, "onError:60>>"+e );
        }
    };
    private final CropCallback mCropCallback = new CropCallback() {
        @Override public void onSuccess(Bitmap cropped) {
                if (isCrop!=true) {
                    mCropView.save(cropped)
                            .compressFormat(mCompressFormat)
                            .execute(createSaveUri(), mSaveCallback);
                    //put api here after cropping the image is success
                }else {
                    ll_isCrop.setVisibility(View.GONE);
                    iv_isCrop.setVisibility(View.VISIBLE);
                    iv_isCrop.setImageBitmap(cropped);
                    isCrop=false;
                }

        }

        @Override public void onError(Throwable e) {
            Toast.makeText(MainActivity.this, "Error While Cropping", Toast.LENGTH_SHORT).show();
        }
    };
    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override public void onSuccess(Uri outputUri) {
            Log.e(TAG, "onSuccess: 109>>" +outputUri);
        }

        @Override public void onError(Throwable e) {
        }
    };
    public Uri createSaveUri() {
        return createNewUri(MainActivity.this, mCompressFormat);
    }
    public static Uri createNewUri(Context context, Bitmap.CompressFormat format) {
        long currentTimeMillis = System.currentTimeMillis();
        Date today = new Date(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String title = dateFormat.format(today);
        String dirPath = getDirPath();
        String fileName = "scv" + title + "." + getMimeType(format);
        String path = dirPath + "/" + fileName;
        File file = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + getMimeType(format));
        values.put(MediaStore.Images.Media.DATA, path);
        long time = currentTimeMillis / 1000;
        values.put(MediaStore.MediaColumns.DATE_ADDED, time);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, time);
        if (file.exists()) {
            values.put(MediaStore.Images.Media.SIZE, file.length());
        }

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Logger.e("SaveUri = " + uri);
        return uri;
    }
    public static String getDirPath() {
        String dirPath = "";
        File imageDir = null;
        File extStorageDir = Environment.getExternalStorageDirectory();
        if (extStorageDir.canWrite()) {
            imageDir = new File(extStorageDir.getPath() + "/Demo");
        }
        if (imageDir != null) {
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            if (imageDir.canWrite()) {
                dirPath = imageDir.getPath();
            }
        }
        return dirPath;
    }
    public static String getMimeType(Bitmap.CompressFormat format) {
        Logger.e("getMimeType CompressFormat = " + format);
        switch (format) {
            case JPEG:
                return "jpeg";
            case PNG:
                return "png";
        }
        return "png";
    }
    /****
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::
     * ::::::Detect whether internet is connected or not:::::
     * ::::::::::::::::::::::::::::::::::::::::::::::::::::::
     *****/
    public static boolean checkInternetConnect(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crop:
                isCrop=true;
                break;
            case R.id.buttonPickImage:
                alertDialog(MainActivity.this);
                break;
            case R.id.iv_buttonDone:
                //1:1
                if (bitmap!=null) {
                    mCropView.setCropMode(CropImageView.CropMode.SQUARE);
                    mCropView.crop(mSourceUri).execute(mCropCallback);
                }else{
                    Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.buttonFitImage:
                if (bitmap!=null) {
                    mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
                }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.button1_1:
                if (bitmap!=null){
                mCropView.setCropMode(CropImageView.CropMode.SQUARE);
                }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.button3_4:
                if (bitmap!=null) {
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.button4_3:
                if (bitmap!=null) {
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.button9_16:
                if (bitmap!=null) {
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.button16_9:
                if (bitmap!=null) {
                mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.buttonCustom:
                if (bitmap!=null) {
                mCropView.setCustomRatio(7, 5);
                }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.buttonFree:
                if (bitmap!=null) {
                mCropView.setCropMode(CropImageView.CropMode.FREE);
                }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.buttonCircle:
                if (bitmap!=null) {
                mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
                }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.buttonShowCircleButCropAsSquare:
                if (bitmap!=null) {
                mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
                 }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.iv_buttonRotateLeft:
                if (bitmap!=null) {
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                    }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;

            case R.id.iv_buttonRotateRight:
                if (bitmap!=null) {
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                    }else {Toast.makeText(MainActivity.this, "please select first to crop the image", Toast.LENGTH_SHORT).show();}
                break;
            case R.id.iv_buttonclose:
                finish();
                overridePendingTransition( 0, 0);
                startActivity(getIntent());
                overridePendingTransition( 0, 0);
                break;
        }
    }
}
