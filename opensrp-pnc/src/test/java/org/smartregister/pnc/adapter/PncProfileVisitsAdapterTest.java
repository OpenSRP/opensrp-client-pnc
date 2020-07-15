package org.smartregister.pnc.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.domain.YamlConfigItem;
import org.smartregister.pnc.domain.YamlConfigWrapper;
import org.smartregister.util.StringUtil;

import java.util.ArrayList;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"org.mockito.*"})
@PrepareForTest({LayoutInflater.class, PncLibrary.class, TextUtils.class})
public class PncProfileVisitsAdapterTest {

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private LayoutInflater mInflater;

    @Mock
    private ArrayList<Pair<YamlConfigWrapper, Facts>> items;

    @Mock
    private PncLibrary pncLibrary;

    private PncProfileVisitsAdapter adapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockStatic(LayoutInflater.class);
        mockStatic(PncLibrary.class);
        when(LayoutInflater.from(any(Context.class))).thenReturn(mInflater);
        adapter = new PncProfileVisitsAdapter(context, items);
    }

    @Test
    public void onCreateViewHolderShouldReturnYamlViewHolder() throws Exception {

        ViewGroup parent = mock(ViewGroup.class);
        View view = mock(View.class);
        PncProfileVisitsAdapter.YamlViewHolder vh = mock(PncProfileVisitsAdapter.YamlViewHolder.class);
        when(mInflater.inflate(anyInt(), any(ViewGroup.class), anyBoolean())).thenReturn(view);
        whenNew(PncProfileVisitsAdapter.YamlViewHolder.class).withArguments(view).thenReturn(vh);

        assertThat(vh, instanceOf(adapter.onCreateViewHolder(parent, -1).getClass()));
    }

    @Test
    public void onBindViewHolderShouldVerifyScenarioOne() {

        String group = "group";
        String subGroup = "sub group: {sub_heading}";
        String template = "test template: {one}";

        TextView sectionHeader = mock(TextView.class);
        TextView subSectionHeader = mock(TextView.class);
        TextView sectionDetailTitle = mock(TextView.class);
        TextView sectionDetails = mock(TextView.class);
        PncProfileVisitsAdapter.YamlViewHolder vh = mock(PncProfileVisitsAdapter.YamlViewHolder.class);

        ReflectionHelpers.setField(vh, "sectionHeader", sectionHeader);
        ReflectionHelpers.setField(vh, "subSectionHeader", subSectionHeader);
        ReflectionHelpers.setField(vh, "sectionDetailTitle", sectionDetailTitle);
        ReflectionHelpers.setField(vh, "sectionDetails", sectionDetails);

        mockStatic(TextUtils.class);
        Facts facts = mock(Facts.class);
        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);
        Pair<YamlConfigWrapper, Facts> pair = mock(new Pair<>(yamlConfigWrapper, facts).getClass());
        YamlConfigItem yamlConfigItem = mock(YamlConfigItem.class);

        ReflectionHelpers.setField(pair, "first", yamlConfigWrapper);
        ReflectionHelpers.setField(pair, "second", facts);

        when(TextUtils.isEmpty(anyString())).thenReturn(false);
        when(context.getResources()).thenReturn(resources);
        when(resources.getColor(anyInt())).thenReturn(Color.RED);
        when(items.get(anyInt())).thenReturn(pair);

        when(yamlConfigWrapper.getGroup()).thenReturn(group);
        when(yamlConfigWrapper.getSubGroup()).thenReturn(subGroup);
        when(yamlConfigWrapper.getYamlConfigItem()).thenReturn(yamlConfigItem);
        when(yamlConfigItem.getTemplate()).thenReturn(template);
        when(yamlConfigItem.getHtml()).thenReturn(Boolean.TRUE);
        when(facts.get("sub_heading")).thenReturn("Sub heading");
        when(facts.get("one")).thenReturn("Works");

        adapter.onBindViewHolder(vh, 0);

        verify(sectionHeader, times(1)).setText(StringUtil.humanize(group));
        verify(sectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(subSectionHeader, times(1)).setText(StringUtil.humanize(subGroup.replace("{sub_heading}", " Sub heading")));
        verify(subSectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(sectionDetailTitle, times(1)).setText(template.replace(": {one}", ""));
    }

    @Test
    public void onBindViewHolderShouldVerifyScenarioTwo() {

        String group = "group";
        String subGroup = "sub group: {sub_heading}";

        TextView sectionHeader = mock(TextView.class);
        TextView subSectionHeader = mock(TextView.class);
        TextView sectionDetailTitle = mock(TextView.class);
        TextView sectionDetails = mock(TextView.class);
        PncProfileVisitsAdapter.YamlViewHolder vh = mock(PncProfileVisitsAdapter.YamlViewHolder.class);

        ReflectionHelpers.setField(vh, "sectionHeader", sectionHeader);
        ReflectionHelpers.setField(vh, "subSectionHeader", subSectionHeader);
        ReflectionHelpers.setField(vh, "sectionDetailTitle", sectionDetailTitle);
        ReflectionHelpers.setField(vh, "sectionDetails", sectionDetails);

        mockStatic(TextUtils.class);
        Facts facts = mock(Facts.class);
        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);
        Pair<YamlConfigWrapper, Facts> pair = mock(new Pair<>(yamlConfigWrapper, facts).getClass());
        ReflectionHelpers.setField(pair, "first", yamlConfigWrapper);
        ReflectionHelpers.setField(pair, "second", facts);

        when(TextUtils.isEmpty(anyString())).thenReturn(true);
        when(items.get(anyInt())).thenReturn(pair);

        when(yamlConfigWrapper.getGroup()).thenReturn(group);
        when(yamlConfigWrapper.getSubGroup()).thenReturn(subGroup);

        adapter.onBindViewHolder(vh, 0);

        verify(sectionHeader, times(1)).setVisibility(View.GONE);
        verify(subSectionHeader, times(1)).setVisibility(View.GONE);
        verify(sectionDetailTitle, times(1)).setVisibility(View.GONE);
        verify(sectionDetails, times(1)).setVisibility(View.GONE);
    }

    @Test
    public void getItemCountShouldReturnOne() {
        int size = 1;
        PowerMockito.when(items.size()).thenReturn(size);
        Assert.assertEquals(size, adapter.getItemCount());
    }

    @Test
    public void getTemplateShouldReturnRawData() {
        String rawTemplate = "nothing";
        PncProfileVisitsAdapter.Template template = adapter.getTemplate(rawTemplate);
        Assert.assertEquals(rawTemplate, template.title);
    }
}
