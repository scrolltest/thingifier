package uk.co.compendiumdev.thingifier.application;


import spark.Request;
import spark.Response;
import uk.co.compendiumdev.thingifier.Thingifier;
import uk.co.compendiumdev.thingifier.api.http.HttpApiRequest;
import uk.co.compendiumdev.thingifier.api.http.HttpApiResponse;
import uk.co.compendiumdev.thingifier.api.http.ThingifierHttpApi;
import uk.co.compendiumdev.thingifier.application.httpapimessagehooks.HttpApiRequestHook;
import uk.co.compendiumdev.thingifier.application.httpapimessagehooks.HttpApiResponseHook;


import java.util.*;

final public class ThingifierHttpApiBridge {

    // todo: the methods here are all very similar, we should refactor this commonality

    private final Thingifier thingifier;
    private final ThingifierHttpApi thingifierHttpApi;
    private List<HttpApiRequestHook> apiRequestHooks;
    private List<HttpApiResponseHook> apiResponseHooks;

    public ThingifierHttpApiBridge(final Thingifier aThingifier){
        this(aThingifier, null, null);
    }

    public ThingifierHttpApiBridge(final Thingifier aThingifier,
                                   List<HttpApiRequestHook> apiRequestHooks,
                                   List<HttpApiResponseHook> apiResponseHooks) {
        this.thingifier = aThingifier;
        if(apiRequestHooks==null){
            this.apiRequestHooks = new ArrayList<>();
        }else{
            this.apiRequestHooks = apiRequestHooks;
        }
        if(apiResponseHooks==null){
            this.apiResponseHooks = new ArrayList<>();
        }else{
            this.apiResponseHooks = apiResponseHooks;
        }

        this.thingifierHttpApi = new ThingifierHttpApi(thingifier,
                                        apiRequestHooks, apiResponseHooks);
    }

    public String get(final Request request, final Response response) {

        final HttpApiRequest theRequest = new HttpApiRequest(request.pathInfo()).
                                                setHeaders(headersAsMap(request))
                                                .setQueryParams(queryParamsAsMap(request)).
                                                setVerb(HttpApiRequest.VERB.GET);

        HttpApiResponse theResponse = thingifierHttpApi.get(theRequest);

        updateResponseFromHttpResponse(theResponse, response);
        return theResponse.getBody();

    }

    public String head(final Request request, final Response response) {
        final HttpApiRequest theRequest = new HttpApiRequest(request.pathInfo()).
                setHeaders(headersAsMap(request))
                .setQueryParams(queryParamsAsMap(request)).
                        setVerb(HttpApiRequest.VERB.HEAD);

        HttpApiResponse theResponse = thingifierHttpApi.head(theRequest);

        updateResponseFromHttpResponse(theResponse, response);
        return theResponse.getBody();
    }

    private Map<String, String> queryParamsAsMap(final Request request) {

        Map<String, String> params = new HashMap<>();

        for(String paramName : request.queryParams()){
            // todo: figure out what to do if more than one
            String paramValue = request.queryParamsValues(paramName)[0];
            if(paramValue==null){
                paramValue="";
            }
            params.put(paramName, paramValue);
        }

        return params;
    }


    public String post(final Request request, final Response response) {

        final HttpApiRequest theRequest = new HttpApiRequest(request.pathInfo()).
                                            setHeaders(headersAsMap(request)).
                                            setBody(request.body()).
                                            setVerb(HttpApiRequest.VERB.POST);

        HttpApiResponse theResponse = thingifierHttpApi.post(theRequest);

        updateResponseFromHttpResponse(theResponse, response);
        return theResponse.getBody();

    }

    public String delete(final Request request, final Response response) {

        final HttpApiRequest theRequest = new HttpApiRequest(request.pathInfo()).
                                                setHeaders(headersAsMap(request)).
                                                setVerb(HttpApiRequest.VERB.DELETE);

        HttpApiResponse theResponse = thingifierHttpApi.delete(theRequest);

        updateResponseFromHttpResponse(theResponse, response);
        return theResponse.getBody();

    }

    public String put(final Request request, final Response response) {

        final HttpApiRequest theRequest = new HttpApiRequest(request.pathInfo()).
                                            setHeaders(headersAsMap(request)).
                                            setBody(request.body()).
                                            setVerb(HttpApiRequest.VERB.PUT);

        HttpApiResponse theResponse = thingifierHttpApi.put(theRequest);

        updateResponseFromHttpResponse(theResponse, response);
        return theResponse.getBody();
    }


    public String query(final Request request, final Response response, final String query) {

        final HttpApiRequest theRequest = new HttpApiRequest(request.pathInfo()).
                                                setHeaders(headersAsMap(request)).
                                                setVerb(HttpApiRequest.VERB.GET);

        HttpApiResponse theResponse = thingifierHttpApi.query(theRequest, query);

        updateResponseFromHttpResponse(theResponse, response);
        return theResponse.getBody();
    }

    private Map<String, String> headersAsMap(final Request request) {
        final Set<String> headerNames = request.headers();
        final Map<String, String> headers = new HashMap<>();

        for (String header : headerNames) {
            headers.put(header, request.headers(header));
        }
        return headers;
    }


    private void updateResponseFromHttpResponse(final HttpApiResponse httpResponse, final Response response) {

        response.status(httpResponse.getStatusCode());

        if (httpResponse.hasType()) {
            response.type(httpResponse.getType());
        }

        final Set<String> keys = httpResponse.getHeaders().keySet();
        for (String headerKey : keys) {
            response.header(headerKey, httpResponse.getHeaders().get(headerKey));
        }
    }



}
