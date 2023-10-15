package ao.space;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class LoadInflationService {

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

    public Uni<Inflation> findAsync(long id) {
        return Inflation.<Inflation>findById(id);
    }

    public Inflation find(long id) {
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
            throw new RuntimeException("thread interrupted");
        } catch (ExecutionException e) {
            var cause = e.getCause();
            if (cause instanceof RuntimeException re)
                throw re;
            else
                throw new RuntimeException(cause);
        }
    }
}
