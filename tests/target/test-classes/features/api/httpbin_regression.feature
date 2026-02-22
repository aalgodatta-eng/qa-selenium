@api_regression
Feature: httpbin Regression API suite (positive and negative coverage)

  Background:
    Given I set API base url
    And I reset API client

  # 2.1 HTTP Methods
  Scenario Outline: HTTP methods should return success and echo method
    When I send "<method>" request to "<path>"
    Then the response status should be <status>
    And the response content type should contain "application/json"
    And the JSON path "method" should be "<method>"
    Examples:
      | method | path   | status |
      | GET    | /get   | 200    |
      | POST   | /post  | 200    |
      | PUT    | /put   | 200    |
      | PATCH  | /patch | 200    |
      | DELETE | /delete| 200    |

  Scenario: HEAD should return headers only
    When I send "HEAD" request to "/get"
    Then the response status should be 200

  Scenario: OPTIONS should return allowed methods
    When I send "OPTIONS" request to "/get"
    Then the response status should be 200

  # 2.2 Auth
  Scenario: Bearer auth positive
    Given I use bearer token "test-token"
    When I send "GET" request to "/bearer"
    Then the response status should be 200
    And the response content type should contain "application/json"
    And the JSON path "authenticated" should not be null

  Scenario: Bearer auth negative (missing token)
    Given I reset API client
    When I send "GET" request to "/bearer"
    Then the response status should be 401

  Scenario: Digest auth positive
    Given I use digest auth "user" and "passwd"
    When I send "GET" request to "/digest-auth/auth/user/passwd"
    Then the response status should be 200

  Scenario: Digest auth negative
    Given I use digest auth "user" and "wrong"
    When I send "GET" request to "/digest-auth/auth/user/passwd"
    Then the response status should be 401

  # 2.3 Status codes
  Scenario Outline: status endpoint should generate the given status code
    When I send "GET" request to "/status/<code>"
    Then the response status should be <code>
    Examples:
      | code |
      | 200  |
      | 201  |
      | 204  |
      | 301  |
      | 400  |
      | 401  |
      | 403  |
      | 404  |
      | 500  |

  # 2.4 Request inspection
  Scenario: Inspect query params, headers and client IP via /anything
    Given I set query param "q" to "abc"
    And I set header "X-Test" to "kc"
    When I send "GET" request to "/anything/inspect"
    Then the response status should be 200
    And the JSON path "args.q" should be "abc"
    And the JSON path "headers.X-Test" should be "kc"
    And the JSON path "method" should be "GET"
    And the JSON path "url" should contain "inspect"

  Scenario: Inspect JSON body via /anything
    Given I set JSON body
      """
      {"id":123,"active":true}
      """
    When I send "POST" request to "/anything/body"
    Then the response status should be 200
    And the JSON path "json.id" should be 123
    And the JSON path "json.active" should not be null

  # 2.5 Response inspection
  Scenario: Validate caching headers via /cache/0 (no caching)
    When I send "GET" request to "/cache/0"
    Then the response status should be 200
    And the response header "Cache-Control" should contain "max-age=0"

  Scenario: Validate ETag response
    When I send "GET" request to "/etag/abc123"
    Then the response status should be 200
    And the response header "ETag" should contain "abc123"

  Scenario: Validate custom response headers
    When I send "GET" request to "/response-headers?X-Env=qa&Cache-Control=no-store"
    Then the response status should be 200
    And the response header "X-Env" should be "qa"
    And the response header "Cache-Control" should contain "no-store"

  # 2.6 Response formats
  Scenario Outline: Different response formats should return expected content types
    When I send "GET" request to "<path>"
    Then the response status should be 200
    And the response content type should contain "<contentType>"
    Examples:
      | path            | contentType |
      | /json           | application/json |
      | /xml            | application/xml  |
      | /html           | text/html        |
      | /robots.txt     | text/plain       |
      | /encoding/utf8  | text/html        |

  Scenario: Gzip response should be JSON
    When I send "GET" request to "/gzip"
    Then the response status should be 200
    And the response content type should contain "application/json"
    And the JSON path "gzipped" should not be null

  # 2.7 Dynamic data
  Scenario: UUID endpoint should return a UUID
    When I send "GET" request to "/uuid"
    Then the response status should be 200
    And the response content type should contain "application/json"
    And the JSON path "uuid" should not be null

  Scenario: Delay endpoint should respond within a reasonable time
    When I send "GET" request to "/delay/1"
    Then the response status should be 200
    And the response time should be less than 5000 ms

  Scenario: Random bytes should return binary
    Given I set header "Accept" to "application/octet-stream"
    When I send "GET" request to "/bytes/32"
    Then the response status should be 200

  # 2.8 Cookies
  Scenario: Create, read and delete cookies
    Given I do not follow redirects
    When I send "GET" request to "/cookies/set?kc=1"
    Then the response status should be 302
    # cookie should be persisted through the session filter
    Given I follow redirects
    When I send "GET" request to "/cookies"
    Then the response status should be 200
    And the JSON path "cookies.kc" should be "1"
    Given I do not follow redirects
    When I send "GET" request to "/cookies/delete?kc"
    Then the response status should be 302
    Given I follow redirects
    When I send "GET" request to "/cookies"
    Then the response status should be 200

  # 2.9 Images
  Scenario Outline: Image endpoints should return correct image content types
    Given I set header "Accept" to "*/*"
    When I send "GET" request to "<path>"
    Then the response status should be 200
    And the response content type should contain "image"
    Examples:
      | path        |
      | /image/png  |
      | /image/jpeg |
      | /image/webp |
      | /image/svg  |

  # 2.10 Redirects
  Scenario: Redirect endpoint should return a redirect when not following
    Given I do not follow redirects
    When I send "GET" request to "/redirect/2"
    Then the response status should be 302

  Scenario: Redirect endpoint should reach final destination when following
    Given I follow redirects
    When I send "GET" request to "/redirect/2"
    Then the response status should be 200
    And the response content type should contain "application/json"

  Scenario: Redirect to a custom URL
    Given I do not follow redirects
    When I send "GET" request to "/redirect-to?url=https://httpbin.org/get&status_code=307"
    Then the response status should be 307

  # 2.11 Anything
  Scenario: /anything should echo arbitrary path, headers and payload
    Given I set header "X-Custom" to "hello"
    And I set query param "n" to "99"
    And I set JSON body
      """
      {"msg":"ping"}
      """
    When I send "POST" request to "/anything/alpha/beta"
    Then the response status should be 200
    And the response content type should contain "application/json"
    And the JSON path "headers.X-Custom" should be "hello"
    And the JSON path "args.n" should be "99"
    And the JSON path "json.msg" should be "ping"
