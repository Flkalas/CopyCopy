package com.mindlink.flkalas.copycopy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Lesenic on 2016-04-18.
 */
public class ReportDialogFragment extends DialogFragment {
    public interface ReportDialogListener{
        public void onRdPositiveClick(DialogFragment dialog);
        public void onRdNegativeClick(DialogFragment dialog);
    }

    ReportDialogListener mListener;

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            mListener = (ReportDialogListener)activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+" must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_report);
        builder.setPositiveButton(R.string.report, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onRdPositiveClick(ReportDialogFragment.this);
                //Toast.makeText(getActivity(), "Report", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.no_report, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mListener.onRdNegativeClick(ReportDialogFragment.this);
                //Toast.makeText(getActivity(), "No", Toast.LENGTH_SHORT).show();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
