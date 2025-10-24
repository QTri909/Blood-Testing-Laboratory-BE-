package sum25.group03.iamservice.security;

import com.auth0.jwk.InvalidPublicKeyException;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwk.SigningKeyNotFoundException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.concurrent.TimeUnit;


@Component
public class CognitoTokenVerifier {

    private final String region = "ap-southeast-2"; // AWS Cognito region
    private final String userPoolId = "ap-southeast-2_7UGXSOgJj"; // user pool id của bạn
    private final String expectedIssuer;
    private final JwkProvider provider;

    public CognitoTokenVerifier() throws Exception {
        expectedIssuer = String.format("https://cognito-idp.%s.amazonaws.com/%s", region, userPoolId);
        String jwkUrl = expectedIssuer + "/.well-known/jwks.json";

        provider = new JwkProviderBuilder(new URL(jwkUrl))
                .cached(10, 24, TimeUnit.HOURS)
                .rateLimited(10, 1, TimeUnit.MINUTES)
                .build();
    }

    public DecodedJWT verify(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);

            // Lấy JWK public key từ kid
            Jwk jwk = provider.get(jwt.getKeyId());

            // Xử lý lỗi InvalidPublicKeyException an toàn
            RSAPublicKey publicKey;
            try {
                publicKey = (RSAPublicKey) jwk.getPublicKey();
            } catch (InvalidPublicKeyException e) {
                throw new JWTVerificationException("Invalid public key: " + e.getMessage(), e);
            }

            RSAKeyProvider keyProvider = new RSAKeyProvider() {
                @Override
                public RSAPublicKey getPublicKeyById(String kid) {
                    return publicKey;
                }

                @Override
                public RSAPrivateKey getPrivateKey() {
                    return null;
                }

                @Override
                public String getPrivateKeyId() {
                    return null;
                }
            };

            Algorithm algorithm = Algorithm.RSA256(keyProvider);

            // Dùng JWTVerifier để xác minh đúng chuẩn
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(expectedIssuer)
                    .acceptLeeway(60) // chấp nhận lệch 60s thời gian
                    .build();

            DecodedJWT verifiedJwt = verifier.verify(token);

            // Kiểm tra hết hạn
            if (verifiedJwt.getExpiresAt().toInstant().isBefore(Instant.now())) {
                throw new JWTVerificationException("Token expired");
            }

            return verifiedJwt;

        } catch (SigningKeyNotFoundException e) {
            throw new JWTVerificationException("Signing key not found (invalid kid)", e);
        } catch (JWTVerificationException e) {
            throw e;
        } catch (Exception e) {
            throw new JWTVerificationException("Token verification failed: " + e.getMessage(), e);
        }
    }
}
