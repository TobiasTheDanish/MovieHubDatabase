package dk.lyngby.routes;

import dk.lyngby.controller.impl.MovieController;
import dk.lyngby.controller.impl.UserRatingController;
import dk.lyngby.security.RouteRoles;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class MovieRoutes {
    private final MovieController controller = new MovieController();
    private final UserRatingController ratingController = new UserRatingController();

    protected EndpointGroup getRoutes() {

        return () -> {
            path("/movies", () -> {
                get("/", controller::fetchAllMovies, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.MANAGER);
                get("/images", controller::fetchMovieImages, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.MANAGER);
                get("/search", controller::fetchMovieSearch, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.MANAGER);
                get("{id}", controller::fetchMovieById, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.MANAGER);
                get("{id}/rating", ratingController::getRating, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.MANAGER);
                post("{id}/rating", ratingController::setRating, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.MANAGER);
            });
        };
    }
}
