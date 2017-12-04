package com.friendslocater.mobilelocator.BackgroundServices;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GPSTracker extends Service implements LocationListener {

	private Context mContext;
	Geocoder geocoder;
	// flag for GPS status
	boolean isGPSEnabled = false;
	private String addr;
	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	Boolean isServiceDestroy=false;
	String value,tomailid;
	int duration;
	public String androidDeviceID;

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	Handler h=new Handler();
	Runnable r=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

		Location lg= getLocation();

			if(lg!=null)
			{

				Log.d("GPSTracker","Service Location = "+ lg.getLatitude()+"--"+lg.getLongitude());

				FirebaseDatabase database = FirebaseDatabase.getInstance();
				DatabaseReference usersRef = database.getReference("MobileLocaterUserProfile");
				/*usersRef.child(androidDeviceID).child("latitude").setValue(lg.getLatitude());
				usersRef.child(androidDeviceID).child("longitude").setValue(lg.getLongitude());*/
				usersRef.child(androidDeviceID).child("latitude").setValue(13.0545);
				usersRef.child(androidDeviceID).child("longitude").setValue(80.2114);

				Log.d("GPSTracker", "Location Updated in Firebase");

			}else
			{
				Log.d("GPSTracker", "Location NULL not Updated in Firebase");
			}

			// mailsender(lg);
		}
	};
	
	/* (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		androidDeviceID = Settings.Secure.getString(this.getContentResolver(),
				Settings.Secure.ANDROID_ID);



		mContext=getApplicationContext();
	    System.out.println("test oncretae service");
		isServiceDestroy=false;
		h.post(r);
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		System.out.println("test  onStartCommand ");

		if(intent!=null && intent.getExtras()!=null){
			
			value=intent.getExtras().getString("key");
			tomailid=intent.getExtras().getString("mail");
			 
			 //System.out.println("test value get==="+value);
			// duration=Integer.parseInt(value);
			 duration=1000 * 60 * 1;//Integer.parseInt(value); // use shared preference for dynamic value
			 //System.out.println("test  duration number==="+(duration));
			 //System.out.println("test  tomailid ==="+tomailid);
			}
		
	//return super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	public Location getLocation() {
		try {

			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			System.out.println("test  isGPSEnabled = "+ isGPSEnabled);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			System.out.println("test  isNetworkEnabled = "+ isNetworkEnabled);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				Toast.makeText(getApplicationContext()," NO NETWORK ", Toast.LENGTH_LONG).show();
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {

					if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

						locationManager.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

							Log.d("test", "NETWORK_PROVIDER Accuracy "+location.getAccuracy());

							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
								//System.out.println("location==="+latitude+"="+longitude);
							}
						}
					}
					// if GPS Enabled get lat/long using GPS Services
					if (isGPSEnabled) {
						if (location == null) {
							locationManager.requestLocationUpdates(
									LocationManager.GPS_PROVIDER,
									MIN_TIME_BW_UPDATES,
									MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

							if (locationManager != null) {
								location = locationManager
										.getLastKnownLocation(LocationManager.GPS_PROVIDER);
								Log.d("GPS Enabled", "GPS Enabled Accuracy = "+ location.getAccuracy());
								if (location != null) {
									latitude = location.getLatitude();
									longitude = location.getLongitude();
									//System.out.println("location==="+latitude+"="+longitude);

								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if(!isServiceDestroy) // check the service is running or not
		{
			/* duration becomes 0 when you kill the app so static value used like mail a/c*/
			Log.e("SendMail", "duration = "+ duration);
			h.postDelayed(r, 60000);
		}
		
		return location;
	}
	
	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS(){
		
		if(locationManager != null){
			if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
				locationManager.removeUpdates(GPSTracker.this);
				System.out.println("location --> inside stopusingGPS method");
			}
		}		
	}
	
	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		if(location != null){
			latitude = location.getLatitude();
		}
		
		// return latitude
		return latitude;
	}
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		if(location != null){
			longitude = location.getLongitude();
		}
		
		// return longitude
		return longitude;
	}
	
	/**
	 * Function to check GPS/wifi enabled
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
   	 
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
 
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	mContext.startActivity(intent);
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {

		Log.d("OnLocationChanged","Location Value = "+ location.getProvider());
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		
		  Log.i("test ", "test Service onBind");
		return null;
	}



	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		// while stopping service destroy method is called
		
		isServiceDestroy=true;
		System.out.println("test=== ondestroy");
	
	
	}
	

	 
	 
	  
	    
	/*  public void mailsender(final Location position)
	  {
	      System.out.println( " Test GPS TRACKER Inside Mail sender My current LatLng ---- "+position.getLatitude()+" --- "+position.getLongitude());

	      //	 	 Toast.makeText(getApplicationContext(), ontime, Toast.LENGTH_SHORT).show();*//*
	     
	      List<Address> addresses;
	      geocoder = new Geocoder(this, Locale.getDefault());

	 *//*     try {
			addresses = geocoder.getFromLocation(position.getLatitude(), position.getLongitude(), 1);
			  String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
		      String city = addresses.get(0).getLocality();
		      String state = addresses.get(0).getAdminArea();
		      //String country = addresses.get(0).getCountryName();
		      String postalCode = addresses.get(0).getPostalCode();
		      //String knownName = addresses.get(0).getFeatureName();
		      
		      addr="address:\n"+address+"\n"+city+"\n"+state+"\n"+postalCode;
		      
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // Here 1 represent max location result to returned, by documents it recommended 1 to 5
*//*
	      try
	      {
	      addresses = geocoder.getFromLocation(position.getLatitude(), position.getLongitude(), 1);
	      Address address = addresses.get(0);
	      // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
			
			StringBuilder strReturnedAddress = new StringBuilder("");

			 for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
	             strReturnedAddress.append(address.getAddressLine(i));
	          }
			 
			 addr = strReturnedAddress.toString();
	      }catch(Exception e)
	      {
	    	  
	      }

		  //Log.e("SendMail","Inside Mail Sender ");

 	  new Thread(new Runnable() {
	 		
	 		@Override
	 		public void run() {
	 			// TODO Auto-generated method stub

				Log.e(" test SendMail","Inside Mail Sender ");

				boolean networkConnectionStatus = isNetworkAvailable();

				Log.e(" test SendMail", "Inside Mail Sender = " + networkConnectionStatus);

				//Toast.makeText(GPSTracker.this, "networkConnectionStatus = "+ networkConnectionStatus, Toast.LENGTH_SHORT).show();
				if(networkConnectionStatus) {
					try {
						GMailSender sender = new GMailSender("srmtechnologies12345@gmail.com", "srm@12345");
						sender.sendMail("Location Updates",
								"My current LatLng-" + position.getLatitude() + " --- " + position.getLongitude() + "\n" + addr,
								"srmtechnologies12345@gmail.com", "vairaganeshv@chn.srmtech.com");
						//tomailid);

						//    Toast.makeText(getApplicationContext(), ontime, Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Log.e("SendMail", e.getMessage(), e);
					}
				}else
				{
					Log.e(" test ", "No network" );
				}
	 		}
	 	}).start();
	 	
	  }
*/

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	}
