package ao.space;

import io.quarkus.vertx.web.Route;
import io.smallrye.config.ConfigMapping;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.FileSystemAccess;
import io.vertx.ext.web.handler.StaticHandler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class LoadStaticFilesRoute {

    @ConfigMapping(prefix = "static")
    interface StaticConfig {
        boolean allowRoot();
        String workDir();
        boolean listDir();
    }

    @Inject
    StaticConfig config;

    @Route(path = "/static/*", methods = Route.HttpMethod.GET)
    void initStaticRoute(RoutingContext rc) {
        StaticHandler.create(
            config.allowRoot() ? FileSystemAccess.ROOT : FileSystemAccess.RELATIVE, 
            config.workDir())
        .setDirectoryListing(config.listDir())
        .handle(rc);
    }
}
