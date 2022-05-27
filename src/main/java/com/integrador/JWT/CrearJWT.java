package com.integrador.JWT;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.integrador.services.IUsuarioService;
import com.integrador.tablas.Role;
import com.integrador.tablas.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CrearJWT {
    private final String secret="secret";
    private final Long accessTokenMS=900000L;//15*60*1000
    private final Long refreshTokenMS=2592000000L;//30*24*60*60*1000

    private final IUsuarioService userService;

    protected String crearAccessTokenPrincipal(HttpServletRequest request, Authentication authentication){
        System.out.println("AutorizacionFiltro - crearAccessTokenPrincipal");
        User user=(User)authentication.getPrincipal();
        Algorithm algorithm=Algorithm.HMAC256(secret.getBytes());

        //access_token
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+accessTokenMS))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

    }

    public String crearAccessTokenUser(String refresh_token,HttpServletRequest request, String authorizationHeader){
        System.out.println("AutorizacionFiltro - crearAccessTokenUser");
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refresh_token);
        String username = decodedJWT.getSubject();
        Usuario user = userService.getUser(username);

        //access_token
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+accessTokenMS))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .sign(algorithm);
    }


    protected String crearRefreshToken(HttpServletRequest request, Authentication authentication){
        System.out.println("AutorizacionFiltro - crearRefreshToken");
        User user=(User)authentication.getPrincipal();
        Algorithm algorithm=Algorithm.HMAC256(secret.getBytes());

        //refresh_token
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+refreshTokenMS))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);
    }

    public DecodedJWT decodJWT(String token){
        System.out.println("AutorizacionFiltro - decodJWT");
        Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }

    public Algorithm algoritmo(){
        return Algorithm.HMAC256(secret.getBytes());
    }

}
