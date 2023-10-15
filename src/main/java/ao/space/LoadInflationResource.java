package ao.space;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/inflations")
public class LoadInflationResource {

    @Entity
    @Table(name = "inflations")
    static class Inflation extends PanacheEntity {
        public String region;
        public String year;
        public Double inflation;
        public String unit;
        public String subregion;
        public String country;
    }

    @Path("/reactive/{id}")
    @GET
    public Uni<Inflation> getInflationReactively(@PathParam("id") long id) {
        return find(id).onFailure().invoke(t -> {
            Log.errorf(t, "find inflation error for id: %d", id);
        });
    }

    @Inject
    Vertx vertx;
    
    @Path("/imperative/{id}")
    @GET
    public Inflation getInflationImperatively(@PathParam("id") long id) {
        var f = new CompletableFuture<Inflation>();
        vertx.runOnContext(() -> 
            Panache.withSession(() -> {
                return find(id)
                    .onItem().invoke(i -> f.complete(i))
                    .onFailure().invoke(t -> f.completeExceptionally(t));
            })
            .subscribe().with(i -> {})
        );

        try {
            return f.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        } catch (ExecutionException e) {
            var cause = e.getCause();
            if (cause instanceof RuntimeException re)
                throw re;
            else
                throw new RuntimeException(cause);
        }
    }

    private Uni<Inflation> find(long id) {
        return Inflation.<Inflation>findById(id)
                .onItem().ifNull().failWith(
                    new NotFoundException("no inflation for id: " + id));
    }

}
