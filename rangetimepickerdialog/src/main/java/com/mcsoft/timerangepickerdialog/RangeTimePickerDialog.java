package com.mcsoft.timerangepickerdialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class RangeTimePickerDialog extends DialogFragment
{
    private AlertDialog mAlertDialog;
    private boolean dialogDismissed;
    private TabLayout tabLayout;
    private TabItem tabItemStartTime, tabItemEndTime;
    private TimePicker timePickerStart, timePickerEnd;
    private Button btnPositive, btnNegative;

    private int colorTabUnselected;
    private int colorTabSelected;
    private int colorTextButton;
    private int colorBackgroundHeader;
    private boolean is24HourView = true;
    private String messageErrorRangeTime = "Error: set a end time greater than start time";
        private String textBtnPositive = "Ok";
    private String textBtnNegative = "Cancel";

    public interface ISelectedTime
    {
        void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd);
    }

    public RangeTimePickerDialog newInstance()
    {
        RangeTimePickerDialog f = new RangeTimePickerDialog();
        return f;
    }

    private ISelectedTime mCallback;

    /**
     * Create a new instance with own attributes (All color MUST BE in this format "R.color.my_color")
     * @param colorBackgroundHeader Color of Background header dialog
     * @param colorTabUnselected Color of tab when unselected
     * @param colorTabSelected Color of tab when selected
     * @param colorTextButton Text color of button
     * @param is24HourView Indicates if the format should be 24 hours
     * @return
     */
    public RangeTimePickerDialog newInstance(int colorBackgroundHeader, int colorTabUnselected, int colorTabSelected, int colorTextButton, boolean is24HourView)
    {
        RangeTimePickerDialog f = new RangeTimePickerDialog();
        this.colorTabUnselected = colorTabUnselected;
        this.colorBackgroundHeader = colorBackgroundHeader;
        this.colorTabSelected = colorTabSelected;
        this.colorTextButton = colorTextButton;
        this.is24HourView = is24HourView;
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View dialogView = inflater.inflate(R.layout.layout_custom_dialog, null);
        builder.setView(dialogView);
        tabLayout = (TabLayout) dialogView.findViewById(R.id.tabLayout);
        tabItemStartTime = (TabItem) dialogView.findViewById(R.id.tabStartTime);
        tabItemEndTime = (TabItem) dialogView.findViewById(R.id.tabEndTime);
        timePickerStart = (TimePicker) dialogView.findViewById(R.id.timePickerStart);
        timePickerEnd = (TimePicker) dialogView.findViewById(R.id.timePickerEnd);
        btnPositive = (Button) dialogView.findViewById(R.id.btnPositiveDialog);
        btnNegative = (Button) dialogView.findViewById(R.id.btnNegativeDialog);
        CardView cardView = (CardView) dialogView.findViewById(R.id.ly_root);

        setColorTabLayout(colorTabSelected, colorTabUnselected, colorBackgroundHeader);

        timePickerStart.setIs24HourView(is24HourView);
        timePickerEnd.setIs24HourView(is24HourView);

        btnPositive.setTextColor(ContextCompat.getColor(getActivity(), colorTextButton));
        btnNegative.setTextColor(ContextCompat.getColor(getActivity(), colorTextButton));
        btnPositive.setText(textBtnPositive);
        btnNegative.setText(textBtnNegative);

        // Create the AlertDialog object and return it
        mAlertDialog = builder.create();
        mAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener()
                {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab)
                    {
                        int tabIconColor = ContextCompat.getColor(getActivity(), colorTabSelected);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                        //tab.getIcon().setTint(Color.YELLOW);
                        if(tab.getPosition()==0)
                        {
                            timePickerStart.setVisibility(View.VISIBLE);
                            timePickerEnd.setVisibility(View.GONE);
                        }
                        else
                        {
                            timePickerStart.setVisibility(View.GONE);
                            timePickerEnd.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab)
                    {
                        int tabIconColor = ContextCompat.getColor(getActivity(), colorTabUnselected);
                        tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
                        //tab.getIcon().setTint(Color.WHITE);
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab)
                    {

                    }
                });

                btnNegative.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dismiss();
                    }
                });
                btnPositive.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        boolean flagCorrect;
                        int hourStart, minuteStart, hourEnd, minuteEnd;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            hourStart = timePickerStart.getHour();
                            minuteStart = timePickerStart.getMinute();
                            hourEnd = timePickerEnd.getHour();
                            minuteEnd = timePickerEnd.getMinute();
                        }
                        else
                        {
                            hourStart = timePickerStart.getCurrentHour();
                            minuteStart = timePickerStart.getCurrentMinute();
                            hourEnd = timePickerEnd.getCurrentHour();
                            minuteEnd = timePickerEnd.getCurrentMinute();
                        }
                        if(hourEnd>hourStart)
                        {
                            flagCorrect = true;
                        }
                        else if(hourEnd==hourStart && minuteEnd>minuteStart)
                        {
                            flagCorrect = true;
                        }
                        else
                        {
                            flagCorrect = false;
                        }
                        if(flagCorrect)
                        {
                            // Check if this dialog was called by a fragment
                            if (getTargetFragment()!=null)
                            {
                                // Return value to Fragment
                                Bundle bundle = new Bundle();
                                bundle.putInt("hourStart", hourStart);
                                bundle.putInt("minuteStart", minuteStart);
                                bundle.putInt("hourEnd", hourEnd);
                                bundle.putInt("minuteEnd", minuteEnd);
                                Intent intent = new Intent().putExtras(bundle);
                                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                            }
                            else
                            {
                                // Return value to activity
                                mCallback.onSelectedTime(hourStart,minuteStart,hourEnd,minuteEnd);
                            }
                            dismiss();
                        }
                        else
                        {
                            Toast.makeText(getActivity(), messageErrorRangeTime, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return mAlertDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        dialogDismissed = true;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (dialogDismissed && mAlertDialog != null)
        {
            mAlertDialog.dismiss();
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mCallback = (ISelectedTime) activity;
        }
        catch (ClassCastException e)
        {
            Log.d("MyDialog", "Activity doesn't implement the interface");
        }
    }

    public void setColorTabUnselected(int colorTabUnselected)
    {
        this.colorTabUnselected = colorTabUnselected;
    }

    public void setColorTabSelected(int colorTabSelected)
    {
        this.colorTabSelected = colorTabSelected;
    }

    public void setColorTextButton(int colorTextButton)
    {
        this.colorTextButton = colorTextButton;
    }

    public void setColorBackgroundHeader(int colorBackgroundHeader)
    {
        this.colorBackgroundHeader = colorBackgroundHeader;
    }

    public void setIs24HourView(boolean is24HourView)
    {
        this.is24HourView = is24HourView;
    }

    public void setMessageErrorRangeTime(String messageErrorRangeTime)
    {
        this.messageErrorRangeTime = messageErrorRangeTime;
    }

    public void setTextBtnPositive(String textBtnPositive)
    {
        this.textBtnPositive = textBtnPositive;
    }

    public void setTextBtnNegative(String textBtnNegative)
    {
        this.textBtnNegative = textBtnNegative;
    }

    private void setColorTabLayout(int colorTabSelected, int colorTabUnselected, int colorBackgroundHeader)
    {
        tabLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), colorBackgroundHeader));
        // Set color header TabLayout
        tabLayout.setTabTextColors(ContextCompat.getColor(getActivity(), colorTabUnselected), ContextCompat.getColor(getActivity(), colorTabSelected));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(getActivity(), colorTabSelected));
        // Use setColorFilter to avoid setTint because setTint is for API >= 21
        int tabIconColor = ContextCompat.getColor(getActivity(), colorTabSelected);
        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        //tabLayout.getTabAt(0).getIcon().setTint(Color.YELLOW);
        tabIconColor = ContextCompat.getColor(getActivity(), colorTabUnselected);
        tabLayout.getTabAt(1).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        //tabLayout.getTabAt(1).getIcon().setTint(Color.WHITE);
    }
}
