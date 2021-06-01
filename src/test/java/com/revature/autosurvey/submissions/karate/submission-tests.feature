@submission-tests
Feature: tests all the functions for Submissions

Background:
* def jsonResponse = read('AutoSurvey_mock_data.json')
* url "http://localhost:8080"


Scenario: 
Take in JSON object, Create a new Response
Take in CSV object, Create a new Response
Take in an ID, update the response
Return an array of all Responses based on the Optionals
Take in an ID, delete the Response from the db

##POST - create new response from JSON    
##Given request jsonResponse
##When method POST
##Then status 201
##And match response contains { uuid: '#present', title: 'This is a second title' }

##* def uuid = response.uuid

##POST - create new response from CSV

Given multipart file csvfile = {read:'classpath:/com/revature/autosurvey/submissions/karate/testdata.csv', filename:'testdata.csv'} 
And multipart field surveyId = 'cf0821d5-ef88-4da6-a197-a993fde05683'
When method POST
Then status 200

* def uuid = response.uuid

##PUT - update response by ID
Given path "/" + uuid
And request jsonResponse
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