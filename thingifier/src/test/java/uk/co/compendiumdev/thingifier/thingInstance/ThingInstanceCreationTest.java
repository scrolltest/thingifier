package uk.co.compendiumdev.thingifier.thingInstance;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.compendiumdev.thingifier.domain.FieldType;
import uk.co.compendiumdev.thingifier.domain.definitions.Field;
import uk.co.compendiumdev.thingifier.domain.definitions.ThingDefinition;
import uk.co.compendiumdev.thingifier.domain.instances.ThingInstance;

public class ThingInstanceCreationTest {

    ThingDefinition entityTestSession;

    @BeforeEach
    public void createEntity(){

        entityTestSession = ThingDefinition.create("Test Session", "Test Sessions");

        entityTestSession.addField(Field.is("Title"));
        entityTestSession.addFields(Field.is("CompletedStatus").withDefaultValue("Not Completed"));
        entityTestSession.addFields(Field.is("review", FieldType.BOOLEAN).withDefaultValue("TRUE"));
        entityTestSession.addFields(Field.is("falsey", FieldType.BOOLEAN));
    }

    @Test
    public void canCreateNewEntityInstance() {

        ThingInstance session;
        session = new ThingInstance(entityTestSession);

        Assertions.assertEquals(4+1, session.getFieldNames().size()); // +1 for guid

        session.setValue("Title", "My Test Session");
        Assertions.assertEquals("My Test Session", session.getValue("Title"));

        Assertions.assertEquals("Test Session", session.getEntity().getName());
    }


    @Test
    public void canCreateUniqueGUIDs(){
        ThingInstance session = new ThingInstance(entityTestSession);

        Assertions.assertNotNull(session.getGUID());
        ThingInstance session2 = new ThingInstance(entityTestSession);
        Assertions.assertNotNull(session2.getGUID());
        Assertions.assertTrue(session2.getGUID().length()>10);

        Assertions.assertNotEquals(session.getGUID(), session2.getGUID());
    }


    @Test
    public void canCreateAThingWithAGUID(){

        // note potential bug this is risky if the GUID is later created
        ThingInstance session = new ThingInstance(entityTestSession, "1234-1234-1324-1234");
        Assertions.assertEquals("1234-1234-1324-1234", session.getGUID());
    }
}
