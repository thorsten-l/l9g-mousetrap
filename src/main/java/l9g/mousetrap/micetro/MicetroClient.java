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

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import l9g.mousetrap.jsonrpc.JsonRpcRequest;
import l9g.mousetrap.jsonrpc.JsonRpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Slf4j
@Service
public class MicetroClient
{
  private final RestClient restClient;

  private final AtomicInteger requestId = new AtomicInteger(0);

  public MicetroClient(RestClient.Builder builder, MicetroConfig config)
  {
    this.restClient = builder
      .baseUrl(config.getApiUrl())
      .defaultHeader(
        HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .build();
  }

  public LinkedHashMap<String, Object> call(String method, Object params)
  {
    JsonRpcRequest request =
      new JsonRpcRequest(method, params, requestId.incrementAndGet());
    log.trace("request={}", request);

    JsonRpcResponse<LinkedHashMap<String,Object>> response = restClient.post()
      .body(request)
      .retrieve()
      .body(new ParameterizedTypeReference<JsonRpcResponse<LinkedHashMap<String,Object>>>(){});

    if(response != null && response.error() != null)
    {
      throw new MicetroApiException(response.error());
    }

    log.debug("response={}", response);
    return response != null ? response.result() : null;
  }

}
