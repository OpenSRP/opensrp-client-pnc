package org.smartregister.pnc.holder;

import android.view.View;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.pnc.R;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PncRegisterViewHolderTest {

    @Mock
    private TextView tvAge;

    @Mock
    private TextView firstDotDivider;

    @Mock
    private View view;

    private PncRegisterViewHolder pncRegisterViewHolder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(view.findViewById(R.id.tv_pncRegisterListRow_age)).thenReturn(tvAge);
        when(view.findViewById(R.id.tv_pncRegisterListRow_firstDotDivider)).thenReturn(firstDotDivider);

        pncRegisterViewHolder = new PncRegisterViewHolder(view);
    }

    @Test
    public void showPatientAgeShouldBeVisible() {
        pncRegisterViewHolder.showPatientAge();

        verify(tvAge, times(1)).setVisibility(View.VISIBLE);
        verify(firstDotDivider, times(1)).setVisibility(View.VISIBLE);
    }

    @Test
    public void hidePatientAgeShouldBeVisible() {
        pncRegisterViewHolder.hidePatientAge();

        verify(tvAge, times(1)).setVisibility(View.GONE);
        verify(firstDotDivider, times(1)).setVisibility(View.GONE);
    }
}
