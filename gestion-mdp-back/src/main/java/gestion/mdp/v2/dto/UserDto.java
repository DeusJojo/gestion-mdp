package gestion.mdp.v2.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gestion.mdp.v2.entity.AppManager;
import gestion.mdp.v2.entity.AppRole;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private Long id;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private AppRole appRole;
    private List<AppManager> appManagers;
}
