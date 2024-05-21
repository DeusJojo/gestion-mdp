package gestion.mdp.v2.service.impl;

import gestion.mdp.v2.dto.ManagerDto;
import gestion.mdp.v2.classe.AppKey;
import gestion.mdp.v2.entity.AppManager;
import gestion.mdp.v2.entity.AppUser;
import gestion.mdp.v2.exception.MyExceptionServerIssue;
import gestion.mdp.v2.exception.RessourceNotFoundException;
import gestion.mdp.v2.mapper.ManagerMapper;
import gestion.mdp.v2.repository.ManagerRepository;
import gestion.mdp.v2.repository.UserRepository;
import gestion.mdp.v2.service.ManagerService;
import gestion.mdp.v2.util.CryptoUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
public class ManagerServiceImpl extends CryptoUtil implements ManagerService {

   private ManagerRepository managerRepository;
   private UserRepository userRepository;
   private static final WebClient webClient = WebClient.create("http://localhost:8090");
   private static final AppKey appKey = new AppKey();

   private String encryption(AppManager appManager){
       try {
           if(appKey.getEncryptionKey() == null){
               String secretKey = CryptoUtil.secretKey();
               appKey.setEncryptionKey(secretKey);
               return CryptoUtil.encrypt(appManager.getPasswordApp(), secretKey);
           } else {
               return CryptoUtil.encrypt(appManager.getPasswordApp(), appKey.getEncryptionKey());
           }
       } catch (Exception e) {
           throw new MyExceptionServerIssue("Erreur cryptage" + " " + e.getMessage());
       }
   }

   private String decrypt(AppManager appManager){
       try {
           AppKey appKeyFined = callApiGetKeyByManagerId(webClient, appManager.getId());
           return CryptoUtil.decrypt(appManager.getPasswordApp(), appKeyFined.getEncryptionKey());
       } catch (Exception e) {
           throw new MyExceptionServerIssue("Erreur decryptage" + " " + e.getMessage());
       }
   }

   public void callApiPostKey(WebClient webClient){
       webClient.post()
               .uri("/key/create")
               .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
               .body(Mono.just(appKey), AppKey.class)
               .retrieve()
               .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                       .flatMap(error -> Mono.error(new Exception(error)))
               )
               .bodyToMono(AppKey.class).block();
   }

    public void callApiDeleteKey(WebClient webClient, Long managerId){
        webClient.delete()
                .uri("/key/delete/{managerId}", managerId)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofMillis(700))
                .onErrorResume(TimeoutException.class, error -> Mono.error(new TimeoutException()))
                .block();
    }

    public AppKey callApiGetKeyByManagerId(WebClient webClient, Long managerId){
        return webClient.get()
               .uri("/key/getByManagerId/{managerId}", managerId)
               .retrieve()
               .onStatus(HttpStatusCode::is5xxServerError, response ->
                       Mono.error(new MyExceptionServerIssue("Erreur serveur")))
               .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                       Mono.error(new RessourceNotFoundException("Clé introuvable")))
               .bodyToMono(AppKey.class)
               .block();
    }


    @Override
    public ManagerDto createManager(ManagerDto managerDto, Principal principal) {
        AppManager appManager = ManagerMapper.mapToManager(managerDto);
        AppUser appUser = userRepository.findByEmail(principal.getName());
        appManager.setAppUser(appUser);
        appManager.setPasswordApp(encryption(appManager));
        AppManager savedAppManager = managerRepository.save(appManager);
        appKey.setManagerId(savedAppManager.getId());
        callApiPostKey(webClient);
        return ManagerMapper.mapToManagerDto(savedAppManager);
    }

    @Override
    public ManagerDto getManagerById(Long managerId) {
        AppManager appManager = managerRepository.findById(managerId)
                .orElseThrow(() ->
                        new RessourceNotFoundException(("L'employé avec l'id " + managerId + " n'existe pas.")));
        return ManagerMapper.mapToManagerDto(appManager);
    }

    @Override
    public ManagerDto getManagerByIdDecrypt(Long managerId) {
       AppManager appManager = ManagerMapper.mapToManager(getManagerById(managerId));
       appManager.setPasswordApp(decrypt(appManager));
       return ManagerMapper.mapToManagerDto(appManager);
    }

    @Override
    public List<ManagerDto> getAllManager() {
        List<AppManager> appManager = managerRepository.findAll();
        return appManager.stream().map(ManagerMapper::mapToManagerDto).toList();
    }

    @Override
    public List<ManagerDto> getManagerByUserId(Principal principal) {
        AppUser appUser = userRepository.findByEmail(principal.getName());
        List<AppManager> appManager = managerRepository.findAllByAppUserId(appUser.getId());
        return appManager.stream().map(ManagerMapper::mapToManagerDto).toList();
    }

    @Override
    public ManagerDto updateManager(Long managerId, ManagerDto managerDto) {
        AppManager appManager = managerRepository.findById(managerId)
                .orElseThrow(() ->
                        new RessourceNotFoundException("L'employé avec l'id " + managerId + " n'existe pas."));

        AppKey appKeyFined = callApiGetKeyByManagerId(webClient, appManager.getId());
        appKey.setEncryptionKey(appKeyFined.getEncryptionKey());
        appKey.setManagerId(appKeyFined.getManagerId());

        appManager.setUsernameApp(managerDto.getUsernameApp());
        appManager.setPasswordApp(encryption(ManagerMapper.mapToManager(managerDto)));

        AppManager updateAppManager = managerRepository.save(appManager);

        return ManagerMapper.mapToManagerDto(updateAppManager);
    }

    @Override
    public void deleteManager(Long managerId) {
       if(appKey.getManagerId() != null){
           callApiDeleteKey(webClient, managerId);
       }
       managerRepository.deleteById(managerId);
    }
}
