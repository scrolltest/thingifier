package uk.co.compendiumdev.challenge.challengesrouting;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import uk.co.compendiumdev.challenge.BasicAuthHeader;
import uk.co.compendiumdev.challenge.ChallengerAuthData;
import uk.co.compendiumdev.challenge.challengers.Challengers;
import uk.co.compendiumdev.thingifier.api.routings.RoutingDefinition;
import uk.co.compendiumdev.thingifier.api.routings.RoutingStatus;
import uk.co.compendiumdev.thingifier.api.routings.RoutingVerb;
import uk.co.compendiumdev.thingifier.spark.SimpleRouteConfig;

import java.util.HashMap;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

public class AuthRoutes {
    public void configure(final Challengers challengers, final List<RoutingDefinition> routes) {
        // authentication and authorisation
        // - create a 'secret' note which can be stored against session using an auth token


        // POST /secret/token with basic auth to get a secret/token to use as X-AUTH-TOKEN header
        // todo: or {username, password} payload
        post("/secret/token", (request, result) -> {

            BasicAuthHeader basicAuth = new BasicAuthHeader(request.headers("Authorization"));

            // admin/password as default username:password
            if(!basicAuth.matches("admin","password")){
                result.header("WWW-Authenticate","Basic realm=\"User Visible Realm\"");
                result.status(401);
                return "";
            }

            ChallengerAuthData challenger = challengers.getChallenger(request.headers("X-CHALLENGER"));

            if(challenger==null){
                result.status(401);
                result.header("X-CHALLENGER", "Challenger not recognised");
            }

            // if no header X-AUTH-TOKEN then grant one
            result.header("X-AUTH-TOKEN", challenger.getXAuthToken());
            result.status(201);
            return "";
        });

        SimpleRouteConfig.routeStatusWhenNot(
                405, "/secret/token", "post");

        routes.add(new RoutingDefinition(
                RoutingVerb.POST,
                "/secret/token",
                RoutingStatus.returnedFromCall(),
                null).addDocumentation("POST /secret/token with basic auth to get a secret/token to use as X-AUTH-TOKEN header, to allow access to the /secret/note end points."));

        // todo: GET /secret/token returns the secret token or 401 if not authenticated


        // POST /secret/note GET /secret/note - limit note to 100 chars
        // no auth token will receive a 403
        // auth token which does not match the session will receive a 401
        // header X-AUTH-TOKEN: token given - if token not found (then) 401

        get("/secret/note", (request, result) -> {

            final String authToken = request.headers("X-AUTH-TOKEN");
            if(authToken==null || authToken.length()==0){
                result.status(401);
                return "";
            }

            ChallengerAuthData challenger = challengers.getChallenger(request.headers("X-CHALLENGER"));

            if(challenger==null){
                result.status(401);
                result.header("X-CHALLENGER", "Challenger not recognised");
            }

            if(!authToken.contentEquals(challenger.getXAuthToken())){
                result.status(403); // given token is not allowed to access anything
                return "";
            }

            result.status(200);
            result.header("Content-Type", "application/json");
            final JsonObject note = new JsonObject();
            note.addProperty("note", challenger.getNote());
            return new Gson().toJson(note);
        });

        routes.add(new RoutingDefinition(
                RoutingVerb.GET,
                "/secret/note",
                RoutingStatus.returnedFromCall(),
                null).addDocumentation("GET /secret/note with X-AUTH-TOKEN to return the secret note for the user."));


        post("/secret/note", (request, result) -> {

            final String authToken = request.headers("X-AUTH-TOKEN");
            if(authToken==null || authToken.length()==0){
                result.status(401);
                return "";
            }

            ChallengerAuthData challenger = challengers.getChallenger(request.headers("X-CHALLENGER"));

            if(challenger==null){
                result.status(401);
                result.header("X-CHALLENGER", "Challenger not recognised");
            }

            if(!authToken.contentEquals(challenger.getXAuthToken())){
                result.status(403); // given token is not allowed to access anything
                return "";
            }

            try{
                final HashMap body = new Gson().fromJson(request.body(), HashMap.class);
                if(body.containsKey("note")){
                    challenger.setNote((String)body.get("note"));
                }

                result.status(200);
                result.header("Content-Type", "application/json");
                final JsonObject note = new JsonObject();
                note.addProperty("note", challenger.getNote());
                return new Gson().toJson(note);

            }catch(Exception e){
                result.status(400);
                return "";
            }

        });

        SimpleRouteConfig.routeStatusWhenNot(
                405, "/secret/note", "get", "post");

        routes.add(new RoutingDefinition(
                RoutingVerb.POST,
                "/secret/note",
                RoutingStatus.returnedFromCall(),
                null).addDocumentation("POST /secret/note with X-AUTH-TOKEN, and a payload of `{'note':'contents of note'}` to amend the contents of the secret note."));


    }
}
