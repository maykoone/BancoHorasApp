package br.com.maykoone.app.bancohoras.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.maykoone.app.bancohoras.ExpandableAdapter;
import br.com.maykoone.app.bancohoras.R;
import br.com.maykoone.app.bancohoras.db.ControleDatabaseHelper;
import br.com.maykoone.app.bancohoras.db.RegistroPontoEntity;

import static br.com.maykoone.app.bancohoras.Util.calculeTime;
import static br.com.maykoone.app.bancohoras.Util.formatTime;

/**
 * Created by maykoone on 09/08/15.
 */
public class ListMonthTimeRecordsFragment extends Fragment {

    private ExpandableListView mExpandableListView;
    private ControleDatabaseHelper mDbHelper;

    private Map<String, List<RegistroPontoEntity>> mListChildren;
    private List<String> mListGroup;
    private ExpandableAdapter mExpandableAdapter;
    private String mSelectedMonth;
    private ArrayAdapter<String> mSpinnerAdapter;

    private String formattedTotalTime;
    private TextView tvTotalTime;

    private String formattedCountTime;
    private TextView tvCountTime;

    public ListMonthTimeRecordsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new ControleDatabaseHelper(getActivity());
        populateListAdapter();
        mExpandableAdapter = new ExpandableAdapter(getActivity(), mListGroup, mListChildren);
        mSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        try {
            mSpinnerAdapter.addAll(mDbHelper.getDistinctMonths());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_month_time_records_fragment, container, false);

        mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableListView);
        mExpandableListView.setAdapter(mExpandableAdapter);

        tvTotalTime = (TextView) rootView.findViewById(R.id.tv_total_time);
        tvCountTime = (TextView) rootView.findViewById(R.id.tv_count_time);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedMonth = (String) parent.getItemAtPosition(position);
                populateListAdapter();
                mExpandableAdapter.updateData(mListGroup, mListChildren);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        return rootView;
    }

    private void populateListAdapter() {
        int month = 0, year = 0;
        Calendar c = Calendar.getInstance();

        if (mSelectedMonth != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy");
            try {
                Date date = sdf.parse(mSelectedMonth);
                c.setTime(date);
                month = c.get(Calendar.MONTH) + 1;
                year = c.get(Calendar.YEAR);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Fail to parse date.", Toast.LENGTH_SHORT).show();
            }
        } else {
            month = c.get(Calendar.MONTH) + 1;
            year = c.get(Calendar.YEAR);
        }
        List<RegistroPontoEntity> result = mDbHelper.getAllRegistrosPontoByMonthAndYear(month, year);        
        Map<String, List<RegistroPontoEntity>> groupByDayOfMonth = new HashMap<>();

        for (RegistroPontoEntity r : result) {
            String time = r.getDataEvento();
            String[] timeParts = time.split(" ");
            if (groupByDayOfMonth.containsKey(timeParts[0])) {
                groupByDayOfMonth.get(timeParts[0]).add(r);
            } else {
                List<RegistroPontoEntity> timeList = new ArrayList<>();
                timeList.add(r);
                groupByDayOfMonth.put(timeParts[0], timeList);
            }
        }
        updateTotalTime(groupByDayOfMonth);
        mListChildren = groupByDayOfMonth;
        mListGroup = new ArrayList<>(groupByDayOfMonth.keySet());
        Collections.sort(mListGroup, Collections.reverseOrder());
    }

    private void updateTotalTime(Map<String, List<RegistroPontoEntity>> registros) {
        long totalTimeMillis = 0;
        long saldoTimeMillis = 0;
        //calcula o total e saldo dos registros por dia e soma os resultados finais
        for (Map.Entry<String, List<RegistroPontoEntity>> entry : registros.entrySet()) {
            List<RegistroPontoEntity> registrosDia = entry.getValue();
            long totalTimeMillisDay = calculeTime(registrosDia);
            long saldoTimeMillisDay = totalTimeMillisDay - (8 * 60 * 60 * 1000);//8 hours
            totalTimeMillis += totalTimeMillisDay;
            saldoTimeMillis += saldoTimeMillisDay;
        }
        
        formattedTotalTime = formatTime(totalTimeMillis);
        formattedCountTime = formatTime(saldoTimeMillis);
        if (tvTotalTime != null) {
            tvTotalTime.setText(formattedTotalTime);
        }
        if (tvCountTime != null) {
            tvCountTime.setText(formattedCountTime);
        }
    }

//    private void fillListGroupAndChildren() {
//        mListGroup = Arrays.asList("7 Seg", "8 Ter", "9 Qua");
//        mListChildren = new HashMap<>();
//
//        mListChildren.put(mListGroup.get(0), Arrays.asList("08:30", "12:00", "13:00"));
//        mListChildren.put(mListGroup.get(1), Arrays.asList("08:30", "12:00", "13:00"));
//        mListChildren.put(mListGroup.get(2), Arrays.asList("08:30", "12:00", "13:00"));
//    }
}
