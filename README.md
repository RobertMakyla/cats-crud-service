# cats-crud-service

## tech stack
- general: Scala 2, Tagless Final
- typelevel: Cats / Cats Effects IO, Http4s, fs2 Stream
- db: Doobie, SQLite (in memory light db), flyway (version controlled db schema evolution)
- code generator: guardrail.dev (for Http4s server, based on OpenAPI specification)
- utils: enumeratum Enums , log4cats
- json serializers: Circe
- testing: Fixture, Property based testing (effectful and non-effectful)
- logging Slf4j (logback.xml) effectful for the app, non-effectful for doobie LogHandler
- config: pureconfig

## todo
- CodecsSpec- fix 
- CodecsSpec- complete ( all contract types, examples - decoding/encoding)
- add circe golden tests (check if each rebuild is changing seed and renaming test files - if so fix it with some simple custom functionality)
- apply Contract into Person (inser/update from HTTP should be an option)

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
