package gestion.mdp.v2.classe;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class  AppKey {
    private String encryptionKey;
    private Long managerId;
}
