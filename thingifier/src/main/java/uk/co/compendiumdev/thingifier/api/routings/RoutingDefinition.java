package uk.co.compendiumdev.thingifier.api.routings;

import uk.co.compendiumdev.thingifier.api.response.ResponseHeader;
import uk.co.compendiumdev.thingifier.domain.definitions.ThingDefinition;

public class RoutingDefinition {
    private final RoutingVerb verb;
    private final String url;
    private final RoutingStatus routingStatus;
    private final ResponseHeader header;
    private String documentation = "";
    private boolean isFilterable;
    private ThingDefinition filterableEntityDefn;

    public RoutingDefinition(RoutingVerb verb, String url, RoutingStatus routingStatus, ResponseHeader header) {
        this.verb = verb;
        if(url.startsWith("/")){
            this.url = url.substring(1);
        }else{
            this.url = url;
        }
        this.routingStatus = routingStatus;
        this.header = header;
        this.isFilterable=false;
        filterableEntityDefn=null;
    }

    public RoutingVerb verb() {
        return this.verb;
    }

    public RoutingStatus status() {
        return routingStatus;
    }

    public String url() {
        return url;
    }

    public String header() {
        if (header.headerName == null) {
            return "";
        }

        return header.headerName;
    }

    public String headerValue() {
        if (header.headerValue == null) {
            return "";
        }

        return header.headerValue;
    }

    public String getDocumentation() {
        return this.documentation;
    }

    public RoutingDefinition addDocumentation(String documentation) {
        this.documentation = documentation;
        return this;
    }

    public boolean isFilterable() {
        return isFilterable;
    }

    public void setAsFilterableFrom(final ThingDefinition definition) {
        isFilterable=true;
        filterableEntityDefn = definition;
    }

    public ThingDefinition getFilterableEntity() {
        return filterableEntityDefn;
    }
}
