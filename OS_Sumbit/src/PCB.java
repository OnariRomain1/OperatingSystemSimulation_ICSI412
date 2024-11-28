import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class PCB extends Kernel{


    Thread thread;
    static int nextpid;
    int processid;
    UserlandProcess userprocess;
    int[] Ids;
    String ProcessName;
    String DeviceName;
    KernelMessage Message;
    LinkedList<KernelMessage> MessageQueue;



    PCB(UserlandProcess up){
        thread = new Thread(this);
        Ids = new int[10];
        Arrays.fill(Ids, -1);

        MessageQueue = new LinkedList<>();
        DeviceName = "";
        ProcessName = up.getClass().getSimpleName();
        userprocess = up;
        Message = null;
        Random random = new Random();
        processid = random.nextInt(1000 - 1) + 1;
    }

    public String getProcessName() {
        return ProcessName;
    }
    public void setProcessName(String ProcessName){
        this.ProcessName = ProcessName;
    }


    LinkedList<KernelMessage> GetMessageQueue(){
        return MessageQueue;
}
    void setDeviceName(String DeviceName){
        this.DeviceName = DeviceName;
    }

   int GetProcessId(){
        return processid;
    }

String GetProcessName(){
        return ProcessName;
}
void SetProcessName(String ProcessName){
        this.ProcessName = ProcessName;
}
    void stop() throws InterruptedException {
        if(userprocess != null) {
            userprocess.stop();
            while (!userprocess.isStopped()) {
                if (userprocess.isStopped()) {
                    break;
                }
                Thread.sleep(10);
            }
        }
    }
    boolean isDone(){
       return userprocess.isDone();
    }

    void start(){
        userprocess.start();
    }

}
