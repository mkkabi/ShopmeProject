package com.shopme.admin.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PasswordEncoderTest {

    @Test
    public void testEncodePassword(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPass = "nam2020";
        String encodedPass = passwordEncoder.encode(rawPass);
        System.out.println(encodedPass);

        boolean matches = passwordEncoder.matches(rawPass, encodedPass);
        assertThat(matches).isTrue();
    }
}
