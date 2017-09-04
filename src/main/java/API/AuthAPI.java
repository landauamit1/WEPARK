package API;

import BL.AuthManager;
import DAL.Repositories.UserRepository;
import Utils.ResponseUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * AuthAPI
 */
@Path("user/auth")
public class AuthAPI {
    private static final AuthManager authManager;

    static {
        authManager = new AuthManager(
                UserRepository.getInstance());
    }

    @POST
    @Path("/register")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON + "; charset=utf-8"})
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response register(String params) throws Exception {
        try {
            String response = authManager.register(params);
            return ResponseUtil.getResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response login(String params) throws Exception {
        try {
            authManager.login(params);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response logout(@QueryParam("userName") String userName) throws Exception {
        try {
            authManager.logout(userName);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response updateToken(@QueryParam("userName") String userName,
                                @QueryParam("token") String token) throws Exception {
        try {
            authManager.updateToken(userName, token);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }
}
