import java.io.FileNotFoundException;
import java.io.IOException;

public class DeviceProcess extends UserlandProcess implements  Device{

    public int Open(String s) throws FileNotFoundException {
        return 0;
    }


    public void Close(int id) {

    }


    public byte[] Read(int id, int size) {
        return new byte[0];
    }

    @Override
    public void Seek(int id, int to) throws IOException {

    }

    @Override
    public int Write(int id, byte[] data) throws IOException {
        return 0;
    }

    public String DeviceName() {
        return null;
    }

    @Override
    public void run() {
        try {
            System.out.println("Device Process started...");
            byte[] s = new byte[1000];
            int id = Open("file test.txt");

            if(id == -1){
                throw new FileNotFoundException("File Descriptor returned:" + id);
            }else {
                Read(id,1000);
                Close(id);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    void main() {
        super.main();
    }
}
