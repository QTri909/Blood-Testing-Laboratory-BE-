package sum25.group03.iamservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CustomJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();


        String rolesStr = jwt.getClaimAsString("roles");
        if (rolesStr != null && !rolesStr.isEmpty()) {
            Arrays.stream(rolesStr.split(","))
                    .map(String::trim)
                    .forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }


        String privStr = jwt.getClaimAsString("privileges");
        if (privStr != null && !privStr.isEmpty()) {
            Arrays.stream(privStr.split(","))
                    .map(String::trim)
                    .forEach(priv -> authorities.add(new SimpleGrantedAuthority(priv)));
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}
