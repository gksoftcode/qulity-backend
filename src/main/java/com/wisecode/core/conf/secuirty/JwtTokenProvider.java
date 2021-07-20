package com.wisecode.core.conf.secuirty;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisecode.core.RoleName;
import com.wisecode.core.entities.Role;
import com.wisecode.core.entities.User;
import com.wisecode.core.util.SystemUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void init(){
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        Map<String,Long> roles = new HashMap<>(1);
        for(Role role : user.getRoles()){
            roles.put(role.getName().name(),role.getId());
        }
        List<String> roleList = new ArrayList<>();
        for(Role role : user.getRoles()){
            roleList.add(role.getName().name());
        }
        boolean isManager = false;
        if(user.getEmployee().getDepartment()!=null ){
            isManager = user.getEmployee().equals(user.getEmployee().getDepartment().getManager());
        }

        return JWT.create()
                .withIssuedAt(new Date())
                .withSubject(SystemUtil.encrypt(user.getEmployee().getId().toString()))
                .withExpiresAt(expiryDate)
                .withIssuer("core")
                .withClaim("active",user.getActive())
                .withClaim("username",user.getUsername())
                .withClaim("roles",roleList)
                .withClaim("fullName",user.getEmployee().getFullName())
                .withClaim("id",user.getEmployee().getId())
                .withClaim("isManager",isManager)
                .withClaim("departmentId",user.getEmployee().getDepartmentId())
                .withClaim("departmentEncId",user.getEmployee().getDepartment()!=null?
                        user.getEmployee().getDepartment().getEncId():"")
                .withClaim("departmentName",user.getEmployee().getDepartment()!=null?
                        user.getEmployee().getDepartment().getName():"")
                .sign(algorithm);

    }

//    public Long getUserIdFromJWT(String token) {
//        Claims claims = Jwts.parser()
//                .setSigningKey(jwtSecret)
//                .parseClaimsJws(token)
//                .getBody();
//        return Long.parseLong(claims.getSubject());
//    }

    public User getUserFromJWT(String token) throws Exception {
        try {
            DecodedJWT jwt = JWT.decode(token);
            User user = new User();
            Long id = Long.parseLong(Objects.requireNonNull(SystemUtil.decrypt(jwt.getSubject())));
            user.setId(id);
            user.setUsername(jwt.getClaim("username").asString());
            user.setActive(jwt.getClaim("active").asBoolean());
            List<String> roles = jwt.getClaim("roles").asList(String.class);
            Set<Role> userRoles = new HashSet<>(1);
            roles.forEach(rol -> {
                Role rl = new Role();
                rl.setName(RoleName.valueOf(rol));
                userRoles.add(rl);
            });
            user.setRoles(userRoles);
            return user;
        } catch (JWTDecodeException exception){
            throw exception;
        }
//        Claims claims = Jwts.parser()
//                .setSigningKey(jwtSecret)
//                .parseClaimsJws(token)
//                .getBody();
//        Object user_data = claims.get("user");
//        if(user_data != null){
//            return objectMapper.readValue(user_data.toString(),User.class);
//        }
//        throw new Exception();
    }

    public boolean validateToken(String authToken) {
        try {
            DecodedJWT jwt = JWT.decode(authToken);
            return jwt.getExpiresAt().after(new Date());
        } catch (JWTDecodeException exception){
            return false;
        }
    }
}
