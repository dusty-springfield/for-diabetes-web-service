package webservice.models.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private final String prefix = "ROLE_";
    private String email;
    private String password;
    private boolean enabled;
    private List<GrantedAuthority> grantedAuthorities;

    public CustomUserDetails(User user) {
        email = user.getEmail();
        password = user.getPassword();
        enabled = user.isEnabled();

        String[] userRoles = user.getRoles().stream().map(r -> {
                    if(!r.getName().startsWith(prefix))
                        return prefix + r.getName();
                    return r.getName();
                }
        ).toArray(String[]::new);

        grantedAuthorities = AuthorityUtils.createAuthorityList(userRoles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
