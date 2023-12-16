package dk.lyngby;

import dk.lyngby.config.ApplicationConfig;
import io.javalin.Javalin;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String port = (System.getenv("DEPLOYED") != null) ? System.getenv("PORT") : ApplicationConfig.getProperty("javalin.port");
        ApplicationConfig
            .startServer(
                Javalin.create(),
                Integer.parseInt(port));
    }
}