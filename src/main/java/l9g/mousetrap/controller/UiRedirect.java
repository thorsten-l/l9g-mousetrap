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
package l9g.mousetrap.controller;

import io.swagger.v3.oas.annotations.Hidden;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A simple controller to redirect from a friendlier URL to the Swagger UI.
 * This controller is hidden from the OpenAPI documentation itself.
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Hidden
@RestController
public class UiRedirect
{
  /**
   * Redirects the user from "/api/docs" to the main Swagger UI page.
   *
   * @return A {@link ResponseEntity} that performs a PERMANENT_REDIRECT.
   */
  @GetMapping("/api/docs")
  public ResponseEntity redirect()
  {
    return ResponseEntity
      .status(HttpStatus.PERMANENT_REDIRECT)
      .location(URI.create("/swagger-ui/index.html"))
      .build();
  }
}
