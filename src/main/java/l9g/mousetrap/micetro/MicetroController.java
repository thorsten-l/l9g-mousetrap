/*
 * Copyright 2026 Thorsten Ludewig (t.ludewig@gmail.com).
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
package l9g.mousetrap.micetro;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.Map;
import l9g.mousetrap.token.AuthenticatedBearerToken;
import l9g.mousetrap.token.BearerTokenConfig.BearerToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/micetro",
                produces = MediaType.APPLICATION_JSON_VALUE)
public class MicetroController
{
  private final MicetroService service;

  private String normalizeZone(String zone)
  {
    if(zone != null &&  ! zone.endsWith("."))
    {
      zone = zone + ".";
    }

    log.debug("zone = '{}'", zone);
    return zone;
  }

  private String normalizeName(String zone, String name)
  {
    if(zone != null && name != null)
    {
      if(name.endsWith(zone.substring(0, zone.length() - 1)))
      {
        name = name.substring(0, name.length() - zone.length());
      }
    }
    else
    {
      name = null;
    }
    log.debug("name = '{}'", name);
    return name;
  }

  @PostMapping
  public ResponseEntity<String> add(@RequestBody Map<String, String> request,
    @Parameter(hidden = true) @AuthenticatedBearerToken BearerToken token)
  {
    log.trace("Bearer Token = {}", token);
    log.debug("request = {}", request);

    String zone = normalizeZone(request.get("zone"));
    String name = normalizeName(zone, request.get("name"));

    if(zone == null || name == null)
    {
      return ResponseEntity.badRequest().build();
    }

    service.addTxtRecords(token, zone, name, request.get("data"));
    return ResponseEntity.ok("OK\n");
  }

  @DeleteMapping
  public ResponseEntity<String> remove(@RequestBody Map<String, String> request,
    @Parameter(hidden = true) @AuthenticatedBearerToken BearerToken token)
  {
    log.trace("Bearer Token = {}", token);
    log.debug("request = {}", request);

    String zone = normalizeZone(request.get("zone"));
    String name = normalizeName(zone, request.get("name"));

    if(zone == null || name == null)
    {
      return ResponseEntity.badRequest().build();
    }

    service.removeTxtRecords(token, zone, name);
    return ResponseEntity.ok("OK\n");
  }

}
