import java.util.Random;

public class RandomDevice implements Device{

    java.util.Random[] randomDevices;

    RandomDevice(){
        randomDevices = new java.util.Random[10];
    }
    /*

        @Open
        Creates a random device and puts it in an empty spot in the random devices array
        if a string is provided converts the string to an int
        and sets the int as a seed for the new random device
     */
    @Override
    public int Open(String s) {

        for(int i =0; i < randomDevices.length;i++){
            if (randomDevices[i] == null){
                if(s != null && !s.isEmpty()) {
                    randomDevices[i] = new Random(Integer.parseInt(s));
                }else {
                    randomDevices[i] = new Random();

                }
                return i;
            }
        }

        return -1;
    }

    /*
        @Close
        nulls the device entry
     */
    @Override
    public void Close(int id) {
        if (id >= 0 && id < randomDevices.length) {
            randomDevices[id] = null;
        }

    }
    /*
        @Read
        create/fill an array with random values
     */
    @Override
    public byte[] Read(int id, int size) {
        byte[] bytes = new byte[size];
        //generating random bytes
        if (randomDevices[id] != null) {
            randomDevices[id].nextBytes(bytes);
            return bytes;
        }
        return null;
    }

    /*
        @Seek
        read random bytes but not return them.
     */
    @Override
    public void Seek(int id, int to) {
        Read(id,to);
    }

    @Override
    public int Write(int id, byte[] data) {
        return 0;
    }

    @Override
    public String DeviceName() {
        return "random";
    }
}
