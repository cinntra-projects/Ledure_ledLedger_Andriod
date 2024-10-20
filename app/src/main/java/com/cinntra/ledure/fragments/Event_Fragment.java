package com.cinntra.ledure.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.cinntra.ledure.R;
import com.cinntra.ledure.adapters.EventsAdapter;
import com.cinntra.ledure.globals.Globals;
import com.cinntra.ledure.model.EventResponse;
import com.cinntra.ledure.model.EventValue;
import com.cinntra.ledure.model.NewEvent;
import com.cinntra.ledure.model.QuotationResponse;
import com.cinntra.ledure.model.SalesEmployeeItem;
import com.cinntra.ledure.webservices.NewApiClient;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;


public class Event_Fragment extends Fragment {

     @BindView(R.id.eventList)
     RecyclerView eventList;
     @BindView(R.id.loader)
     SpinKitView loader;
    @BindView(R.id.no_datafound)
    ImageView no_datafound;
    private ArrayList<EventValue> TaskEventList;
    public Event_Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Event_Fragment newInstance(String param1, String param2) {
        Event_Fragment fragment = new Event_Fragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this,v);
      //  loadData();
        callApi();
        return v;
    }
    private void callApi() {
        TaskEventList= new ArrayList<>();
        loader.setVisibility(View.VISIBLE);

        SalesEmployeeItem eventValue = new SalesEmployeeItem();
        eventValue.setEmp(Prefs.getString(Globals.EmployeeID,""));
        eventValue.setDate(Globals.CurrentSelectedDate);


        Call<EventResponse> call = NewApiClient.getInstance().getApiService(getActivity()).getcalendardata(eventValue);
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {

                if(response.code()==200&&response.body()!=null)
                {
                    if(response.body().getData().size()>0){
                        TaskEventList.clear();
                        TaskEventList.addAll(response.body().getData());
                        loader.setVisibility(View.GONE);
                        no_datafound.setVisibility(View.GONE);
                        setAdapter();
                    }
                    else {
                        no_datafound.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.GONE);
                    }
                }
                else
                {
                    //Globals.ErrorMessage(CreateContact.this,response.errorBody().toString());
                    Gson gson = new GsonBuilder().create();
                    QuotationResponse mError = new QuotationResponse();
                    try {
                        String s =response.errorBody().string();
                        mError= gson.fromJson(s,QuotationResponse.class);
                        Toast.makeText(getActivity(), mError.getError().getMessage().getValue(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        //handle failure to read error
                    }
                    //Toast.makeText(CreateContact.this, msz, Toast.LENGTH_SHORT).show();
                    loader.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                loader.setVisibility(View.GONE);
            }

        });

    }


    private void setAdapter() {
        EventsAdapter adapter;
        eventList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        adapter = new EventsAdapter(getActivity(), filter("Event"));
        eventList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if(adapter.getItemCount()==0)
            no_datafound.setVisibility(View.VISIBLE);
        else
            no_datafound.setVisibility(View.GONE);



    }
    public ArrayList<EventValue> filter(String text ) {

        ArrayList<EventValue> templist= new ArrayList<>();
        templist.clear();
        for (EventValue st : TaskEventList) {

            if(st.getType().equals(text)) {

                templist.add(st);

            }


        }

        return templist;
    }
    private ArrayList<NewEvent> geEvents(ArrayList<NewEvent> list)
          {

      ArrayList<NewEvent> events = new ArrayList<>();
        for (NewEvent event :list
             ) {


            if(event.getType()==Globals.TYPE_EVENT) {
                // &&Globals.CurrentSelectedDate.equalsIgnoreCase(event.getDateFrom())

                try {
                    String to = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    Date FromDate = new SimpleDateFormat("yyyy-MM-dd").parse(event.getDateFrom().trim());
                    Date ToDate = new SimpleDateFormat("yyyy-MM-dd").parse(event.getDateTo().trim());
                    Date ToDayDate = new SimpleDateFormat("yyyy-MM-dd").parse(to);

                    boolean dateStatus = isDateInBetweenIncludingEndPoints(FromDate, ToDate, ToDayDate);


                   /* Log.e("DATE From",""+event.getDateFrom().trim());
                    Log.e("DATE TO",""+event.getDateTo().trim());
                    Log.e("DATE TODAY",""+to);
                    Log.e("DATE STATUS",""+dateStatus);*/

                    if (dateStatus)
                        events.add(event);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        }

        return events;
    }

    public static boolean isDateInBetweenIncludingEndPoints(final Date min, final Date max, final Date date){
        return !(date.before(min) || date.after(max));
    }




    /********************** Manage List in local *******************************/

    private void loadData()
          {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(Globals.TaskEventList, null);
        Type type = new TypeToken<ArrayList<NewEvent>>() {}.getType();
        TaskEventList = gson.fromJson(json, type);
        if (TaskEventList == null) {
            TaskEventList = new ArrayList<>();
        }

  /*      eventList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        eventList.setAdapter(new EventsAdapter(getActivity(), geEvents(TaskEventList)));*/
       }
}