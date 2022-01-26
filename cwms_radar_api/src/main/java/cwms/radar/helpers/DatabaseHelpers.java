package cwms.radar.helpers;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

public class DatabaseHelpers {

    /**
     * Handles making sure the connection session is set right.
     * @param conn an already opened connection.
     * @param request the http request object.
     * @return the same connection passed in.
     * @throws SQLException If there is issue setting the session or with the connection
     */
    public static Connection setSession(Connection conn,
                                        HttpServletRequest request) throws SQLException {
        String sessionKey = (String)request.getSession(false).getAttribute("SESSION_KEY");
        try (
            CallableStatement setSession = conn.prepareCall("call cwms_env.set_session_id(?)");
        ) {
            setSession.setString(1,sessionKey);
            setSession.execute();
            return conn;
        }
    }
}
