package org.smartregister.pnc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.pnc.utils.PncDbConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"org.mockito.*"})
@PrepareForTest(LayoutInflater.class)
public class ClientLookUpListAdapterTest {

    private ClientLookUpListAdapter adapter;

    @Mock
    private List<CommonPersonObject> data;

    @Mock
    private Context context;

    @Mock
    private static ClientLookUpListAdapter.ClickListener clickListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        adapter = new ClientLookUpListAdapter(data, context);
        ReflectionHelpers.setField(adapter, "clickListener", clickListener);
    }

    @Test
    public void onCreateViewHolderShouldReturnMyViewHolder() throws Exception {

        PowerMockito.mockStatic(LayoutInflater.class);

        LayoutInflater inflater = PowerMockito.mock(LayoutInflater.class);
        View itemView = PowerMockito.mock(View.class);
        ViewGroup parent = PowerMockito.mock(ViewGroup.class);
        ClientLookUpListAdapter.MyViewHolder viewHolder = PowerMockito.mock(ClientLookUpListAdapter.MyViewHolder.class);

        PowerMockito.doReturn(context).when(parent).getContext();
        PowerMockito.when(LayoutInflater.from(any(Context.class))).thenReturn(inflater);
        PowerMockito.doReturn(itemView).when(inflater).inflate(anyInt(), any(ViewGroup.class), anyBoolean());
        PowerMockito.whenNew(ClientLookUpListAdapter.MyViewHolder.class).withArguments(itemView).thenReturn(viewHolder);

        int viewType = -1;

        Assert.assertThat(viewHolder, instanceOf(adapter.onCreateViewHolder(parent, viewType).getClass()));
    }

    @Test
    public void onBindViewHolderShouldVerifyImplementation() {

        TextView txtName = PowerMockito.mock(TextView.class);
        View itemView = PowerMockito.mock(View.class);
        TextView txtDetails = PowerMockito.mock(TextView.class);

        ClientLookUpListAdapter.MyViewHolder viewHolder = PowerMockito.mock(ClientLookUpListAdapter.MyViewHolder.class);
        ReflectionHelpers.setField(viewHolder, "txtName", txtName);
        ReflectionHelpers.setField(viewHolder, "itemView", itemView);
        ReflectionHelpers.setField(viewHolder, "txtDetails", txtDetails);

        String firstName = "First Name";
        String lastName = "Last Name";
        String openSrpId = "AFFFFFFF000000000000000000034";

        String caseId = "3sf-3-323ef3-fdsf3";
        String relationalId = "3223-s-df-3";
        String type = "unkonwn";
        Map<String, String> columnsMap = new HashMap<>();
        columnsMap.put(PncDbConstants.Column.Client.FIRST_NAME, firstName);
        columnsMap.put(PncDbConstants.Column.Client.LAST_NAME, lastName);
        columnsMap.put(PncDbConstants.KEY.OPENSRP_ID, openSrpId);

        CommonPersonObject commonPersonObject = PowerMockito.spy(new CommonPersonObject(caseId, relationalId, columnsMap, type));
        commonPersonObject.setColumnmaps(columnsMap);

        PowerMockito.when(data.get(anyInt())).thenReturn(commonPersonObject);
        PowerMockito.doReturn("Opensrp Id").when(context).getString(anyInt());

        int position = 0;
        adapter.onBindViewHolder(viewHolder, position);

        String fullName = firstName + " " + lastName;
        String details = "Opensrp Id - " + openSrpId;
        Mockito.verify(txtName, Mockito.times(1)).setText(fullName);
        Mockito.verify(txtDetails, Mockito.times(1)).setText(details);
    }

    @Test
    public void getItemCountShouldReturnValidSize() {

        int size = 1;
        PowerMockito.when(data.size()).thenReturn(size);
        Assert.assertEquals(size, adapter.getItemCount());
    }

    @Test
    public void onClickShouldVerifyListener() {

        View view = PowerMockito.mock(View.class);
        ClientLookUpListAdapter.MyViewHolder viewHolder = new ClientLookUpListAdapter.MyViewHolder(view);
        viewHolder.onClick(view);

        Mockito.verify(clickListener, Mockito.times(1)).onItemClick(view);
    }

    @Test
    public void setOnClickListenerShouldVerifyValidClickListener() {
        adapter.setOnClickListener(clickListener);
    }
}
