package com.poembook.poembook.constant.enumaration;


import lombok.Data;

import static com.poembook.poembook.auth.constant.Authority.*;

public enum Role {
    ROLE_POET(POET_AUTHORITIES),
    ROLE_EDITOR(EDITOR_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    private final String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
