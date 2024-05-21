package gestion.mdp.v2.service;

import gestion.mdp.v2.dto.UserDto;
import gestion.mdp.v2.dto.RoleDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    RoleDto createRole(RoleDto roleDto);
    UserDto getUserByEmail(String email);
    List<UserDto> getAllUser();
    UserDto updateUser(Long userId, UserDto userDto);
    void deleteUser(Long userId);

}
