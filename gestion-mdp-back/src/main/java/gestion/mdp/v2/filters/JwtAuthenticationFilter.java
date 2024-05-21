package gestion.mdp.v2.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static gestion.mdp.v2.util.JWTUtil.*;

@AllArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        System.out.println("attemptAuthentication");
//        String email = request.getParameter("email");
//        String password = request.getParameter("password");
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(email, password);
//        return authenticationManager.authenticate(authenticationToken);
//    }

        // Récupérer les données JSON à partir du corps de la requête
        JSONObject jsonData = null;
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String json = sb.toString();
            jsonData = new JSONObject(json);
        } catch (IOException e) {
            // Gérer l'erreur d'entrée/sortie
            e.printStackTrace();
            // Potentiellement, envoyer une réponse d'erreur
            return null;
        } catch (JSONException e) {
            // Gérer l'erreur JSON
            e.printStackTrace();
            // Potentiellement, envoyer une réponse d'erreur
            return null;
        }

        String email = null;
        String password = null;

        // Extraire les données de l'objet JSON
            try {
                email = jsonData.getString("email");
                password = jsonData.getString("password");
            } catch (JSONException e) {
                // Gérer l'absence de clés email/password dans l'objet JSON
                e.printStackTrace();
                // Potentiellement, envoyer une réponse d'erreur
                return null;
            }

        if (email == null || password == null) {
            // Gérer le cas où les données email ou password sont manquantes
            // Potentiellement, envoyer une réponse d'erreur
            return null;
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManager.authenticate(authenticationToken);
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        System.out.println("succesfullAuthentication");
        User user = (User) authResult.getPrincipal();
        Algorithm algo1 = Algorithm.HMAC256(SECRET);
        String jwtAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRE_ACCESS_TOKEN))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                        .sign(algo1);
        response.setHeader("Authorization", jwtAccessToken);

        String jwtRefreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRE_REFRESH_TOKEN))
                .withIssuer(request.getRequestURL().toString())
                .sign(algo1);
        Map<String, String> idToken = new HashMap<>();
        idToken.put("access-token", jwtAccessToken);
        idToken.put("refresh-token", jwtRefreshToken);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), idToken);
    }
}
