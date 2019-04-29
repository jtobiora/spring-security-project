package com.security.demo.service;

import com.security.demo.model.Role;
import com.security.demo.model.RoleName;
import com.security.demo.repo.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Optional<Role> findByName(RoleName roleName){
        return roleRepository.findByName(roleName);
    }
}
