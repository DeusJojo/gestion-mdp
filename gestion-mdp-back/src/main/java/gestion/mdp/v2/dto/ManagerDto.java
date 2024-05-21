package gestion.mdp.v2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gestion.mdp.v2.entity.AppUser;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ManagerDto {
    private Long id;
    private String usernameApp;
    private String passwordApp;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private AppUser appUser;
}
