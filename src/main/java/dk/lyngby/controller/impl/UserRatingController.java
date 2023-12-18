package dk.lyngby.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.dao.impl.UserRatingDao;
import dk.lyngby.dto.UserDTO;
import dk.lyngby.dto.UserRatingDTO;
import dk.lyngby.exception.ApiException;
import dk.lyngby.exception.AuthorizationException;
import dk.lyngby.model.UserRating;
import dk.lyngby.security.TokenFactory;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserRatingController {
    private final UserRatingDao userRatingDao = UserRatingDao.getInstance();
    private final Logger LOGGER = LoggerFactory.getLogger(MovieController.class);
    private final boolean isDeployed = (System.getenv("DEPLOYED") != null);
    private final String TMDB_API_KEY;
    private final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";

    private final ObjectMapper om = new ObjectMapper();
    private final TokenFactory TOKEN_FACTORY = TokenFactory.getInstance();

    public UserRatingController(){
        try {
            TMDB_API_KEY = isDeployed ? System.getenv("TMDB_API_KEY") : ApplicationConfig.getProperty("tmdb.key");
        } catch (IOException e) {
            LOGGER.error("Could not get apikey. Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void setRating(Context ctx) throws ApiException, AuthorizationException {
        try {
            String token = ctx.header("Authorization").split(" ")[1];
            UserDTO userDTO = TOKEN_FACTORY.verifyToken(token);

            String movieId = ctx.pathParam("id");


            double rating = parseRatingFromBody(ctx);
            UserRating userRating = userRatingDao.read(userDTO.getUsername(), movieId);
            if (userRating != null) {
                userRating.setRating(rating);
                userRating = userRatingDao.update(userRating);
            } else {
                UserRatingDTO dto = UserRatingDTO.builder()
                        .movieId(movieId)
                        .rating(rating)
                        .username(userDTO.getUsername())
                        .build();
                userRating = userRatingDao.create(dto);
            }

            ctx.status(201);
            ctx.json(new UserRatingDTO(userRating));
        } catch (NullPointerException e) {
            throw new AuthorizationException(401, "Missing 'Authorization' header.");
        }
    }

    public void getRating(Context ctx) throws AuthorizationException, ApiException {
        try {
            String token = ctx.header("Authorization").split(" ")[1];
            UserDTO userDTO = TOKEN_FACTORY.verifyToken(token);

            String movieId = ctx.pathParam("id");

            UserRating entity = userRatingDao.read(userDTO.getUsername(), movieId);

            if (entity == null) {
                throw new ApiException(400, "No rating matching username: '" + userDTO.getUsername() + "', movieId: '" + movieId + "'.");
            }

            ctx.json(new UserRatingDTO(entity));
        } catch (NullPointerException e) {
            throw new AuthorizationException(401, "Missing 'Authorization' header.");
        }
    }

    public void getAllRatingsForUser(Context ctx) throws AuthorizationException, ApiException {
        try {
            String token = ctx.header("Authorization").split(" ")[1];
            UserDTO userDTO = TOKEN_FACTORY.verifyToken(token);

            List<UserRating> entities = userRatingDao.readAll(userDTO.getUsername());
            if (entities.isEmpty()) {
                throw new ApiException(400, "This user have no ratings associated with it");
            }

            ctx.json(entities.stream().map(UserRatingDTO::new).collect(Collectors.toList()));
        } catch (NullPointerException e) {
            throw new AuthorizationException(401, "Missing 'Authorization' header.");
        }
    }

    private double parseRatingFromBody(Context ctx) throws ApiException {
        String body = ctx.body();

        try {
            Map json = om.readValue(body, Map.class);
            return Double.parseDouble(json.get("rating").toString());
        } catch (JsonProcessingException | NumberFormatException e) {
            throw new ApiException(400, "Malform JSON provided");
        }
    }
}
