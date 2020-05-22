package org.smartregister.pnc.holder;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.pnc.R;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegisterViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewPatientName;
    public TextView textViewGa;
    public Button dueButton;
    public View dueButtonLayout;
    public View patientColumn;
    public TextView tvAge;
    public TextView tvPatientId;

    public TextView firstDotDivider;

    public PncRegisterViewHolder(View itemView) {
        super(itemView);

        textViewPatientName = itemView.findViewById(R.id.tv_maternityRegisterListRow_patientName);
        textViewGa = itemView.findViewById(R.id.tv_maternityRegisterListRow_ga);
        dueButton = itemView.findViewById(R.id.btn_maternityRegisterListRow_clientAction);
        dueButtonLayout = itemView.findViewById(R.id.ll_maternityRegisterListRow_clientActionWrapper);
        tvAge = itemView.findViewById(R.id.tv_maternityRegisterListRow_age);
        tvPatientId = itemView.findViewById(R.id.tv_maternityRegisterListRow_patientId);

        patientColumn = itemView.findViewById(R.id.patient_column);
        firstDotDivider = itemView.findViewById(R.id.tv_maternityRegisterListRow_firstDotDivider);
    }

    public void showPatientAge() {
        tvAge.setVisibility(View.VISIBLE);
        firstDotDivider.setVisibility(View.VISIBLE);
    }

    public void hidePatientAge() {
        tvAge.setVisibility(View.GONE);
        firstDotDivider.setVisibility(View.GONE);
    }
}