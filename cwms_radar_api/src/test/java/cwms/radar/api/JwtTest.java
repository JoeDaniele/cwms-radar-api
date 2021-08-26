package cwms.radar.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtTest
{
	public static class MockUser
	{
		String name;
		String level;

		MockUser(String name, String level)
		{
			this.name = name;
			this.level = level;
		}
	}

	@Test
	public void testJwtStuff()
	{
		Algorithm algorithm = Algorithm.HMAC256("very_secret");

		JWTGenerator<MockUser> generator = (user, alg) -> {
			JWTCreator.Builder token = JWT.create()
					.withClaim("name", user.name)
					.withClaim("level", user.level);
			return token.sign(alg);
		};

		JWTVerifier verifier = JWT.require(algorithm).build();

		JWTProvider provider = new JWTProvider(algorithm, generator, verifier);

		MockUser mockUser = new MockUser("Mocky McMockface", "admin");

		String token = provider.generateToken(mockUser);

		assertNotNull(token);
		assertFalse(token.isEmpty());

		// token was:
		// eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsZXZlbCI6ImFkbWluIiwibmFtZSI6Ik1vY2t5IE1jTW9ja2ZhY2UifQ.xAj38PTIh1W3OGxXaDOHjCmvs7tHAFexWqCIEjdABj8
	}

	@Test
	public void testJwtDecodeStuff()
	{
		Algorithm algorithm = Algorithm.HMAC256("very_secret");
		JWTGenerator<MockUser> generator = (user, alg) -> {
			JWTCreator.Builder token = JWT.create()
					.withClaim("name", user.name)
					.withClaim("level", user.level);
			return token.sign(alg);
		};
		JWTVerifier verifier = JWT.require(algorithm).build();
		JWTProvider provider = new JWTProvider(algorithm, generator, verifier);


		String token2 = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsZXZlbCI6ImFkbWluIiwibmFtZSI6Ik1vY2t5IE1jTW9ja2ZhY2UifQ.xAj38PTIh1W3OGxXaDOHjCmvs7tHAFexWqCIEjdABj8";
		DecodedJWT decoded = (DecodedJWT) provider.validateToken(token2).get();
		assertNotNull(decoded);

		String name = decoded.getClaim("name").asString();
		assertEquals(name,"Mocky McMockface" );


	}

//	@Test
//	public void testVerify() throws JwkException
//	{
//		String token = "";
//		DecodedJWT jwt = JWT.decode(token);
//		JwkProvider provider = new UrlJwkProvider("https://dev-1x0a81f5.us.auth0.com/.well-known/jwks.json");
//		Jwk jwk = provider.get(jwt.getKeyId());
//		Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
//		algorithm.verify(jwt);
//
//	}



}
