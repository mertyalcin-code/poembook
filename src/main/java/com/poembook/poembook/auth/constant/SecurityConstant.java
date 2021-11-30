package com.poembook.poembook.auth.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 432_000_000; // 5 days expressed in milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token onaylandamadı";
    public static final String POEMBOOK = "POEMBOOK";
    public static final String POEMBOOK_ADMINISTRATION = "Poembook";
    public static final String AUTHORITIES = "authorities";
    public static final String FORBIDDEN_MESSAGE = "Bu sayfaya ulaşmak için giriş yapmalısın";
    public static final String ACCESS_DENIED_MESSAGE = "Bu sayfa için yetkin yok";
    public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
    public static final String[] PUBLIC_URLS = {"/user/login", "/user/register","/user/forget-password","/user/forget-password/code/**"};
    //public static final String[] PUBLIC_URLS = {"**"};
}
