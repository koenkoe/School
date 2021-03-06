package com.koenhabets.school.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.koenhabets.school.HomeworkComparator;
import com.koenhabets.school.R;
import com.koenhabets.school.activities.TaskDetailsActivity;
import com.koenhabets.school.adapters.HomeworkAdapter;
import com.koenhabets.school.api.som.HomeworkItem;
import com.koenhabets.school.api.som.HomeworkRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeworkFragment extends Fragment {
    private List<HomeworkItem> homeworkItems = new ArrayList<>();
    private ListView listView;
    private RequestQueue requestQueue;
    private HomeworkAdapter adapter;

    public HomeworkFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_homework, container, false);

        listView = rootView.findViewById(R.id.listViewHomework);
        requestQueue = Volley.newRequestQueue(getContext());

        Collections.sort(homeworkItems, new HomeworkComparator());

        adapter = new HomeworkAdapter(getContext(), homeworkItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HomeworkItem item = homeworkItems.get(position);
                Intent intent = new Intent(getContext(), TaskDetailsActivity.class);
                intent.putExtra("taskSubject", item.getTaskSubject());
                intent.putExtra("taskDescription", item.getDescription());
                startActivity(intent);
            }
        });
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -2);
        Date date = cal.getTime();
        String dateString = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
        Log.i("homework date", dateString);
        getHomework(dateString);
        return rootView;
    }

    private void getHomework(String date) {
        SharedPreferences sharedPref = getContext().getSharedPreferences("com.koenhabets.school", Context.MODE_PRIVATE);
        String somApiUrl =  sharedPref.getString("somApiUrl", "");
        String somAccessToken = sharedPref.getString("somAccessToken", "");

        HomeworkRequest request = new HomeworkRequest(somAccessToken, date, somApiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("response", response);
                try {
                    parseResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }

    private void parseResponse(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject task = jsonArray.getJSONObject(i);
                JSONObject subjectJson = task.getJSONObject("vak");
                JSONObject studiewijzerItem = task.getJSONObject("studiewijzerItem");

                String subject = subjectJson.getString("afkorting");
                String date = task.getString("datumTijd");
                String description = "";
                String[] dat = date.split("T");
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date d = null;
                try {
                    d = format.parse(dat[0]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat format2 = new SimpleDateFormat("EEE dd-MM-yyyy", Locale.getDefault());
                date = format2.format(d);
                try {
                    description = studiewijzerItem.getString("opdrachtBeschrijving");
                } catch (JSONException ignored) {
                }
                String taskSubject = studiewijzerItem.getString("onderwerp");
                Date date1 = new Date();
                if (d.getTime() > date1.getTime()) {
                    HomeworkItem item = new HomeworkItem(date, subject, description, taskSubject);
                    homeworkItems.add(item);
                }
            } catch (JSONException ignored){

            }
        }
        adapter.notifyDataSetChanged();
    }
}
