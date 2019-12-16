package com.auzmor.calendar.configurations.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.*;

public class CustomUserAuthenticationConverter implements UserAuthenticationConverter {

  private final String EMAIL = "email";
  private Collection<? extends GrantedAuthority> authorities;

  public void setAuthorities(String[] authorities) {
    this.authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils.arrayToCommaDelimitedString(authorities));
  }
  @Override
  public Map<String, ?> convertUserAuthentication(Authentication authentication) {
    final Map<String, Object> response = new LinkedHashMap<String, Object>();
    response.put("username", authentication.getName());

    if (authentication.getAuthorities() != null
      && !authentication.getAuthorities().isEmpty()) {
      response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
    }
    return response;
  }

  @Override
  public Authentication extractAuthentication(Map<String, ?> map) {
    if (!map.containsKey("username")) {
      final String uuid = map.get("uuid") != null ? map.get("uuid").toString() : null;
      final String firstName = map.get("firstName") != null ? map.get("firstName").toString() : null;
      final String lastName = map.get("lastName") != null ? map.get("lastName").toString() : null;
      final String email = map.get("email") != null ? map.get("email").toString() : null;
      final String country = map.get("country") != null ? map.get("country").toString() : null;
      final String mobile = map.get("mobile") != null ? map.get("mobile").toString() : null;
      final String type = map.get("type") != null ? map.get("type").toString() : null;
      final String token = map.get("nylasToken") != null ? map.get("nylasToken").toString() : null;
      final String accountId = map.get("accountId") != null ? map.get("accountId").toString() : null;

      return new UsernamePasswordAuthenticationToken(
        new CustomPrincipal(uuid, firstName, lastName, email, country, mobile, type,
          getCustomOrganization((Map<String, ?>)map.get("organization")),
          getRoles((List<Map<String, ?>>) map.get("roles")),token, accountId
        ), null,
        getAuthorities(map)
      );
    }
    return null;
  }

  private CustomOrganization getCustomOrganization(Map<String, ?> map) {
    final String domain = map.get("domain") != null ? map.get("domain").toString() : null;
    final String url = map.get("url") != null ? map.get("url").toString() : null;
    final String favicon = map.get("favicon") != null ? map.get("favicon").toString() : null;
    final String logo = map.get("logo") != null ? map.get("logo").toString() : null;
    return new CustomOrganization(map.get("uuid").toString(), map.get("name").toString(), domain, url, favicon, logo);
  }

  private Set<CustomRole> getRoles(List<Map<String, ?>> list) {
    Set<CustomRole> roles = new HashSet<>();
    for (Map<String, ?> map: list) {
      CustomRole role = new CustomRole(map.get("uuid").toString(),
        map.get("authority").toString(),
        getPermissions((List<Map<String, ?>>) map.get("permissions")
        ));
      roles.add(role);
    }
    return roles;
  }

  private Set<CustomPermission> getPermissions(List<Map<String, ?>> list) {
    Set<CustomPermission> permissions = new HashSet<>();
    for (Map<String, ?> map: list) {
      CustomPermission permission = new CustomPermission(map.get("uuid").toString(), map.get("name").toString());
      permissions.add(permission);
    }
    return permissions;
  }

  private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
    if (!map.containsKey("authorities")) {
      return authorities;
    }

    Object mapAuthorities = map.get("authorities");
    if (mapAuthorities instanceof String) {
      return AuthorityUtils.commaSeparatedStringToAuthorityList((String) mapAuthorities);
    }

    if (mapAuthorities instanceof Collection) {
      return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils.collectionToCommaDelimitedString((Collection<?>) mapAuthorities));
    }

    throw new IllegalArgumentException("Authorities must be either a String or Collection");
  }
}
