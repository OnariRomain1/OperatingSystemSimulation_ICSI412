import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class FakeFileSystem implements Device{

    RandomAccessFile[] Files;
    File fileName;

    FakeFileSystem(File fileName) throws FileNotFoundException {
        Files = new RandomAccessFile[10];
        this.fileName = fileName;
        if (!fileName.exists()){
            throw new FileNotFoundException();
        }

    }
    /*
        @Open
        creates and record a RandomAccessFile in the array
     */
    @Override
    public int Open(String s) throws FileNotFoundException {

        for(int i =0; i < Files.length;i++){
            if(Files[i] == null){
                if(s != null && !s.isEmpty()){
                    try{
                        Files[i] = new RandomAccessFile(s,"r");

                    }catch (FileNotFoundException e){
                       throw new FileNotFoundException("Could not open File");
                    }

                } else{
                    try{
                        Files[i] =  new RandomAccessFile(fileName,"r");
                    } catch (FileNotFoundException e){
                        throw new FileNotFoundException("Could not open File");
                    }
                }
                return i;
            }
        }

        return -1;
    }
    /*
        @Close
        close the RandomAccessFile and clears The File array
     */
    @Override
    public void Close(int id) throws IOException {
        if(id >= 0 && id < Files.length) {
            Files[id].close();
            Files[id] = null;
        }

    }

    @Override
    public byte[] Read(int id, int size) throws IOException {
        byte[] bytes = new byte[size];
        //generating random bytes
        if(Files[id] != null) {
            Files[id].read(bytes);
            return bytes;
        }
        return null;
    }

    @Override
    public void Seek(int id, int to) throws IOException {
        if(Files[id] != null) {
            Files[id].seek(to);
        }
    }

    @Override
    public int Write(int id, byte[] data) throws IOException {
        if(Files[id] != null) {
            Files[id].write(data);
            return id;
        }
        return -1;
    }

    @Override
    public String DeviceName() {
        return "file";
    }
}
