package org.smartregister.pnc.repository;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.pnc.BaseTest;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.pojo.PncMetadata;

public class PncRepositoryTest extends BaseTest {

    @Mock
    private PncLibrary pncLibrary;

    @Mock
    private PncConfiguration pncConfiguration;

    @Mock
    private PncMetadata pncMetadata;

    private PncRepository pncRepository;

    @Before
    public void setUp() {

        pncRepository = new PncRepository();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdateLastInteractedWithShouldCallExpectedMethods() {
        PncRepository pncRepositorySpy = Mockito.spy(pncRepository);
        String baseEntityId = "2wds-dw3rwer";
        String tableName = "ec_client";
        SQLiteDatabase sqLiteDatabase = Mockito.mock(SQLiteDatabase.class);
        CommonRepository commonRepository = Mockito.mock(CommonRepository.class);
        Context context = Mockito.mock(Context.class);

        Mockito.doReturn(true).when(commonRepository).isFts();
        Mockito.doReturn(commonRepository).when(context).commonrepository(tableName);
        Mockito.doReturn(context).when(pncLibrary).context();
        Mockito.doReturn(sqLiteDatabase).when(pncRepositorySpy).getWritableDatabase();
        Mockito.doReturn(tableName).when(pncMetadata).getTableName();
        Mockito.doReturn(pncMetadata).when(pncConfiguration).getPncMetadata();
        Mockito.doReturn(pncConfiguration).when(pncLibrary).getPncConfiguration();

        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);

        pncRepositorySpy.updateLastInteractedWith(baseEntityId);

        Mockito.verify(sqLiteDatabase, Mockito.times(1))
                .update(Mockito.eq(tableName),
                        Mockito.any(ContentValues.class),
                        Mockito.eq("base_entity_id = ?"),
                        Mockito.eq(new String[]{baseEntityId}));

        Mockito.verify(sqLiteDatabase, Mockito.times(1))
                .update(Mockito.eq(CommonFtsObject.searchTableName(tableName)),
                        Mockito.any(ContentValues.class),
                        Mockito.eq(CommonFtsObject.idColumn + " = ?"),
                        Mockito.eq(new String[]{baseEntityId}));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }
}