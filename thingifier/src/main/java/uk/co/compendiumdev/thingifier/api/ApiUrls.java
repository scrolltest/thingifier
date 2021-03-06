package uk.co.compendiumdev.thingifier.api;

import uk.co.compendiumdev.thingifier.apiconfig.ThingifierApiConfig;
import uk.co.compendiumdev.thingifier.domain.instances.ThingInstance;

public class ApiUrls {
    private final ThingifierApiConfig apiConfig;

    public ApiUrls(final ThingifierApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    public String getCreatedLocationHeader(final ThingInstance thingInstance) {
        String uniqueID;
        String entityPath;

        if (apiConfig.willUrlShowInstancesAsPlural()) {
            entityPath = thingInstance.getEntity().getPlural();
        } else {
            entityPath = thingInstance.getEntity().getName();
        }

        if (apiConfig.willUrlsShowIdsIfAvailable() && thingInstance.hasIDField()){
            uniqueID = thingInstance.getID();
        }else{
            uniqueID = thingInstance.getGUID();
        }

        return entityPath + "/" + uniqueID;
    }
}
