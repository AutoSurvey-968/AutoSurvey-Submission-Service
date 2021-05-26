@submission-tests
Feature: tests all the functions for Surveys

Background:
* def kittens = read('test.json')
* url "http://localhost:8080"


Scenario: 
Take in JSON object, Create a new Response
Take in CSV object, Create a new Response
Take in an ID, update the response
Return an array of all Responses based on the Optionals
Take in an ID, delete the Response from the db
    
