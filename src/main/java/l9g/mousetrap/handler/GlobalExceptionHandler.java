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
package l9g.mousetrap.handler;

import l9g.mousetrap.token.MissingOrInvalidTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 *
 * Global exception handler for the REST API.
 * <p>
 * This class uses {@link RestControllerAdvice} to centralize exception handling
 * across all controllers. It catches specific exceptions and transforms them
 * into appropriate HTTP responses.
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler
{

  /**
   * Handles the case where a bearer token is missing or invalid.
   *
   * @param ex The caught {@link MissingOrInvalidTokenException}.
   *
   * @return A {@link ResponseEntity} with HTTP status 400 (Bad Request) and an
   * error message in the body.
   */
  @ExceptionHandler(MissingOrInvalidTokenException.class)
  public ResponseEntity handleMissingOrInvalidToken(
    MissingOrInvalidTokenException ex)
  {
    log.error("{}", ex.getMessage());
    return ResponseEntity.badRequest().build();
  }

  /**
   * Handles generic authentication failures.
   *
   * @param ex The caught {@link AuthenticationException}.
   *
   * @return A {@link ResponseEntity} with HTTP status 401 (Unauthorized) and
   * an error message in the body.
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity handleAuthenticationException(AuthenticationException ex)
  {
    log.error("Authentication failed: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

}
