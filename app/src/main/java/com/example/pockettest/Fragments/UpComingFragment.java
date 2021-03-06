/*
 * Copyright (c) 2020. Made by Vivek Surya and Raj Rathod. All Rights Reserved.
 * Last Modified 15/11/20 12:41 PM
 */

package com.example.pockettest.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.pockettest.Adapters.OnListAdapter;
import com.example.pockettest.Adapters.UpListAdapter;
import com.example.pockettest.DataBase.SharedPrefManager;
import com.example.pockettest.Model.Quiz;
import com.example.pockettest.R;
import com.example.pockettest.Util.Urls;
import com.example.pockettest.Util.VolleySingleton;
import com.faltenreich.skeletonlayout.Skeleton;
import com.faltenreich.skeletonlayout.SkeletonLayoutUtils;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpComingFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Quiz> upComingQuizList;
    private UpListAdapter upListAdapter;
    private Skeleton skeleton;
    private String slug;
    private Context context;
    private TextView helper;
    public UpComingFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_up_coming, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.toolbar_search,menu);
        MenuItem item=menu.findItem(R.id.search_filter);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView  = view.findViewById(R.id.upcoming_rv);
        skeleton = view.findViewById(R.id.upcoming_skeletonLayout);
        helper = view.findViewById(R.id.upgoing_helper);
        if(getArguments() != null){
            slug = getArguments().getString("slug");
        }
        context = view.getContext();
        upComingQuizList = new ArrayList<>();
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        skeleton = SkeletonLayoutUtils.applySkeleton(recyclerView,  R.layout.skeleton_layout_quiz, 3);
        skeleton.showSkeleton();
        getQuiz();

    }

    private void getQuiz() {
        upComingQuizList.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Urls.BASE_URL + slug + Urls.GET_QUIZ, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                skeleton.showOriginal();
                try {
                    JSONArray parentArray = new JSONArray(response);
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject quizObj = parentArray.getJSONObject(i);
                        Quiz quiz = new Quiz();
                        quiz.setTitle(quizObj.getString("title"));
                        quiz.setDescription(quizObj.getString("description"));
                        quiz.setTotal_marks(quizObj.getString("total_marks"));
                        quiz.setPrimary_key(quizObj.getString("pk"));
                        quiz.setPublish_date(LocalDateTime.parse(quizObj.getString("publish_date")));
                        quiz.setEnd_time(LocalDateTime.parse(quizObj.getString("end_date")));
                        LocalDateTime dateTime = LocalDateTime.now();
                        int startTimeDiff = dateTime.compareTo(quiz.getPublish_date());
                        int endTimeDiff = dateTime.compareTo(quiz.getEnd_time());
                        if (startTimeDiff < 0) {
                            upComingQuizList.add(quiz);
                        }
                    }

                } catch (JSONException e) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Something went wrong. Please try again later", Snackbar.LENGTH_SHORT).show();
                }
                if(upComingQuizList.size() == 0){
                    helper.setVisibility(View.VISIBLE);
                }else {
                    upListAdapter = new UpListAdapter(upComingQuizList, context);
                    recyclerView.setAdapter(upListAdapter);
                    upListAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Something went wrong. Please try again later", Snackbar.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SharedPrefManager.getInstance(context).getToken());
                return headers;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }
}