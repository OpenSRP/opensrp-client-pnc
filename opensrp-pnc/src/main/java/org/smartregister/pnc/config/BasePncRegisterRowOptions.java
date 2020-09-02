package org.smartregister.pnc.config;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.holder.PncRegisterViewHolder;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.view.contract.SmartRegisterClient;

public class BasePncRegisterRowOptions implements PncRegisterRowOptions {
    @Override
    public boolean isDefaultPopulatePatientColumn() {
        return false;
    }

    @Override
    public void populateClientRow(@NonNull Cursor cursor, @NonNull CommonPersonObjectClient commonPersonObjectClient, @NonNull SmartRegisterClient smartRegisterClient, @NonNull PncRegisterViewHolder pncRegisterViewHolder) {
        PncUtils.setVisitButtonStatus(pncRegisterViewHolder.dueButton, commonPersonObjectClient);
    }

    @Override
    public boolean isCustomViewHolder() {
        return false;
    }

    @Nullable
    @Override
    public PncRegisterViewHolder createCustomViewHolder(@NonNull View itemView) {
        return null;
    }

    @Override
    public boolean useCustomViewLayout() {
        return false;
    }

    @Override
    public int getCustomViewLayoutId() {
        return 0;
    }
}
