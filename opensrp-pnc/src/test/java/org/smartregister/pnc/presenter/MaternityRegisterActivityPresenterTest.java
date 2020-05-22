package org.smartregister.pnc.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.pnc.BaseTest;
import org.smartregister.pnc.contract.PncRegisterActivityContract;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityRegisterActivityPresenterTest extends BaseTest {

    private BasePncRegisterActivityPresenter presenter;

    @Mock
    private PncRegisterActivityContract.View view;

    @Mock
    private PncRegisterActivityContract.Model model;


    @Before
    public void setUp() throws Exception {
        presenter = new TestMaternityRegisterActivityPresenter(view, model);
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
}