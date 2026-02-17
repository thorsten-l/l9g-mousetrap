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
package l9g.mousetrap.token;

import java.security.Principal;
import l9g.mousetrap.token.BearerTokenConfig.BearerToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Slf4j
public class BearerTokenArgumentResolver implements
  HandlerMethodArgumentResolver
{

  private final BearerTokenConfig tokenConfig;

  public BearerTokenArgumentResolver(BearerTokenConfig tokenConfig)
  {
    this.tokenConfig = tokenConfig;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter)
  {
    return parameter.hasParameterAnnotation(AuthenticatedBearerToken.class)
      && BearerToken.class.isAssignableFrom(parameter.getParameterType());
  }

  @Override
  public Object resolveArgument(
    MethodParameter parameter,
    ModelAndViewContainer mavContainer,
    NativeWebRequest webRequest,
    WebDataBinderFactory binderFactory
  )
  {
    Principal principal = webRequest.getUserPrincipal();
    
    log.debug("{}", principal);
    
    if(principal == null)
    {
      throw new MissingOrInvalidTokenException("anonymous");
    }

    BearerToken token = tokenConfig.getMap().get(principal.getName());
    if(token == null)
    {
      throw new MissingOrInvalidTokenException(principal.getName());
    }

    return token;
  }

}
