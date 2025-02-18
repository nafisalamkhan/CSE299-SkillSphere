package com.cse299.skillSphere.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query(value = """
            select u.* from user u join user_roles ur on u.id = ur.user_id
            where ur.role_id in (:roles)
            """, nativeQuery = true)
    List<User> findAllByRolesIn(Set<Long> roles);
}

