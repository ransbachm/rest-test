package moe.pgnhd.theshop;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import io.github.cdimascio.dotenv.Dotenv;
import moe.pgnhd.theshop.handlers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {

    private static Logger LOG = LoggerFactory.getLogger(Main.class);
    public static Management management;
    public static Dotenv dotenv = Dotenv.load();
    static boolean isDev = true;

    private static Handlebars hbs;

    public static String render(String templatePath, Map<String, Object> model) {
        try{
            Template template = hbs.compile("templates/" + templatePath);
            return template.apply(model);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws SQLException {
        LOG.info("Application is starting");

        try {
            management = new Management();
        } catch (SQLException e) {
            LOG.error("DB Connection failed - shutting down");
            LOG.error(e.getMessage());
            return;
        }

        // Because resources are copied to target directory
        // Live refresh cannot work with the class path loader
        if (!isDev) {
            hbs = new Handlebars();
            staticFiles.location("/public/");
        } else {
            hbs = new Handlebars(new FileTemplateLoader(
                    "src/main/resources/"));
            staticFiles.externalLocation("src/main/resources/public");
        }

        port(4567);

        get("/", (Request req, Response res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("testtext", "Rendered");
            return render("index", model);
        });


        get("hello", HelloHandler::handleHelloRequest);
        get("hello2", HelloHandler::handleAnyUserFirstName);
        get("my/orders", OrderHandler::handleGetOrders);
        get("/product/:id", ProductHandler::handleProducts);
        get("/seller/:id", SellerHandler::handleSeller);

        post("search", SearchHandler::handleSearch);
    }

}