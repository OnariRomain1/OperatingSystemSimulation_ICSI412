public class Ping extends UserlandProcess{

    public void run() {
        while (true) {
            System.out.println("I am Ping! ");
            byte[] contents = new byte[1000];
            KernelMessage kernelMessage = new KernelMessage(2,OS.GetPidByName("Pong"),contents);
            OS.SendMessage(kernelMessage);
            if(OS.WaitForMessage() != null){
                OS.SendMessage(kernelMessage);
            }
            cooperate();
            try {
                Thread.sleep(10); // sleep for 50 ms
            } catch (Exception e) { }



        }
    }
    void main(){


    }
}
