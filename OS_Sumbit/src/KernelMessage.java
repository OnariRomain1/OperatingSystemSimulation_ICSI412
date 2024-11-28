import java.util.HashMap;
import java.util.LinkedList;

public class KernelMessage {

    int SendPid;
    int targetPid;
    byte[] data;
    HashMap<Integer,PCB> targetPcb;
    KernelMessage(){}
    KernelMessage(int SendPid, int targetPid, byte[] data){
        this.SendPid = SendPid;
        this.targetPid = targetPid;
        this.data = new byte[1000];
        targetPcb = new HashMap<>();
    }

    KernelMessage(KernelMessage Message){
        this(Message.SendPid, Message.targetPid, Message.data);
    }

    void SetSendPid(int SendPid){
        this.SendPid = SendPid;
    }

    String ToString(){
        String Message;
        Message = "From SenderPid :" + SendPid +", " +
        "To TargetPid : "+ targetPid +
        " Contents: " +data.length;

        return Message;
    }




}
