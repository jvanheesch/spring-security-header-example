package com.github.jvanheesch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ApplicationTest {
    // TODO
    @WithMockUser
    @Test
    void test() {
        System.out.println("ApplicationTest.test");
    }
}
