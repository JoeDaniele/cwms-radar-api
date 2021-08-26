package mil.army.usace.hec;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.interfaces.DecodedJWT;
import cwms.radar.api.errors.RadarError;
import io.javalin.core.security.Role;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import javalinjwt.JavalinJWT;
import org.jetbrains.annotations.NotNull;

public class AccessManager implements io.javalin.core.security.AccessManager
{
	Logger logger = Logger.getLogger(AccessManager.class.getName());
	private final String userRoleClaim;
	private final Map<String, Role> rolesMapping;
	private final Role defaultRole;

	public AccessManager(String userRoleClaim, Map<String, Role> rolesMapping, Role defaultRole) {
		this.userRoleClaim = userRoleClaim;
		this.rolesMapping = rolesMapping;
		this.defaultRole = defaultRole;
	}

	private Role extractRole(Context context) {
		if (!JavalinJWT.containsJWT(context)) {
			logger.info("context did not contain jwt");
			return defaultRole;
		}

		DecodedJWT jwt = JavalinJWT.getDecodedFromContext(context);
		String userLevel = jwt.getClaim(userRoleClaim).asString();

		return Optional.ofNullable(rolesMapping.get(userLevel)).orElse(defaultRole);
	}

	@Override
	public void manage(@NotNull Handler handler, @NotNull Context context, Set<Role> permittedRoles) throws Exception {
		Role role = extractRole(context);
		logger.info("extracted " + role);
		if(role == null && permittedRoles.isEmpty()){
			logger.info("no roles specified and none provided");
			// When no roles specified, assume allowed.
			handler.handle(context);
		} else if (permittedRoles.contains(role)) {
			logger.info("role is in permitted " + role);
			handler.handle(context);
		} else {
			logger.info("role not in permitted " + role);
			context.status(HttpServletResponse.SC_UNAUTHORIZED).json(RadarError.notAuthorized());
			context.status(401).result("Unauthorized");
		}
	}
}
