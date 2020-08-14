package com.example.profiler.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Locale;


public class SetDate implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private EditText editText;
    private Calendar calendar;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public SetDate(EditText editText, Context context){
        this.editText = editText;
        this.editText.setOnClickListener(this);
        calendar = Calendar.getInstance();
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)     {
        // this.editText.setText();

        String myFormat = "MMM dd, yyyy"; //In which you need put here
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        editText.setText(simpleDateFormat.format(calendar.getTime()));

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        new DatePickerDialog(context, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
