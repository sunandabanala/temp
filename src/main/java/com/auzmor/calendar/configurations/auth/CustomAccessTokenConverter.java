package com.auzmor.calendar.configurations.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
@Setter
@Component
public class CustomAccessTokenConverter  extends DefaultAccessTokenConverter implements AccessTokenConverter, JwtAccessTokenConverterConfigurer {

  private boolean includeGrantType;
  private UserAuthenticationConverter userAuthenticationConverter = new CustomUserAuthenticationConverter();

  @Override
  public void configure(JwtAccessTokenConverter jwtAccessTokenConverter) {
    jwtAccessTokenConverter.setAccessTokenConverter(this);
  }

  @Override
  public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
    Map<String, Object> response = new HashMap<>();
    OAuth2Request clientToken = authentication.getOAuth2Request();
    if (!authentication.isClientOnly()) {
      response.putAll(userAuthenticationConverter.convertUserAuthentication(authentication.getUserAuthentication()));
    } else if (clientToken.getAuthorities() != null && !clientToken.getAuthorities().isEmpty()) {
      response.put(UserAuthenticationConverter.AUTHORITIES, AuthorityUtils.authorityListToSet(clientToken.getAuthorities()));
    }
    if (clientToken.getScope() != null) {
      response.put("scope", clientToken.getScope());
    }
    if (token.getAdditionalInformation().containsKey("jti")) {
      response.put("jti", token.getAdditionalInformation().get("jti"));
    }
    if (token.getExpiration() != null) {
      response.put("exp", token.getExpiration().getTime()/1000);
    }
    if (includeGrantType && authentication.getOAuth2Request().getGrantType() != null) {
      response.put("grant_type", authentication.getOAuth2Request().getGrantType());
    }
    response.putAll(token.getAdditionalInformation());
    response.put("client_id", clientToken.getClientId());
    if (clientToken.getResourceIds() != null && !clientToken.getResourceIds().isEmpty()) {
      response.put("aud", clientToken.getResourceIds());
    }
    return response;
  }

  @Override
  public OAuth2AccessToken extractAccessToken(String s, Map<String, ?> map) {
    DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(s);
    Map<String, Object> info = new HashMap<>(map);

    info.remove("exp");
    info.remove("aud");
    info.remove("client_id");
    info.remove("scope");

    if (map.containsKey("exp")) {
      token.setExpiration(new Date((Long) map.get("exp")*1000L));
    }

    if (map.containsKey("jti")) {
      info.put("jti", map.get("jti"));
    }

    token.setScope(extractScope(map));
    token.setAdditionalInformation(info);
    return token;
  }

  private Set<String> extractScope(Map<String, ?> map) {
    Set<String> scope = Collections.emptySet();
    if (map.containsKey("scope")) {
      Object scopeObj = map.get("scope");
      if (String.class.isInstance(scopeObj)) {
        scope = new LinkedHashSet<>(Arrays.asList(String.class.cast(scopeObj).split(" ")));
      } else if (Collection.class.isAssignableFrom(scopeObj.getClass())) {
        @SuppressWarnings("unchecked")
        Collection<String> scopeColl = (Collection<String>) scopeObj;
        scope = new LinkedHashSet<>(scopeColl);
      }
    }
    return scope;
  }

  @Override
  public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
    Set<String> scope = extractScope(map);
    Map<String, String> parameters = new HashMap<>();
    Authentication user = userAuthenticationConverter.extractAuthentication(map);
    String clientId = (String) map.get("client_id");
    parameters.put("clientId", clientId);
    if (includeGrantType && map.containsKey("grant_type")) {
      parameters.put("grant_type", (String) map.get("grant_type"));
    }
    Set<String> resourceIds = new LinkedHashSet<String>(map.containsKey("aud")? getAudience(map): Collections.emptySet());
    Collection<? extends GrantedAuthority> authorities = null;
    if (user == null && map.containsKey(authorities)) {
      @SuppressWarnings("unchecked")
      String[] roles = ( (Collection<String>) map.get(authorities)).toArray(new String[0]);
      authorities = AuthorityUtils.createAuthorityList(roles);
    }
    OAuth2Request request = new OAuth2Request(parameters, clientId, authorities, true, scope, resourceIds, null, null, null);
    return new  OAuth2Authentication(request, user);
  }

  private Collection<String> getAudience(Map<String, ?> map) {
    Object auds = map.get("aud");
    if (auds instanceof Collection) {
      @SuppressWarnings("unchecked")
      Collection<String> result = (Collection<String>) auds;
      return  result;
    }
    return Collections.singleton((String) auds);
  }
}
