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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import l9g.mousetrap.token.BearerTokenConfig.BearerToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Slf4j
@Service
public class MicetroService
{
  private final MicetroClient client;

  private final MicetroConfig micetroConfig;

  private final Cache<String, String> sessionCache;

  private static final String COMMENT_TAG = "l9g-mousetrap";

  private static final String CACHE_SESSION_KEY = "login";

  /////////////////////////////////////////////////////////////////////////////
  
  public MicetroService(MicetroClient client, MicetroConfig micetroConfig)
  {
    this.client = client;
    this.micetroConfig = micetroConfig;
    this.sessionCache = Caffeine.newBuilder()
      .expireAfterWrite(Duration.ofSeconds(micetroConfig.getSessionCacheTtl()))
      .build();
  }

  /////////////////////////////////////////////////////////////////////////////

  private boolean zonePermitted(BearerToken token, String zone)
  {
    boolean permitted = false;

    if(token != null && zone != null)
    {
      for(String z : token.getPermittedZones())
      {
        if(z.equalsIgnoreCase(zone))
        {
          permitted = true;
          break;
        }
      }
    }

    if( ! permitted)
    {
      log.warn("token '{}' not permitted to access zone '{}'", token, zone);
    }
    return permitted;
  }

  private String login()
  {
    log.debug("login");
    return sessionCache.get(CACHE_SESSION_KEY, _key ->
    {
      log.debug("login - cache miss");
      Map<String, Object> params = new LinkedHashMap<>();
      params.put("server", micetroConfig.getServer());
      params.put("loginName", micetroConfig.getLoginName());
      params.put("password", micetroConfig.getPassword());
      params.put("unauthorizedAsForbidden", true);
      LinkedHashMap<String, Object> response = client.call("login", params);
      return (String)response.get("session");
    });
  }

  private List<String> findZoneRefs(String zone, String session)
  {
    List<String> result = null;

    Map<String, Object> params = new LinkedHashMap<>();
    params.put("filter", "type=primary name=" + zone);
    params.put("limit", 500);
    params.put("offset", 0);
    params.put("sortBy", "natural");
    params.put("sortOrder", "Ascending");
    params.put("session", session);

    LinkedHashMap<String, Object> response = client.call(
      "GetDNSZones", params);

    List<Map<String, Object>> list = (List)response.get("dnsZones");

    if(list.size() > 0)
    {
      result = new ArrayList<>();
      for(Map<String, Object> map : list)
      {
        String ref = (String)map.get("ref");
        if(ref != null)
        {
          result.add(ref);
        }
      }
    }

    return result;
  }

  private List<String> findTxtDnsRecord(String session, String dnsZoneRef, String name)
  {
    List<String> result = null;

    Map<String, Object> params = new LinkedHashMap<>();
    params.put("dnsZoneRef", dnsZoneRef);
    params.put("filter", "type=TXT comment=" + COMMENT_TAG + " name=" + name);
    params.put("includeMetaRecords", true);
    params.put("limit", 500);
    params.put("offset", 0);
    params.put("syncZone", false);
    params.put("session", session);

    LinkedHashMap<String, Object> response = client.call(
      "GetDNSRecords", params);

    List<Map<String, Object>> list = (List)response.get("dnsRecords");

    if(list.size() > 0)
    {
      result = new ArrayList<>();
      for(Map<String, Object> map : list)
      {
        String ref = (String)map.get("ref");
        if(ref != null)
        {
          result.add(ref);
        }
      }
    }

    return result;
  }

  private void addTxtDnsRecord(String session, String dnsZoneRef, String name, String data)
  {
    Map<String, Object> dnsRecord = new LinkedHashMap<>();
    dnsRecord.put("name", name);
    dnsRecord.put("type", "TXT");
    dnsRecord.put("ttl", "0");
    dnsRecord.put("data", data);
    dnsRecord.put("comment", COMMENT_TAG);
    dnsRecord.put("enabled", true);
    dnsRecord.put("dnsZoneRef", dnsZoneRef);

    Map<String, Object> params = new LinkedHashMap<>();
    params.put("dnsRecord", dnsRecord);
    params.put("forceOverrideOfNamingConflictCheck", true);
    params.put("session", session);

    LinkedHashMap<String, Object> response = client.call(
      "AddDNSRecord", params);

    log.debug("{}", response);
  }

  private void removeObjects(String session, List<String> objRefs)
  {
    Map<String, Object> params = new LinkedHashMap<>();
    params.put("objRefs", objRefs);
    params.put("session", session);

    LinkedHashMap<String, Object> response = client.call(
      "RemoveObjects", params);

    log.debug("{}", response);
  }

  /////////////////////////////////////////////////////////////////////////////

  
  public void addTxtRecords(
    BearerToken token, String zone, String name, String data)
  {
    log.info("ADD: zone={}, name={}", zone, name);
    if(zonePermitted(token, zone))
    {
      String session = login();
      log.debug("session={}", session);
      List<String> zoneRefs = findZoneRefs(zone, session);
      log.debug("{}", zoneRefs);
      for(String ref : zoneRefs)
      {
        addTxtDnsRecord(session, ref, name, data);
      }
    }
  }

  public void removeTxtRecords(
    BearerToken token, String zone, String name)
  {
    log.info("REMOVE: zone={}, name={}", zone, name);

    if(zonePermitted(token, zone))
    {
      String session = login();
      log.debug("session={}", session);
      List<String> zoneRefs = findZoneRefs(zone, session);
      log.debug("{}", zoneRefs);
      List<String> objRefs = new ArrayList<>();
      for(String ref : zoneRefs)
      {
        List<String> records = findTxtDnsRecord(session, ref, name);
        if(records != null && records.size() > 0)
        {
          objRefs.addAll(records);
        }
      }
      log.debug("objRefs={}", objRefs);
      if(objRefs.size() > 0)
      {
        removeObjects(session, objRefs);
      }
    }
  }

}
