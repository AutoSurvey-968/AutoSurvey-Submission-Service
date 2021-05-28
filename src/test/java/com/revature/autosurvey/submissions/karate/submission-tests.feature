@submission-tests
Feature: tests all the functions for Surveys

Background:
* def jsonResponse = read('test.json')
* def csvResponse = read('test2.csv')
* url "http://localhost:8080"


Scenario: 
Take in JSON object, Create a new Response
Take in CSV object, Create a new Response
Take in an ID, update the response
Return an array of all Responses based on the Optionals
Take in an ID, delete the Response from the db

##POST - create new response from JSON    
Given request jsonResponse
When method POST
Then status 201
And match response contains { uuid: '#present', title: 'This is a second title' }

* def uuid = response.uuid

##POST - create new response from CSV
Given request csvResponse
When method POST
Then status 201
And match response contains { uuid: '#present', title: 'This is a second title' }

##PUT - update response by ID
Given path "/" + uuid
AND request jsonResponse
When method PUT
Then status 200

##GET - returns array of every survey as JSON
When method GET
Then status 200
And match response == '#notnull'

##DELETE - deletes survey
Given path "/" + uuid
When method DELETE
Then status 204