package webservice.repository;

import org.springframework.data.repository.CrudRepository;
import webservice.models.user.UserDetails;

public interface UserDetailsRepository extends CrudRepository<UserDetails, Long> {
}
