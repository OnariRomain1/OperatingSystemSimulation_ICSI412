import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class Kernel extends Process implements Device{

    Scheduler scheduler;
    VFS vfs;

    Kernel(){
        this.scheduler = new Scheduler();
        vfs = new VFS();
    }

    @Override
    void main() {

    }


    @Override
    /*
        @run
        infite loop that checks the callType
        then runs the specific function
        it starts the next process and stops current process

     */
    public void run() {
        while (true) {
            switch (OS.currentCall) {
                case CallType.CREATE_PROCESS:

                    try {
                        if (OS.priority != null) {
                            switch (OS.priority) {
                                case REALTIME:
                                    OS.CreateProcess(scheduler.currentUserlandProcess, Priority.REALTIME);
                                    break;
                                case INTERACTIVE:
                                    OS.CreateProcess(scheduler.currentUserlandProcess, Priority.BACKGROUND);
                                    break;
                                case BACKGROUND:
                                    OS.CreateProcess(scheduler.currentUserlandProcess, Priority.INTERACTIVE);
                                    break;
                                default:
                                    OS.priority = Priority.INTERACTIVE;
                                    OS.CreateProcess(scheduler.currentUserlandProcess, Priority.BACKGROUND);
                                    break;

                            }
                            if (scheduler != null && scheduler.currentUserlandProcess != null) {
                                OS.CreateProcess(scheduler.currentUserlandProcess);
                            }
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }


                    break;
                case CallType.SWITCH_PROCESS:
                    if (scheduler != null) {
                       scheduler.SwitchProcess();

                    }
                    break;

                case CallType.CREATE_DEVICE:
                    if (scheduler != null) {
                        try {
                            Open(scheduler.currentPcb.DeviceName);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    }
                case SEND_MESSAGE:
                    if(scheduler != null){
                        if(scheduler.currentPcb != null) {
                            SendMessage(scheduler.currentPcb.Message);
                        }
                    }
                case WAIT_FOR_MESSAGE:
                    if(scheduler != null){
                        if(scheduler.currentPcb != null) {
                            WaitForMessage();
                        }
                    }

            }

            if (scheduler != null && scheduler.currentUserlandProcess != null) {
                scheduler.getCurrentUserlandProcess().start();
            }
            try {
                this.stop();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

        void Exit() throws InterruptedException {
            scheduler.getCurrentUserlandProcess().stop();
            Random random = new Random();
            int randomNumber = random.nextInt(10);
            if( scheduler.getWaitForMessagePcbs().get(scheduler.currentPcb.processid) !=null){
                scheduler.currentPcb.userprocess.stop();
            }
            if (randomNumber < 6 && randomNumber > 3) {
                //RealTime Process
                OS.priority =Priority.REALTIME;

            } else if (randomNumber < 3 && randomNumber > 1) {
              OS.priority =Priority.INTERACTIVE;
            } else {
                OS.priority =Priority.BACKGROUND;

            }
        }


        /*
        @Open finds an empty entry in the currentRunning PCB's Id array and
        fills it with the return value from the vfs's Open
        returns -1 if it fails
    */
    @Override
    public int Open(String s) throws FileNotFoundException {

        for(int i =0; i < scheduler.currentPcb.Ids.length;i++){
            if(scheduler.currentPcb.Ids[i] == -1){
                int vfsId = vfs.Open(s);
                if(vfsId == -1){
                    return -1;
                }
                scheduler.currentPcb.Ids[i] = vfsId;
                return i;
            }
        }

        return -1;
    }

    @Override
    public void Close(int id) throws IOException {
        vfs.Close(scheduler.currentPcb.Ids[id]);
        scheduler.currentPcb.Ids[id] = -1;
    }

    @Override
    public byte[] Read(int id, int size) throws IOException {
        return vfs.Read(scheduler.currentPcb.Ids[id],size);

    }

    @Override
    public void Seek(int id, int to) throws IOException {
        vfs.Seek(scheduler.currentPcb.Ids[id], to);
    }

    @Override
    public int Write(int id, byte[] data) throws IOException {
        return vfs.Write(id,data);
    }

    @Override
    public String DeviceName() {
        return null;
    }


    /*
    @SendMessage
    uses the copy constructor to make a copy of the original message
    populates the sender’s pid
    finds the target’s PCB
    adds the message to the message queue
    if this PCB is waiting for a message restores it to the proper queue
 */
    void SendMessage(KernelMessage km){

        KernelMessage messageCopy = new KernelMessage(km);
        if(scheduler != null){
            messageCopy.SetSendPid(GetPid());
            PCB currentPCB;
            if ((currentPCB = scheduler.currentPcb) != null) {
                currentPCB.Message = messageCopy;
                PCB target = scheduler.getMessagePcbMap().get(currentPCB.Message.targetPid);
                if(target != null) {
                    scheduler.currentPcb.MessageQueue.add(target.Message);
                    System.out.println("ProcessName: " + currentPCB.ProcessName + " " + target.Message.ToString());
                }

                if (WaitForMessage()!= null){

                    switch (scheduler.currentPriority) {
                        case INTERACTIVE:

                            scheduler.getWaitForMessagePcbs().remove(currentPCB.processid,currentPCB);
                            scheduler.getProcessPriorites().InteractiveProcesses.add(scheduler.currentPcb);
                        case REALTIME:
                           scheduler.getWaitForMessagePcbs().remove(currentPCB.processid,currentPCB);
                           scheduler.getProcessPriorites().RealTimeProcesses.add(scheduler.currentPcb);

                        case BACKGROUND:
                            scheduler.getWaitForMessagePcbs().remove(currentPCB.processid,currentPCB);
                            scheduler.getProcessPriorites().BackgroundProcesses.add(scheduler.currentPcb);
                    }

                }
            }
        }
    }
    /*
       @WaitForMessage
       checks to see if the current process has a message
       if so, takes it off of the queue and returns it
       If not takes the process off of its specified priority queue
       and adds the process to a Wait List
    */
    KernelMessage WaitForMessage(){

        if(scheduler!=null) {
            if (scheduler.currentPcb.Message !=null){
                return scheduler.currentPcb.MessageQueue.poll();
            } else {
                PCB currentPCB = scheduler.getCurrentPcb();
                if (currentPCB != null) {
                    switch (scheduler.currentPriority) {
                        case INTERACTIVE:
                            scheduler.getProcessPriorites().InteractiveProcesses.remove(scheduler.currentPcb);
                            scheduler.getWaitForMessagePcbs().put(currentPCB.processid,currentPCB);
                            break;
                        case REALTIME:
                            scheduler.getProcessPriorites().RealTimeProcesses.remove(scheduler.currentPcb);
                            scheduler.getWaitForMessagePcbs().put(currentPCB.processid,currentPCB);
                            break;
                        case BACKGROUND:
                            scheduler.getProcessPriorites().BackgroundProcesses.remove(scheduler.currentPcb);
                            scheduler.getWaitForMessagePcbs().put(currentPCB.processid,currentPCB);
                            break;
                    }
                }
            }
        }
        return null;
    }

    /*
           @GetPid
           returns the current process pid
        */
    int GetPid(){

        return scheduler.currentPcb.processid;

    }

    /*
            @getPidByName
            returns the pid of a process with that name
         */
    int GetPidByName(String processName){

        if(scheduler!= null) {

            if (scheduler.currentPcb!= null) {
                //kernel.scheduler.currentPcb.ProcessName = processName;
                //    System.out.println("Name Not Found: " + kernel.scheduler.currentPcb.getProcessName() + " Does Not Match " + processName);
                switch (scheduler.currentPriority) {
                    case INTERACTIVE:
                        LinkedList<PCB> interactive = scheduler.getProcessPriorites().InteractiveProcesses;
                        for (int i = 0; i < interactive.size(); i++) {
                            if (interactive.get(i).getProcessName().equals(processName)) {
                                return interactive.get(i).processid;
                            }
                        }
                        break;

                    case REALTIME:
                        LinkedList<PCB> realTime = scheduler.getProcessPriorites().RealTimeProcesses;
                        for (int i = 0; i < realTime.size(); i++) {
                            if (realTime.get(i).getProcessName().equals(processName)) {
                                return realTime.get(i).processid;
                            }
                        }
                        break;

                    case BACKGROUND:
                        LinkedList<PCB> background =  scheduler.getProcessPriorites().BackgroundProcesses;
                        for (int i = 0; i < background.size(); i++) {
                            if (background.get(i).getProcessName().equals(processName)) {
                                return background.get(i).processid;
                            }
                        }
                        break;



                }
            }

        }
        return -1;

    }







}



