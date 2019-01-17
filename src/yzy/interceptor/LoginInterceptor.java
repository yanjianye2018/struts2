package yzy.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import yzy.domain.User;


import java.util.Map;

/**
 * @author yzy
 */
public class LoginInterceptor extends MethodFilterInterceptor {
    @Override
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {
        //获取session中的user
        ActionContext actionContext = ActionContext.getContext();
        Map<String, Object> map = actionContext.getSession();
        User user = (User) map.get("user");
        if (user == null) {
            //如果为null说明未登录

            ActionSupport action = (ActionSupport) actionInvocation.getAction();
            action.addActionError("您还没有登录!");
            return "login";
        }
        return actionInvocation.invoke();//放行
    }
}
