package org.smartregister.pnc.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"org.mockito.*"})
public class ViewPagerAdapterTest {

    @Mock
    private Fragment fragment;

    private ViewPagerAdapter viewPagerAdapter;
    private String title = "Title";

    @Before
    public void setUp() {
        viewPagerAdapter = new ViewPagerAdapter(mock(FragmentManager.class));
        viewPagerAdapter.addFragment(fragment, title);
    }

    @Test
    public void getItemShouldReturnFragment() {
        assertThat(fragment, instanceOf(viewPagerAdapter.getItem(0).getClass()));
    }

    @Test
    public void getCountShouldReturnOne() {
        assertEquals(1, viewPagerAdapter.getCount());
    }

    @Test
    public void getPageTitleShouldReturnValidTitle() {
        assertEquals(title, viewPagerAdapter.getPageTitle(0));
    }
}
