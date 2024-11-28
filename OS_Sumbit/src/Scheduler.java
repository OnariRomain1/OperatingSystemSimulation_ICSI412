import java.io.IOException;
import java.time.Clock;
import java.util.*;

public class Scheduler {
    private LinkedList<UserlandProcess> sleepingProcesses;
    private Timer timer;
    private Clock clock;
    private Priorites processPriorites;
    private HashMap<Integer,PCB> MessagePcbMap;
    private HashMap<Integer,PCB> WaitForMessagePcbs;
    public UserlandProcess currentUserlandProcess;
    public Priority currentPriority;
    public PCB currentPcb;
    public Kernel kernel;
    /*
        Scheduler
        schedules an interrupt for every 250ms
        the interrupt stops the currently running process
     */
    Scheduler(){
        sleepingProcesses = new LinkedList<UserlandProcess>();
        currentUserlandProcess = null;
        timer = new Timer();
        processPriorites = new Priorites();
        MessagePcbMap = new HashMap<>();
        WaitForMessagePcbs = new HashMap<>();
        currentPriority = Priority.INTERACTIVE;
        Date date = new Date();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                if (currentPcb != null)
                    if (currentPcb.userprocess != null) {
                        if (!currentPcb.userprocess.isDone()) {
                            currentPcb.userprocess.requestStop();

                        }
                    }

            }

        };

        timer.scheduleAtFixedRate(timerTask, date, 250);
    }

    public HashMap<Integer, PCB> getMessagePcbMap() {
        return MessagePcbMap;
    }

    public UserlandProcess getCurrentUserlandProcess(){
        return currentUserlandProcess;
    }

    public Priorites getProcessPriorites(){
        return processPriorites;
    }

    public PCB getCurrentPcb(){
        return currentPcb;
    }

    public HashMap<Integer,PCB> getWaitForMessagePcbs() {
        return WaitForMessagePcbs;
    }

    /*
            @CreateProcess
            adds the userland process to the list of processes and,
            if nothing else is running, calls switchProcess()
         */
    public int CreateProcess(UserlandProcess up){
        PCB pcb;
        if (up != null){
             pcb = new PCB(up);
             currentPcb = pcb;
            currentPriority = Priority.INTERACTIVE;
            processPriorites.InteractiveProcesses.add(pcb);
            return pcb.processid;
        }

       if (up == null){
               SwitchProcess();

       }
        return 0;

    }

    /*
        CreateProcess
        Adds the userland to the specified list based on priority and returns its process ID
     */
    public int CreateProcess(UserlandProcess up, Priority priority){
        PCB pcb;
        if (up != null){
            pcb = new PCB(up);
            currentPcb = pcb;
            switch (priority){
                case REALTIME:
                    processPriorites.RealTimeProcesses.add(pcb);
                    currentPriority = Priority.REALTIME;
                    return pcb.GetProcessId();
                case BACKGROUND:
                    processPriorites.BackgroundProcesses.add(pcb);
                    currentPriority = Priority.BACKGROUND;
                    return pcb.GetProcessId();
                default:
                    processPriorites.InteractiveProcesses.add(pcb);
                    currentPriority = Priority.INTERACTIVE;
                    return pcb.GetProcessId();
                }

            }

        if (up == null){
               SwitchProcess();
        }

        return 0;

    }

    /*
        @SwtichProcess
        When a process is stopped it gets put in its corresponding queue and then
        calcualtes the probability for the next process to run based on priority
     */
    public void SwitchProcess(){
        if(currentPcb !=null) {
            if (currentPcb.userprocess != null) {
                if(currentPcb.userprocess.isDone()){
                    MessagePcbMap.remove(currentPcb.processid);
                }
                if (currentPcb.userprocess.isStopped()) {
                    int demotion = 0;
                    PCB currentPCB;
                    //probably the wrong way to terminate

                    switch (currentPriority) {
                        case REALTIME:
                            currentPCB = processPriorites.RealTimeProcesses.poll();
                            processPriorites.RealTimeProcesses.addLast(currentPCB);
                        case BACKGROUND:
                            currentPCB = processPriorites.BackgroundProcesses.poll();
                            processPriorites.BackgroundProcesses.addLast(currentPCB);
                        default:
                            currentPCB = processPriorites.InteractiveProcesses.poll();
                            processPriorites.InteractiveProcesses.addLast(currentPCB);

                    }

                    Random random = new Random();
                    int randomNumber = random.nextInt(10);
                    demotion++;


                    if (randomNumber < 6 && randomNumber > 3) {
                        //RealTime Process
                        if (demotion > 5) {
                            demotion = 0;
                            currentPriority = Priority.INTERACTIVE;
                        }
                        PCB realTimeProcess = processPriorites.RealTimeProcesses.getFirst();
                        currentPcb.userprocess = realTimeProcess.userprocess;
                    } else if (randomNumber < 3 && randomNumber > 1) {
                        PCB interactiveProcess = processPriorites.InteractiveProcesses.getFirst();
                        currentPcb.userprocess = interactiveProcess.userprocess;
                        if (demotion > 5) {
                            demotion = 0;
                            currentPriority = Priority.INTERACTIVE;
                        }
                    } else {
                        PCB BackgroundProcess = processPriorites.BackgroundProcesses.getFirst();
                        currentPcb.userprocess = BackgroundProcess.userprocess;
                        currentPriority = Priority.BACKGROUND;

                    }

                }
            }
        }
        }



    public void Sleep(int milliseconds){
        UserlandProcess userProcess = currentPcb.userprocess;
        sleepingProcesses.add(userProcess);

        long WaitTime = clock.millis() + milliseconds;
        if (clock.millis() >= WaitTime){
            if (currentPcb!= null) {
                currentPcb.userprocess = sleepingProcesses.poll();
            }
        }





    }

    /*
           @GetPid
           returns the current process pid
        */
    int GetPid(){
        if (kernel != null) {
            return currentPcb.processid;
        }
        return -1;
    }
    /*
            @getPidByName
            returns the pid of a process with that name
         */
    int GetPidByName(String processName){

            if (currentPcb!= null) {
                     switch (currentPriority) {
                    case INTERACTIVE:
                        LinkedList<PCB> interactive =getProcessPriorites().InteractiveProcesses;
                        for (int i = 0; i < interactive.size(); i++) {
                            if (interactive.get(i).getProcessName().equals(processName)) {
                                return interactive.get(i).processid;
                            }
                        }
                        break;

                    case REALTIME:
                        LinkedList<PCB> realTime = getProcessPriorites().RealTimeProcesses;
                        for (int i = 0; i < realTime.size(); i++) {
                            if (realTime.get(i).getProcessName().equals(processName)) {
                                return realTime.get(i).processid;
                            }
                        }
                        break;

                    case BACKGROUND:
                        LinkedList<PCB> background =  getProcessPriorites().BackgroundProcesses;
                        for (int i = 0; i < background.size(); i++) {
                            if (background.get(i).getProcessName().equals(processName)) {
                                return background.get(i).processid;
                            }
                        }
                        break;



                }
            }




        return -1;

    }


}
