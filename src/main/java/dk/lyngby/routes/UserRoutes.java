package dk.lyngby.routes;

import dk.lyngby.controller.impl.UserController;
import dk.lyngby.controller.impl.UserRatingController;
import dk.lyngby.security.RouteRoles;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UserRoutes {
    private final UserController userController = new UserController();
    private final UserRatingController ratingController = new UserRatingController();

    protected EndpointGroup getRoutes() {

        return () -> {
            path("/auth", () -> {
                post("/login", userController::login, RouteRoles.ANYONE);
                post("/register", userController::register, RouteRoles.ANYONE);
            });
            path("user", () -> {
                get("ratings", ratingController::getAllRatingsForUser, RouteRoles.MANAGER, RouteRoles.ADMIN, RouteRoles.USER);
            });
        };
    }
}