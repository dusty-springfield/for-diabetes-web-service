package webservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class TokensPurgeTask {

    @Autowired
    VerificationService verificationService;

    @Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpired() {
        verificationService.deleteExpiredTokens(new Date());
    }
}