package gestion.mdp.v2.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import gestion.mdp.v2.dto.RoleDto;
import gestion.mdp.v2.dto.UserDto;
import gestion.mdp.v2.entity.AppRole;
import gestion.mdp.v2.exception.MyExceptionServerIssue;
import gestion.mdp.v2.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static gestion.mdp.v2.util.JWTUtil.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {

    private UserService userService;

    @PostMapping("/user/createUser")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        UserDto savedUser = userService.createUser(userDto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @PostMapping("/role")
    public ResponseEntity<RoleDto> createRole(@RequestBody RoleDto roleDto){
        RoleDto savedRole = userService.createRole(roleDto);
        return new ResponseEntity<>(savedRole, HttpStatus.CREATED);
    }

    @GetMapping("/user/{email}")
    @RolesAllowed("USER")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email){
        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/user")
    @RolesAllowed("ADMIN")
    public ResponseEntity<List<UserDto>> getAllUser(){
        List<UserDto> userDtoList = userService.getAllUser();
        return ResponseEntity.ok(userDtoList);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long userId,
                                              @RequestBody UserDto updateUser){
        UserDto userDto = userService.updateUser(userId, updateUser);
        return ResponseEntity.ok(userDto);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.ok("L'utilisateur a été supprimé avec succès.");
    }

    @GetMapping("refreshToken")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authToken = request.getHeader(AUTHORIZATION_HEADER);
        if(authToken != null && authToken.startsWith(PREFIX)){
            try {
                String jwt = authToken.substring(PREFIX.length());
                Algorithm algorithm = Algorithm.HMAC256(SECRET);
                JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
                String email = decodedJWT.getSubject();
                UserDto userDto = userService.getUserByEmail(email);
                List<AppRole> roles = new ArrayList<>();
                roles.add(userDto.getAppRole());
                String jwtAccessToken = JWT.create()
                        .withSubject(userDto.getEmail())
                        .withExpiresAt(new Date(System.currentTimeMillis()+EXPIRE_ACCESS_TOKEN))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", roles.stream().map(r -> "ROLE_" + r.getRoleName()).toList())
                        .sign(algorithm);
                Map<String, String> idToken = new HashMap<>();
                idToken.put("access-token", jwtAccessToken);
                idToken.put("refresh-token", jwt);
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), idToken);
            } catch (Exception e){
                throw new MyExceptionServerIssue("Erreur serveur" + " " + e.getMessage());
            }
        } else {
            throw new MyExceptionServerIssue("Refresh token required");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> profile(Principal principal){
        UserDto userDto = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(userDto);
    }
}
