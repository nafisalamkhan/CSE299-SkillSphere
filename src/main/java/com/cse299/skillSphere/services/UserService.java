package com.cse299.skillSphere.services;

import com.cse299.skillSphere.auth.UserRequest;
import com.cse299.skillSphere.messages.Status;
import com.cse299.skillSphere.models.Role;
import com.cse299.skillSphere.models.User;
import com.cse299.skillSphere.repositories.RoleRepository;
import com.cse299.skillSphere.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MinIOService minIOService;


    public User registerUser(UserRequest userRequest) {

        if (userRepository.existsByUsernameEqualsIgnoreCase(userRequest.getUsername())) {
            throw new RuntimeException("Username not available.");
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        Role userRole = roleRepository.findByName(userRequest.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found!"));
        user.setRoles(Set.of(userRole));

        if (userRequest.getProfileImage() != null && !userRequest.getProfileImage().isEmpty()) {
            user.setProfileImageFilePath(minIOService.uploadFile(userRequest.getProfileImage()));
        }

        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), true, true, true,
                true, mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }


    //for one to one messaging


    public void saveUser(User user) {
        user.Status(Status.ONLINE);
        userRepository.save(user);
    }

    public void disconnect(User user) {
        var connectedUser = userRepository.findByUsername(user.getUsername())
                .orElse(null);
        if (connectedUser != null) {
            connectedUser.setStatus(Status.OFFLINE);
            userRepository.save(connectedUser);
        }
    }

    public List<User> findConnectedUsers() {
        return userRepository.findAllByStatus(Status.ONLINE);
    }
}
