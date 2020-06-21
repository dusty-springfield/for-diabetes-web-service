package webservice.models.verification;

import org.hibernate.annotations.GenericGenerator;
import webservice.models.user.User;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Entity
public class VerificationToken {

    private static final int EXPIRATION = 60 * 24; //24 hours

    private static final int TIMEOUT = 3; //3 minutes

    @Id
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "native"
    )
    @GenericGenerator(
            name = "native",
            strategy = "native"
    )
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column
    private String credential;

    @Enumerated(EnumType.ORDINAL)
    private VerificationTokenType tokenType;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    public VerificationToken() {
    }

    public VerificationToken(User u, String t, String c, VerificationTokenType tokenType) {
        setUser(u);
        setToken(t);
        setCredential(c);
        setExpiryDate(calculateExpiryDate());
        setTokenType(tokenType);
    }

    public Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, EXPIRATION);
        return new Date(cal.getTime().getTime());
    }

    public static int getExpiration() {
        return EXPIRATION;
    }

    public static int getTimeout() {
        return TIMEOUT;
    }

    public String getCredential() {
        return credential;
    }

    public void setCredential(String credential) {
        this.credential = credential;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public VerificationTokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(VerificationTokenType tokenType) {
        this.tokenType = tokenType;
    }

    public static int getEXPIRATION() {
        return EXPIRATION;
    }

    public static int getDELTA() {
        return TIMEOUT;
    }
}

