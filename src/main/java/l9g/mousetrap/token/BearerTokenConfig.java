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

import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Configuration properties for loading bearer tokens from the application
 * configuration (e.g., YAML file). This class maps properties under the
 * {@code bearer-tokens} prefix.
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Configuration
@ConfigurationProperties(prefix = "bearer-tokens")
@Data
@ToString
public class BearerTokenConfig
{
  private Map<String,BearerToken> map;
  
  @Data
  @ToString
  @RefreshScope
  public static class BearerToken
  {
    private String token;

    private String owner;
   
    private String description;
        
    private List<String> permittedZones;

    private boolean enabled = false;
  }
}
