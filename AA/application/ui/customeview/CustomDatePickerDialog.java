package com.application.ui.customeview;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import java.util.Calendar;

public class CustomDatePickerDialog extends DatePickerDialog {

  private int minYear = 1900;
  private int minMonth = 1;
  private int minDay = 1;

  private int maxYear;
  private int maxMonth;
  private int maxDay;

  public CustomDatePickerDialog(Context context,
      OnDateSetListener listener, int year, int monthOfYear,
      int dayOfMonth) {
    super(context, listener, year, monthOfYear, dayOfMonth);
    Calendar cal = Calendar.getInstance();
    maxYear = cal.get(Calendar.YEAR);
    maxMonth = cal.get(Calendar.MONTH);
    maxDay = cal.get(Calendar.DATE);
  }

  @Override
  public void onDateChanged(DatePicker view, int year, int month, int day) {
    super.onDateChanged(view, year, month, day);
    if (year > maxYear || month > maxMonth && year == maxYear
        || day > maxDay && year == maxYear && month == maxMonth) {
      view.updateDate(maxYear, maxMonth, maxDay);
    } else if (year < minYear || month < minMonth && year == minYear
        || day < minDay && year == minYear && month == minMonth) {
      view.updateDate(minYear, minMonth, minDay);
    }
  }

}
