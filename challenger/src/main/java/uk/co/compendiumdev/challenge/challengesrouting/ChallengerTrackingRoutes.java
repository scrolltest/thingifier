package uk.co.compendiumdev.challenge.challengesrouting;

import uk.co.compendiumdev.challenge.ChallengerAuthData;
import uk.co.compendiumdev.challenge.challengers.Challengers;
import uk.co.compendiumdev.challenge.persistence.PersistenceLayer;
import uk.co.compendiumdev.thingifier.api.routings.RoutingDefinition;
import uk.co.compendiumdev.thingifier.api.routings.RoutingStatus;
import uk.co.compendiumdev.thingifier.api.routings.RoutingVerb;
import uk.co.compendiumdev.thingifier.spark.SimpleRouteConfig;

import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

public class ChallengerTrackingRoutes {

    public void configure(final Challengers challengers,
                          final boolean single_player_mode,
                          final List<RoutingDefinition> routes,
                          final PersistenceLayer persistenceLayer){

        // refresh challenger to avoid purging
        get("/challenger/*", (request, result) -> {
            String xChallengerGuid = request.splat()[0];
            if(xChallengerGuid != null && xChallengerGuid.trim()!=""){
                ChallengerAuthData challenger = challengers.getChallenger(xChallengerGuid);
                if(challenger!=null){
                    challenger.touch();
                    result.status(204);
                }else{
                    // try to load challenger from persistence
                    challenger = persistenceLayer.tryToLoadChallenger(challengers, xChallengerGuid);
                    if(challenger==null) {
                        result.status(404);
                    }else{
                        result.status(204);
                    }
                }
            }else{
                result.status(404);
            }
            return "";
        });

        SimpleRouteConfig.
                routeStatusWhenNot(
                        405, "/challenger/*", "get");

        // create a challenger
        post("/challenger", (request, result) -> {

            if(single_player_mode){
                result.header("X-CHALLENGER", challengers.SINGLE_PLAYER.getXChallenger());
                result.header("Location", "/gui/challenges");
                result.status(201);
                return "";
            }

            String xChallengerGuid = request.headers("X-CHALLENGER");
            if(xChallengerGuid == null || xChallengerGuid.trim()==""){
                // create a new challenger
                final ChallengerAuthData challenger = challengers.createNewChallenger();
                result.header("X-CHALLENGER", challenger.getXChallenger());
                result.header("Location", "/gui/challenges/" + challenger.getXChallenger());
                result.status(201);
                return "";
            }else {
                ChallengerAuthData challenger = challengers.getChallenger(xChallengerGuid);
                if(challenger==null){
                    // try to load challenger status
                    challenger = persistenceLayer.tryToLoadChallenger(challengers, xChallengerGuid);
                }
                if(challenger==null){
                    // if X-CHALLENGER header exists, and is not a known UUID,
                    // return 410, challenger ID not valid
                    result.header("X-CHALLENGER", "Challenger not found");
                    result.status(422);
                }else{
                    // if X-CHALLENGER header exists, and has a valid UUID, and UUID exists, then return 200
                    result.header("X-CHALLENGER", challenger.getXChallenger());
                    result.header("Location", "/gui/challenges/" + challenger.getXChallenger());
                    result.status(200);
                }
            }
            result.status(400);
            return "Unknown Challenger State";
        });

        SimpleRouteConfig.
                routeStatusWhenNot(
                        405, "/challenger", "post");

        String explanation = "";
        if(single_player_mode) {
            explanation =  " Not necessary in single user mode.";
        }


        routes.add(new RoutingDefinition(
                RoutingVerb.POST,
                "/challenger",
                RoutingStatus.returnedFromCall(),
                null).addDocumentation("Create an X-CHALLENGER guid to allow tracking challenges, use the X-CHALLENGER header in all requests to track challenge completion for multi-user tracking." + explanation));

        routes.add(new RoutingDefinition(
                RoutingVerb.GET,
                "/challenger/:guid",
                RoutingStatus.returnedFromCall(),
                null).addDocumentation("Restore a saved challenger matching the supplied X-CHALLENGER guid to allow continued tracking of challenges." + explanation));

    }
}
