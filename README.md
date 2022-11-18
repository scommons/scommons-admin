
[![CI](https://github.com/scommons/scommons-admin/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/scommons/scommons-admin/actions/workflows/ci.yml?query=workflow%3Aci+branch%3Amaster)
[![Coverage Status](https://coveralls.io/repos/github/scommons/scommons-admin/badge.svg?branch=master)](https://coveralls.io/github/scommons/scommons-admin?branch=master)
[![Docker image](https://img.shields.io/docker/v/scommons/admin?label=docker%20image&sort=semver)](https://hub.docker.com/r/scommons/admin)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-1.8.0.svg)](https://www.scala-js.org)

# scommons-admin
Easy applications, users and permissions management

## How to Setup DB

To setup the Postgres DB, please, follow the instructions
[here](https://github.com/scommons/scommons-admin/blob/master/db_setup.md)

## How to Run App using pre-built Docker image

```bash
docker run -d --name scommons-admin -p 9000:9000 \
  -e "POSTGRES_PASSWORD=mysecretpassword" \
  -e "ADMIN_DB_HOST=172.17.0.1" \
  -e "ADMIN_DB_PORT=5432" \
  -e "ADMIN_DB_USER=admin" \
  -e "ADMIN_DB_PASSWORD=admin" \
  -e "ADMIN_DB_ADMIN_USER=admin_admin" \
  -e "ADMIN_DB_ADMIN_PASSWORD=superadmin" \
  -e "JAVA_OPTS=-Dlogger.resource=/logback.prod.xml -Xmx448M -XX:MaxMetaspaceSize=128m -XX:+PrintCommandLineFlags" \
  scommons/admin
```

## How to Build and Run locally

To build and run ALL tests use the following command:
```bash
sbt clean "project scommons-admin-server" test it:test && sbt ";project scommons-admin-server ;set Test / test := {} ;project scommons-admin" test
```

#### How to Run Server locally in DEV mode

Before you can run server, please, make sure you have PostgreSQL DB up and running.

To start the application server locally in development mode with refresh workflow:
```bash
sbt clean ';project scommons-admin-server ;set Assets / WebKeys.exportedMappings := Seq()' run
```

## Admin Client UI

To see the Admin Client UI in browser:
```
http://localhost:9000/scommons-admin/admin.html
```

## REST API Documentation

To see the Swagger REST API documentation page and try endpoints in browser:
```
http://localhost:9000/scommons-admin/swagger.html
```

### Documentation

You can find more documentation
[here](https://scommons.org/scommons-admin/)

### Screenshots

#### Assign users to applications

![Screenshot](https://raw.githubusercontent.com/scommons/scommons-admin/master/docs/images/screenshots/user-apps.png)

#### Assign roles to users

![Screenshot](https://raw.githubusercontent.com/scommons/scommons-admin/master/docs/images/screenshots/user-roles.png)

#### Assign permissions to roles

![Screenshot](https://raw.githubusercontent.com/scommons/scommons-admin/master/docs/images/screenshots/role-permissions.png)

