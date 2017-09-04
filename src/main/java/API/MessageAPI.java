package API;

import BL.UserManager;
import DAL.Repositories.*;
import Utils.ResponseUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * MessageAPI
 */
@Path("message")
public class MessageAPI {
    private static final UserManager userManager;

    static {
        userManager = new UserManager(
                ParkingSpotRepository.getInstance(),
                AddressRepository.getInstance(),
                UserRepository.getInstance(),
                LocationRepository.getInstance(),
                FavoriteRepository.getInstance());
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response register(String params) throws Exception {
        try {
            userManager.sendMessage(params);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }
}
