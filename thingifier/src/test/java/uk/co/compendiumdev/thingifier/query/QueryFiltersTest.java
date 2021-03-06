package uk.co.compendiumdev.thingifier.query;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.compendiumdev.thingifier.Thing;
import uk.co.compendiumdev.thingifier.Thingifier;
import uk.co.compendiumdev.thingifier.apiconfig.ParamConfig;
import uk.co.compendiumdev.thingifier.domain.FieldType;
import uk.co.compendiumdev.thingifier.domain.definitions.Field;
import uk.co.compendiumdev.thingifier.domain.instances.ThingInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryFiltersTest {

    // todo: advanced filtering i.e. < > partial text match, etc.
    // e.g https://www.moesif.com/blog/technical/api-design/REST-API-Design-Filtering-Sorting-and-Pagination/
    // https://softwareengineering.stackexchange.com/questions/233164/how-do-searches-fit-into-a-restful-interface

    @Test
    public void canFilterAQueryToIncludeInstancesWithMatchingFields(){

        Thingifier aThingifier = new Thingifier();
        Thing thing = aThingifier.createThing("thing", "things");
        thing.definition().addField(Field.is("truefalse", FieldType.BOOLEAN));

        thing.addInstance(thing.createInstance().setValue("truefalse", "true"));
        thing.addInstance(thing.createInstance().setValue("truefalse", "true"));
        thing.addInstance(thing.createInstance().setValue("truefalse", "true"));
        thing.addInstance(thing.createInstance().setValue("truefalse", "false"));

        // TODO: risk that Spark does not pass in args in a way that flow through to simple query
        //       so test this at an HTTP level as well
        Map<String, String> params = new HashMap<>();
        params.put("truefalse", "true");

        SimpleQuery queryResults = new SimpleQuery(aThingifier, "things").
                                        performQuery(params,
                                                aThingifier.apiConfig().forParams());

        Assertions.assertTrue(queryResults.isResultACollection(), "result should be a collection");
        final List<ThingInstance> instances = queryResults.getListThingInstance();
        Assertions.assertEquals(3, instances.size(), "expected 3 true values");

    }
}
