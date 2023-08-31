# cats-crud-service

## tech stack
general: Scala 2, Tagless Final
typelevel: Cats / Cats Effects IO, Http4s, fs2 Stream
db: Doobie, SQLite (in memory light db), flyway (version controlled db schema evolution)
code generator: guardrail.dev (for Http4s server, based on OpenAPI specification)
utils: enumeratum Enums 
json serialziers: Circe
testing: Fixture, Property based testing (effectful and non-effectful)

## todo
config: pureconfig
cats IO cache 