package cwms.radar.security;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import io.javalin.http.Context;

import org.junit.jupiter.api.Test;



public class NoAuthorizorTest {

    @Test
    public void test_authorizor_always_throws_exception() {
        Context ctx = mock(Context.class);
        CwmsAuthorizer authorizer = new CwmsNoAuthorizer();

        assertThrows(CwmsAuthException.class, () -> {
            authorizer.can_perform(ctx);
        });
    }
}
