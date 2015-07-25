package br.com.maykoone.app.bancohoras.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by maykoone on 04/07/15.
 */
public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static final String TIME_TO_EDIT = "timeToEdit";
    public static final String ID_TO_EDIT = "idToEdit";
    private OnTimeSelectedListener onTimeSelectedListener;
    private boolean isSelectedOnce;
    private Calendar mSelectedTime;
    private int mSelectedId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onTimeSelectedListener = (OnTimeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimeSelectedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedTime = Calendar.getInstance();
        if (getArguments() != null) {
            String timeToEdit = getArguments().getString(TIME_TO_EDIT);
            mSelectedId = getArguments().getInt(ID_TO_EDIT);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                mSelectedTime.setTime(sdf.parse(timeToEdit));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        int hour = mSelectedTime.get(Calendar.HOUR_OF_DAY);
        int minute = mSelectedTime.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (isSelectedOnce) { //no android api 16 está chamando duas vezes
            Log.i("TimePicker", "is already called");
            return;
        }
//        Calendar c = Calendar.getInstance();
        mSelectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mSelectedTime.set(Calendar.MINUTE, minute);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String time = sdf.format(mSelectedTime.getTime());
        if (mSelectedId > 0) {
            onTimeSelectedListener.onTimeSelected(time, mSelectedId);
        } else {
            onTimeSelectedListener.onTimeSelected(time);
        }

        isSelectedOnce = true;
//        getActivity().getSupportFragmentManager().

    }

    public interface OnTimeSelectedListener {
        void onTimeSelected(String time);

        void onTimeSelected(String time, Integer id);
    }
}
