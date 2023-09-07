# cats-crud-service

## tech stack
- general: Scala 2, Tagless Final
- typelevel: Cats / Cats Effects IO, Http4s, fs2 Stream
- db: Doobie, SQLite (in memory light db), flyway (version controlled db schema evolution)
- code generator: guardrail.dev (for Http4s server, based on OpenAPI specification)
- utils: enumeratum Enums 
- json serialziers: Circe
- testing: Fixture, Property based testing (effectful and non-effectful)

## todo
- plugin scalafmt (I want some nice formatting: for-comprehension align, https://stackoverflow.com/questions/47465834/code-formatting-how-to-align-inside-for-comprehension
- http/db-doobie logging
- cache (effectful?)
- config: pureconfig ?
- testing: circe golden

### Get all
http://localhost:8080/people/api/all

### Get by ID
http://localhost:8080/people/api/person/test-id-1

### Delete
curl -X DELETE http://localhost:8080/people/api/person/test-id-1

### Update
curl -X PUT http://localhost:8080/people/api/person/test-id-2 -H 'Content-Type: application/json' -d '{ "name":"OLD JACK", "age":99, "sex": "Male", "credit":200.123, "joined":6000 }'
   
### Create
curl -X POST http://localhost:8080/people/api/person -H 'Content-Type: application/json' -d '{ "name":"OLD MERRY", "age":70, "sex": "Female", "credit":34200.123, "joined":200 }'
