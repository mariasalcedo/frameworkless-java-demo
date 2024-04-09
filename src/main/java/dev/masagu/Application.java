package dev.masagu;

import java.io.IOException;

public class Application {

    // --enable-preview --add-modules jdk.incubator.concurrent
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(TravelInfo.getInfo("Ingolstadt", "USD").toString());
    }
}