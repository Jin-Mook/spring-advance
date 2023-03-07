package com.kt.myrestapi;

import org.junit.jupiter.api.Test;

import java.util.List;

public class LambdaTest {

    @Test
    void consumer() {
        List<String> list = List.of("aa", "bb", "cc");// Immutable List 값 추가 x
        list.forEach(s -> System.out.println("s = " + s));
        list.forEach(System.out::println);
    }

    @Test
    void runnable() {
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("LambdaTest.run class");
            }
        });
        t1.start();

        Thread t2 = new Thread(() -> System.out.println("Lambda Expression"));
        t2.start();
    }


}
