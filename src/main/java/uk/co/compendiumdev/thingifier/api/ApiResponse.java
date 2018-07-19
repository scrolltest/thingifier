package uk.co.compendiumdev.thingifier.api;

import com.google.gson.Gson;
import uk.co.compendiumdev.thingifier.generic.instances.ThingInstance;
import uk.co.compendiumdev.thingifier.reporting.JsonThing;

import java.util.*;

public class ApiResponse {
    public static final String GUID_HEADER = "X-Thing-Instance-GUID";

    private final int statusCode;
    // instead of storing a json as the body, store the things to return
    // let getBody do the conversion to json or xml
    List<ThingInstance> thingsToReturn;
    // isCollection true, return as collection, false, return as instance
    private boolean isCollection;
    // isErrorResponse true, return the stored collection of error messages
    private static boolean isErrorResponse;
    private Collection<String> errorMessages;

    private Map<String, String> headers;


    public ApiResponse(int statusCode) {
        this.statusCode = statusCode;
        headers = new HashMap<>();
        thingsToReturn = new ArrayList();
        isCollection = true;
        isErrorResponse=false;
        errorMessages = new ArrayList<>();
    }

    public int getStatusCode() {
        return this.statusCode;
    }




    public static ApiResponse success() {
        return new ApiResponse(200);
    }

    public ApiResponse returnSingleInstance(ThingInstance instance) {
        this.isCollection = false;
        thingsToReturn.clear();
        thingsToReturn.add(instance);
        return this;
    }

    public ApiResponse returnInstanceCollection(List<ThingInstance> items) {
        thingsToReturn.clear();
        thingsToReturn.addAll(items);
        isCollection=true;
        return this;
    }

    public String getBody() {

        if(isErrorResponse){
            return getErrorMessageJson(errorMessages);
        }
        // we always return an object
        // collections are named with their plural
        if(isCollection){
            System.out.println(JsonThing.asJson(thingsToReturn));
            return JsonThing.asJson(thingsToReturn);
        }else{
            ThingInstance instance = thingsToReturn.get(0);
            System.out.println(JsonThing.asJson(instance));
            return JsonThing.jsonObjectWrapper(instance.getEntity().getName(), JsonThing.asJson(thingsToReturn.get(0)));
        }
    }




    /*
            HEADERS
     */

    private ApiResponse setHeader(String headername, String value) {
        this.headers.put(headername, value);
        return this;
    }

    public String getHeaderValue(String headername) {
        return headers.get(headername);
    }

    private ApiResponse setLocationHeader(String location) {
        return setHeader("Location", location);
    }

    public Set<Map.Entry<String, String>> getHeaders(){
        return headers.entrySet();
    }





    /*
            SPECIAL CASE RESPONSES
     */

    public static ApiResponse created(ThingInstance thingInstance) {
        ApiResponse response = new ApiResponse(201);

        if(thingInstance!=null){
            response.returnSingleInstance(thingInstance);
            response.setLocationHeader(thingInstance.getEntity().getName() + "/" + thingInstance.getGUID()).
                    setHeader(ApiResponse.GUID_HEADER, thingInstance.getGUID());
        }

        return response;
    }




    /*
            ERROR MESSAGES
     */


    // error messages should always be plural to make it easier to parse
    public static String getErrorMessageJson(String errorMessage) {
        Collection<String> localErrorMessages = new ArrayList<>();
        localErrorMessages.add(errorMessage);
        return getErrorMessageJson(localErrorMessages);
    }

    public static String getErrorMessageJson(Collection<String> myErrorMessages) {
        Map errorResponseBody = new HashMap<String,Collection<String>>();
        errorResponseBody.put("errorMessages", myErrorMessages);
        return new Gson().toJson(errorResponseBody);

    }

    public static ApiResponse error404(String errorMessage) {
        return error(404, errorMessage);
    }

    public static ApiResponse error(int statusCode, String errorMessage) {
        Collection<String> localErrorMessages = new ArrayList<>();
        localErrorMessages.add(errorMessage);
        return error(statusCode, localErrorMessages);
    }

    public static ApiResponse error(int statusCode, Collection<String> errorMessages) {
        isErrorResponse=true;
        errorMessages.addAll(errorMessages);
        return new ApiResponse(statusCode);
    }



}
