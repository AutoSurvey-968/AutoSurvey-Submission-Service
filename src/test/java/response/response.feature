Feature: get all responses
Scenario: get all responses

Given url submissionUrl
When method get
Then status 200
And match response == {  }