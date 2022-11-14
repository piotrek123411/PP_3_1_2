package ru.itsinfo.springbootsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itsinfo.springbootsecurity.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
