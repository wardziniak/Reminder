package com.wardziniak.reminder.fragments;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wardziniak.reminder.R;
import com.wardziniak.reminder.activities.AddAlarmFragmentInteractionListener;
import com.wardziniak.reminder.providers.DatabaseEntries;
import com.wardziniak.reminder.services.AlarmManagerService;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link AddAlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAlarmFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ALARM_ID = "alarmId";
    public static final long NEW_ALARM = 0;

    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";
    private static final String ARG_DAY = "day";

    private Button saveButton;
    private EditText messageEditText;
    private TextView dateTextView;
    private TextView timeTextView;

    private long alarmId;

    private AddAlarmFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddAlarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddAlarmFragment newInstance() {
        AddAlarmFragment fragment = new AddAlarmFragment();
        return fragment;
    }

    public static AddAlarmFragment newInstance(long alarmId) {
        AddAlarmFragment fragment = new AddAlarmFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ALARM_ID, alarmId);
        fragment.setArguments(args);
        return fragment;
    }

    public AddAlarmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            alarmId = getArguments().getLong(ARG_ALARM_ID);
        }
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_alarm, container, false);
        saveButton = (Button) v.findViewById(R.id.saveButton);
        dateTextView = (TextView) v.findViewById(R.id.datePickerId);
        timeTextView = (TextView) v.findViewById(R.id.timePickerId);
        messageEditText = (EditText) v.findViewById(R.id.messageFieldId);
        saveButton.setOnClickListener(this);
        dateTextView.setOnClickListener(this);
        timeTextView.setOnClickListener(this);
        v.setOnClickListener(this);
        mListener.refreshAddAlarmFragment();
//        final Calendar c = Calendar.getInstance();
//        updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
//        mListener.onDateChange(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        return v;
    }

    private void startService() {
        Intent startServiceIntent = new Intent(getActivity(), AlarmManagerService.class);
        getActivity().startService(startServiceIntent);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AddAlarmFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddAlarmFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveButton:
                mListener.setAlarm(alarmId, messageEditText.getText().toString());
                break;
            case R.id.datePickerId:
                mListener.showDatePicker();
                break;
            case R.id.timePickerId:
                mListener.showTimePicker();
                break;
            default:
                break;
        }
    }

    public void updateDate(Calendar calendar) {
        dateTextView.setText("" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR));
    }

    public void updateTime(Calendar calendar) {
        timeTextView.setText("" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (alarmId != 0)
            return new CursorLoader(getActivity(), ContentUris.withAppendedId(Uri.parse(DatabaseEntries.SCHEME + DatabaseEntries.AUTHORITY + DatabaseEntries.ALARM_PATH), alarmId),
                    DatabaseEntries.sAlarmProjection, null, null, null);
        else
            return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        messageEditText.setText(cursor.getString(cursor.getColumnIndex(DatabaseEntries.AlarmEntry.COLUMN_NAME_ALA_MESSAGE)));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(DatabaseEntries.AlarmEntry.COLUMN_NAME_ALA_TIMESTAMP)));
        updateDate(calendar);
        updateTime(calendar);
        mListener.onAlarmLoad(calendar);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
