package org.smartregister.pnc.presenter;

import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.pnc.BaseTest;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.contract.PncRegisterActivityContract;

public class PncRegisterActivityPresenterTest extends BaseTest {

    private BasePncRegisterActivityPresenter presenter;

    @Mock
    private PncRegisterActivityContract.View view;

    @Mock
    private PncRegisterActivityContract.Model model;

    @Mock
    private PncLibrary maternityLibrary;


    @Override
    public void setUp() {
        super.setUp();
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", maternityLibrary);
        presenter = new TestPncRegisterActivityPresenter(view, model);
    }

    @Test
    public void updateInitialsShouldCallViewUpdateInitialsText() {
        String initials = "JR";
        Mockito.doReturn(initials).when(model).getInitials();

        presenter.updateInitials();

        Mockito.verify(view).updateInitialsText(Mockito.eq(initials));
    }

    @Test
    public void saveLanguageShouldCallModelSaveLanguage() {
        String language = "en";

        presenter.saveLanguage(language);

        Mockito.verify(model).saveLanguage(Mockito.eq(language));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }
}
