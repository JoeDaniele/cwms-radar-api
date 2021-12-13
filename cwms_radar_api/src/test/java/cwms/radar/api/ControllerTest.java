package cwms.radar.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockFileDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

@TestInstance(Lifecycle.PER_CLASS)
public class ControllerTest {
    protected Connection conn = null;
    protected PolicyFactory sanitizer = new HtmlPolicyBuilder()
                                            .disallowElements("<script>")
                                            .toFactory();

    /**
     * Another way to get a test connection.
     * @return An opened mock database connection
     * @throws SQLException issue with the mock file
     * @throws IOException can't read/find the mock file
     */
    public Connection getTestConnection() throws SQLException, IOException {
        if (conn == null) {
            InputStream stream = ControllerTest.class.getResourceAsStream("/ratings_db.txt");
            assertNotNull(stream);
            this.conn = new MockConnection(
                                    new MockFileDatabase(stream
                                    )
                        );
            assertNotNull(this.conn, "Connection is null; something has gone wrong with the fixture setup");
        }
        return conn;
    }

    /**
     * Loads a resource from the classpath.
     * @param fileName filename on the classpath
     * @return the contents of the file
     */
    public String loadResourceAsString(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(fileName);
        assertNotNull(stream, "Could not load the resource as stream:" + fileName);
        Scanner scanner = new Scanner(stream);
        String contents = scanner.useDelimiter("\\A").next();
        scanner.close();
        return contents;
    }

    /**
     * Create the in memory test database.
     * @throws SQLException problems with the file context
     * @throws IOException unable to open file
     */
    @BeforeAll
    public void baseLineDbMocks() throws SQLException, IOException {
        InputStream stream = ControllerTest.class.getResourceAsStream("/ratings_db.txt");
        assertNotNull(stream);
        this.conn = new MockConnection(
                                new MockFileDatabase(stream
                                )
                    );
        assertNotNull(this.conn, "Connection is null; something has gone wrong with the fixture setup");
    }
}
