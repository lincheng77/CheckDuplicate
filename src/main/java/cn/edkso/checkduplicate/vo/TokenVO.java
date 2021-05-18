package cn.edkso.checkduplicate.vo;

import lombok.Data;

@Data
public class TokenVO {
    private String access_token;

    public TokenVO(String access_token) {
        this.access_token = access_token;
    }
}
