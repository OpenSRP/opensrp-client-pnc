package org.smartregister.pnc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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
public class PncProfileVisitsAdapter extends RecyclerView.Adapter<PncProfileVisitsAdapter.YamlViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<Pair<YamlConfigWrapper, Facts>> items;

    // data is passed into the constructor
    public PncProfileVisitsAdapter(@NonNull Context context, ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.items = items;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public YamlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.pnc_profile_overview_row, parent, false);
        return new YamlViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull YamlViewHolder holder, int position) {
        Pair<YamlConfigWrapper, Facts> pair = items.get(position);

        YamlConfigWrapper yamlConfigWrapper = pair.first;
        Facts facts = pair.second;

        if (yamlConfigWrapper != null && facts != null) {
            String group = yamlConfigWrapper.getGroup();

            if (!TextUtils.isEmpty(group)) {
                holder.sectionHeader.setText(StringUtil.humanize(group));
                holder.sectionHeader.setVisibility(View.VISIBLE);
            } else {
                holder.sectionHeader.setVisibility(View.GONE);
            }

            String subGroup = yamlConfigWrapper.getSubGroup();
            if (!TextUtils.isEmpty(subGroup)) {
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

    private void fillSectionDetailAndTemplate(@NonNull YamlViewHolder holder, @NonNull Facts facts, @Nullable YamlConfigItem yamlConfigItem) {
        if (yamlConfigItem != null && yamlConfigItem.getTemplate() != null) {
            Template template = getTemplate(yamlConfigItem.getTemplate());

            boolean isHtml = yamlConfigItem.getHtml() != null && yamlConfigItem.getHtml();

            if (PncUtils.isTemplate(template.detail)) {
                String output = PncUtils.fillTemplate(isHtml, template.detail, facts);

                if (isHtml) {
                    PncUtils.setTextAsHtml(holder.sectionDetails, output);
                } else {
                    holder.sectionDetails.setText(output);//Perhaps refactor to use Json Form Parser Implementation
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

    private void setRowRedFontText(@NonNull YamlViewHolder holder, @NonNull Facts facts, @Nullable YamlConfigItem yamlConfigItem) {
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

    private PncProfileVisitsAdapter.Template getTemplate(String rawTemplate) {
        PncProfileVisitsAdapter.Template template = new PncProfileVisitsAdapter.Template();

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

    // total number of rows
    @Override
    public int getItemCount() {
        //return size;
        return items.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class YamlViewHolder extends RecyclerView.ViewHolder {

        public View parent;
        private TextView sectionHeader;
        private TextView subSectionHeader;
        private TextView sectionDetails;
        private TextView sectionDetailTitle;

        YamlViewHolder(View itemView) {
            super(itemView);
            sectionHeader = itemView.findViewById(R.id.overview_section_header);
            subSectionHeader = itemView.findViewById(R.id.overview_subsection_header);
            sectionDetailTitle = itemView.findViewById(R.id.overview_section_details_left);
            sectionDetails = itemView.findViewById(R.id.overview_section_details_right);

            parent = itemView;
        }
    }

    private class Template {
        public String title = "";
        public String detail = "";
    }

}