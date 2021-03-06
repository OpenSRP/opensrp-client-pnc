package org.smartregister.pnc.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */
public class YamlConfig {

    private String group;
    private String subGroup;
    private String relevance;
    private List<YamlConfigItem> fields;
    private String testResults;

    public YamlConfig() {
    }

    public YamlConfig(@Nullable String group, @Nullable String subGroup, @Nullable List<YamlConfigItem> fields,
                      @Nullable String testResults, @NonNull String relevance) {
        this.group = group;
        this.subGroup = subGroup;
        this.fields = fields;
        this.testResults = testResults;
        this.relevance = relevance;
    }

    @Nullable
    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(@Nullable String sub_group) {
        this.subGroup = sub_group;
    }

    @Nullable
    public String getGroup() {
        return group;
    }

    public void setGroup(@Nullable String group) {
        this.group = group;
    }

    @Nullable
    public List<YamlConfigItem> getFields() {
        return fields;
    }

    public void setFields(@Nullable List<YamlConfigItem> fields) {
        this.fields = fields;
    }

    @Nullable
    public String getTestResults() {
        return testResults;
    }

    public void setTestResults(@Nullable String test_results) {
        this.testResults = test_results;
    }

    public String getRelevance() {
        return relevance;
    }

    public void setRelevance(String relevance) {
        this.relevance = relevance;
    }
}