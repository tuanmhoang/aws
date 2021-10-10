### How to run
1. Go to module `jmp-service-rest`
2. Run class `io.learn.restservice.RestServiceApplication` from IDEA


### How to use with Swagger UI
1. Open in browser Swagger Documentation with link to [Swagger UI](http://localhost:9090/swagger-ui/)
2. 

### How to user with curl
1. Create sample user with command:
```shell
curl -X POST "http://localhost:9090/v1/users" -H "accept: */*" -H "Content-Type: application/json" -d "{\"birthday\":\"2001-03-20\",\"name\":\"Pepa\",\"surname\":\"Pig\"}"
```

response
```text
201 Created
{
  "id": 1,
  "name": "Pepa",
  "surname": "Pig",
  "birthday": "2001-03-20",
  "_links": {
    "self": {
      "href": "http://localhost:9090/v1/users/1"
    },
    "users": {
      "href": "http://localhost:9090/v1/users"
    },
    "delete": {
      "href": "http://localhost:9090/v1/users/1"
    }
  }
}
```

2. Get information about user with id from previous response
```shell
    curl -X GET "http://localhost:9090/v1/users/1" -H "accept: */*"
```
response
```text
200 OK
{
  "id": 1,
  "name": "Pepa",
  "surname": "Pig",
  "birthday": "2001-03-20",
  "_links": {
    "self": {
      "href": "http://localhost:9090/v1/users/1"
    },
    "users": {
      "href": "http://localhost:9090/v1/users"
    },
    "delete": {
      "href": "http://localhost:9090/v1/users/1"
    }
  }
}
```

3. Update information about user with command:
```shell
curl -X PUT "http://localhost:9090/v1/users/1" -H "accept: */*" -H "Content-Type: application/json" -d "{\"birthday\":\"2005-05-11\",\"id\":1,\"name\":\"Pepe\",\"surname\":\"Frog\"}"
```
response
```text
200 OK
{
  "id": 1,
  "name": "Pepe",
  "surname": "Frog",
  "birthday": "2005-05-11",
  "_links": {
    "self": {
      "href": "http://localhost:9090/v1/users/1"
    },
    "users": {
      "href": "http://localhost:9090/v1/users"
    },
    "delete": {
      "href": "http://localhost:9090/v1/users/1"
    }
  }
}
```

4. Get all users
```shell
curl -X GET "http://localhost:9090/v1/users" -H "accept: */*"
```
response
```text
200 OK
[
  {
    "id": 1,
    "name": "Pepe",
    "surname": "Frog",
    "birthday": "2000-01-02",
    "links": [
      {
        "rel": "self",
        "href": "http://localhost:9090/v1/users/1"
      },
      {
        "rel": "users",
        "href": "http://localhost:9090/v1/users"
      },
      {
        "rel": "delete",
        "href": "http://localhost:9090/v1/users/1"
      }
    ]
  }
]
```

4. Add subscription with command:
```shell
curl -X POST "http://localhost:9090/v1/subscriptions" -H "accept: */*" -H "Content-Type: application/json" -d "{\"startDate\":\"2020-02-03\",\"userId\":1}"
```
response
```text
201 Created
{
  "id": 1,
  "userId": 1,
  "startDate": "2020-02-03",
  "_links": {
    "self": {
      "href": "http://localhost:9090/v1/subscriptions/1"
    },
    "subscriptions": {
      "href": "http://localhost:9090/v1/subscriptions"
    },
    "delete": {
      "href": "http://localhost:9090/v1/subscriptions/1"
    }
  }
}
```

5. Remove user, should work for not existing users also (or already deleted)
```shell
curl -X DELETE "http://localhost:9090/v1/users/2" -H "accept: */*"
```

response
```text
204 No Content
```

To run docker: use docker compose up