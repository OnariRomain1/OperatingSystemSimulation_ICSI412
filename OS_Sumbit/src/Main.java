/*
    Created By: Onari Romain
    This project is a simulation of how an operating system works for
    my ICSI 412 Operating systems class. It's incomplete and will be finished by the end of my fall 2024 Semester
    The most recent feature added is messages, which  are bundles of data that are transmitted between two processes.
 */
import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {

        Init init = new Init();
        OS.Startup(init);
        OS.Startup(new Ping());
        OS.Startup(new Pong());

    }
}
