package ru.vinpin.mainapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MainAppApplicationTests {

    @Test
    void tests() {
        if (2 != 2){
            throw new AssertionError();
        }
        else return;
    }

}
