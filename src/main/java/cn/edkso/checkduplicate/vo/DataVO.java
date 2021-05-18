package cn.edkso.checkduplicate.vo;

import lombok.Data;

@Data
public class DataVO {
    private String msg;

    public DataVO(String msg) {
        this.msg = msg;
    }
}
