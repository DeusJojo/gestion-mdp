package gestion.mdp.v2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "manager")
public class AppManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username_app")
    private String usernameApp;
    @Column(name = "password_app", nullable = false)
    private String passwordApp;
    @ManyToOne
    @JoinColumn(name = "app_user_id")
    @JsonIgnore
    private AppUser appUser;
}
