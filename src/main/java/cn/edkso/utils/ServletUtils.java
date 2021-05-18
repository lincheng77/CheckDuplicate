package cn.edkso.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class ServletUtils {

    public static ServletRequestAttributes getRequestAttributes(){

        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    public static HttpServletRequest getRequest(){

        return getRequestAttributes().getRequest();
    }


    public static HttpSession getSession(){
        return getRequest().getSession();
    }

    public static HttpSession getSession(HttpServletRequest request){
        return request.getSession();
    }
}
