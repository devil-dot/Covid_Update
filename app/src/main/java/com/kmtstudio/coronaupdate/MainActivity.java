package com.kmtstudio.coronaupdate;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kmtstudio.coronaupdate.api.ApiUtilities;
import com.kmtstudio.coronaupdate.api.CountryData;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView totalConfirm, totalActive, totalRecovered, totalDeath, totalTests;
    private TextView todayConfirm, todayRecovered, todayDeath, dateTxt;

    private PieChart pieChart;

    private List<CountryData> dataList;
    public String country = "Bangladesh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dataList = new ArrayList<>();

        if (getIntent().getStringExtra("country") != null) {

            country = getIntent().getStringExtra("country");
        }

        TextView cname = findViewById(R.id.cnameID);
        cname.setText(country);

        initID();

        cname.setOnClickListener(v -> {

            Intent i = new Intent(MainActivity.this, CountryActivity.class);
            startActivity(i);

        });

        ApiUtilities.getApiInterface().getCountryData()
                .enqueue(new Callback<List<CountryData>>() {
                    @Override
                    public void onResponse(Call<List<CountryData>> call, Response<List<CountryData>> response) {

                        dataList.addAll(response.body());

                        for (int i = 0; i < dataList.size(); i++) {

                            if (dataList.get(i).getCountry().equals(country)) {

                                int confirm = Integer.parseInt(dataList.get(i).getCases());
                                int active = Integer.parseInt(dataList.get(i).getActive());
                                int recovered = Integer.parseInt(dataList.get(i).getRecovered());
                                int death = Integer.parseInt(dataList.get(i).getDeaths());


                                totalConfirm.setText(NumberFormat.getInstance().format(confirm));
                                totalActive.setText(NumberFormat.getInstance().format(active));
                                totalRecovered.setText(NumberFormat.getInstance().format(recovered));
                                totalDeath.setText(NumberFormat.getInstance().format(death));

                                todayDeath.setText(NumberFormat.getInstance().format(Integer.parseInt(dataList.get(i).getTodayDeaths())));
                                todayConfirm.setText(NumberFormat.getInstance().format(Integer.parseInt(dataList.get(i).getTodayCases())));
                                todayRecovered.setText(NumberFormat.getInstance().format(Integer.parseInt(dataList.get(i).getTodayRecovered())));
                                totalTests.setText(NumberFormat.getInstance().format(Integer.parseInt(dataList.get(i).getTests())));

                                setText(dataList.get(i).getUpdated());

                                pieChart.addPieSlice(new PieModel("Confirm", confirm, getResources().getColor(R.color.yellow)));
                                pieChart.addPieSlice(new PieModel("Active", active, getResources().getColor(R.color.blue_pie)));
                                pieChart.addPieSlice(new PieModel("Recovered", recovered, getResources().getColor(R.color.green_pie)));
                                pieChart.addPieSlice(new PieModel("Death", death, getResources().getColor(R.color.red_pie)));


                                pieChart.startAnimation();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<List<CountryData>> call, Throwable t) {

                        Toast.makeText(MainActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setText(String updated) {

        DateFormat format = new SimpleDateFormat("MMM dd, yyy");

        long milliseconds = Long.parseLong(updated);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliseconds);

        dateTxt.setText("Updated at " + format.format(calendar.getTime()));
    }

    public void initID() {

        totalConfirm = findViewById(R.id.totalConfirmID);
        totalActive = findViewById(R.id.totalActiveID);
        totalRecovered = findViewById(R.id.totalRecoveredID);
        totalDeath = findViewById(R.id.totalDeathID);
        todayConfirm = findViewById(R.id.todayConfirmID);
        todayRecovered = findViewById(R.id.todayRecoveredID);
        todayDeath = findViewById(R.id.todayDeathID);
        totalTests = findViewById(R.id.totalTestsID);
        dateTxt = findViewById(R.id.dateID);
        pieChart = findViewById(R.id.pieChartID);

    }

    @Override
    public void onBackPressed() {

        FancyAlertDialog.Builder
                .with(this)
                .setTitle("Closing Application")
                .setBackgroundColor(Color.parseColor("#303F9F"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                .setMessage("Do you really want to Exit ?")
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground(Color.parseColor("#FF4081"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                .setPositiveBtnText("Yes")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                .setAnimation(Animation.POP)
                .isCancellable(false)
                .onPositiveClicked(dialog -> finish())
                .onNegativeClicked(Dialog::cancel)
                .build()
                .show();
    }
}