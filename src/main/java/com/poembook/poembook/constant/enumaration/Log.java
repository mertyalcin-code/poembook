package com.poembook.poembook.constant.enumaration;

import static com.poembook.poembook.auth.constant.Authority.*;
import static com.poembook.poembook.auth.constant.Authority.SUPER_ADMIN_AUTHORITIES;

public enum Log {
    LOG_USER_REGISTRATION,
    LOG_USER_ADD,
    LOG_USER_UPDATE,
    LOG_USER_SELF_UPDATE,
    LOG_USER_DELETE,
    LOG_RESET_PASSWORD,
    LOG_CHANGE_PASSWORD,
    LOG_CHANGE_USERNAME,
    LOG_CHANGE_EMAIL,
    LOG_POEM_CREATE,
    LOG_POEM_UPDATE,
    LOG_POEM_DELETE,
    LOG_CATEGORY_CREATE,
    LOG_CATEGORY_UPDATE,
    LOG_CATEGORY_DELETE,


}
