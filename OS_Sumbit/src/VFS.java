import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

public class VFS implements Device{
        DeviceConnections[] devices;

    VFS(){
        devices = new DeviceConnections[10];
    }
    /*
        @Open
        Separates the string to determine the device
        then passes the rest of the string
        as the parameters for the respective device
        then calls that devices open
        Using the returned value to create an entry in the deviceConnections array
        then returns the index

     */
    @Override
    public int Open(String s) throws FileNotFoundException {
        if(s!= null && !s.isEmpty()) {
            StringBuilder deviceType = new StringBuilder();
            StringBuilder deviceContents = new StringBuilder();
            int startIndex = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == ' ') {
                    startIndex = i + 1;
                    break;
                }
                deviceType.append(s.charAt(i));

            }
            for (int i = startIndex; i < s.length(); i++) {
                deviceContents.append(s.charAt(i));
            }

            switch (deviceType.toString()) {
                case "file":

                    FakeFileSystem newFile = new FakeFileSystem(new File(deviceContents.toString()));
                    int newFileId = newFile.Open(deviceContents.toString());
                    if(newFileId == -1){
                        throw new FileNotFoundException(newFile +" File could not be Opened...");
                    }
                    return CreateDeviceConnection(newFile, newFileId);

                case "random":
                    RandomDevice newRandomDevice = new RandomDevice();
                    int newRandomId = newRandomDevice.Open(deviceContents.toString());
                    if(newRandomId == -1){
                        throw new FileNotFoundException(newRandomDevice +" Random device could not be Opened...");
                    }
                    return CreateDeviceConnection(newRandomDevice, newRandomId);


            }
        }
        return -1;
    }

    /*
        @CreateDeviceConnection
        adds the device and device id to the DevicesConnections array

     */
    public int CreateDeviceConnection(Device device, int DeviceId){
        for (int i =0;i <devices.length;i++){
            if(devices[i] == null) {
                devices[i] = new DeviceConnections(device,DeviceId);
                return i;
            }
        }
        return -1;
    }
    /*
        @Close
        removes the device and id entry
     */
    @Override
    public void Close(int id) throws IOException {
        if(id >=0 && id <devices.length){
            devices[id].device.Close(devices[id].deviceId);
            devices[id].device = null;
        }
    }

    @Override
    public byte[] Read(int id, int size) throws IOException {
        if(devices[id] != null) {
            return devices[id].device.Read(devices[id].deviceId, size);
        }
        throw new IOException("Could not read: device not found...");
    }

    @Override
    public void Seek(int id, int to) throws IOException {
        if (devices[id] != null){
            devices[id].device.Seek(devices[id].deviceId,to);
        }else {
            throw new IOException("could not seek: device not found");
        }
    }

    @Override
    public int Write(int id, byte[] data) throws IOException {
        if (devices[id] != null) {
            return devices[id].device.Write(devices[id].deviceId, data);
        }
        throw new IOException("could not write: device not found");
    }

    @Override
    public String DeviceName() {
        return "vfs";
    }


}
