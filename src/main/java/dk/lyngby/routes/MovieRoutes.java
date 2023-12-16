package dk.lyngby.routes;

import dk.lyngby.controller.impl.MovieController;
import dk.lyngby.security.RouteRoles;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class MovieRoutes {
    private final MovieController controller = new MovieController();

    protected EndpointGroup getRoutes() {

        return () -> {
            path("/movies", () -> {
                get("/", controller::fetchAllMovies, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.MANAGER);
                get("/search", controller::fetchMovieSearch, RouteRoles.ANYONE, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.MANAGER);
                get("{id}", controller::fetchMovieById, RouteRoles.ANYONE, RouteRoles.USER, RouteRoles.ADMIN, RouteRoles.MANAGER);
            });
        };
    }
}
