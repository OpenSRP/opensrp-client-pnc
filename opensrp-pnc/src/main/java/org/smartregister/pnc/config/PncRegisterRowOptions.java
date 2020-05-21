package org.smartregister.pnc.config;

import android.database.Cursor;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.pnc.holder.PncRegisterViewHolder;
import org.smartregister.view.contract.SmartRegisterClient;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public interface PncRegisterRowOptions<T extends PncRegisterViewHolder> {

    boolean isDefaultPopulatePatientColumn();

    /**
     * You should set all the data that should be displayed for each client column here. For this use
     * the #maternityRegisterViewHolder passed and in case you are using a custom one you can just cast it to
     * whatever you provided in {@link PncRegisterRowOptions#createCustomViewHolder}
     *
     * @param cursor                   cursor object on the current row
     * @param commonPersonObjectClient Contains the column maps for the current user
     * @param smartRegisterClient
     * @param maternityRegisterViewHolder    The recycler view holder which holds the required views
     */
    void populateClientRow(@NonNull Cursor cursor, @NonNull CommonPersonObjectClient commonPersonObjectClient, @NonNull SmartRegisterClient smartRegisterClient, @NonNull PncRegisterViewHolder maternityRegisterViewHolder);

    boolean isCustomViewHolder();

    @Nullable
    T createCustomViewHolder(@NonNull View itemView);

    boolean useCustomViewLayout();

    @LayoutRes
    int getCustomViewLayoutId();

}
