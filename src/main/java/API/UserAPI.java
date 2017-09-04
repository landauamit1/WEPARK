package API;

import BL.UserManager;
import DAL.Repositories.*;
import Entities.Favorite;
import Json.Response.ParkingSpotSearchInfo;
import Json.Response.UserPersonalInfo;
import Json.UserBaseInfo;
import Utils.ResponseUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * UserAPI
 */
@Path("user")
public class UserAPI {
    private static final UserManager userManager;

    static {
        userManager = new UserManager(
                ParkingSpotRepository.getInstance(),
                AddressRepository.getInstance(),
                UserRepository.getInstance(),
                LocationRepository.getInstance(),
                FavoriteRepository.getInstance());
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response getUsers() {
        List<UserBaseInfo> response = userManager.getUsers();
        return ResponseUtil.getResponse(response);
    }

    @DELETE
    @Path("/{userName}")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON + "; charset=utf-8"})
    public Response deleteUser(@PathParam("userName") String userName) throws Exception {
        try {
            userManager.deleteUser(userName);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @GET
    @Path("/favorite/{userName}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response getFavorites(@PathParam("userName") String userName) throws Exception {
        try {
            List<Favorite> response = userManager.getFavorites(userName);
            return ResponseUtil.getResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/favorite/{userName}")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON + "; charset=utf-8"})
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response addFavorites(@PathParam("userName") String userName, String favorite) throws Exception {
        try {
            userManager.addFavorites(userName, favorite);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @PUT
    @Path("/favorite/{userName}")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON + "; charset=utf-8"})
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response updateFavorites(@PathParam("userName") String userName, String favorite) throws Exception {
        try {
            userManager.updateFavorites(userName, favorite);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @DELETE
    @Path("/favorite/{userName}")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON + "; charset=utf-8"})
    @Consumes(MediaType.TEXT_PLAIN + "; charset=utf-8")
    public Response deleteFavorites(@PathParam("userName") String userName, @QueryParam("favoriteId") int favoriteId) throws Exception {
        try {
            userManager.deleteFavorites(userName, favoriteId);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response getPersonalInfo(@QueryParam("userName") String userName) throws Exception {
        try {
            UserPersonalInfo response = userManager.getPersonalInfo(userName);
            return ResponseUtil.getResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @PUT
    @Path("/info")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON + "; charset=utf-8"})
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response updatePersonalInfo(String params) throws Exception {
        try {
            userManager.updatePersonalInfo(params);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @GET
    @Path("/interested")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON + "; charset=utf-8"})
    public Response getInterestedParkingSpot(@QueryParam("params") String params) throws Exception {
        try {
            ParkingSpotSearchInfo response = userManager.getInterestedParkingSpot(params);
            return ResponseUtil.getResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }
}
