package com.cordova.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.pm.PackageManager;
import java.io.ByteArrayOutputStream;

import android.util.Log;

import java.io.FileOutputStream;
import android.net.Uri;
import android.content.Intent;
import android.app.Activity;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import androidx.core.content.FileProvider;
import java.io.InputStream;

public class IGStory extends CordovaPlugin {
  private static final String TAG = "IGStory";
  private CallbackContext callback = null;

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
  }

  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (action.equals("shareToStory")) {
      PackageManager pm = this.cordova.getActivity().getBaseContext().getPackageManager();
      if (isPackageInstalled("com.instagram.android", pm)) {
        String backgroundImageUrl = args.getString(0);
        String attributionLinkUrl = args.getString(1);
        try {
          Log.e(TAG, "WE HAVE A BACKGROUND");
          File parentDir = this.webView.getContext().getExternalFilesDir(null);
          File backgroundImageFile = File.createTempFile("instagramBackground", ".png", parentDir);
          Log.e(TAG, backgroundImageUrl.toString());
          URL backgroundURL = new URL(backgroundImageUrl);
          saveImage(backgroundURL, backgroundImageFile);
          Log.e(TAG, backgroundImageFile.toString());
          Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
          intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

          FileProvider FileProvider = new FileProvider();
          Uri backgroundUri = FileProvider.getUriForFile(this.cordova.getActivity().getBaseContext(), this.cordova.getActivity().getBaseContext().getPackageName() + ".provider" ,backgroundImageFile);
          intent.setDataAndType(backgroundUri, "image/*");
          if(attributionLinkUrl != null && attributionLinkUrl.isEmpty()) {
            intent.putExtra("content_url", attributionLinkUrl);
          }
          Activity activity = this.cordova.getActivity();
          activity.grantUriPermission("com.instagram.android", backgroundUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
          activity.startActivityForResult(intent, 0);
          callbackContext.success("shared");
        } catch (Exception e) {
          callbackContext.error("Something went wrong.");
          e.printStackTrace();
        }
      } else {
        callbackContext.error("Instagram is not installed.");
      }
    }
    return true;
  }

  private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
    boolean found = true;
    try {
      packageManager.getPackageInfo(packageName, 0);
    } catch (PackageManager.NameNotFoundException e) {
      found = false;
    }
    return found;
  }

  private byte[] downloadUrl(URL toDownload) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    try {
      byte[] chunk = new byte[4096];
      int bytesRead;
      InputStream stream = toDownload.openStream();

      while ((bytesRead = stream.read(chunk)) > 0) {
        outputStream.write(chunk, 0, bytesRead);
      }

    } catch (IOException e) {
      Log.e(TAG, "SAVE ERROR (IO): " + e.getMessage());
      return null;
    } catch (Exception e) {
      Log.e(TAG, "SAVE ERROR (REG): " + e.getMessage());
      return null;
    }

    return outputStream.toByteArray();
  }

  private void saveImage(URL pathUrl, File file) {
    FileOutputStream os = null;

    try {
      os = new FileOutputStream(file, true);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      os.write(downloadUrl(pathUrl));
      os.flush();
      os.close();
    } catch (IOException e) {
      Log.e(TAG, "SAVE ERROR: " + e.getMessage());
    }
  }
}
