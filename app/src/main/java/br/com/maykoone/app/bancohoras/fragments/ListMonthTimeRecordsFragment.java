package br.com.maykoone.app.bancohoras.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.maykoone.app.bancohoras.ExpandableAdapter;
import br.com.maykoone.app.bancohoras.R;
import br.com.maykoone.app.bancohoras.db.ControleDatabaseHelper;
import br.com.maykoone.app.bancohoras.db.RegistroPontoEntity;

/**
 * Created by maykoone on 09/08/15.
 */
public class ListMonthTimeRecordsFragment extends Fragment {

    private ExpandableListView mExpandableListView;
    private ControleDatabaseHelper mDbHelper;

    private Map<String, List<String>> mListChildren;
    private List<String> mListGroup;
    private ExpandableAdapter mExpandableAdapter;

    public ListMonthTimeRecordsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new ControleDatabaseHelper(getActivity());
//        fillListGroupAndChildren();
        populateListAdapter();
        mExpandableAdapter = new ExpandableAdapter(getActivity(), mListGroup, mListChildren);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_month_time_records_fragment, container, false);

        mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        mExpandableListView.setAdapter(mExpandableAdapter);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.addAll(Arrays.asList("JUN 2015", "JUL 2015", "AGO 2015"));
        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        spinner.setAdapter(spinnerAdapter);
        return rootView;
    }

    private void populateListAdapter() {
        List<RegistroPontoEntity> result = mDbHelper.getAllRegistrosPontoByMonthAndYear(8, 2015);
        Map<String, List<String>> groupByDayOfMonth = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (RegistroPontoEntity r : result) {
            String time = r.getDataEvento();
            String[] timeParts = time.split(" ");
            if (groupByDayOfMonth.containsKey(timeParts[0])) {
                groupByDayOfMonth.get(timeParts[0]).add(time);
            } else {
                List<String> timeList = new ArrayList<>();
                timeList.add(time);
                groupByDayOfMonth.put(timeParts[0], timeList);
            }
        }
        mListChildren = groupByDayOfMonth;
        mListGroup = new ArrayList<>(groupByDayOfMonth.keySet());
    }

    private void fillListGroupAndChildren() {
        mListGroup = Arrays.asList("7 Seg", "8 Ter", "9 Qua");
        mListChildren = new HashMap<>();

        mListChildren.put(mListGroup.get(0), Arrays.asList("08:30", "12:00", "13:00"));
        mListChildren.put(mListGroup.get(1), Arrays.asList("08:30", "12:00", "13:00"));
        mListChildren.put(mListGroup.get(2), Arrays.asList("08:30", "12:00", "13:00"));
    }
}
