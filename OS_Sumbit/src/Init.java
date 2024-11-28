/*
     The Init Class
     Manages what happens on startup
 */
public class Init extends UserlandProcess {

    @Override
    public void run() {

        try {
            OS.CreateProcess(new Pong());
            OS.CreateProcess(new Ping());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    @Override
    public void main(){

    }
}
