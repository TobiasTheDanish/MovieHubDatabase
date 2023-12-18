package dk.lyngby.controller.impl;

import dk.lyngby.config.ApplicationConfig;
import dk.lyngby.exception.ApiException;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MovieController {
    private final Logger LOGGER = LoggerFactory.getLogger(MovieController.class);
    private final boolean isDeployed = (System.getenv("DEPLOYED") != null);
    private final String TMDB_API_KEY;
    private final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";

    public MovieController(){
        try {
            TMDB_API_KEY = isDeployed ? System.getenv("TMDB_API_KEY") : ApplicationConfig.getProperty("tmdb.key");
        } catch (IOException e) {
            LOGGER.error("Could not get apikey. Error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void fetchAllMovies(Context ctx) throws ApiException {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(TMDB_BASE_URL + "discover/movie?include_adult=false&include_video=false&language=en-US&page=1&sort_by=popularity.desc"))
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer " + TMDB_API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ctx.status(response.statusCode());
            ctx.json(response.body());
        } catch (URISyntaxException e) {
            throw new ApiException(500, "Could not create URI");
        } catch (IOException | InterruptedException e) {
            throw new ApiException(500, "Failed to send request. Error message: " + e.getMessage());
        }
    }

    public void fetchMovieById(Context ctx) throws ApiException {
        String movieId = ctx.pathParam("id");

        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(TMDB_BASE_URL + "/movie/" + movieId + "?language=en-US"))
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer " + TMDB_API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ctx.status(response.statusCode());
            ctx.json(response.body());
        } catch (URISyntaxException e) {
            throw new ApiException(500, "Could not create URI");
        } catch (IOException | InterruptedException e) {
            throw new ApiException(500, "Failed to send request. Error message: " + e.getMessage());
        }
    }

    public void fetchMovieSearch(Context ctx) throws ApiException {
        String query = urlEncode(ctx.queryParam("query"));
        if (query.isEmpty()) throw new ApiException(400, "Missing query parameter. Please provide a query parameter named 'query' with the search text.");

        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(TMDB_BASE_URL + "/search/movie?query=" + query + "&include_adult=false&language=en-US&page=1"))
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer " + TMDB_API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            ctx.status(response.statusCode());
            ctx.json(response.body());
        } catch (URISyntaxException e) {
            throw new ApiException(500, "Could not create URI");
        } catch (IOException | InterruptedException e) {
            throw new ApiException(500, "Failed to send request. Error message: " + e.getMessage());
        }
    }

    public void fetchMovieImages(Context ctx) throws ApiException {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(TMDB_BASE_URL + "configuration"))
                    .header("accept", "application/json")
                    .header("Authorization", "Bearer " + TMDB_API_KEY)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("Response: " + response.body());
            ctx.status(response.statusCode());
            ctx.json(response.body());
        } catch (URISyntaxException e) {
            throw new ApiException(500, "Could not create URI");
        } catch (IOException | InterruptedException e) {
            throw new ApiException(500, "Failed to send request. Error message: " + e.getMessage());
        }
    }
    public String urlEncode(String str) {
        if (str == null) return "";

        StringBuilder sb = new StringBuilder();

        str.chars().forEach((c) -> {
            String encodedChar = Character.isAlphabetic(c) ? Character.toString(c) : String.format("%%%02x", c);
            sb.append(encodedChar);
        });

        return sb.toString();
    }
}
