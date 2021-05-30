# AutoSurvey-Submission-Service

## Project Description

The submission microservice for AutoSurvey-968. Hello, I am Testy McTesterson.

## Technologies Used

* Java - SE1.8
* Java Spring
  - Reactive Web
* SonarCloud
* Lombok
* Swagger
* DataStax
* Eureka
* Karate
* JUnit
* Jacoco
* Mockaroo

## Implemented Features

- [x] Create and maintain tables of anonymous survey responses
- [x] Convert and upload responses to a database from a .csv file
- [x] Upload direct user responses to a database
- [x] Get responses, organized by batch number or batch's calendar week (or both!)
- [x] Able to update and delete specific responses

## Getting Started

**See [Primary README.md](https://github.com/AutoSurvey-968/AutoSurvey-back) for full program setup instructions.**

Set environment variables:
* AWS_USER - Keyspaces username
* AWS_PASS - Keyspaces password
* TRUST_PASS - Local Truststore password

## Usage

```
{base-url}/
```

### GET:
**Authorization level**: ADMIN-ONLY

Gets all responses in database.

### POST:
**Authorization level**: ADMIN-ONLY

Submits a response or multiple responses to the database.

Accepts a .csv file.

```
{base-url}/:id
```
### PUT:
**Authorization level**: ADMIN-ONLY

Update a specific response

### DELETE:
**Authorization level**: ADMIN-ONLY

Delete a specific response

## Contributors

- [Matt Kerwin](https://github.com/MatKerwin)
- [Christopher Morrison](https://github.com/cmorrison-rev)
- [Nerijus Gelezinis](https://github.com/NGelezinis)
- [Robert Bierly](https://github.com/rnbiv45)
- [Arieh Gennello](https://github.com/MoldedPixels)


## License

This project uses the following license: [<license_name>](<link>).
