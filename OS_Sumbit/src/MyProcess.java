import java.io.FileNotFoundException;
import java.io.IOException;

public class MyProcess extends UserlandProcess implements Device{

    public int Open(String s) throws FileNotFoundException {
        return 0;
    }


    public void Close(int id) throws IOException {

    }


    public byte[] Read(int id, int size) throws IOException {
        return new byte[0];
    }


    public void Seek(int id, int to) throws IOException {

    }


    public int Write(int id, byte[] data) throws IOException {
        return 0;
    }

    @Override
    public String DeviceName() {
        return null;
    }

    @Override
    public void run() {
        try {
            System.out.println("MyProcess Started...");
            int fd = Open("random 100");

            System.out.println("Open: Test.txt returned:" +fd);
            Read(fd, 1000);

            Close(fd);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
