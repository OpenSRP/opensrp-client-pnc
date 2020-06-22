package org.smartregister.pnc.repository;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.NotImplementedException;
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
import org.smartregister.pnc.pojo.PncChild;
import org.smartregister.pnc.utils.PncDbConstants;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

@RunWith(MockitoJUnitRunner.Silent.class)
@PrepareForTest({PncLibrary.class, SQLiteDatabase.class, DrishtiApplication.class})
public class PncChildRepositoryTest extends BaseTest {

    private static final String TABLE_NAME = PncDbConstants.Table.PNC_BABY;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private ContentValues contentValues;

    private PncChildRepository pncChildRepository;
    private PncChild pncChild;

    @Override
    public void setup() {
        super.setup();
        pncChildRepository = PowerMockito.spy(new PncChildRepository());
        pncChild = PowerMockito.spy(new PncChild());
    }

    @Test
    public void saveOrUpdate() {

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(pncChildRepository.getWritableDatabase()).thenReturn(sqLiteDatabase);

        PowerMockito.when(pncChildRepository.getWritableDatabase().insert(TABLE_NAME, null, contentValues)).thenReturn((long) 0);
        Assert.assertTrue(pncChildRepository.saveOrUpdate(pncChild));
    }

    @Test(expected = NotImplementedException.class)
    public void findOne() {

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(pncChildRepository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        pncChildRepository.findOne(pncChild);
    }

    @Test(expected = NotImplementedException.class)
    public void delete() {
        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(pncChildRepository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        pncChildRepository.delete(pncChild);
    }

    @Test(expected = NotImplementedException.class)
    public void findAll() {
        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(pncChildRepository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        pncChildRepository.findAll();
    }
}
