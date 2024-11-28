public class DeviceConnections {

    Device device;
    int deviceId;


    DeviceConnections(Device device, int deviceId){
        this.device = device;
        this.deviceId = deviceId;
    }

    public Device getDevice() {
        return device;
    }

    public int getDeviceId() {
        return deviceId;
    }
}
