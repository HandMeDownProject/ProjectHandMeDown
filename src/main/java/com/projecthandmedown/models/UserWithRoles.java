package com.projecthandmedown.models;
import com.projecthandmedown.repositories.RoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserWithRoles extends User implements UserDetails {
        List<String> roles;

    public UserWithRoles(User user, List<String> strings) {
        super(user);  // Call the copy constructor defined in User
        roles = strings;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ; // Since we're not using the authorization part of the component

        return AuthorityUtils.commaSeparatedStringToAuthorityList(roles.get(0));
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
        return true;
    }
}


