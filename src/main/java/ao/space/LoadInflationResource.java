package ao.space;

import ao.space.LoadInflationService.Inflation;
import io.quarkus.logging.Log;
import io.smallrye.common.annotation.RunOnVirtualThread;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/inflations")
public class LoadInflationResource {

    @Inject
    LoadInflationService service;

    @Path("/reactive/{id}")
    @GET
    public Uni<Inflation> getInflationReactively(@PathParam("id") long id) {
        return service.findAsync(id)
            .onItem().ifNull().failWith(
                new NotFoundException("no inflation for id: " + id));
    }

    @Path("/virtual/{id}")
    @GET
    @RunOnVirtualThread
    public Inflation getInflationWithVirtualThread(@PathParam("id") long id) {
        return getInflationImperatively(id);
    }
    
    @Path("/imperative/{id}")
    @GET
    public Inflation getInflationImperatively(@PathParam("id") long id) {
        var r = service.find(id);
        if (r == null)
            throw new NotFoundException("no inflation for id: " + id);
        return r;
    }
}
