package mil.army.usace.hec;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cwms.radar.api.BlobController;
import cwms.radar.api.CatalogController;
import cwms.radar.api.ClobController;
import cwms.radar.api.LevelsController;
import cwms.radar.api.LocationCategoryController;
import cwms.radar.api.LocationController;
import cwms.radar.api.LocationGroupController;
import cwms.radar.api.OfficeController;
import cwms.radar.api.ParametersController;
import cwms.radar.api.PoolController;
import cwms.radar.api.RatingController;
import cwms.radar.api.TimeSeriesCategoryController;
import cwms.radar.api.TimeSeriesController;
import cwms.radar.api.TimeSeriesGroupController;
import cwms.radar.api.TimeZoneController;
import cwms.radar.api.UnitsController;
import cwms.radar.api.enums.UnitSystem;
import cwms.radar.api.errors.RadarError;
import cwms.radar.formatters.FormattingException;
import io.javalin.Javalin;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.core.security.Role;
import io.javalin.core.util.CorsPlugin;
import io.javalin.core.util.Header;
import io.javalin.core.validation.JavalinValidation;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJackson;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import javalinjwt.JavalinJWT;
import org.apache.http.entity.ContentType;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jetbrains.annotations.NotNull;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import static io.javalin.apibuilder.ApiBuilder.crud;
import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.staticInstance;


public class RadarAPI {
    private static final Logger logger = Logger.getLogger(RadarAPI.class.getName());
    private static final MetricRegistry metrics = new MetricRegistry();
    private static final Meter total_requests = metrics.meter("radar.total_requests");
    private Javalin app = null;
    private int port = -1;

    public static void main(String[] args){
        DataSource ds = new DataSource();
        int port = Integer.parseInt(System.getProperty("RADAR_LISTEN_PORT","7000"));
        try{
            ds.setDriverClassName(getconfig("RADAR_JDBC_DRIVER","oracle.jdbc.driver.OracleDriver"));
            ds.setUrl(getconfig("RADAR_JDBC_URL","jdbc:oracle:thin:@localhost/CWMSDEV"));
            ds.setUsername(getconfig("RADAR_JDBC_USERNAME"));
            ds.setPassword(getconfig("RADAR_JDBC_PASSWORD"));
            ds.setInitialSize(Integer.parseInt(getconfig("RADAR_POOL_INIT_SIZE","5")));
            ds.setMaxActive(Integer.parseInt(getconfig("RADAR_POOL_MAX_ACTIVE","10")));
            ds.setMaxIdle(Integer.parseInt(getconfig("RADAR_POOL_MAX_IDLE","5")));
            ds.setMinIdle(Integer.parseInt(getconfig("RADAR_POOL_MIN_IDLE","2")));
        } catch( Exception err ){
            logger.log(Level.SEVERE,"Required Parameter not set in environment",err);
            System.exit(1);
        }
        RadarAPI api = new RadarAPI(ds,port);
        api.start();
    }

    enum Roles implements Role
    {
        ANYONE,
        USER,
        ADMIN
    }

    public RadarAPI(javax.sql.DataSource ds, int port){
        this.port = port;
        PolicyFactory sanitizer = new HtmlPolicyBuilder().disallowElements("<script>").toFactory();
        JavalinValidation.register(UnitSystem.class, UnitSystem::systemFor);

        ObjectMapper om = JavalinJackson.getObjectMapper();
        om.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        om.registerModule(new JavaTimeModule());

        Map<String, Role> rolesMapping = new HashMap<String, Role>() {{
            put("user", Roles.USER);
            put("admin", Roles.ADMIN);
        }};
        AccessManager accessManager = new AccessManager("level", rolesMapping, null);

        JavalinJackson.configure(om);
        Javalin javalin = Javalin.create(config -> {
            config.defaultContentType = "application/json";
            config.contextPath = "/";
            OpenApiPlugin plugin = new OpenApiPlugin(getOpenApiOptions());
            
            config.registerPlugin(plugin);
            if(System.getProperty("RADAR_DEBUG_LOGGING", "false").equalsIgnoreCase("true"))
            {
                config.enableDevLogging();
            }
            config.requestLogger((ctx, ms) -> logger.info(ctx.toString()));
            config.configureServletContextHandler(
                    sch -> sch.addServlet(new ServletHolder(new MetricsServlet(metrics)), "/metrics/*"));
            config.addStaticFiles("/static");
            config.accessManager(accessManager);
            final String origins = "";

            CorsPlugin cor = CorsPlugin.forOrigins(origins);
            config.registerPlugin(cor);
        });
        app = javalin
                .attribute(PolicyFactory.class, sanitizer)
                .before( ctx -> {
                    ctx.header("X-Content-Type-Options","nosniff");
                    ctx.header("X-Frame-Options","SAMEORIGIN");
                    ctx.header("X-XSS-Protection", "1; mode=block");
                    ctx.attribute("database",ds.getConnection());
                    /* authorization on connection setup will go here
                    Connection conn = ctx.attribute("db");
                    */
                    logger.info(ctx.header("accept"));
                    total_requests.mark();
                })
                .before(buildJwtDecodeHandler())
                .before(ctx -> {
                    // Trying to see if we can allow CORS for all GET requests.
                    // Not sure this will work b/c I think preflight OPTIONS requests
                    // expect to be told what METHODS are allowed.
                    if (ctx.method() == "GET") {
                        ctx.header(Header.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
                    }
                    // Arg, do we have to do something special for pre-flight OPTIONS checks?
                })
                .after( ctx -> {
                    try{
                        ((Connection)ctx.attribute("database")).close();
                    } catch( SQLException e ){
                        logger.log(Level.WARNING, "Failed to close database connection", e);
                    }
                })
                .exception(FormattingException.class, (fe, ctx ) -> {
                    final RadarError re = new RadarError("Formatting error");

                    if( fe.getCause() instanceof IOException ){
                        ctx.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    } else {
                        ctx.status(HttpServletResponse.SC_NOT_IMPLEMENTED);
                    }
                    logger.log(Level.SEVERE,fe, () -> re + "for request: " + ctx.fullUrl());
                    ctx.json(re);
                })
                .exception(UnsupportedOperationException.class, (e, ctx) -> {
                    ctx.status(HttpServletResponse.SC_NOT_IMPLEMENTED).json(RadarError.notImplemented());
                })
                .exception(BadRequestResponse.class, (e, ctx) -> {
                    RadarError re = new RadarError("Bad Request", e.getDetails());
                    logger.log(Level.INFO, re.toString(), e );
                    ctx.status(e.getStatus()).json(re);
                })
                .exception(IllegalArgumentException.class, (e, ctx ) -> {
                    RadarError re = new RadarError("Bad Request");
                    logger.log(Level.INFO, re.toString(), e );
                    ctx.status(HttpServletResponse.SC_BAD_REQUEST).json(re);
                })
                .exception(Exception.class, (e,ctx) -> {
                    RadarError errResponse = new RadarError("System Error");
                    logger.log(Level.WARNING,String.format("error on request[%s]: %s", errResponse.getIncidentIdentifier(), ctx.req.getRequestURI()),e);
                    ctx.status(500);
                    ctx.contentType(ContentType.APPLICATION_JSON.toString());
                    ctx.json(errResponse);
                })
                .routes( () -> {
                    //get("/", ctx -> { ctx.result("welcome to the CWMS REST API").contentType(Formats.PLAIN);});
                    crud("/locations/:location_code", new LocationController(metrics));
                    crud("/location/category/:category-id", new LocationCategoryController(metrics));
                    crud("/location/group/:group-id", new LocationGroupController(metrics));
                    crud("/offices/:office", new OfficeController(metrics));
                    crud("/units/:unit_name", new UnitsController(metrics));
                    crud("/parameters/:param_name", new ParametersController(metrics));
                    crud("/timezones/:zone", new TimeZoneController(metrics));
                    crud("/levels/:location", new LevelsController(metrics));
                    TimeSeriesController tsController = new TimeSeriesController(metrics);
                    final Set<Role> justAdmin = new LinkedHashSet<>();
                    justAdmin.add(Roles.ADMIN);
                    specialCrud("/timeseries/:timeseries", tsController, justAdmin);
                    get("/timeseries/recent/:group-id", tsController::getRecent);

                    crud("/timeseries/category/:category-id", new TimeSeriesCategoryController(metrics));
                    crud("/timeseries/group/:group-id", new TimeSeriesGroupController(metrics));
                    crud("/ratings/:rating", new RatingController(metrics));
                    crud("/catalog/:dataSet", new CatalogController(metrics));
                    crud("/blobs/:blob-id", new BlobController(metrics));
                    crud("/clobs/:clob-id", new ClobController(metrics));
                    crud("/pools/:pool-id", new PoolController(metrics), justAdmin);
                });

    }

    @NotNull
    private Handler buildJwtDecodeHandler()
    {
        // This pulls jwt out of header,
        // decodes it,
        // validates it,
        // puts it in context
        // But it doesn't do role authorization!

        Algorithm algorithm = Algorithm.HMAC256("very_secret");
//        Algorithm.RSA256(publickey, null);

        JWTVerifier verifier = JWT.require(algorithm).build();

        return (context) -> JavalinJWT.getTokenFromHeader(context)
                .flatMap((str) -> validateToken(verifier, str))
                .ifPresent(jwt -> JavalinJWT.addDecodedToContext(context, jwt));
    }

    private Optional<DecodedJWT> validateToken( JWTVerifier verifier, String token) {
        try {
            return Optional.of(verifier.verify(token));
        } catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }

    private static OpenApiOptions getOpenApiOptions() {
        Info info = new Info().version("2.0").description("CWMS REST API for Data Retrieval");
        SwaggerOptions swaggerOptions = new SwaggerOptions("/static/swagger-ui.html");

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme(   "bearer")
                .bearerFormat("JWT")
                ;

        Components components = new Components()
                .addSecuritySchemes( "bearerAuth", securityScheme);

        OpenApiOptions options = new OpenApiOptions(() -> new OpenAPI().info(info).components(components))
                    .path("/swagger-docs")
                    .swagger(swaggerOptions)
                    .activateAnnotationScanningFor("cwms.radar.api")
                    .defaultDocumentation( doc -> {
                        doc.json("500", RadarError.class);
                        doc.json("400", RadarError.class);
                        doc.json("404", RadarError.class);
                        doc.json("501", RadarError.class);
                    })
                ;

        return options;
    }

    // I want a way to pass roles to the create, update and delete but not to get or getAll.
    public static void specialCrud(@NotNull String path, @NotNull CrudHandler crudHandler, @NotNull Set<Role> permittedRoles) {
        String fullPath = ApiBuilder.prefixPath(path);
        String[] subPaths = Arrays.stream(fullPath.split("/")).filter(it -> !it.isEmpty()).toArray(String[]::new);
        if (subPaths.length < 2) {
            throw new IllegalArgumentException("CrudHandler requires a path like '/resource/:resource-id'");
        }
        String resourceId = subPaths[subPaths.length - 1];
        if (!resourceId.startsWith(":")) {
            throw new IllegalArgumentException("CrudHandler requires a path-parameter at the end of the provided path, e.g. '/users/:user-id'");
        }
        if (subPaths[subPaths.length - 2].startsWith(":")) {
            throw new IllegalArgumentException("CrudHandler requires a resource base at the beginning of the provided path, e.g. '/users/:user-id'");
        }

        String fullPathNoResource = fullPath.replace(resourceId, "");

        // These two don't need roles
        staticInstance().get(fullPath, ctx -> crudHandler.getOne(ctx, resourceId));
        staticInstance().get(fullPathNoResource, ctx -> crudHandler.getAll(ctx));

        // These need roles checked.
        staticInstance().post(fullPathNoResource, ctx -> crudHandler.create(ctx), permittedRoles);
        staticInstance().patch(fullPath, ctx -> crudHandler.update(ctx, resourceId), permittedRoles);
        staticInstance().delete(fullPath, ctx -> crudHandler.delete(ctx, resourceId), permittedRoles);
    }
    
    public void start(){
        this.app.start(this.port);
    }

    public void stop(){
        this.app.stop();
    }

    private static String getconfig(String envName){
        return System.getenv(envName);
    }
    private static String getconfig(String envName,String defaultVal){
        String val = System.getenv(envName);
        return val != null ? val : defaultVal;
    }
}
