/*
 * Copyright 2025 Thorsten Ludewig (t.ludewig@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package l9g.mousetrap.config;

import l9g.mousetrap.token.BearerTokenConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 *
 * Spring Security configuration for the application.
 * <p>
 * This class sets up the security filter chain, which handles authentication
 * and authorization for the application's endpoints. It disables stateful
 * security features like CSRF and sessions, and configures a custom filter for
 * stateless Bearer Token authentication.
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig
{
  private final BearerTokenConfig bearerTokenConfig;

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint(
    @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
  )
  {
    return (request, response, authException) ->
    {
      resolver.resolveException(request, response, null, authException);
    };
  }

  /**
   * Configures the main security filter chain for the application.
   * <p>
   * This bean defines the security rules:
   * <ul>
   * <li>Disables CSRF, sessions, HTTP Basic, and form login.</li>
   * <li>Sets up a custom entry point to delegate auth exceptions.</li>
   * <li>Adds the {@link StaticBearerTokenFilter} to process Bearer tokens.</li>
   * <li>Requires authentication for the {@code /api/v1/cardinfo} endpoint.</li>
   * <li>Permits all other requests.</li>
   * </ul>
   *
   * @param http The {@link HttpSecurity} to configure.
   * @param authenticationEntryPoint The entry point for handling auth exceptions.
   *
   * @return The configured {@link SecurityFilterChain}.
   *
   * @throws Exception if an error occurs during configuration.
   */
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationEntryPoint authenticationEntryPoint)
    throws Exception
  {
    http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(sm -> sm.disable())
      .httpBasic(hb -> hb.disable())
      .formLogin(fl -> fl.disable())
      .logout(lo -> lo.disable());

    http.exceptionHandling(eh -> eh
      .authenticationEntryPoint(authenticationEntryPoint)
    );

    http.addFilterBefore(new StaticBearerTokenFilter(bearerTokenConfig),
      AbstractPreAuthenticatedProcessingFilter.class);

    http.authorizeHttpRequests(auth -> auth
      .requestMatchers("/api/v1/micetro").authenticated()
      .anyRequest().permitAll()
    );

    return http.build();
  }

  /**
   * A filter that authenticates requests based on a static Bearer Token.
   * <p>
   * This filter extracts a token from the {@code Authorization: Bearer} header,
   * looks it up in a pre-configured map of known tokens, and if found and
   * valid, creates an {@link Authentication} object and places it in the
   * {@link SecurityContextHolder}.
   */
  static class StaticBearerTokenFilter extends OncePerRequestFilter
  {
    private final Map<String, BearerTokenConfig.BearerToken> tokensByName;

    private final Map<String, String> tokenIndex;

    StaticBearerTokenFilter(BearerTokenConfig config)
    {
      this.tokensByName = config.getMap();
      this.tokenIndex = tokensByName.entrySet().stream()
        .collect(java.util.stream.Collectors.toUnmodifiableMap(
          e -> e.getValue().getToken(),
          Map.Entry :: getKey,
          (a, b) -> a
        ));
    }

    @Override
    protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException
    {

      String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

      log.debug("auth = {}", auth);
      
      if(auth == null ||  ! auth.startsWith("Bearer "))
      {
        chain.doFilter(request, response);
        return;
      }

      String token = new String(
        Base64.getDecoder().decode(auth.substring("Bearer ".length()).trim()),
        StandardCharsets.UTF_8
      );

      if(token.isEmpty())
      {
        chain.doFilter(request, response);
        return;
      }

      String name = tokenIndex.get(token);
      if(name == null)
      {
        chain.doFilter(request, response);
        return;
      }

      BearerTokenConfig.BearerToken bt = tokensByName.get(name);
      if(bt == null ||  ! bt.isEnabled())
      {
        chain.doFilter(request, response);
        return;
      }

      Authentication authToken = new StaticBearerAuthenticationToken(
        name,
        bt.getOwner(),
        AuthorityUtils.NO_AUTHORITIES
      );
      SecurityContextHolder.getContext().setAuthentication(authToken);

      try
      {
        chain.doFilter(request, response);
      }
      finally
      {
        SecurityContextHolder.clearContext();
      }
    }

  }

  /**
   * A custom {@link Authentication} token representing a successfully
   * authenticated client via a static Bearer token.
   * <p>
   * It holds the principal (the token's name) and details (the token's owner).
   */
  static class StaticBearerAuthenticationToken extends AbstractAuthenticationToken
  {
    private final String principalName;

    private final String owner;

    StaticBearerAuthenticationToken(String principalName, String owner,
      java.util.Collection authorities)
    {
      super(authorities);
      this.principalName = principalName;
      this.owner = owner;
      setAuthenticated(true);
    }

    @Override
    public Object getCredentials()
    {
      return ""; // kein Geheimnis mehr speichern
    }

    @Override
    public Object getPrincipal()
    {
      return principalName;
    }

    @Override
    public Object getDetails()
    {
      return owner;
    }

  }

}
