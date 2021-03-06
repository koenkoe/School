package com.koenhabets.school.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.koenhabets.school.R;
import com.koenhabets.school.adapters.GradeDetailsAdapter;
import com.koenhabets.school.api.som.GradeItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.koenhabets.school.SchoolApp.getContext;

public class GradesActivity extends AppCompatActivity {
    private TextView textViewSubject;
    private TextView textViewGrade;
    private List<GradeItem> gradeItems = new ArrayList<>();
    private ListView listView;
    private GradeDetailsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = findViewById(R.id.listView);
        adapter = new GradeDetailsAdapter(getContext(), gradeItems);
        listView.setAdapter(adapter);

        textViewSubject = findViewById(R.id.textViewGradeDetailsSubject);
        textViewGrade = findViewById(R.id.textViewGradeDetailsGrade);

        Intent intent = getIntent();
        String response = intent.getStringExtra("jsonObject");

        try {
            JSONObject jsonObject = new JSONObject(response);
            String subject = jsonObject.getString("subject");
            String grade = jsonObject.getString("grade");
            textViewSubject.setText(subject);
            textViewGrade.setText(grade);
            JSONArray jsonArray = jsonObject.getJSONArray("subjectGrades");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                GradeItem gradeItem = new GradeItem(jsonObject1.getString("grade"), jsonObject1.getString("subject"), jsonObject1.getString("datumInvoer"),
                        jsonObject1.getInt("weight"), jsonObject1.getInt("periode"), jsonObject1.getString("type"), jsonObject1.getString("description"));
                gradeItems.add(gradeItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
