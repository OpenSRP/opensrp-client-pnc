package org.smartregister.pnc.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.text.WordUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.R;
import org.smartregister.pnc.config.PncRegisterProviderMetadata;
import org.smartregister.pnc.config.PncRegisterRowOptions;
import org.smartregister.pnc.holder.FooterViewHolder;
import org.smartregister.pnc.holder.PncRegisterViewHolder;
import org.smartregister.pnc.pojo.PncBaseDetails;
import org.smartregister.pnc.scheduler.PncVisitScheduler;
import org.smartregister.pnc.scheduler.VisitScheduler;
import org.smartregister.pnc.scheduler.VisitStatus;
import org.smartregister.pnc.utils.ConfigurationInstancesHelper;
import org.smartregister.pnc.utils.PncConstants;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.pnc.utils.PncViewConstants;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class PncRegisterProvider implements RecyclerViewProvider<PncRegisterViewHolder> {
    private final LayoutInflater inflater;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private Context context;

    private PncRegisterProviderMetadata pncRegisterProviderMetadata;

    @Nullable
    private PncRegisterRowOptions pncRegisterRowOptions;

    public PncRegisterProvider(@NonNull Context context, @NonNull View.OnClickListener onClickListener, @NonNull View.OnClickListener paginationClickListener) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.context = context;

        // Get the configuration
        this.pncRegisterProviderMetadata = ConfigurationInstancesHelper
                .newInstance(PncLibrary.getInstance()
                        .getPncConfiguration()
                        .getPncRegisterProviderMetadata());

        Class<? extends PncRegisterRowOptions> pncRegisterRowOptionsClass = PncLibrary.getInstance()
                .getPncConfiguration()
                .getPncRegisterRowOptions();
        if (pncRegisterRowOptionsClass != null) {
            this.pncRegisterRowOptions = ConfigurationInstancesHelper.newInstance(pncRegisterRowOptionsClass);
        }
    }

    public static void fillValue(@Nullable TextView v, @NonNull String value) {
        if (v != null) {
            v.setText(value);
        }
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, PncRegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        if (pncRegisterRowOptions != null && pncRegisterRowOptions.isDefaultPopulatePatientColumn()) {
            pncRegisterRowOptions.populateClientRow(cursor, pc, client, viewHolder);
        } else {
            populatePatientColumn(pc, viewHolder);

            if (pncRegisterRowOptions != null) {
                pncRegisterRowOptions.populateClientRow(cursor, pc, client, viewHolder);
            }
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(R.string.str_page_info), currentPageCount,
                        totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) { /*Implement Abstract Method */ }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public PncRegisterViewHolder createViewHolder(ViewGroup parent) {
        int resId = R.layout.pnc_register_list_row;

        if (pncRegisterRowOptions != null
                && pncRegisterRowOptions.useCustomViewLayout()
                && pncRegisterRowOptions.getCustomViewLayoutId() != 0) {
            resId = pncRegisterRowOptions.getCustomViewLayoutId();
        }

        View view = inflater.inflate(resId, parent, false);

        if (pncRegisterRowOptions != null && pncRegisterRowOptions.isCustomViewHolder()) {
            return pncRegisterRowOptions.createCustomViewHolder(view);
        } else {
            return new PncRegisterViewHolder(view);
        }
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof FooterViewHolder;
    }

    public void populatePatientColumn(CommonPersonObjectClient commonPersonObjectClient, PncRegisterViewHolder viewHolder) {
        Map<String, String> patientColumnMaps = commonPersonObjectClient.getColumnmaps();

        String firstName = pncRegisterProviderMetadata.getClientFirstName(patientColumnMaps);
        String middleName = pncRegisterProviderMetadata.getClientMiddleName(patientColumnMaps);
        String lastName = pncRegisterProviderMetadata.getClientLastName(patientColumnMaps);
        String patientName = Utils.getName(firstName, middleName + " " + lastName);

        String dobString = Utils.getDuration(pncRegisterProviderMetadata.getDob(patientColumnMaps));
        String translatedYearInitial = context.getResources().getString(R.string.abbrv_years);
        fillValue(viewHolder.textViewPatientName, WordUtils.capitalize(patientName));

        fillValue(viewHolder.tvAge, String.format(context.getString(R.string.patient_age_holder), WordUtils.capitalize(PncUtils.getClientAge(dobString, translatedYearInitial))));
        //String ga = pncRegisterProviderMetadata.getGA(patientColumnMaps);
        fillValue(viewHolder.textViewGa, String.format(context.getString(R.string.patient_ga_holder), " "));

        String patientId = pncRegisterProviderMetadata.getPatientID(patientColumnMaps);
        fillValue(viewHolder.tvPatientId, String.format(context.getString(R.string.patient_id_holder), patientId));

        addButtonClickListeners(commonPersonObjectClient, viewHolder);
    }

    public void addButtonClickListeners(@NonNull CommonPersonObjectClient client, PncRegisterViewHolder viewHolder) {

        viewHolder.dueButton.setTag(R.id.BUTTON_TYPE, R.string.start_pnc);

        PncBaseDetails pncBaseDetails = new PncBaseDetails();
        pncBaseDetails.setBaseEntityId(client.getCaseId());
        pncBaseDetails = PncLibrary.getInstance().getPncRegistrationDetailsRepository().findOne(pncBaseDetails);
        if (pncBaseDetails != null && pncBaseDetails.getProperties() != null) {
            HashMap<String, String> data = pncBaseDetails.getProperties();

            if ("1".equals(data.get(PncConstants.JsonFormKeyConstants.OUTCOME_SUBMITTED))) {

                String deliveryDateStr = data.get(PncConstants.FormGlobalConstants.DELIVERY_DATE);
                LocalDate deliveryDate = LocalDate.parse(deliveryDateStr, DateTimeFormat.forPattern("dd-MM-yyyy"));
                VisitScheduler pncVisitScheduler = new PncVisitScheduler(deliveryDate, client.getCaseId());

                if (pncVisitScheduler.getStatus() == VisitStatus.PNC_DUE) {
                    viewHolder.dueButton.setText(R.string.pnc_due);
                    viewHolder.dueButton.setTag(R.id.BUTTON_TYPE, R.string.pnc_due);
                }
                else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_OVERDUE) {
                    viewHolder.dueButton.setText(R.string.pnc_due);
                    viewHolder.dueButton.setTag(R.id.BUTTON_TYPE, R.string.pnc_overdue);
                    viewHolder.dueButton.setTextColor(ContextCompat.getColor(viewHolder.dueButton.getContext(), R.color.pnc_circle_red));
                    viewHolder.dueButton.setBackgroundResource(R.drawable.pnc_overdue_bg);
                }
                else if (pncVisitScheduler.getStatus() == VisitStatus.RECORD_PNC) {
                    viewHolder.dueButton.setText(R.string.record_pnc);
                    viewHolder.dueButton.setTag(R.id.BUTTON_TYPE, R.string.record_pnc);
                }
                else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_DONE_TODAY) {
                    viewHolder.dueButton.setText(R.string.pnc_done_today);
                    viewHolder.dueButton.setTag(R.id.BUTTON_TYPE, R.string.pnc_done_today);
                    viewHolder.dueButton.setTextColor(ContextCompat.getColor(viewHolder.dueButton.getContext(), R.color.dark_grey));
                    viewHolder.dueButton.setBackground(null);
                }
                else if (pncVisitScheduler.getStatus() == VisitStatus.PNC_CLOSE) {
                    viewHolder.dueButton.setText(R.string.pnc_close);
                    viewHolder.dueButton.setTag(R.id.BUTTON_TYPE, R.string.pnc_close);
                }
            }
        }


        View patient = viewHolder.patientColumn;
        attachPatientOnclickListener(PncViewConstants.Provider.PATIENT_COLUMN, patient, client);
        attachPatientOnclickListener(PncViewConstants.Provider.ACTION_BUTTON_COLUMN, viewHolder.dueButton, client);

    }

    public void attachPatientOnclickListener(@NonNull String viewType, @NonNull View view, @NonNull CommonPersonObjectClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(R.id.VIEW_TYPE, viewType);
        view.setTag(R.id.VIEW_CLIENT, client);
    }
}