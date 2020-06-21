package webservice.repository;

import org.springframework.data.repository.CrudRepository;
import webservice.models.user.User;
import webservice.models.verification.VerificationToken;
import webservice.models.verification.VerificationTokenType;

import java.util.Date;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);

    VerificationToken findByTokenAndTokenType(String token, VerificationTokenType tokenType);

    VerificationToken findByUser(User user);

    Iterable<VerificationToken> findAllByExpiryDateIsLessThan(Date now);
}
