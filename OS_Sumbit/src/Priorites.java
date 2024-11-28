import java.util.LinkedList;

public class Priorites {

    LinkedList<PCB> RealTimeProcesses;
    LinkedList<PCB> InteractiveProcesses;
    LinkedList<PCB> BackgroundProcesses;

    Priorites(){
        RealTimeProcesses = new LinkedList<>();
        InteractiveProcesses = new LinkedList<>();
        BackgroundProcesses = new LinkedList<>();

    }



    LinkedList<PCB> GetRealTimeProcesses(){
        return RealTimeProcesses;
    }

    LinkedList<PCB> GetInteractiveProcesses(){
        return InteractiveProcesses;
    }

    LinkedList<PCB> GetBackgroundProcesses(){
        return BackgroundProcesses;
    }

}
