import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.LinkedList;

/*
    The OS Class
 */
public class OS {
    private static Kernel kernel;
    public static CallType currentCall;
    public static Priority priority;
    private static ArrayList<Object> parameters =  new ArrayList<>();;

    OS(){

    }

    /*
        @CreateProcess
        Makes a static entry for CreateProcess
        Resets parameters, Adds the new parameters
        and switches to the kernel then casts the return value
     */

    public static int CreateProcess(UserlandProcess up) throws InterruptedException {
        currentCall = CallType.CREATE_PROCESS;
        parameters.clear();
        parameters.add(up);
        //switch to kernel
        UserlandProcess currentProcess = null;
        if(kernel != null) {
            kernel.start();
            currentProcess = kernel.scheduler.getCurrentUserlandProcess();
            kernel.scheduler.getMessagePcbMap().put(GetPid(),kernel.scheduler.getCurrentPcb());

        }

        int kernelPID = 0;
       if (currentProcess != null) {
            currentProcess.stop();
       }

           while (currentProcess == null){
               if(currentProcess !=null ){
                   break;
               }
               try {

                   Thread.sleep(10); // sleep for 10 ms
               } catch (Exception e) { }

           }

        return kernelPID;
    }

    public static int CreateProcess(UserlandProcess up, Priority priority) throws InterruptedException {
        currentCall = CallType.CREATE_PROCESS;

        switch (priority){
            case REALTIME:
                break;
            case INTERACTIVE:
                break;
            case BACKGROUND:
                break;
            default:

        }
        parameters.clear();
        parameters.add(up);
        //switch to kernel
        UserlandProcess currentProcess = null;
        if(kernel != null) {
            kernel.start();
            currentProcess = kernel.scheduler.getCurrentUserlandProcess();
            kernel.scheduler.getMessagePcbMap().put(GetPid(),kernel.scheduler.getCurrentPcb());
        }

        int kernelPID = 0;
        if (currentProcess != null) {
            currentProcess.stop();
        }

        while (currentProcess == null){
            if(currentProcess !=null ){
                break;
            }
            try {

                Thread.sleep(10); // sleep for 10 ms
            } catch (Exception e) { }

        }

        System.out.println("KernelId is " + kernelPID);

        return kernelPID;
    }

    public static void CreateDevice(String deviceName) throws InterruptedException {
        currentCall = CallType.CREATE_DEVICE;
        parameters.clear();
        parameters.add(deviceName);

        // Switch to kernel to handle device creation
        if (kernel != null) {
            kernel.start();
            PCB currentPcb = kernel.scheduler.getCurrentPcb();
            currentPcb.setDeviceName(deviceName);


        }
    }
    /*
        @Startup
        Creates the kernel and calls createProcess on init and idle process
     */
    public static void Startup(UserlandProcess init) throws InterruptedException {

        kernel = new Kernel();

        IdleProcess idleProcess = new IdleProcess();
        kernel.scheduler.CreateProcess(init);
        kernel.scheduler.CreateProcess(idleProcess);

    }


    public static void Sleep(int milliseconds) throws InterruptedException {
        currentCall = CallType.SLEEP_PROCESS;
        parameters.clear();
        UserlandProcess currentProcess = kernel.scheduler.currentUserlandProcess;
        parameters.add(currentProcess);
        //switch to kernel

        if(kernel != null) {
            kernel.start();
            currentProcess = kernel.scheduler.getCurrentUserlandProcess();
        }

        int kernelPID = 0;
        if (currentProcess != null) {
            currentProcess.stop();
        }

        while (currentProcess == null){
            if(currentProcess !=null ){
                break;
            }
            try {

                Thread.sleep(10); // sleep for 10 ms
            } catch (Exception e) { }

        }

    }

/*
    @SendMessage
    uses the copy constructor to make a copy of the original message
    populates the sender’s pid
    finds the target’s PCB
    adds the message to the message queue
    if this PCB is waiting for a message restores it to the proper queue
 */
public static void SendMessage(KernelMessage km){
        currentCall = CallType.SEND_MESSAGE;
        KernelMessage messageCopy = new KernelMessage(km);
        if(kernel!= null){
            messageCopy.SetSendPid(GetPid());
            PCB currentPCB;
            if ((currentPCB = kernel.scheduler.currentPcb) != null) {
                currentPCB.Message = messageCopy;
                   PCB target = kernel.scheduler.getMessagePcbMap().get(currentPCB.Message.targetPid);
                if(target != null) {
                    kernel.scheduler.currentPcb.MessageQueue.add(target.Message);
                    System.out.println("ProcessName: " + currentPCB.ProcessName + " " + target.Message.ToString());

                }
                   if (WaitForMessage()!= null){

                    switch (kernel.scheduler.currentPriority) {
                        case INTERACTIVE:

                            kernel.scheduler.getWaitForMessagePcbs().remove(currentPCB.processid,currentPCB);
                            kernel.scheduler.getProcessPriorites().InteractiveProcesses.add(kernel.scheduler.currentPcb);
                        case REALTIME:
                            kernel.scheduler.getWaitForMessagePcbs().remove(currentPCB.processid,currentPCB);
                            kernel.scheduler.getProcessPriorites().RealTimeProcesses.add(kernel.scheduler.currentPcb);

                        case BACKGROUND:
                            kernel.scheduler.getWaitForMessagePcbs().remove(currentPCB.processid,currentPCB);
                            kernel.scheduler.getProcessPriorites().BackgroundProcesses.add(kernel.scheduler.currentPcb);
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
    public static  KernelMessage WaitForMessage(){
        currentCall = CallType.WAIT_FOR_MESSAGE;
        if(kernel!=null) {
            if (kernel.scheduler.currentPcb != null) {
                if (kernel.scheduler.currentPcb.Message != null) {
                    System.out.println(kernel.scheduler.currentPcb.Message.ToString());
                    return kernel.scheduler.currentPcb.MessageQueue.poll();
                } else {
                    PCB currentPCB = kernel.scheduler.getCurrentPcb();
                    if (currentPCB != null) {
                        switch (kernel.scheduler.currentPriority) {
                            case INTERACTIVE:
                                kernel.scheduler.getProcessPriorites().InteractiveProcesses.remove(kernel.scheduler.currentPcb);
                                kernel.scheduler.getWaitForMessagePcbs().put(currentPCB.processid, currentPCB);
                                break;
                            case REALTIME:
                                kernel.scheduler.getProcessPriorites().RealTimeProcesses.remove(kernel.scheduler.currentPcb);
                                kernel.scheduler.getWaitForMessagePcbs().put(currentPCB.processid, currentPCB);
                                break;
                            case BACKGROUND:
                                kernel.scheduler.getProcessPriorites().BackgroundProcesses.remove(kernel.scheduler.currentPcb);
                                kernel.scheduler.getWaitForMessagePcbs().put(currentPCB.processid, currentPCB);
                                break;
                        }
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
    public static  int GetPid(){
        if (kernel != null) {
            if (kernel.scheduler.currentPcb != null){
                return kernel.scheduler.currentPcb.processid;
        }
        }
        return -1;
    }
    /*
        @getPidByName
        returns the pid of a process with that name
     */
    static int GetPidByName(String processName){

        if(kernel!= null) {


                if (kernel.scheduler.currentPcb!= null) {
                       if (kernel.scheduler.currentPriority != null) {
                        switch (kernel.scheduler.currentPriority) {
                            case INTERACTIVE:
                                LinkedList<PCB> interactive = kernel.scheduler.getProcessPriorites().InteractiveProcesses;
                                for (int i = 0; i < interactive.size(); i++) {
                                    if (interactive.get(i).getProcessName().equals(processName)) {
                                        return interactive.get(i).processid;
                                    }
                                }
                                break;

                            case REALTIME:
                                LinkedList<PCB> realTime = kernel.scheduler.getProcessPriorites().RealTimeProcesses;
                                for (int i = 0; i < realTime.size(); i++) {
                                    if (realTime.get(i).getProcessName().equals(processName)) {
                                        return realTime.get(i).processid;
                                    }
                                }
                                break;

                            case BACKGROUND:
                                LinkedList<PCB> background = kernel.scheduler.getProcessPriorites().BackgroundProcesses;
                                for (int i = 0; i < background.size(); i++) {
                                    if (background.get(i).getProcessName().equals(processName)) {
                                        return background.get(i).processid;
                                    }
                                }
                                break;

                            default:
                                interactive = kernel.scheduler.getProcessPriorites().InteractiveProcesses;
                                for (int i = 0; i < interactive.size(); i++) {
                                    if (interactive.get(i).getProcessName().equals(processName)) {
                                        return interactive.get(i).processid;
                                    }
                                }
                                break;
                        }
                    }
                }


        }
           return -1;

    }



}
