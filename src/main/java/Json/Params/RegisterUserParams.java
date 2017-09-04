package Json.Params;

import com.google.gson.annotations.SerializedName;

/**
 * RegisterUserParams
 */
public class RegisterUserParams extends LoginUserParams {
    @SerializedName("carModel")
    private String carModel;
    @SerializedName("licensePlate")
    private String licensePlate;

    public String getCarModel() {
        return carModel;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
}
