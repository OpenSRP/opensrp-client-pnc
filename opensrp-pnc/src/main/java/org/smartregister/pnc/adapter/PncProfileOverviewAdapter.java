package org.smartregister.pnc.adapter;

import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.R;
import org.smartregister.pnc.domain.YamlConfigItem;
import org.smartregister.pnc.domain.YamlConfigWrapper;
import org.smartregister.pnc.utils.PncUtils;
import org.smartregister.util.StringUtil;

import java.util.ArrayList;


/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class PncProfileOverviewAdapter extends RecyclerView.Adapter<PncProfileOverviewAdapter.ViewHolder> {

    private ArrayList<Pair<YamlConfigWrapper, Facts>> mData;
    private LayoutInflater mInflater;
    private Context context;

    // data is passed into the constructor
    public PncProfileOverviewAdapter(@NonNull Context context, @NonNull ArrayList<Pair<YamlConfigWrapper, Facts>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.pnc_profile_overview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<YamlConfigWrapper, Facts> pair = mData.get(position);

        YamlConfigWrapper yamlConfigWrapper = pair.first;
        Facts facts = pair.second;

        if (yamlConfigWrapper != null && facts != null) {
            String group = yamlConfigWrapper.getGroup();
            boolean hasRelevance = StringUtils.isNotBlank(yamlConfigWrapper.getRelevance());
            if (!TextUtils.isEmpty(group)) {
                holder.sectionHeader.setText(StringUtil.humanize(group));
                holder.sectionHeader.setVisibility(View.VISIBLE);
            } else {
                holder.sectionHeader.setVisibility(View.GONE);
            }

            String subGroup = yamlConfigWrapper.getSubGroup();
            if ((!TextUtils.isEmpty(subGroup) && !hasRelevance) ||
                    (hasRelevance && PncLibrary.getInstance().getPncRulesEngineHelper().getRelevance(facts, yamlConfigWrapper.getRelevance()))) {
                if (PncUtils.isTemplate(subGroup)) {
                    subGroup = PncUtils.fillTemplate(subGroup, facts);
                }

                holder.subSectionHeader.setText(StringUtil.humanize(subGroup));
                holder.subSectionHeader.setVisibility(View.VISIBLE);
            } else {
                holder.subSectionHeader.setVisibility(View.GONE);
            }

            if (yamlConfigWrapper.getYamlConfigItem() != null) {
                YamlConfigItem yamlConfigItem = yamlConfigWrapper.getYamlConfigItem();

                fillSectionDetailAndTemplate(holder, facts, yamlConfigItem);
                setRowRedFontText(holder, facts, yamlConfigItem);

                holder.sectionDetailTitle.setVisibility(View.VISIBLE);
                holder.sectionDetails.setVisibility(View.VISIBLE);

            } else {
                holder.sectionDetailTitle.setVisibility(View.GONE);
                holder.sectionDetails.setVisibility(View.GONE);
            }
        }
    }

    private void fillSectionDetailAndTemplate(@NonNull ViewHolder holder, @NonNull Facts facts, @Nullable YamlConfigItem yamlConfigItem) {
        if (yamlConfigItem != null && yamlConfigItem.getTemplate() != null) {
            Template template = getTemplate(yamlConfigItem.getTemplate());

            boolean isHtml = yamlConfigItem.getHtml() != null && yamlConfigItem.getHtml();

            if (PncUtils.isTemplate(template.detail)) {
                String output = PncUtils.fillTemplate(isHtml, template.detail, facts);

                if (isHtml) {
                    PncUtils.setTextAsHtml(holder.sectionDetails, output);
                } else {
                    holder.sectionDetails.setText(output);
                }
            } else {
                holder.sectionDetails.setText(template.detail);
            }

            if (PncUtils.isTemplate(template.title)) {
                String output = PncUtils.fillTemplate(template.title, facts);
                holder.sectionDetailTitle.setText(output);
            } else {
                holder.sectionDetailTitle.setText(template.title);
            }
        }
    }

    private void setRowRedFontText(@NonNull ViewHolder holder, @NonNull Facts facts, @Nullable YamlConfigItem yamlConfigItem) {
        if (yamlConfigItem != null && yamlConfigItem.getIsRedFont() != null && PncLibrary.getInstance().getPncRulesEngineHelper().getRelevance(facts, yamlConfigItem.getIsRedFont())) {
            holder.sectionDetailTitle.setTextColor(getColor(R.color.overview_font_red));
            holder.sectionDetails.setTextColor(getColor(R.color.overview_font_red));
        } else {
            holder.sectionDetailTitle.setTextColor(getColor(R.color.overview_font_left));
            holder.sectionDetails.setTextColor(getColor(R.color.overview_font_right));
        }
    }

    private int getColor(@ColorRes int colorId) {
        return context.getResources().getColor(colorId);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    private String processUnderscores(String string) {
        return string.replace("_", " ").toUpperCase();
    }

    public Template getTemplate(String rawTemplate) {
        Template template = new Template();

        if (rawTemplate.contains(":")) {
            String[] templateArray = rawTemplate.split(":");
            if (templateArray.length > 1) {
                template.title = templateArray[0].trim();
                template.detail = templateArray[1].trim();
            }
        } else {
            template.title = rawTemplate;
        }

        return template;

    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;
        private TextView sectionHeader;
        private TextView subSectionHeader;
        private TextView sectionDetails;
        private TextView sectionDetailTitle;

        ViewHolder(View itemView) {
            super(itemView);
            sectionHeader = itemView.findViewById(R.id.overview_section_header);
            subSectionHeader = itemView.findViewById(R.id.overview_subsection_header);
            sectionDetailTitle = itemView.findViewById(R.id.overview_section_details_left);
            sectionDetails = itemView.findViewById(R.id.overview_section_details_right);

            parent = itemView;
        }
    }

    public static class Template {
        public String title = "";
        public String detail = "";
    }

}