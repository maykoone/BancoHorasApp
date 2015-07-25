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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.maykoone.app.bancohoras.CustomArrayAdapter;
import br.com.maykoone.app.bancohoras.R;
import br.com.maykoone.app.bancohoras.db.ControleDatabaseHelper;
import br.com.maykoone.app.bancohoras.db.RegistroPontoEntity;

/**
 * A placeholder fragment containing a simple view.
 */
public class ListTimeRecordsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayAdapter<RegistroPontoEntity> adapter;
    private String totalTime;
    private TextView tvTotalTime;

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
                    RegistroPontoEntity rEdit = adapter.getItem(mSelectedItem);
                    DialogFragment fragment = new TimePickerDialogFragment();
                    Bundle args = new Bundle();
                    args.putString(TimePickerDialogFragment.TIME_TO_EDIT, rEdit.getDataEvento());
                    args.putInt(TimePickerDialogFragment.ID_TO_EDIT, rEdit.getId());
                    fragment.setArguments(args);
                    fragment.show(getActivity().getSupportFragmentManager(), "timerPicker");
                    actionMode.finish();
                    return true;
                case R.id.action_item_remove:
                    RegistroPontoEntity rDelete = adapter.getItem(mSelectedItem);
                    new ControleDatabaseHelper(getActivity().getApplicationContext()).deleteRegistroPonto(rDelete);
                    adapter.remove(rDelete);
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
        super.onCreate(savedInstanceState);
//            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        adapter = new CustomArrayAdapter(getActivity(), new ArrayList<RegistroPontoEntity>());
        populateListAdapater();
    }

    private void populateListAdapater() {
        ControleDatabaseHelper db = new ControleDatabaseHelper(getActivity().getApplicationContext());
        List<RegistroPontoEntity> list = db.getAllRegistrosPonto();
        totalTime = formatTime(calculeTime(list));
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            List<RegistroPontoEntity> l;

            Runnable setList(List<RegistroPontoEntity> l) {
                this.l = l;
                return this;
            }

            @Override
            public void run() {
                Log.i("Runnable Count Time ", totalTime);
                totalTime = formatTime(calculeTime(l));
                tvTotalTime.setText(totalTime);
                h.postDelayed(this, 60000);
            }
        }.setList(list), 60000);

        adapter.clear();
        adapter.addAll(list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.main_list_view);
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
        tvTotalTime.setText(totalTime);
        listView.setAdapter(adapter);
//            listView.addFooterView(tv);
        return rootView;
    }

    public void notifyUpdate() {
        populateListAdapater();
        tvTotalTime.setText(totalTime);
//            adapter.notifyDataSetChanged();
    }

    private long calculeTime(List<RegistroPontoEntity> registros) {
        long totalTime = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Iterator<RegistroPontoEntity> it = registros.iterator();
        while (it.hasNext()) {
            try {
                Date dataRegistroInicio = sdf.parse(it.next().getDataEvento());
                Date dataRegistroFinal = it.hasNext() ? sdf.parse(it.next().getDataEvento()) : new Date();
                totalTime += dataRegistroFinal.getTime() - dataRegistroInicio.getTime();
            } catch (ParseException e) {
                Log.e("calculeTime", e.getMessage());
            }
        }

        return totalTime;
    }

    private String formatTime(long time) {
        long diffMinutes = time / (60 * 1000) % 60;
        long diffHours = time / (60 * 60 * 1000) % 24;
        return String.format("%02d:%02d", diffHours, diffMinutes);
    }
}
