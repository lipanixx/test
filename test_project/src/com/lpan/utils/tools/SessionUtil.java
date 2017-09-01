package com.lpan.utils.tools;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/9/1.
 */
public class SessionUtil {

    public static void setObject(HttpServletRequest request, String key, Object obj, Integer sec){
        HttpSession session = request.getSession();
        session.setAttribute(key, obj);
        session.setMaxInactiveInterval(sec);
    }

    public static Object getObject(HttpServletRequest request, String key){
        HttpSession session = request.getSession();
        Object object = session.getAttribute(key);
        return object;
    }

}
