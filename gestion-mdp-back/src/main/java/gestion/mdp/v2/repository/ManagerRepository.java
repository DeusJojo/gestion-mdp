package gestion.mdp.v2.repository;

import gestion.mdp.v2.entity.AppManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManagerRepository extends JpaRepository<AppManager, Long> {
    List<AppManager> findAllByAppUserId(Long id);
}
