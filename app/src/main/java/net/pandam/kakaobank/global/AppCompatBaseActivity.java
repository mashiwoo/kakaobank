/*******************************************************


		Copyright (C) 2014 mashiwoo 

 *******************************************************/

package net.pandam.kakaobank.global;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.androidquery.AQuery;

import net.pandam.kakaobank.R;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class AppCompatBaseActivity extends AppCompatActivity
{
	private SharedPreferences pref;
	protected Application application;


	private AQuery aq;
	
	protected Context context = this;

	private boolean isMainActivity = true;
	private ProgressDialog progressDialog = null;


	private Handler menuHandler = 	new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			Bundle data = msg.getData();
			int action = data.getInt("action");

		}
	};
	
	public void setMainActivity(boolean isMain)
	{
		isMainActivity = isMain;
	}


	private String getBestProvider(Context context)
	{
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		return ((LocationManager)context.getSystemService(Context.LOCATION_SERVICE)).getBestProvider(criteria, true);
	}



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		application = (Application)getApplication();
		pref = PreferenceManager.getDefaultSharedPreferences(context);
		aq = new AQuery(context);
	}


	@Override
	protected void onResume()
	{
		super.onResume();
	}


	protected void alert(int msgId)
	{
		String message = getString(msgId);
		alert(message);
	}

	protected void alert(String msg)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.setMessage(msg);
		alert.show();
	}

	protected void confirm(int msgId)
	{
		String message = getString(msgId);
		confirm(message);
	}

	protected void confirm(String msg)
	{
		AlertDialog.Builder confirm = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		confirm.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		confirm.setNegativeButton("No", null);
		confirm.setMessage(msg);
		confirm.show();
	}

	protected boolean downloadImg(String url, String filename) {
		URL imgurl;
		int read;
		try {
			imgurl = new URL(url);
			HttpURLConnection con = (HttpURLConnection) imgurl.openConnection();
			int len = con.getContentLength();
			byte[] raster = new byte[len];
			InputStream is = con.getInputStream();
			FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
			for (;;) {
				read = is.read(raster);
				if (read <= 0) {
					break;
				}
				fos.write(raster, 0, read);
			}
			is.close();
			fos.close();
			con.disconnect();

		} catch (Exception e) {
			return false;
		}
		return true;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (isMainActivity)
		{
			if (keyCode == KeyEvent.KEYCODE_BACK)
			{
				if (event.getRepeatCount() == 0)
				{

                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
                    alt_bld.setMessage("종료 하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Action for 'Yes' Button
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = alt_bld.create();
                    alert.show();
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	protected void showModalProgress(boolean show, String message)
	{
		try
		{
			if (show)
			{
				if (progressDialog == null)
				{
					progressDialog = ProgressDialog.show(context, "", message, true);
					progressDialog.setCancelable(true);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				}
			}
			else
			{
				if (progressDialog != null && progressDialog.isShowing())
				{
					progressDialog.dismiss();
					progressDialog = null;
				}
			}
		}
		catch(Exception ex)
		{
			//
		}
	}

	protected void showModalProgress(boolean show)
	{
		showModalProgress(show, null);
	}
}


