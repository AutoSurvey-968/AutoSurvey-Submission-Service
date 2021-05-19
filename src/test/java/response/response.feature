Feature: get all responses

Scenario: get all responses
Given url submissionUrl
When method get
Then status 200
And match response == {  }## single response

Scenario: get a response
When method get
Then status 200
And match response == {  }##multiple responses

Scenario: get a response that doesn't exist
When method get
Then status 400
##maybe a match response too

Scenario: delete a response
Given url submissionUrl
When method delete
Then status 204

Scenario: delete a response that doesn't exist
Given url submissionUrl
When method delete
Then status 400

Feature: upload
Scenario: upload a csv
Given url submissionUrl
When method add
Then status 201
And match response == {  }##multiple responses

Scenario: upload a response
Given url submissionUrl
When method add
Then status 201
And match response == {  }## single response

Scenario: upload a not response
Given url submissionUrl
When method add
Then status 400

Scenario: update a response
Given url submissionUrl
When method put
Then status 200
And match response == {  }## single response
