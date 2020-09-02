package org.smartregister.pnc.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.domain.YamlConfigItem;
import org.smartregister.pnc.domain.YamlConfigWrapper;
import org.smartregister.pnc.helper.PncRulesEngineHelper;
import org.smartregister.util.StringUtil;

import java.util.ArrayList;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.robolectric.util.ReflectionHelpers.setField;

@RunWith(MockitoJUnitRunner.class)
public class PncProfileOverviewAdapterTest {

    @Mock
    private ArrayList<Pair<YamlConfigWrapper, Facts>> mData;

    @Mock
    private LayoutInflater mInflater;

    @Mock
    private Context context;

    @Mock
    private Resources resources;

    @Mock
    private Facts facts;

    @Mock
    private PncLibrary pncLibrary;

    private PncProfileOverviewAdapter adapter;

    @Mock
    private PncRulesEngineHelper pncRulesEngineHelper;

    @Before
    @PrepareForTest(LayoutInflater.class)
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        adapter = new PncProfileOverviewAdapter(context, mData);
        setField(adapter, "mInflater", mInflater);
    }

    @Test
    public void onCreateViewHolderShouldReturnViewHolder() {

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

        Mockito.doReturn(true).when(pncRulesEngineHelper)
                .getRelevance(Mockito.any(Facts.class), Mockito.anyString());
        Mockito.doReturn(pncRulesEngineHelper).when(pncLibrary).getPncRulesEngineHelper();
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);

        setField(vh, "sectionHeader", sectionHeader);
        setField(vh, "subSectionHeader", subSectionHeader);
        setField(vh, "sectionDetailTitle", sectionDetailTitle);
        setField(vh, "sectionDetails", sectionDetails);

        YamlConfigWrapper yamlConfigWrapper = mock(YamlConfigWrapper.class);
        YamlConfigItem yamlConfigItem = mock(YamlConfigItem.class);

        Pair<YamlConfigWrapper, Facts> yamlConfigWrapperFactsPair = Pair.create(yamlConfigWrapper, facts);
        when(context.getResources()).thenReturn(resources);
        when(resources.getColor(anyInt())).thenReturn(Color.RED);
        when(mData.get(anyInt())).thenReturn(yamlConfigWrapperFactsPair);
        when(yamlConfigWrapper.getGroup()).thenReturn(group);
        when(yamlConfigWrapper.getSubGroup()).thenReturn(subGroup);
        when(yamlConfigWrapper.getYamlConfigItem()).thenReturn(yamlConfigItem);
        when(yamlConfigItem.getTemplate()).thenReturn(template);
        when(yamlConfigItem.getIsRedFont()).thenReturn("yes");
        when(facts.get("one")).thenReturn("two");

        adapter.onBindViewHolder(vh, 0);

        verify(sectionHeader, times(1)).setText(StringUtil.humanize(group));
        verify(sectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(subSectionHeader, times(1)).setText(StringUtil.humanize(subGroup));
        verify(subSectionHeader, times(1)).setVisibility(View.VISIBLE);
        verify(sectionDetailTitle, times(1)).setText(template.replace(": {one}", ""));
        verify(sectionDetails, times(1)).setText(": Two");
        verify(sectionDetailTitle, times(1)).setTextColor(Color.RED);
        verify(sectionDetails, times(1)).setTextColor(Color.RED);
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
        Pair<YamlConfigWrapper, Facts> yamlConfigWrapperFactsPair = Pair.create(yamlConfigWrapper, facts);
        when(mData.get(anyInt())).thenReturn(yamlConfigWrapperFactsPair);
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


    @After
    public void tearDown(){
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }
}
