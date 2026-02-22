@api_smoke
Feature: JSONPlaceholder API smoke coverage

  Background:
    Given I set API base url

  Scenario: Get a single post
    When I GET "/posts/1"
    Then the response status should be 200
    And json path "id" should be 1
    And json path "userId" should be 1
    And json path "title" should not be null
    And the response should match json schema "schemas/jsonplaceholder_post_schema.json"

  Scenario: List posts for a user
    When I GET "/posts?userId=1"
    Then the response status should be 200
    And the response array at "$" size should be greater than 0
    And each item in response array has json path "userId" equals 1
    And the response should match json schema "schemas/jsonplaceholder_posts_array_schema.json"

  Scenario: Get a user
    When I GET "/users/1"
    Then the response status should be 200
    And json path "id" should be 1
    And json path "email" should contain "@"
    And the response should match json schema "schemas/jsonplaceholder_user_schema.json"

  Scenario: List comments for a post
    When I GET "/comments?postId=1"
    Then the response status should be 200
    And the response array at "$" size should be greater than 0
    And each item in response array has json path "postId" equals 1
    And the response should match json schema "schemas/jsonplaceholder_comments_array_schema.json"

  Scenario: Get a todo
    When I GET "/todos/1"
    Then the response status should be 200
    And json path "id" should be 1
    And json path "userId" should be 1
    And the response should match json schema "schemas/jsonplaceholder_todo_schema.json"

  Scenario: Get an album
    When I GET "/albums/1"
    Then the response status should be 200
    And json path "id" should be 1
    And json path "userId" should be 1
    And json path "title" should not be null
    And the response should match json schema "schemas/jsonplaceholder_album_schema.json"

  Scenario: List photos in an album
    When I GET "/photos?albumId=1"
    Then the response status should be 200
    And the response array at "$" size should be greater than 0
    And each item in response array has json path "albumId" equals 1
    And the response should match json schema "schemas/jsonplaceholder_photos_array_schema.json"

  Scenario: Create a post
    When I POST "/posts" with json body:
      """
      {
        "title": "kc smoke",
        "body": "hello from automation",
        "userId": 1
      }
      """
    Then the response status should be 201
    And json path "id" should not be null
    And json path "userId" should be 1
    And the response should match json schema "schemas/jsonplaceholder_post_create_response_schema.json"

  Scenario: Update a post with PUT
    When I PUT "/posts/1" with json body:
      """
      {
        "id": 1,
        "title": "updated title",
        "body": "updated body",
        "userId": 1
      }
      """
    Then the response status should be 200
    And json path "id" should be 1
    And json path "title" should be "updated title"
    And the response should match json schema "schemas/jsonplaceholder_post_schema.json"

  Scenario: Partial update a post with PATCH
    When I PATCH "/posts/1" with json body:
      """
      {
        "title": "patched title"
      }
      """
    Then the response status should be 200
    And json path "title" should be "patched title"

  Scenario: Delete a post
    When I DELETE "/posts/1"
    Then the response status should be 200
