package com.goforgoldner.c_drive.service.impl;

import com.goforgoldner.c_drive.domain.UserPrincipal;
import com.goforgoldner.c_drive.domain.entities.UserEntity;
import com.goforgoldner.c_drive.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<UserEntity> userEntity = userRepository.findByUsername(username);

    if (userEntity.isEmpty()) {
      System.out.println("User not found!");
      throw new UsernameNotFoundException("The username: " + username + " was not found.");
    } else {
      System.out.println("User found!");
      return new UserPrincipal(userEntity.get());
    }
  }
}
