// This string is autogenerated by ChangeAppSettings.sh, do not change spaces amount
package com.googlecode.opentyrian;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.util.Log;
import java.io.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.StatFs;
import java.util.Locale;

class Settings
{
	static String SettingsFileName = "libsdl-settings.cfg";

	static AlertDialog changeConfigAlert = null;
	static Thread changeConfigAlertThread = null;

	static void Save(final MainActivity p)
	{
		try {
			ObjectOutputStream out = new ObjectOutputStream(p.openFileOutput( SettingsFileName, p.MODE_WORLD_READABLE ));
			out.writeBoolean(Globals.DownloadToSdcard);
			out.writeBoolean(Globals.PhoneHasArrowKeys);
			out.writeBoolean(Globals.PhoneHasTrackball);
			out.writeBoolean(Globals.UseAccelerometerAsArrowKeys);
			out.writeBoolean(Globals.UseTouchscreenKeyboard);
			out.writeInt(Globals.TouchscreenKeyboardSize);
			out.writeInt(Globals.AccelerometerSensitivity);
			out.writeInt(Globals.TrackballDampening);
			out.writeInt(Globals.AudioBufferConfig);
			out.close();
		} catch( FileNotFoundException e ) {
		} catch( SecurityException e ) {
		} catch ( IOException e ) {};
	}

	static void Load( final MainActivity p )
	{
		try {
			ObjectInputStream settingsFile = new ObjectInputStream(new FileInputStream( p.getFilesDir().getAbsolutePath() + "/" + SettingsFileName ));
			Globals.DownloadToSdcard = settingsFile.readBoolean();
			Globals.PhoneHasArrowKeys = settingsFile.readBoolean();
			Globals.PhoneHasTrackball = settingsFile.readBoolean();
			Globals.UseAccelerometerAsArrowKeys = settingsFile.readBoolean();
			Globals.UseTouchscreenKeyboard = settingsFile.readBoolean();
			Globals.TouchscreenKeyboardSize = settingsFile.readInt();
			Globals.AccelerometerSensitivity = settingsFile.readInt();
			Globals.TrackballDampening = settingsFile.readInt();
			Globals.AudioBufferConfig = settingsFile.readInt();
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(p);
			builder.setTitle("Phone configuration");
			builder.setPositiveButton("Change phone configuration", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
						changeConfigAlert = null;
						dialog.dismiss();
						showDownloadConfig(p);
				}
			});
			/*
			builder.setNegativeButton("Start", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int item) 
				{
						changeConfigAlert = null;
						dialog.dismiss();
						startDownloader(p);
				}
			});
			*/
			AlertDialog alert = builder.create();
			alert.setOwnerActivity(p);
			changeConfigAlert = alert;

			class Callback implements Runnable
			{
				MainActivity p;
				Callback( MainActivity _p ) { p = _p; }
				public void run()
				{
					try {
						Thread.sleep(1500);
					} catch( InterruptedException e ) {};
					if( changeConfigAlert == null )
						return;
					changeConfigAlert.dismiss();
					startDownloader(p);
				}
			};
			changeConfigAlertThread = new Thread(new Callback(p));
			changeConfigAlertThread.start();

			alert.show();
			
			
			return;
			
		} catch( FileNotFoundException e ) {
		} catch( SecurityException e ) {
		} catch ( IOException e ) {};
		
		// This code fails for both of my phones!
		/*
		Configuration c = new Configuration();
		c.setToDefaults();
		
		if( c.navigation == Configuration.NAVIGATION_TRACKBALL || 
			c.navigation == Configuration.NAVIGATION_DPAD ||
			c.navigation == Configuration.NAVIGATION_WHEEL )
		{
			Globals.AppNeedsArrowKeys = false;
		}
		
		System.out.println( "libSDL: Phone keypad type: " + 
				(
				c.navigation == Configuration.NAVIGATION_TRACKBALL ? "Trackball" :
				c.navigation == Configuration.NAVIGATION_DPAD ? "Dpad" :
				c.navigation == Configuration.NAVIGATION_WHEEL ? "Wheel" :
				c.navigation == Configuration.NAVIGATION_NONAV ? "None" :
				"Unknown" ) );
		*/

		showDownloadConfig(p);
	}

	static void showDownloadConfig(final MainActivity p) {

		long freeSdcard = 0;
		long freePhone = 0;
		try {
			StatFs sdcard = new StatFs(Environment.getExternalStorageDirectory().getPath());
			StatFs phone = new StatFs(Environment.getDataDirectory().getPath());
			freeSdcard = (long)sdcard.getAvailableBlocks() * sdcard.getBlockSize() / 1024 / 1024;
			freePhone = (long)phone.getAvailableBlocks() * phone.getBlockSize() / 1024 / 1024;
		}catch(Exception e) {}

		final CharSequence[] items = {"Phone storage - " + String.valueOf(freePhone) + " Mb free", "SD card - " + String.valueOf(freeSdcard) + " Mb free"};

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle("Where to download application data");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.DownloadToSdcard = (item == 1);

				dialog.dismiss();
				showKeyboardConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	};

	static void showKeyboardConfig(final MainActivity p)
	{
		if( ! Globals.AppNeedsArrowKeys )
		{
			showTrackballConfig(p);
			return;
		}
		
		final CharSequence[] items = {"Arrows / joystick / dpad", "Trackball", "None, only touchscreen"};

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle("What kind of navigation keys does your phone have?");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.PhoneHasArrowKeys = (item == 0);
				Globals.PhoneHasTrackball = (item == 1);

				dialog.dismiss();
				showTrackballConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}

	static void showTrackballConfig(final MainActivity p)
	{
		Globals.TrackballDampening = 0;
		if( ! Globals.PhoneHasTrackball )
		{
			showAdditionalInputConfig(p);
			return;
		}
		
		final CharSequence[] items = {"No dampening", "Fast", "Medium", "Slow"};

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle("Trackball dampening");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.TrackballDampening = item;

				dialog.dismiss();
				showAdditionalInputConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}
	
	static void showAdditionalInputConfig(final MainActivity p)
	{
		if( ! Globals.AppNeedsArrowKeys )
		{
			showAccelerometerConfig(p);
			return;
		}
		final CharSequence[] items = {
			"On-screen keyboard" + ( Globals.AppUsesMouse ? " (disables mouse input)" : ""),
			"Accelerometer as navigation keys" + ( Globals.AppUsesJoystick ? " (disables joystick input)" : "" ),
			"Both accelerometer and on-screen keyboard",
			"No additional controls"
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle("Additional controls to use");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.UseTouchscreenKeyboard = (item == 0 || item == 2);
				Globals.UseAccelerometerAsArrowKeys = (item == 1 || item == 2);

				dialog.dismiss();
				showAccelerometerConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}

	static void showAccelerometerConfig(final MainActivity p)
	{
		Globals.AccelerometerSensitivity = 0;
		if( ! Globals.UseAccelerometerAsArrowKeys )
		{
			showScreenKeyboardConfig(p);
			return;
		}
		
		final CharSequence[] items = {"Fast", "Medium", "Slow"};

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle("Accelerometer sensitivity");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.AccelerometerSensitivity = item;

				dialog.dismiss();
				showScreenKeyboardConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}

	static void showScreenKeyboardConfig(final MainActivity p)
	{
		Globals.TouchscreenKeyboardSize = 0;
		if( ! Globals.UseTouchscreenKeyboard )
		{
			showAudioConfig(p);
			return;
		}
		
		final CharSequence[] items = {"Big", "Medium", "Small"};

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle("On-screen keyboard size (toggle auto-fire by sliding across Fire button)");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.TouchscreenKeyboardSize = item;

				dialog.dismiss();
				showAudioConfig(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}
	
	static void showAudioConfig(final MainActivity p)
	{
		final CharSequence[] items = {"Small (fast devices)", "Medium", "Large (if sound is choppy)"};

		AlertDialog.Builder builder = new AlertDialog.Builder(p);
		builder.setTitle("Size of audio buffer");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int item) 
			{
				Globals.AudioBufferConfig = item;
				dialog.dismiss();
				Save(p);
				startDownloader(p);
			}
		});
		AlertDialog alert = builder.create();
		alert.setOwnerActivity(p);
		alert.show();
	}
	
	static void Apply()
	{
		nativeIsSdcardUsed( Globals.DownloadToSdcard ? 1 : 0 );
		
		if( Globals.PhoneHasTrackball )
			nativeSetTrackballUsed();
		if( Globals.AppUsesMouse )
			nativeSetMouseUsed();
		if( Globals.AppUsesJoystick && !Globals.UseAccelerometerAsArrowKeys )
			nativeSetJoystickUsed();
		if( Globals.AppUsesMultitouch )
			nativeSetMultitouchUsed();
		if( Globals.UseTouchscreenKeyboard )
		{
			nativeSetTouchscreenKeyboardUsed();
			nativeSetupScreenKeyboard(Globals.TouchscreenKeyboardSize, Globals.AppTouchscreenKeyboardKeysAmount);
		}
		nativeSetAccelerometerSensitivity(Globals.AccelerometerSensitivity);
		nativeSetTrackballDampening(Globals.TrackballDampening);
		String lang = new String(Locale.getDefault().getLanguage());
		if( Locale.getDefault().getCountry().length() > 0 )
			lang = lang + "_" + Locale.getDefault().getCountry();
		System.out.println( "libSDL: setting envvar LANG to '" + lang + "'");
		nativeSetEnv( "LANG", lang );
		// TODO: get current user name and set envvar USER, the API is not availalbe on Android 1.6 so I don't bother with this
	}
	
	static void startDownloader(MainActivity p)
	{
		class Callback implements Runnable
		{
			public MainActivity Parent;
			public void run()
			{
				Parent.startDownloader();
			}
		}
		Callback cb = new Callback();
		cb.Parent = p;
		p.runOnUiThread(cb);
	};
	

	private static native void nativeIsSdcardUsed(int flag);
	private static native void nativeSetTrackballUsed();
	private static native void nativeSetTrackballDampening(int value);
	private static native void nativeSetAccelerometerSensitivity(int value);
	private static native void nativeSetMouseUsed();
	private static native void nativeSetJoystickUsed();
	private static native void nativeSetMultitouchUsed();
	private static native void nativeSetTouchscreenKeyboardUsed();
	private static native void nativeSetupScreenKeyboard(int size, int nbuttons);
	public static native void nativeSetEnv(final String name, final String value);
}

