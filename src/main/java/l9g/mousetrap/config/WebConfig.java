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
import java.util.List;
import l9g.mousetrap.token.BearerTokenArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer
{
  private final BearerTokenConfig tokenConfig;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers)
  {
    resolvers.add(new BearerTokenArgumentResolver(tokenConfig));
  }

}
