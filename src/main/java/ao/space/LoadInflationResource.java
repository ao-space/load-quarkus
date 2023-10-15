package ao.space;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.logging.Log;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
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

    @Inject
    Vertx vertx;

    @Path("/reactive/{id}")
    @GET
    public Uni<Inflation> getInflationReactively(@PathParam("id") long id) {
        return findAsync(id).onFailure().invoke(t -> {
            Log.errorf(t, "find inflation error for id: %d", id);
        });
    }

    @Path("/virtual/{id}")
    @GET
    @RunOnVirtualThread
    public Inflation getInflationWithVirtualThread(@PathParam("id") long id) {
        return find(id);
    }
    
    @Path("/imperative/{id}")
    @GET
    public Inflation getInflationImperatively(@PathParam("id") long id) {
        return find(id);
    }

    private Inflation find(long id) {
        var f = new CompletableFuture<Inflation>();
        vertx.runOnContext(() -> 
            Panache.withSession(() -> findAsync(id))
            .subscribe()
            .with(i -> f.complete(i), t -> f.completeExceptionally(t))
        );

        try {
            return f.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InternalServerErrorException("thread interrupted");
        } catch (ExecutionException e) {
            var cause = e.getCause();
            if (cause instanceof RuntimeException re)
                throw re;
            else
                throw new InternalServerErrorException(cause);
        }
    }

    private Uni<Inflation> findAsync(long id) {
        return Inflation.<Inflation>findById(id)
                .onItem().ifNull().failWith(
                    new NotFoundException("no inflation for id: " + id));
    }

}
