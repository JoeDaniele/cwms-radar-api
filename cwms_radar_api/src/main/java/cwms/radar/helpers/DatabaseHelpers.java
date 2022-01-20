package cwms.radar.helpers;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

public class DatabaseHelpers {
    /**
     * Appropriately set the session id within the CWMS Database itself.
     * @param conn an open JDBC connection
     * @param request the HttpServletRequest so appropriate parameters can be retrieved
     * @return the connection that was passed in.
     * @throws SQLException anything fails with the connection.
     *     Also, if bad credentials are passed in.
     */
    @SuppressWarnings("checkstyle:linelength")
    public static Connection setSession(Connection conn, HttpServletRequest request) throws SQLException {
        String sessionKey = (String)request.getSession(false).getAttribute("SESSION_KEY");
        try (CallableStatement setSession = conn.prepareCall("call cwms_env.set_session_id(?)");) {
            setSession.setString(1,sessionKey);
            setSession.execute();
            return conn;
        }
    }
}
