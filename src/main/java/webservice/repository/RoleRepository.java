package webservice.repository;

import org.springframework.data.repository.CrudRepository;
import webservice.models.role.Role;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findByName(String name);
}
