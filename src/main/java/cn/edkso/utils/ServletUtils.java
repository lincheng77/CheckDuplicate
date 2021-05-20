package cn.edkso.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ServletUtils {

    public static ServletRequestAttributes getRequestAttributes(){

        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    public static HttpServletRequest getRequest(){
        return getRequestAttributes().getRequest();
    }
    public static HttpServletResponse getResponse(){
        return getRequestAttributes().getResponse();
    }
    public static ServletContext getServletContext(){
        return getRequestAttributes().getRequest().getServletContext();
    }
    public static HttpSession getSession(){
        return getRequest().getSession();
    }

    public static HttpSession getSession(HttpServletRequest request){
        return request.getSession();
    }
}
