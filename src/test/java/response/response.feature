Feature: get all responses
Scenario: get all responses

Given url submissionUrl
When method get
Then status 200
And match response == {  }

Scenario: delete a response

Given url submissionUrl
When method delete
Then status 204

Scenario: upload a csv
Scenario: upload a response
Scenario: update a response
Scenario: get a response