package com.wisecode.core.service;

import com.wisecode.core.entities.User;
import com.wisecode.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDtlsService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO Auto-generated method stub
        User u = repository.findByUsername(username).orElseThrow(
                ()->new UsernameNotFoundException(username + " Not found in our Users List..")
        );
        return u;
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = repository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );
        return user;
    }

}
