package cwms.radar.security;

import io.javalin.http.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CwmsAuthorizer {

    public abstract void can_perform(HttpServletRequest request,
                                     HttpServletResponse response) throws CwmsAuthException;

    public void can_perform(Context context) throws CwmsAuthException {
        can_perform(context.req,context.res);
    }




}
