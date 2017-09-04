package Json;

import com.google.gson.annotations.SerializedName;

/**
 * UserBaseInfo
 */
public class UserBaseInfo {
    @SerializedName("name")
    protected String name;
    @SerializedName("licensePlate")
    protected String licensePlate;
    @SerializedName("carModel")
    protected String carModel;

    public UserBaseInfo(String name, String licensePlate, String carModel) {
        this.name = name;
        this.licensePlate = licensePlate;
        this.carModel = carModel;
    }

    public String getName() {
        return name;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getCarModel() {
        return carModel;
    }

    @Override
    public String toString() {
        return "UserBaseInfo{" +
                "name='" + name + '\'' +
                ", licensePlate='" + licensePlate + '\'' +
                ", carModel='" + carModel + '\'' +
                '}';
    }
}
