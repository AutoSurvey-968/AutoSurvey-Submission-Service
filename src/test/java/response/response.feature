Feature: get all responses
Scenario: get all responses

Given url submissionUrl
When method get
Then status 200
And match response == {  }

Scenario: get a response
When method get
Then status 200
And match response == {  }

Scenario: delete a response

Given url submissionUrl
When method delete
Then status 204

Feature: upload
Scenario: upload a csv
Given url submissionUrl
When method add

Scenario: upload a response
Given url submissionUrl
When method add

Scenario: update a response
Given url submissionUrl
When method put
