package br.com.maykoone.app.bancohoras.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.maykoone.app.bancohoras.CustomArrayAdapter;
import br.com.maykoone.app.bancohoras.R;
import br.com.maykoone.app.bancohoras.db.ControleDatabaseHelper;
import br.com.maykoone.app.bancohoras.db.RegistroPontoEntity;

import static br.com.maykoone.app.bancohoras.Util.calculeTime;
import static br.com.maykoone.app.bancohoras.Util.formatTime;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListTimeRecordsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayAdapter<RegistroPontoEntity> listAdapter;
    private List<RegistroPontoEntity> mRegistrosPontosList;
    private ListView listView;

    private String formattedTotalTime;
    private TextView tvTotalTime;

    private String formattedCountTime;
    private TextView tvCountTime;

    private ActionMode currentActionMode;
    private int mSelectedItem;
    private ActionMode.Callback modelCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.setTitle(R.string.action_mode_title);
            actionMode.getMenuInflater().inflate(R.menu.rowselection, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_item_edit:
                    RegistroPontoEntity rEdit = listAdapter.getItem(mSelectedItem);
                    DialogFragment fragment = new TimePickerDialogFragment();
                    Bundle args = new Bundle();
                    args.putString(TimePickerDialogFragment.TIME_TO_EDIT, rEdit.getDataEvento());
                    args.putInt(TimePickerDialogFragment.ID_TO_EDIT, rEdit.getId());
                    fragment.setArguments(args);
                    fragment.show(getActivity().getSupportFragmentManager(), "timerPicker");
                    actionMode.finish();
                    return true;
                case R.id.action_item_remove:
                    RegistroPontoEntity rDelete = listAdapter.getItem(mSelectedItem);
                    new ControleDatabaseHelper(getActivity().getApplicationContext()).deleteRegistroPonto(rDelete);
                    listAdapter.remove(rDelete);
                    Toast.makeText(getActivity(), "Removendo: " + rDelete, Toast.LENGTH_LONG).show();
                    populateListAdapater();
                    actionMode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            currentActionMode = null;
            mSelectedItem = -1;
        }
    };
    private ControleDatabaseHelper mDbHelper;

    public ListTimeRecordsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ListTimeRecordsFragment newInstance(int sectionNumber) {
        ListTimeRecordsFragment fragment = new ListTimeRecordsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(ListTimeRecordsFragment.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        listAdapter = new CustomArrayAdapter(getActivity(), new ArrayList<RegistroPontoEntity>());
        mDbHelper = new ControleDatabaseHelper(getActivity().getApplicationContext());
        populateListAdapater();
        formattedTotalTime = "00:00";

        //Create a handler for update a text view each minute
        final Handler h = new Handler();
        h.post(new Runnable() {

            @Override
            public void run() {
                Log.i("Runnable Count Time ", formattedTotalTime);
                if (mRegistrosPontosList != null && !(mRegistrosPontosList.size() % 2 == 0)) {
                    updateTotalTime();
                }
                h.postDelayed(this, 20000);
            }
        });
    }

    private void populateListAdapater() {
        mRegistrosPontosList = mDbHelper.getAllRegistrosPontoForToday();
        updateTotalTime();

        listAdapter.clear();
        listAdapter.addAll(mRegistrosPontosList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(ListTimeRecordsFragment.class.getName(), "onCreateView");
        View rootView = inflater.inflate(R.layout.list_today_time_records_fragment, container, false);
        listView = (ListView) rootView.findViewById(R.id.main_list_view);
        listView.setAdapter(listAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (currentActionMode != null) {
                    return false;
                }
                mSelectedItem = position;
                currentActionMode = getActivity().startActionMode(modelCallback);
                view.setSelected(true);

                return true;
            }
        });

        tvTotalTime = (TextView) rootView.findViewById(R.id.tv_total_time);
        tvTotalTime.setText(formattedTotalTime);

        tvCountTime = (TextView) rootView.findViewById(R.id.tv_count_time);
        tvCountTime.setText(formattedCountTime);

        return rootView;
    }

    public void notifyUpdate(RegistroPontoEntity e) {
        populateListAdapater();
//        tvTotalTime.setText(formattedTotalTime);
//            listAdapter.notifyDataSetChanged();
    }

    private void updateTotalTime() {
        long totalTimeMillis = calculeTime(mRegistrosPontosList);
        formattedTotalTime = formatTime(totalTimeMillis);
        formattedCountTime = formatTime(totalTimeMillis - (8 * 60 * 60 * 1000));//8 hours
        if (tvTotalTime != null) {
            tvTotalTime.setText(formattedTotalTime);
        }
        if (tvCountTime != null) {
            tvCountTime.setText(formattedCountTime);
        }
    }

}
