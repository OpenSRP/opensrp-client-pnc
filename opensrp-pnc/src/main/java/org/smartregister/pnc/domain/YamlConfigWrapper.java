package org.smartregister.pnc.domain;


import androidx.annotation.Nullable;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class YamlConfigWrapper {

    private String group;
    private String subGroup;
    private String relevance;
    private YamlConfigItem yamlConfigItem;

    public YamlConfigWrapper(@Nullable String group, @Nullable String subGroup, @Nullable YamlConfigItem yamlConfigItem,String relevance) {
        this.group = group;
        this.subGroup = subGroup;
        this.yamlConfigItem = yamlConfigItem;
        this.relevance = relevance;
    }

    @Nullable
    public YamlConfigItem getYamlConfigItem() {
        return yamlConfigItem;
    }

    public void setYamlConfigItem(@Nullable YamlConfigItem yamlConfigItem) {
        this.yamlConfigItem = yamlConfigItem;
    }

    @Nullable
    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(@Nullable String subGroup) {
        this.subGroup = subGroup;
    }

    @Nullable
    public String getGroup() {
        return group;
    }

    public void setGroup(@Nullable String group) {
        this.group = group;
    }

    public String getRelevance() {
        return relevance;
    }

    public void setRelevance(String relevance) {
        this.relevance = relevance;
    }
}