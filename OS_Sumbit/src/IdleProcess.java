public class IdleProcess extends UserlandProcess{

    @Override
    public void run() {
        while (true){
            cooperate();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

   public void main(){

   }
}
