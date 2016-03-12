package com.koenhabets.school;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context arg0, Intent intent) {

        Log.i("Alarm", "Started");

        RequestQueue requestQueue;
        final String requestToken = "62cb407efe211d9a6248849234191f170353";
        Log.i("RequestToken", requestToken);
        requestQueue = Volley.newRequestQueue(arg0);

        String url = "https://api.scholica.com/2.0/communities/1/module";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        boolean b = response.contains("H31");
                        if(b){
                            SharedPreferences sharedPref = arg0.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                            String noti = sharedPref.getString("notification", "false");
                            if (Objects.equals(noti, "false")){
                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(arg0);
                                mBuilder.setSmallIcon(R.drawable.ic_stat_action_list);
                                mBuilder.setContentTitle("JAAAA");
                                mBuilder.setVibrate(new long[] { 100, 100, 100, 100, 100});
                                mBuilder.setOngoing(true);

                                NotificationManager mNotificationManager = (NotificationManager) arg0.getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.notify(2, mBuilder.build());
                            }
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("notification", "true");
                            editor.apply();

                            Log.i("Uitval", "ja");
                        } else {
                            SharedPreferences sharedPref = arg0.getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("notification", "false");
                            editor.apply();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("path", "/netpresenter");
                params.put("token", requestToken);
                return params;
            }
        };
        requestQueue.add(postRequest);

    }


}