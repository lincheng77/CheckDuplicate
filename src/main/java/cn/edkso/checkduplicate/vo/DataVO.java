package cn.edkso.checkduplicate.vo;

import lombok.Data;

@Deprecated
@Data
public class DataVO {
    private String msg;

    public DataVO(String msg) {
        this.msg = msg;
    }
}
