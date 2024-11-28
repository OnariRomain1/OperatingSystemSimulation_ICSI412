public class HelloWorld extends UserlandProcess {

    public void run() {
        while (true) {
            System.out.println("Hello World!");
            cooperate();
            try {
                Thread.sleep(10); // sleep for 50 ms
            } catch (Exception e) { }



        }
    }
    void main(){


    }


}
