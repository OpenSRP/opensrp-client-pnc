package org.smartregister.pnc.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
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
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.domain.YamlConfigItem;
import org.smartregister.pnc.domain.YamlConfigWrapper;
import org.smartregister.pnc.helper.LibraryHelper;
import org.smartregister.pnc.helper.PncRulesEngineHelper;
import org.smartregister.pnc.helper.TextUtilHelper;

import java.util.List;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.robolectric.util.ReflectionHelpers.setField;

@RunWith(MockitoJUnitRunner.class)
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

    @Mock
    private TextUtilHelper textUtilHelper;

    @Mock
    private LibraryHelper libraryHelper;

    private PncProfileOverviewAdapter adapter;

    @Before
    @PrepareForTest(LayoutInflater.class)
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        adapter = new PncProfileOverviewAdapter(context, mData, facts);
        setField(adapter, "mInflater", mInflater);
        setField(adapter, "textUtilHelper", textUtilHelper);
        setField(adapter, "libraryHelper", libraryHelper);

    }

    @Test
    public void onCreateViewHolderShouldReturnViewHolder() throws Exception {

        PncProfileOverviewAdapter.ViewHolder viewHolder = mock(PncProfileOverviewAdapter.ViewHolder.class);
        View view = mock(View.class);
        doReturn(view).when(mInflater).inflate(anyInt(), any(ViewGroup.class), anyBoolean());

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

        setField(vh, "sectionHeader", sectionHeader);
        setField(vh, "subSectionHeader", subSectionHeader);
        setField(vh, "sectionDetailTitle", sectionDetailTitle);
        setField(vh, "sectionDetails", sectionDetails);

        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);
        YamlConfigItem yamlConfigItem = mock(YamlConfigItem.class);
        PncRulesEngineHelper pncRulesEngineHelper = mock(PncRulesEngineHelper.class);

        when(textUtilHelper.isEmpty(anyString())).thenReturn(false);
        when(context.getResources()).thenReturn(resources);
        when(resources.getColor(anyInt())).thenReturn(Color.RED);
        when(mData.get(anyInt())).thenReturn(yamlConfigWrapper);
        when(yamlConfigWrapper.getGroup()).thenReturn(group);
        when(yamlConfigWrapper.getSubGroup()).thenReturn(subGroup);
        when(yamlConfigWrapper.getYamlConfigItem()).thenReturn(yamlConfigItem);
        when(yamlConfigItem.getTemplate()).thenReturn(template);
        when(yamlConfigItem.getIsRedFont()).thenReturn("yes");
        when(libraryHelper.getPncLibraryInstance()).thenReturn(pncLibrary);
        when(pncLibrary.getPncRulesEngineHelper()).thenReturn(pncRulesEngineHelper);
        when(pncRulesEngineHelper.getRelevance(any(Facts.class), anyString())).thenReturn(true);
        when(facts.get("one")).thenReturn("two");

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

        setField(vh, "sectionHeader", sectionHeader);
        setField(vh, "subSectionHeader", subSectionHeader);
        setField(vh, "sectionDetailTitle", sectionDetailTitle);
        setField(vh, "sectionDetails", sectionDetails);

        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);
        YamlConfigItem yamlConfigItem = mock(YamlConfigItem.class);

        when(textUtilHelper.isEmpty(anyString())).thenReturn(true);
        when(context.getResources()).thenReturn(resources);
        when(resources.getColor(anyInt())).thenReturn(Color.BLACK);
        when(mData.get(anyInt())).thenReturn(yamlConfigWrapper);
        when(yamlConfigWrapper.getGroup()).thenReturn(group);
        when(yamlConfigWrapper.getSubGroup()).thenReturn(subGroup);
        when(yamlConfigWrapper.getYamlConfigItem()).thenReturn(yamlConfigItem);

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

        setField(vh, "sectionHeader", sectionHeader);
        setField(vh, "subSectionHeader", subSectionHeader);
        setField(vh, "sectionDetailTitle", sectionDetailTitle);
        setField(vh, "sectionDetails", sectionDetails);

        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);

        when(mData.get(anyInt())).thenReturn(yamlConfigWrapper);
        when(yamlConfigWrapper.getGroup()).thenReturn("");
        when(yamlConfigWrapper.getSubGroup()).thenReturn("");

        adapter.onBindViewHolder(vh, 0);

        verify(sectionDetailTitle, times(1)).setVisibility(View.GONE);
        verify(sectionDetails, times(1)).setVisibility(View.GONE);
    }

    @Test
    public void getItemCountShouldReturnOne() {
        int size = 1;
        when(mData.size()).thenReturn(size);
        assertEquals(size, adapter.getItemCount());
    }

    @Test
    public void getTemplateShouldReturnRawData() {
        String rawTemplate = "nothing";
        PncProfileOverviewAdapter.Template template = adapter.getTemplate(rawTemplate);
        assertEquals(rawTemplate, template.title);
    }
}