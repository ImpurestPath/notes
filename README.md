# Notes - simple REST application to store your notes

## How to get it

Use `git clone` command to get sources of this application
Then you can either build jar file to run in your system or build docker container that include all needed files.
In both cases all notes are stored in H2 database in data folder.

### JAR file

Jar is built by using `./mvnw clean install`

Start the application from repository root by using ` java -jar target/notes-0.0.1-SNAPSHOT.jar`


### Docker container

Docker container is built by using `./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=notes`

Start the application by using `docker run -p 8080:8080 notes`



## Main functions

You can use Swagger-ui after starting the app at
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) 
to see and try all endpoints.

### Notes
Notes model for all endpoints is 
```
  {
    "content": "string",
    "createdAt": "2021-06-12T13:48:39.822Z",
    "id": integer,
    "name": "string",
    "tags": [
      {
        "id": integer,
        "name": "string"
      }
    ]
  }
```
Id of tag and note is not required on creating, but required on updating and removing. 

Name and tags are not required.

Created time is generated on server side.

Content is required.
#### Get notes

All notes can be found at `GET /notes`. It produces json array.

Also, notes can be filtered by:
* Created time. Returns all notes that were created after specified time.  `GET /notes/since/{time}`
* Tag. Returns all notes with specified tag. `GET /notes/tag/{tagId}`
* Search query. Returns all notes that contains search query in name or content. `GET /notes/search/{query}`

#### Add note

Note can be added by sending `PUT /notes` with json note in body.

#### Update note

Note can be updated by sending `POST /notes` with json note (that must contains id) in body.

#### Removed note

Note can be removed by sending `DELETE /notes` with json note (that must contains id) in body.


### Tags

Tag model for requests is 
```
{
"id": integer,
"name": "string"
}
```
Name is unique and two tags with same name and different id are not exist.
#### Get tags

All tags can be found at `GET /tags`. It produces json array.

#### Create tags

Tags are created when creating or updating note. No endpoint for this action.

#### Update or delete tag

Tags cannot be updated or deleted.