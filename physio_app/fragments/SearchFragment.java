package com.geoxhonapps.physio_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geoxhonapps.physio_app.ContextFlowUtilities;
import com.geoxhonapps.physio_app.R;
import com.geoxhonapps.physio_app.RestUtilities.ADoctorUser;
import com.geoxhonapps.physio_app.RestUtilities.APatient;
import com.geoxhonapps.physio_app.StaticFunctionUtilities;
import com.geoxhonapps.physio_app.activities.PatientInfoActivity;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private View rootView;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private MyAdapter adapter;
    private List<String> dataList;
    private List<APatient> patients;
    public interface OnItemClickListener {
        void onItemClick(String item);
    }
    @Override
    public void onResume() {
        super.onResume();
        fillSearchBar();
    }
    public void fillSearchBar(){
        recyclerView = rootView.findViewById(R.id.recyclerView);
        searchView = rootView.findViewById(R.id.searchView);
        recyclerView.removeAllViews();
        // Initialize the data list
        dataList = new ArrayList<>();

        this.patients = ((ADoctorUser) StaticFunctionUtilities.getUser()).getPatients(false);
        for (APatient patient : patients) {
            String displayName = patient.getDisplayName();
            dataList.add(displayName);
        }

        // Set up RecyclerView with LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Create and set the adapter
        adapter = new MyAdapter(dataList, new OnItemClickListener() {
            @Override
            public void onItemClick(String item) {
                // Handle item click
                onItemClicked(item);
            }
        });
        recyclerView.setAdapter(adapter);

        // Set up the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Filter the data list based on the search query
                filterData(query);
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                // Filter the data list based on the search query
                filterData(newText);
                return true;
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.r5, container, false);
        fillSearchBar();
        return rootView;
    }

    private void filterData(String query) {
        List<String> filteredList = new ArrayList<>();

        // Iterate over the original data list and add items that match the search query
        for (String item : dataList) {
            if (item.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        // Update the adapter with the filtered data list
        adapter.setDataList(filteredList);

    }

    private void onItemClicked(String item) {
        // Set the selected item as the query in the SearchView
        searchView.setQuery(item, false);

        // Store the selected item in a variable
        String selectedResult = item;
        // Use the selected result as needed
        ContextFlowUtilities.moveTo(PatientInfoActivity.class, true, patients.get(dataList.indexOf(selectedResult)));

        // Example: Show a toast message
        Toast.makeText(getActivity(), "Selected: " + selectedResult, Toast.LENGTH_SHORT).show();
    }
}


