package uk.co.compendiumdev.thingifier.api.restapihandlers;

import uk.co.compendiumdev.thingifier.Thing;
import uk.co.compendiumdev.thingifier.Thingifier;
import uk.co.compendiumdev.thingifier.api.response.ApiResponse;
import uk.co.compendiumdev.thingifier.domain.instances.ThingInstance;
import uk.co.compendiumdev.thingifier.query.SimpleQuery;

import java.util.List;

public class RestApiDeleteHandler {
    private final Thingifier thingifier;

    public RestApiDeleteHandler(final Thingifier aThingifier) {
        this.thingifier = aThingifier;
    }

    public ApiResponse handle(final String url) {
        // this should probably not delete root items
        Thing thing = thingifier.getThingNamedSingularOrPlural(url);
        if (thing != null) {
            // can't delete root level with a DELETE
            return ApiResponse.error(405, "Cannot delete root level entity");
        }

        SimpleQuery queryresult = new SimpleQuery(thingifier, url).performQuery();

        if (queryresult.wasItemFoundUnderARelationship()) {
            // delete the relationships not the items
            ThingInstance parent = queryresult.getParentInstance();
            ThingInstance child = queryresult.getLastInstance();
            parent.removeRelationshipsTo(child, queryresult.getLastRelationshipName());
        } else {
            List<ThingInstance> items = queryresult.getListThingInstance();
            if (items.size() == 0) {
                // 404 not found - nothing to delete
                return ApiResponse.error404(String.format("Could not find any instances with %s", url));
            }
            for (ThingInstance instance : items) {
                thingifier.deleteThing(instance);
            }

        }
        return ApiResponse.success();
    }
}
