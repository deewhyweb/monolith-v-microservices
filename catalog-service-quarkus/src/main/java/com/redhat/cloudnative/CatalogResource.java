package com.redhat.cloudnative;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.json.Json;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Path("/api")
@Produces("application/json")
@Consumes("application/json")
public class CatalogResource {

    @GET
    @Path("/products")
    public List<Catalog> getAll() {
        return Catalog.listAll();
    }

    @GET
    @Path("/product/{itemId}")
    public List<Catalog> getAvailability(@PathParam("itemId") String itemId) {
        return Catalog.<Catalog>streamAll()
        .filter(p -> p.itemId.equals(itemId))
        .collect(Collectors.toList());
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Override
        public Response toResponse(Exception exception) {
            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }
            return Response.status(code)
                    .entity(Json.createObjectBuilder().add("error", exception.getMessage()).add("code", code).build())
                    .build();
        }

    }
}