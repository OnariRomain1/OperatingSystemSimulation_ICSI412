import java.util.concurrent.Semaphore;
/*
   Process Class
 */
public abstract class Process implements Runnable {

  private boolean quantum_Expired;
  private final Thread thread;

  private final Semaphore semaphore;

Process(){
  thread = new Thread(this);
  semaphore = new Semaphore(0);
  quantum_Expired = true;
  thread.start();
}

  public Semaphore getSemaphore() {
    return semaphore;
  }


/*
    @RequestStop
    sets the boolean indicating that this process’ quantum has expired
 */

  void requestStop(){
    quantum_Expired = true;

  }
/*
  @Main

 */
  abstract void main();
  /*
      @isStopped
      indicates if the semaphore is 0
   */

  boolean isStopped(){
      if (semaphore.availablePermits() == 0){

        return true;
      }
      return false;
  }
/*
  @isDone
  true when the Java thread is not alive

 */
  boolean isDone(){
    if (!thread.isAlive()){
      return true;
    }
   return false;
  }
/*
    @start
    releases (increments) the semaphore, allowing this thread to run
 */
  void start(){

    semaphore.release();


  }
  /*
      @stop
      acquires (decrements) the semaphore, stopping this thread from running
   */
  void stop() throws InterruptedException {

      semaphore.acquire();


  }
  /*
      @run
      acquire the semaphore, then call main
   */
  public void run() {
    try {
      stop();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    main();
  }

  /*
      @Cooperate
      if the boolean is true, set the boolean to false and call OS.switchProcess()

   */
  void cooperate(){
    // System.out.println(quantum_Expired);
    if (quantum_Expired){
      quantum_Expired = false;

      OS.currentCall = CallType.SWITCH_PROCESS;
      //System.out.println("Switching User Processes" + OS.currentCall.name());
    }
  }
}
