package API;

import BL.ParkingSpotManager;
import DAL.Repositories.AddressRepository;
import DAL.Repositories.LocationRepository;
import DAL.Repositories.ParkingSpotRepository;
import DAL.Repositories.UserRepository;
import Json.Response.ParkingSpotInfo;
import Json.Response.ParkingSpotSearchInfo;
import Json.UserBaseInfo;
import Utils.ResponseUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * ParkingSpotAPI
 */

@Path("parkingSpot")
public class ParkingSpotAPI {
    private static final ParkingSpotManager parkingSpotManager;

    static {
        parkingSpotManager = new ParkingSpotManager(
                ParkingSpotRepository.getInstance(),
                AddressRepository.getInstance(),
                UserRepository.getInstance(),
                LocationRepository.getInstance());
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response getParkingSpots() {
        List<ParkingSpotInfo> response = parkingSpotManager.getParkingSpots();
        return ResponseUtil.getResponse(response);
    }

    @GET
    @Path("/{parkingSpotId}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response getParkingSpot(@PathParam("parkingSpotId") int parkingSpotId, @QueryParam("params") String params) throws Exception{
        try {
            ParkingSpotSearchInfo response = parkingSpotManager.getParkingSpot(parkingSpotId, params);
            return ResponseUtil.getResponse(response);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @DELETE
    @Path("/{parkingSpotId}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response deleteParkingSpot(@PathParam("parkingSpotId") int parkingSpotId) throws Exception {
        try {
            parkingSpotManager.deleteParkingSpot(parkingSpotId);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/report")
    @Produces({MediaType.APPLICATION_JSON + "; charset=utf-8", MediaType.TEXT_PLAIN})
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response report(String params) throws Exception {
        try {
            int response = parkingSpotManager.report(params);
            return ResponseUtil.getResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }

    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response search(@QueryParam("params") String params) throws Exception {
        List<ParkingSpotSearchInfo> response = parkingSpotManager.search(params);
        return ResponseUtil.getResponse(response);
    }

    @POST
    @Path("/interested/{parkingSpotId}")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response addInterestedUser(@PathParam("parkingSpotId") int parkingSpotId, String userName) throws Exception {
        try {
            int response = parkingSpotManager.addInterested(userName, parkingSpotId);
            return ResponseUtil.getResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @DELETE
    @Path("/interested")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes({MediaType.TEXT_PLAIN + "; charset=utf-8"})
    public Response deleteInterestedUser(@QueryParam("userName") String userName) throws Exception {
        try {
            parkingSpotManager.deleteInterested(userName);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @PUT
    @Path("/interested/{parkingSpotId}")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON + "; charset=utf-8"})
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response updateInterestedUser(@PathParam("parkingSpotId") int parkingSpotId, String userName) throws Exception {
        try {
            int response = parkingSpotManager.updateInterested(userName, parkingSpotId);
            return ResponseUtil.getResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @GET
    @Path("/interested")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response getInterestedUser(int parkingSpotId) throws Exception {
        try {
            List<UserBaseInfo> response = parkingSpotManager.getInterested(parkingSpotId);
            return ResponseUtil.getResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

    @POST
    @Path("/taken")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response updateStatus(String params) throws Exception {
        try {
            parkingSpotManager.updateStatus(params);
            return ResponseUtil.getOkResponse();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }
}
