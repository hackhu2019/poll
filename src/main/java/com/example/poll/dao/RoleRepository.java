package com.example.poll.dao;

import com.example.poll.model.entity.Role;
import com.example.poll.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author huhao
 * @created 2022/10/9
 * Description RoleRepository
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
