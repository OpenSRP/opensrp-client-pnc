package org.smartregister.pnc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.pnc.R;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.util.Utils;

import java.util.List;

public class ClientLookUpListAdapter extends RecyclerView.Adapter<ClientLookUpListAdapter.MyViewHolder> {

    private List<CommonPersonObject> data;
    private Context context;
    private static ClickListener clickListener;

    public ClientLookUpListAdapter(List<CommonPersonObject> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.maternity_lookup_item, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        CommonPersonObject commonPersonObject = data.get(i);
        String firstName = Utils.getValue(commonPersonObject.getColumnmaps(), PncDbConstants.Column.Client.FIRST_NAME, true);
        String lastName = Utils.getValue(commonPersonObject.getColumnmaps(), PncDbConstants.Column.Client.LAST_NAME, true);
        String opensrpId = Utils.getValue(commonPersonObject.getColumnmaps(), PncDbConstants.KEY.OPENSRP_ID, true);
        String fullName = firstName + " " + lastName;
        String details = context.getString(R.string.maternity_opensrp_id_type) + " - " + opensrpId;

        holder.txtName.setText(fullName);
        holder.itemView.setTag(Utils.convert(commonPersonObject));
        holder.txtDetails.setText(details);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtName;
        private TextView txtDetails;

        private MyViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txtName);
            txtDetails = view.findViewById(R.id.txtDetails);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view);
            }
        }
    }

    public interface ClickListener {
        void onItemClick(View view);
    }

    public void setOnClickListener(ClickListener onClickListener) {
        ClientLookUpListAdapter.clickListener = onClickListener;
    }
}
