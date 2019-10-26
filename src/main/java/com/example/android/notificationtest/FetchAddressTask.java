package com.example.android.notificationtest;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressTask extends AsyncTask<Location, Void, String> {

    private  final String TAG = FetchAddressTask.class.getSimpleName();
    private Context mContext;
    private onTaskCompleted mListener;

    interface onTaskCompleted{
        void onTaskCompleted(String result);
    }

    FetchAddressTask(Context applicationContext, onTaskCompleted listener){
        mContext = applicationContext;
        mListener = listener;
    }
    @Override
    protected String doInBackground(Location... locations) {
        Geocoder mGeocoder = new Geocoder(mContext, Locale.getDefault());

        Location location = locations[0];
        List<Address> addresses = new ArrayList<>();
        String resultMessage = "";
        try{
            addresses = mGeocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
        }catch (IOException ioException){
            resultMessage = mContext.getString(R.string.service_not_available);
            Log.e(TAG, resultMessage, ioException);
        } catch(IllegalArgumentException illegalArgumentException){
            //catch invalid latitude or invalid longitude value
            resultMessage = mContext.getString(R.string.invalid_lat_long_used);
            Log.e(TAG, resultMessage +  ". "+ "Lat: "+ location.getLatitude()+ "Long: " + location.getLongitude()
            , illegalArgumentException);

        }

        if(addresses.isEmpty() || addresses.size() ==0 ){
            if(resultMessage.isEmpty()){
                resultMessage = mContext.getString(R.string.no_address_found);
                Log.e(TAG, resultMessage);
            }
        }else{
            //if address is found through the geocoder, pass it to resultMessage variable
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();

            //loop through the addressparts

            for (int i =0; i <= address.getMaxAddressLineIndex(); i++){
                addressParts.add(address.getAddressLine(i));

            }
            resultMessage = TextUtils.join("\n", addressParts);
        }
        return resultMessage;
    }

    @Override
    protected void onPostExecute(String address) {

        mListener.onTaskCompleted(address);
        super.onPostExecute(address);
    }
}
