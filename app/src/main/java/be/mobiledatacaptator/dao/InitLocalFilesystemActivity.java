package be.mobiledatacaptator.dao;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcExceptionLogger;
import be.mobiledatacaptator.utilities.MdcUtil;

/**
 * Created by rvanloon on 22/09/2016.
 */

public class InitLocalFilesystemActivity extends Activity {

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyStoragePermissions(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE){
            ckeckIfPathExists(data);
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void ckeckIfPathExists(Intent data){
        String msg = "Zorg dat volgend path bestaat:\r\n";
        msg += Environment.getExternalStorageDirectory() + getString(R.string.dropbox_location_projects);

        try {
            if (!(UnitOfWork.getInstance().getDao().existsFile(getString(R.string.dropbox_location_projects)))){
                throw new Exception(msg);
            }
            finish();
        }catch (Exception e){
            data.putExtra("msg",msg);
            MdcUtil.showToastLong(msg,this);
            MdcExceptionLogger.error(e, this);
            finish();
        }

    }
}
