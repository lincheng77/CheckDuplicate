package cn.edkso.checkduplicate.enums;

public enum ResultEnum {

    PRODUCT_NOT_EXIST(1,"该商品不存在"),
    PRODUCT_STOCK_ERROR(2,"库存不足"),
    REGISTER_ERROR(411, "注册失败"),
    LOGIN_ERROR(421, "登录失败，账号或密码错误"),
    NOT_LOGGED_IN(422, "未登录！"),
    PARAMS_ERROR_OR_SYSTEM_EXCEPTION(432, "参数错误或系统异常"),
    UPLOAD_ERROR(441, "上传出现异常"),
    ;
    private Integer code;
    private String message;

    ResultEnum(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
