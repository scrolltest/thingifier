package uk.co.compendiumdev.thingifier;

import uk.co.compendiumdev.thingifier.generic.definitions.FieldValue;
import uk.co.compendiumdev.thingifier.generic.definitions.ThingDefinition;
import uk.co.compendiumdev.thingifier.generic.instances.ThingInstance;

import java.util.*;


final public class Thing {

    private final ThingDefinition definition;
    private Map<String, ThingInstance> instances = new HashMap<String, ThingInstance>();

    public Thing(ThingDefinition thingDefinition) {
        this.definition = thingDefinition;
    }


    // TODO should probably create a Thing with a populated definition rather than this way round
    public static Thing create(String name, String plural) {
        Thing thing = new Thing(ThingDefinition.create(name, plural));
        return thing;
    }

    public ThingInstance createInstance() {
        return new ThingInstance(definition);
    }

    public ThingInstance createInstance(String guid) {
        return new ThingInstance(definition, guid);
    }

    public Thing addInstance(ThingInstance instance) {

        instances.put(instance.getGUID(), instance);
        return this;
    }


    public int countInstances() {
        return instances.size();
    }


    public ThingInstance findInstanceByField(FieldValue fieldValue) {

        for (ThingInstance thing : instances.values()) {
            if (thing.getValue(fieldValue.getName()).contentEquals(fieldValue.getValue())) {
                return thing;
            }
        }

        return null;
    }

    public ThingInstance findInstanceByGUID(String instanceFieldValue) {

        if (instances.containsKey(instanceFieldValue)) {
            return instances.get(instanceFieldValue);
        }

        return null;
    }


    public Collection<ThingInstance> getInstances() {
        return instances.values();
    }


    public Thing deleteInstance(String guid) {

        if (!instances.containsKey(guid)) {
            throw new IndexOutOfBoundsException(
                    String.format("Could not find a %s with GUID %s",
                            definition.getName(), guid));
        }

        ThingInstance item = instances.get(guid);

        instances.remove(guid);

        item.removeAllRelationships();

        return this;
    }

    /*

        Definition abstractions

     */

    public ThingDefinition definition() {
        return definition;
    }

    private List<String> getGuidList() {
        List<String> guids = new ArrayList<>();
        for(ThingInstance instance : instances.values()){
            guids.add(instance.getGUID());
        }

        return guids;
    }

    public void deleteAllInstances() {
        List<String> guids = getGuidList();

        for(String aGuid : guids){
            deleteInstance(aGuid);
        }
    }


}
