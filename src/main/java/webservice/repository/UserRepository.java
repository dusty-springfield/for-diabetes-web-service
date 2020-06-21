package webservice.repository;

import webservice.models.user.User;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}

