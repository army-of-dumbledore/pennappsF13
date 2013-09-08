package edu.upenn.seas.pennapps.dumbledore.pollio;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by joe on 9/7/13.
 */
public class Picture_poll extends Activity {
    private static final int CAMERA_REQUEST = 1888;
    String JPEG_FILE_PREFIX = "po";
    String JPEG_FILE_SUFFIX = ".jpg";
    Bitmap mImageBitmap;


    List<ImageView> viewList;
	private ImageView mImageView;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_poll);

        GridView gridview = (GridView) findViewById(R.id.pictureOptionGrid);
        gridview.setAdapter(new ImageAdapter(this));

    }
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
    public void dispatchTakePictureIntent(int actionCode)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, actionCode);
    }




    private void handleSmallCameraPhoto(Intent intent)
    {
        Bundle extras = intent.getExtras();
        mImageBitmap = (Bitmap)extras.get("data");
        mImageView.setImageBitmap(mImageBitmap);
    }
    public void dispatchChoosePictureIntent()
    {

    }
}
