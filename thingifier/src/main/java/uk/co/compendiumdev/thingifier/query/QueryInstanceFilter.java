package uk.co.compendiumdev.thingifier.query;

import uk.co.compendiumdev.thingifier.domain.definitions.ThingDefinition;
import uk.co.compendiumdev.thingifier.domain.instances.ThingInstance;

import java.util.Map;

public class QueryInstanceFilter {
    private final Map<String, String> params;

    public QueryInstanceFilter(final Map<String, String> queryParams) {
        this.params = queryParams;
    }

    public boolean matches(final ThingInstance instance) {
        for(Map.Entry<String,String> field : params.entrySet()){

            final ThingDefinition defn = instance.getEntity();

            String fieldName = field.getKey();

            if(defn.hasFieldNameDefined(fieldName)){
                if(!instance.getValue(fieldName).
                        equals(field.getValue())){
                    return false;
                }
            }
        }

        return true;
    }
}
