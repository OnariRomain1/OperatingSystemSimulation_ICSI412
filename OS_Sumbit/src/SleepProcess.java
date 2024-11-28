public class SleepProcess extends UserlandProcess {
    @Override
    public void run() {
        try {
            while (true) {
                try {
                    System.out.println("Started SleepingProcess...");
                    Thread.sleep(10); // sleep for 10 ms
                    break;
                }catch(Exception e){

                }
            }




        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}