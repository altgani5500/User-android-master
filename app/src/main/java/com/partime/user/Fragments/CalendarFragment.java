package com.partime.user.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.JsonObject;
import com.p_v.flexiblecalendar.FlexibleCalendarView;
import com.p_v.flexiblecalendar.view.BaseCellView;
import com.partime.user.R;
import com.partime.user.Responses.CustomEvent;
import com.partime.user.Responses.ScheduleMessage;
import com.partime.user.Responses.ScheduleResponse;
import com.partime.user.activities.ViewScheduleActivity;
import com.partime.user.helpers.JavaAPIService;
import com.partime.user.helpers.JavaAPIUtils;
import com.partime.user.helpers.ProgressBarUtil;
import com.partime.user.helpers.Utilities;
import com.partime.user.prefences.AppPrefence;
import com.partime.user.uicomman.BaseFragment;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends BaseFragment {

    private Map<Integer, List<CustomEvent>> eventMap;
    private FlexibleCalendarView calendarView;
    private TextView txtOne;
    private TextView txtTwo;
    private TextView txtThree;
    AppPrefence appPrefence = AppPrefence.INSTANCE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarView = (FlexibleCalendarView) rootView.findViewById(R.id.calendar_view);
        txtOne = rootView.findViewById(R.id.txtMonthOneCalendar);
        txtTwo = rootView.findViewById(R.id.txtMonthTwoCalendar);
        txtThree = rootView.findViewById(R.id.txtMonthThreeCalendar);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        appPrefence.initAppPreferences(getContext());

        Calendar curCal = Calendar.getInstance();
        Calendar curCal2 = Calendar.getInstance();
        Calendar curCal3 = Calendar.getInstance();
        String cMonth = curCal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        curCal2.add(Calendar.MONTH, -1);
        curCal3.add(Calendar.MONTH, +1);
        String pMonth = curCal2.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String nMonth = curCal3.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());

        eventMap = new HashMap<>();
        validateNet(String.valueOf(curCal.get(Calendar.YEAR)), String.valueOf(curCal.get(Calendar.MONTH) + 1));

        txtOne.setText(pMonth);
        txtTwo.setText(cMonth);
        txtThree.setText(nMonth);

        calendarView.setMonthViewHorizontalSpacing(10);
        calendarView.setMonthViewVerticalSpacing(10);
        calendarView.setOnMonthChangeListener(new FlexibleCalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month, @FlexibleCalendarView.Direction int direction) {

                validateNet(String.valueOf(year), String.valueOf(month+1));
                setMonths(month + 1);
            }
        });

        calendarView.setCalendarView(new FlexibleCalendarView.CalendarView() {
            @Override
            public BaseCellView getCellView(int position, View convertView, ViewGroup parent, int cellType) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar3_date_cell_view, null);
                    cellView.setTextColor(getResources().getColor(R.color.black));
                    cellView.setTextSize(11F);
                }
                return cellView;
            }

            @Override
            public BaseCellView getWeekdayCellView(int position, View convertView, ViewGroup parent) {
                BaseCellView cellView = (BaseCellView) convertView;
                if (cellView == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    cellView = (BaseCellView) inflater.inflate(R.layout.calendar3_week_cell_view, null);
                    cellView.setTextColor(getResources().getColor(R.color.black));

                }
                return cellView;
            }

            @Override
            public String getDayOfWeekDisplayValue(int dayOfWeek, String defaultValue) {
                return null;
            }
        });

        calendarView.setEventDataProvider(new FlexibleCalendarView.EventDataProvider() {
            @Override
            public List<CustomEvent> getEventsForTheDay(int year, int month, int day) {
                return getEvents(year, month, day);
            }
        });

        calendarView.setOnDateClickListener(new FlexibleCalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day);

                String date = String.valueOf(year) + "-" + String.valueOf(month+1) + "-" + String.valueOf(day);
                String cMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                String title = String.valueOf(day) + " " + cMonth + " " + year;

                Intent intent = new Intent(getContext(), ViewScheduleActivity.class);
                intent.putExtra("SELECTED_DATE", date);
                intent.putExtra("TITLE", title);
                startActivity(intent);

                // Toast.makeText(getContext(), cal.getTime().toString() + " Clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void validateNet(String year, String month) {

        if (Utilities.isNetworkAvailable(getContext())) {

            getSchedule(year, month);

        } else {
            Toast.makeText(getContext(), getContext().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }

    }

    private void initializeEvents(List<ScheduleMessage> message) {

        if (!eventMap.isEmpty()) {
            eventMap.clear();
        }
        for (int i = 0; i < message.size(); i++) {

            //  colorLst.clear();
            int events = message.get(i).getCount();
            List<CustomEvent> colorLst = new ArrayList<>();
            for (int j = 0; j < events; j++) {

                colorLst.add(new CustomEvent(android.R.color.holo_blue_light));
                String date[] = message.get(i).getDate().split("-");
                int day = Integer.parseInt(date[2]);
                eventMap.put(day, colorLst);
            }

        }

        calendarView.refresh();
    }

    private void getSchedule(String year, String month) {

        //set the tasks from api.......
        ProgressDialog progressBar = new ProgressBarUtil().showProgressDialog(getContext());

        String authKey = appPrefence.getString(AppPrefence.SharedPreferncesKeys.API_TOKEN.toString());
        String language = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("month", month);
        jsonObject.addProperty("year", year);

        JavaAPIService apiService = JavaAPIUtils.getAPIService();

        apiService.getSchedule("Bearer " + authKey, language, jsonObject).enqueue(new Callback<ScheduleResponse>() {
            @Override
            public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                new ProgressBarUtil().hideProgressDialog(progressBar);

                if (response != null) {
                    String s=response.body().getMessage().toString();
                    appPrefence.setInt(AppPrefence.SharedPreferncesKeys.ENROLLED_E_ID.toString(),response.body().getEnrolledEnterpriseId());

                    initializeEvents(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                new ProgressBarUtil().hideProgressDialog(progressBar);
                Toast.makeText(getContext(), getContext().getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setMonths(int i) {

        if (i == 1) {
            txtOne.setText(R.string.december);
            txtTwo.setText(R.string.january);
            txtThree.setText(R.string.february);
        } else if (i == 2) {
            txtOne.setText(R.string.january);
            txtTwo.setText(R.string.february);
            txtThree.setText(R.string.march);
        } else if (i == 3) {
            txtOne.setText(R.string.february);
            txtTwo.setText(R.string.march);
            txtThree.setText(R.string.april);
        } else if (i == 4) {
            txtOne.setText(R.string.march);
            txtTwo.setText(R.string.april);
            txtThree.setText(R.string.may);
        } else if (i == 5) {
            txtOne.setText(R.string.april);
            txtTwo.setText(R.string.may);
            txtThree.setText(R.string.june);
        } else if (i == 6) {
            txtOne.setText(R.string.may);
            txtTwo.setText(R.string.june);
            txtThree.setText(R.string.july);
        } else if (i == 7) {
            txtOne.setText(R.string.june);
            txtTwo.setText(R.string.july);
            txtThree.setText(R.string.august);
        } else if (i == 8) {
            txtOne.setText(R.string.july);
            txtTwo.setText(R.string.august);
            txtThree.setText(R.string.september);
        } else if (i == 9) {
            txtOne.setText(R.string.august);
            txtTwo.setText(R.string.september);
            txtThree.setText(R.string.october);
        } else if (i == 10) {
            txtOne.setText(R.string.september);
            txtTwo.setText(R.string.october);
            txtThree.setText(R.string.november);
        } else if (i == 11) {
            txtOne.setText(R.string.october);
            txtTwo.setText(R.string.november);
            txtThree.setText(R.string.december);
        } else if (i == 12) {
            txtOne.setText(R.string.november);
            txtTwo.setText(R.string.december);
            txtThree.setText(R.string.january);
        }
    }

    public List<CustomEvent> getEvents(int year, int month, int day) {
        return eventMap.get(day);
    }
}
