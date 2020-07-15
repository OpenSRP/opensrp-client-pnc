package org.smartregister.pnc.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
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
import org.smartregister.pnc.helper.PncRulesEngineHelper;

import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"org.mockito.*"})
@PrepareForTest({LayoutInflater.class, PncLibrary.class, TextUtils.class})
public class PncProfileOverviewAdapterTest {

    @Mock
    private List<YamlConfigWrapper> mData;

    @Mock
    private LayoutInflater mInflater;

    @Mock
    private Facts facts;

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private PncLibrary pncLibrary;

    private PncProfileOverviewAdapter adapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(LayoutInflater.class);
        PowerMockito.mockStatic(PncLibrary.class);

        PowerMockito.when(LayoutInflater.from(any(Context.class))).thenReturn(mInflater);
        PowerMockito.when(PncLibrary.getInstance()).thenReturn(pncLibrary);

        adapter = new PncProfileOverviewAdapter(context, mData, facts);

    }

    @Test
    public void onCreateViewHolderShouldReturnViewHolder() throws Exception {

        PncProfileOverviewAdapter.ViewHolder viewHolder = mock(PncProfileOverviewAdapter.ViewHolder.class);
        View view = mock(View.class);
        PowerMockito.doReturn(view).when(mInflater).inflate(anyInt(), any(ViewGroup.class), anyBoolean());
        PowerMockito.whenNew(PncProfileOverviewAdapter.ViewHolder.class).withArguments(view).thenReturn(viewHolder);

        PncProfileOverviewAdapter.ViewHolder vh = adapter.onCreateViewHolder(mock(ViewGroup.class), -1);
        Assert.assertThat(viewHolder, instanceOf(vh.getClass()));
    }

    @Test
    public void onBindViewHolderShouldVerifyScenarioOne() {

        String group = "group";
        String subGroup = "sub group";
        String template = "test template: {one}";

        TextView sectionHeader = mock(TextView.class);
        TextView subSectionHeader = mock(TextView.class);
        TextView sectionDetailTitle = mock(TextView.class);
        TextView sectionDetails = mock(TextView.class);
        PncProfileOverviewAdapter.ViewHolder vh = mock(PncProfileOverviewAdapter.ViewHolder.class);

        ReflectionHelpers.setField(vh, "sectionHeader", sectionHeader);
        ReflectionHelpers.setField(vh, "subSectionHeader", subSectionHeader);
        ReflectionHelpers.setField(vh, "sectionDetailTitle", sectionDetailTitle);
        ReflectionHelpers.setField(vh, "sectionDetails", sectionDetails);

        mockStatic(TextUtils.class);
        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);
        YamlConfigItem yamlConfigItem = mock(YamlConfigItem.class);
        PncRulesEngineHelper pncRulesEngineHelper = mock(PncRulesEngineHelper.class);

        PowerMockito.when(TextUtils.isEmpty(anyString())).thenReturn(false);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(resources.getColor(anyInt())).thenReturn(Color.RED);
        PowerMockito.when(mData.get(anyInt())).thenReturn(yamlConfigWrapper);
        PowerMockito.when(yamlConfigWrapper.getGroup()).thenReturn(group);
        PowerMockito.when(yamlConfigWrapper.getSubGroup()).thenReturn(subGroup);
        PowerMockito.when(yamlConfigWrapper.getYamlConfigItem()).thenReturn(yamlConfigItem);
        PowerMockito.when(yamlConfigItem.getTemplate()).thenReturn(template);
        PowerMockito.when(yamlConfigItem.getIsRedFont()).thenReturn("yes");
        PowerMockito.when(pncLibrary.getPncRulesEngineHelper()).thenReturn(pncRulesEngineHelper);
        PowerMockito.when(pncRulesEngineHelper.getRelevance(any(Facts.class), anyString())).thenReturn(true);
        PowerMockito.when(facts.get("one")).thenReturn("two");

        adapter.onBindViewHolder(vh, 0);

        verify(sectionHeader, times(1)).setText(group.toUpperCase());
        verify(sectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(subSectionHeader, times(1)).setText(subGroup.toUpperCase());
        verify(subSectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(sectionDetailTitle, times(1)).setText(template.replace(": {one}", ""));
        verify(sectionDetails, times(1)).setText(": Two");
        verify(sectionDetailTitle, times(1)).setTextColor(Color.RED);
        verify(sectionDetails, times(1)).setTextColor(Color.RED);
        verify(sectionDetailTitle, times(1)).setVisibility(View.VISIBLE);
        verify(sectionDetails, times(1)).setVisibility(View.VISIBLE);
    }

    @Test
    public void onBindViewHolderShouldVerifyScenarioTwo() {

        String group = "";
        String subGroup = "";

        TextView sectionHeader = mock(TextView.class);
        TextView subSectionHeader = mock(TextView.class);
        TextView sectionDetailTitle = mock(TextView.class);
        TextView sectionDetails = mock(TextView.class);
        PncProfileOverviewAdapter.ViewHolder vh = mock(PncProfileOverviewAdapter.ViewHolder.class);

        ReflectionHelpers.setField(vh, "sectionHeader", sectionHeader);
        ReflectionHelpers.setField(vh, "subSectionHeader", subSectionHeader);
        ReflectionHelpers.setField(vh, "sectionDetailTitle", sectionDetailTitle);
        ReflectionHelpers.setField(vh, "sectionDetails", sectionDetails);

        mockStatic(TextUtils.class);
        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);
        YamlConfigItem yamlConfigItem = mock(YamlConfigItem.class);

        PowerMockito.when(TextUtils.isEmpty(anyString())).thenReturn(true);
        PowerMockito.when(context.getResources()).thenReturn(resources);
        PowerMockito.when(resources.getColor(anyInt())).thenReturn(Color.BLACK);
        PowerMockito.when(mData.get(anyInt())).thenReturn(yamlConfigWrapper);
        PowerMockito.when(yamlConfigWrapper.getGroup()).thenReturn(group);
        PowerMockito.when(yamlConfigWrapper.getSubGroup()).thenReturn(subGroup);
        PowerMockito.when(yamlConfigWrapper.getYamlConfigItem()).thenReturn(yamlConfigItem);

        adapter.onBindViewHolder(vh, 0);

        verify(sectionHeader, times(1)).setVisibility(View.GONE);
        verify(subSectionHeader, times(1)).setVisibility(View.GONE);
        verify(sectionDetailTitle, times(1)).setTextColor(Color.BLACK);
        verify(sectionDetails, times(1)).setTextColor(Color.BLACK);
        verify(sectionDetailTitle, times(1)).setVisibility(View.VISIBLE);
        verify(sectionDetails, times(1)).setVisibility(View.VISIBLE);
    }

    @Test
    public void onBindViewHolderShouldVerifyScenarioThree() {

        TextView sectionHeader = mock(TextView.class);
        TextView subSectionHeader = mock(TextView.class);
        TextView sectionDetailTitle = mock(TextView.class);
        TextView sectionDetails = mock(TextView.class);
        PncProfileOverviewAdapter.ViewHolder vh = mock(PncProfileOverviewAdapter.ViewHolder.class);

        ReflectionHelpers.setField(vh, "sectionHeader", sectionHeader);
        ReflectionHelpers.setField(vh, "subSectionHeader", subSectionHeader);
        ReflectionHelpers.setField(vh, "sectionDetailTitle", sectionDetailTitle);
        ReflectionHelpers.setField(vh, "sectionDetails", sectionDetails);

        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);

        PowerMockito.when(mData.get(anyInt())).thenReturn(yamlConfigWrapper);
        PowerMockito.when(yamlConfigWrapper.getGroup()).thenReturn("");
        PowerMockito.when(yamlConfigWrapper.getSubGroup()).thenReturn("");

        adapter.onBindViewHolder(vh, 0);

        verify(sectionDetailTitle, times(1)).setVisibility(View.GONE);
        verify(sectionDetails, times(1)).setVisibility(View.GONE);
    }

    @Test
    public void getItemCountShouldReturnOne() {
        int size = 1;
        PowerMockito.when(mData.size()).thenReturn(size);
        Assert.assertEquals(size, adapter.getItemCount());
    }

    @Test
    public void getTemplateShouldReturnRawData() {
        String rawTemplate = "nothing";
        PncProfileOverviewAdapter.Template template = adapter.getTemplate(rawTemplate);
        Assert.assertEquals(rawTemplate, template.title);
    }
}
