package org.smartregister.pnc.repository;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.smartregister.pnc.BaseTest;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.view.activity.DrishtiApplication;

import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.Silent.class)
@PrepareForTest({PncLibrary.class, SQLiteDatabase.class, DrishtiApplication.class})
public class PncRegistrationDetailsRepositoryTest extends BaseTest {

    private static final String TABLE_NAME = PncDbConstants.Table.PNC_REGISTRATION_DETAILS;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    private PncRegistrationDetailsRepository pncRegistrationDetailsRepository;

    @Override
    public void setUp() {
        super.setUp();
        pncRegistrationDetailsRepository = PowerMockito.spy(new PncRegistrationDetailsRepository());
    }

    @Test
    public void createTable() {

        PncRegistrationDetailsRepository.createTable(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(3)).execSQL(anyString());
    }

    @Test
    public void getTableName() {
        Assert.assertEquals(pncRegistrationDetailsRepository.getTableName(), TABLE_NAME);
    }

    @Test
    public void getPropertyNames(){
        String[] propertiesName = pncRegistrationDetailsRepository.getPropertyNames();
        Assert.assertNotNull(propertiesName);
        Assert.assertTrue(propertiesName.length > 0);
    }
}
