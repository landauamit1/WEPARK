package API;

import DAL.Repositories.LocationRepository;
import DAL.Repositories.ParkingLotRepository;
import Json.Response.ParkingLotInfo;
import BL.ParkingLotManager;
import Utils.ResponseUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * ParkingLotAPI
 */

@Path("parkingLot")
public class ParkingLotAPI {
    private static final ParkingLotManager parkingLotManager;

    static {
        parkingLotManager = new ParkingLotManager(
                ParkingLotRepository.getInstance(),
                LocationRepository.getInstance());
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    @Consumes(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response search(@QueryParam("params") String params) throws Exception {
        try {
            List<ParkingLotInfo> response = parkingLotManager.search(params);
            return ResponseUtil.getResponse(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtil.getErrorResponse(e.getMessage());
        }
    }

}
