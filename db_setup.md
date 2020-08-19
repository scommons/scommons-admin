
## How to Setup Postgres DB

### Create and start the `postgres` docker container

```bash
docker run -d --name postgres -p 5432:5432 -e "POSTGRES_PASSWORD=mysecretpassword" postgres:9.6.9
```

### Create the `admin_db`

```bash
docker exec -it postgres psql -U postgres -w
```

Then copy and paste the content of the file `./dao/src/main/resources/scommons/admin/dao/changelog/createDb.sql`

Exit the `psql` console by typing `\q` and then `enter`.

### Create initial schema in the `admin_db`

```bash
docker exec -it postgres psql -U admin_admin -w -d admin_db
```

Then paste the content of the file `./dao/src/main/resources/scommons/admin/dao/changelog/initialSql.sql`

Exit the `psql` console by typing `\q` and then `enter`.

### Create initial test user

The following script will crate `test` user with `test` password and `SUPERUSER` access

```bash
docker exec -it postgres psql -U admin_admin -w -d admin_db
```

and paste the content of the file `./server/src/it/resources/test_data.sql`

Exit the `psql` console by typing `\q` and then `enter`.
