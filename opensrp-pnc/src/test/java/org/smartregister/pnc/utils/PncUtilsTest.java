package org.smartregister.pnc.utils;

import android.content.ContentValues;
import android.content.Intent;

import net.sqlcipher.database.SQLiteDatabase;

import org.jeasy.rules.api.Facts;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.pnc.BaseTest;
import org.smartregister.pnc.PncLibrary;
import org.smartregister.pnc.config.PncConfiguration;
import org.smartregister.pnc.pojo.PncMetadata;
import org.smartregister.repository.Repository;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PncUtilsTest extends BaseTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private PncLibrary pncLibrary;

    @Mock
    private PncConfiguration pncConfiguration;

    @Mock
    private PncMetadata pncMetadata;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void fillTemplateShouldReplaceTheBracketedVariableWithCorrectValue() {
        String template = "Gender: {gender}";
        Facts facts = new Facts();
        facts.put("gender", "Male");

        assertEquals("Gender:  Male", PncUtils.fillTemplate(template, facts));
    }

    @Test
    public void convertStringToDate() {
        Date date = PncUtils.convertStringToDate(PncConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, "2019-10-28 18:09:49");
        assertEquals("2019-10-28 18:09:49", PncUtils.convertDate(date, PncConstants.DateFormat.YYYY_MM_DD_HH_MM_SS));
    }

    @Test
    public void getIntentValue() {
        Intent intent = Mockito.mock(Intent.class);
        Mockito.when(intent.hasExtra("test")).thenReturn(false);
        assertNull(PncUtils.getIntentValue(intent, "test"));

        Mockito.when(intent.hasExtra("test")).thenReturn(true);
        Mockito.when(intent.getStringExtra("test")).thenReturn("test");
        assertEquals("test", PncUtils.getIntentValue(intent, "test"));
    }

    @Test
    public void getIntentValueReturnNull() {
        assertNull(PncUtils.getIntentValue(null, "test"));
    }

    @Test
    public void metadata() {
        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);
        Mockito.doReturn(pncConfiguration).when(pncLibrary).getPncConfiguration();
        Mockito.doReturn(pncMetadata).when(pncConfiguration).getPncMetadata();

        assertEquals(pncMetadata, PncUtils.metadata());

        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", null);
    }

    @Test
    public void testGetClientAge() {
        assertEquals("13", PncUtils.getClientAge("13y 4m", "y"));
        assertEquals("4m", PncUtils.getClientAge("4m", "y"));
        assertEquals("5", PncUtils.getClientAge("5y 4w", "y"));
        assertEquals("3y", PncUtils.getClientAge("3y", "y"));
        assertEquals("5w 6d", PncUtils.getClientAge("5w 6d", "y"));
        assertEquals("6d", PncUtils.getClientAge("6d", "y"));
    }

    @Test
    public void isTemplateShouldReturnFalseIfStringDoesNotContainMatchingBraces() {
        assertFalse(PncUtils.isTemplate("{ This is a sytling brace"));
        assertFalse(PncUtils.isTemplate("This is display text"));
    }

    @Test
    public void isTemplateShouldReturnTrueIfStringContainsMatchingBraces() {
        assertTrue(PncUtils.isTemplate("Project Name: {project_name}"));
    }

    @Test
    public void testGenerateNIdsShouldGenerateNIds() {
        assertEquals(2, PncUtils.generateNIds(2).length);
        assertEquals(0, PncUtils.generateNIds(0).length);
    }

    @Test
    public void testUpdateLastInteractedWithShouldCallExpectedMethods() {
        String baseEntityId = "2wds-dw3rwer";
        String tableName = "ec_client";
        Repository repository = Mockito.mock(Repository.class);
        SQLiteDatabase sqLiteDatabase = Mockito.mock(SQLiteDatabase.class);
        CommonRepository commonRepository = Mockito.mock(CommonRepository.class);
        Context context = Mockito.mock(Context.class);

        Mockito.doReturn(true).when(commonRepository).isFts();
        Mockito.doReturn(commonRepository).when(context).commonrepository(tableName);
        Mockito.doReturn(context).when(pncLibrary).context();
        Mockito.doReturn(sqLiteDatabase).when(repository).getWritableDatabase();
        Mockito.doReturn(repository).when(pncLibrary).getRepository();
        Mockito.doReturn(tableName).when(pncMetadata).getTableName();
        Mockito.doReturn(pncMetadata).when(pncConfiguration).getPncMetadata();
        Mockito.doReturn(pncConfiguration).when(pncLibrary).getPncConfiguration();

        ReflectionHelpers.setStaticField(PncLibrary.class, "instance", pncLibrary);

        PncUtils.updateLastInteractedWith(baseEntityId);

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
}
