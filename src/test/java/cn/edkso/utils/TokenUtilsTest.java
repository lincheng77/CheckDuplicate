package cn.edkso.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenUtilsTest {


    @Test
    public void tokenUtilsTest(){
        String username ="zhangsan";
        String password = "123";
        String token = TokenUtils.token(username,password);
        System.out.println(token);
        boolean b = TokenUtils.verify("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwYXNzd29yZCI6IjEyMyIsImV4cCI6MTYyMTUwNjU1NiwidXNlcm5hbWUiOiJ6aGFuZ3NhbiJ9.JrPQNocKeIIQXkR10Dx15ZHemHzQV-3qNLNeH_jBiVY");
        System.out.println(b);
    }
}