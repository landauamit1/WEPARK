package Utils;


import Helpers.JsonSerializer;

import javax.ws.rs.core.Response;

/**
 * Response Utils
 */
public class ResponseUtil {

    public static Response getErrorResponse(String message) {
        return Response.serverError().entity(message).build();
    }

    public static Response getOkResponse() {
        return Response.ok().entity("Success").build();
    }

    public static <T> Response getResponse(T data) {
        return Response.ok().entity(JsonSerializer.serialize(data)).build();
    }

    public static Response getResponse(String data) {
        return Response.ok().entity(data).build();
    }
}

