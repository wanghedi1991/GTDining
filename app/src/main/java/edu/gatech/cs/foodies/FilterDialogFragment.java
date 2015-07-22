package edu.gatech.cs.foodies;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

/**
 * Created by Hedi Wang on 2015/7/22.
 */
public class FilterDialogFragment extends DialogFragment {
    FilterDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_filter_dialog, null);
        final CheckBox diningDollar = (CheckBox) view.findViewById(R.id.dinning_dollar);
        final CheckBox mealPlan = (CheckBox) view.findViewById(R.id.meal_plan);
        final CheckBox buzzfund = (CheckBox) view.findViewById(R.id.buzzfund);
        final Spinner cost = (Spinner) view.findViewById(R.id.cost);
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.cost_array, android.R.layout.simple_list_item_1);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cost.setAdapter(arrayAdapter);
        builder.setView(view).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton(R.string.filter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogPositiveClick(diningDollar.isChecked(), mealPlan.isChecked(), buzzfund.isChecked(), cost.getSelectedItemPosition());
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (FilterDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public interface FilterDialogListener {
        void onDialogPositiveClick(boolean diningDollar, boolean mealPlan, boolean buzzFund, int costLevel);
    }
}
