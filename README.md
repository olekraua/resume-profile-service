# profile-service

Standalone repository for the profile-service microservice.

## Local build

```bash
./mvnw -pl microservices/backend/services/profile-service -am -Dmaven.test.skip=true package
```

## Local run

```bash
./mvnw -pl microservices/backend/services/profile-service -am spring-boot:run
```

## Included modules

- shared
- staticdata
- profile
- notification
- auth
- media
- web
- search
- microservices/backend/services/profile-service

