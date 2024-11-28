public class Pong extends UserlandProcess{

    public void run() {
        while (true) {
            System.out.println("I am Pong! ");
            byte[] contents = new byte[1000];
            KernelMessage kernelMessage = new KernelMessage(1,OS.GetPidByName("Ping"),contents);
            if(OS.WaitForMessage()!= null){
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
