public class LongRunningProcess  extends UserlandProcess{

    @Override
    public void run() {
        System.out.println("Long-running real-time process started.");
        // Simulate a long-running task
        for (int i = 0; i < 1000000; i++) {
            // Simulate heavy computation or work
            if (i % 100000 == 0) {
                System.out.println("Real-time process still running... " + i);
            }

        }
        System.out.println("Long-running real-time process Done.");
    }
}
