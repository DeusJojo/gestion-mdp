package gestion.mdp.v2.service.impl;

import gestion.mdp.v2.entity.AppUser;
import gestion.mdp.v2.exception.RessourceNotFoundException;
import gestion.mdp.v2.mapper.UserMapper;
import gestion.mdp.v2.repository.UserRepository;
import gestion.mdp.v2.dto.RoleDto;
import gestion.mdp.v2.entity.AppRole;
import gestion.mdp.v2.mapper.RoleMapper;
import gestion.mdp.v2.repository.RoleRepository;
import gestion.mdp.v2.dto.UserDto;
import gestion.mdp.v2.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

   private UserRepository userRepository;
   private RoleRepository roleRepository;
   private PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        AppUser appUser = UserMapper.mapToUser(userDto);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        if(userDto.getAppRole() != null){
            AppRole appRole = roleRepository.findByRoleName(userDto.getAppRole().getRoleName());
            appUser.setAppRole(appRole);
        }
        AppUser savedAppUser = userRepository.save(appUser);
        return UserMapper.mapToUserDto(savedAppUser);
    }

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        AppRole appRole = RoleMapper.mapToUser(roleDto);
        AppRole savedAppRole = roleRepository.save(appRole);
        return RoleMapper.mapToUserDto(savedAppRole);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        AppUser appUser = userRepository.findByEmail(email);
        return UserMapper.mapToUserDto(appUser);
    }

    @Override
    public List<UserDto> getAllUser() {
        List<AppUser> appUser = userRepository.findAll();
        return appUser.stream().map(UserMapper::mapToUserDto).toList();
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        AppUser appUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RessourceNotFoundException("L'employé avec l'id " + userId + " n'existe pas."));
        appUser.setEmail(userDto.getEmail());
        appUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        AppUser updateAppUser = userRepository.save(appUser);

        return UserMapper.mapToUserDto(updateAppUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
