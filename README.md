# Decisions

I decided to break the project into two parts: 
- Create a small framework to handle requests, routing, and object initialization.
- Implement the API with all business logic.

I also avoided using any libraries/frameworks to demonstrate my skills and decisions as much as possible. The exceptions were the Jackson library 
for converting objects to/from JSON, the Reflections library, which was used for scanning classes with annotations, and JUnit/Mockito, 
which helps in writing tests.

## Framework
### Routing
Here I opted to implement an annotation-based routing system to handle all the requests. By doing so, I could extract all the routing logic 
from the application into the framework, making it easier to determine which route is handled by which method. Additionally, 
with this implementation, it is really simple to create new controllers. To implement routing, you need to:
- Have a class annotated with @Controller, specifying the base path of that REST controller.
- Have a method inside that controller annotated with @RequestMapping, declaring the request method. You can also specify a complementary path
that is concatenated with the controller's base path.
- To add query parameters, create method parameters annotated with @QueryParam, specifying the name of the parameter and whether it is optional.
- To add a request body, create a method parameter annotated with @Body.
- To send a custom HTTP status code on a successful request, annotate the method with @ResponseStatus, using the desired code as a parameter.

### DI
I decided to use simple dependency injection to make the code more flexible and easier to test. By doing that I could mock all the 
dependencies and isolate the tests of each component. To implement this, I created the [ApplicationContainer](src%2Fmain%2Fjava%2Fcom%2Fgambim%2Fframework%2Fcontainer%2FApplicationContainer.java)
class, which handles the orchestration of all object instances. I also implemented the application so that all classes annotated 
with @Controller or @Component are instantiated by the container. This made it simple to build all the application layers.

### Authentication
To make authentication generic and easy to implement, I used DI and inversion of control to create the [AuthenticationService](src%2Fmain%2Fjava%2Fcom%2Fgambim%2Fframework%2Fsecurity%2FAuthenticationService.java) 
interface. If an AuthenticationService implementation is created in the application using @Component, it will be used in the 
authentication flow. To use authentication, you should:
- Create an implementation of the AuthenticationService and annotate it with @Component, so it is created in the ApplicationContainer.
- Annotate the controller methods that require authentication with @Authenticated.

This approach allows me to reuse authentication across all endpoints, and it's easy to add authentication where needed. If the 
authentication needs to change, it's only necessary to change the AuthenticationService implementation.

### Data
To store all the data, I created an [InMemoryRepository](src%2Fmain%2Fjava%2Fcom%2Fgambim%2Fframework%2Fdata%2FInMemoryRepository.java) 
class with all the basic operations I needed: list, find, and save. All the data is stored in a HashMap using the entity 
ID as the key. This was done to improve the performance of find-by-ID operations, as the HashMap provides constant time for get operations.
The InMemoryRepository also implements the [Repository](src%2Fmain%2Fjava%2Fcom%2Fgambim%2Fframework%2Fdata%2FRepository.java) interface,
so replacing it with a database repository should be straightforward as long as the new repository implements the Repository interface.

The InMemoryRepository can store any class that extends the Entity class. I implemented it this way to reuse the repository across all 
models and ensure that all stored models have an ID.

I also built a [FileSeeder](src%2Fmain%2Fjava%2Fcom%2Fgambim%2Fframework%2Fdata%2FFileSeeder.java) that can be used to seed data from a 
JSON file to a repository when the application starts. It was built using Generics to be reusable for any model and its respective repository.



### Exceptions
I created multiple exceptions to handle errors in the application:
- [AuthenticationException](src%2Fmain%2Fjava%2Fcom%2Fgambim%2Fframework%2Fexception%2FAuthenticationException.java) handles authentication errors.
- [InvalidRequestException](src%2Fmain%2Fjava%2Fcom%2Fgambim%2Fframework%2Fexception%2FInvalidRequestException.java) handles errors in the request payload/parameters.
- [NotFoundException](src%2Fmain%2Fjava%2Fcom%2Fgambim%2Fframework%2Fexception%2FNotFoundException.java) handles route and resource not found errors.
- [ValidationException](src%2Fmain%2Fjava%2Fcom%2Fgambim%2Fframework%2Fexception%2FValidationException.java) handles the validation of models being created/updated.

All these exceptions are API exceptions and result in 4xx status codes, according to the exception. To handle exceptions
generated by the server code I created the [InternalException](src%2Fmain%2Fjava%2Fcom%2Fgambim%2Fframework%2Fexception%2FInternalException.java).
This exception results in a 500 status code and an "Internal server error." message.


## Product
I created a few layers to handle incoming requests, apply the business logic, manipulate data, and store it:

- controller: creates all the routes and maps them to service methods.
- service: responsible for all business logic and processing data between repositories and controllers
- repository: stores all the data.
- entity: models the data stored in the repositories.
- DTO: models the data received and sent in the controllers.
- converter: converts data between entities and DTOs.
- validation: validates DTOs before creating/updating.
- seeder: populates data in the application when it starts.

# Improvements
As the intent of this implementation was to showcase my coding skills, I avoided using libraries. However, a significant improvement would be 
to use some libraries to reduce boilerplate code, such as Project Lombok, which automatically generates getters, setters, and constructors, 
among other things, using annotations. Apache Commons could also be used to improve URI and parameter parsing.

The application container could be improved by providing a way to easily override components to give more flexibility to the framework. 
It would also be useful to configure methods that return objects to be added to the container. This would make it easier to add objects that 
need some configuration, like the Jackson ObjectMapper, which needs to register modules. Another option is to use a more robust framework like 
Spring Boot for additional options while expanding the API.

Authentication credentials shouldn't be fixed and hardcoded. It's better to remove them from the repository and use a credential service like 
AWS Secrets Manager or AWS Parameter Store.

Regarding the product itself, creating controllers to manage Locations and ServiceCategories is likely a necessary step to build the application.

If the goal is to deploy the application on AWS, a good option is to create the infrastructure using AWS CDK.

# API documentation

### Potential Vendors for a Given Job:
Request:
``` http request
GET http://localhost:8000/api/vendors?jobId={jobId}

Headers:
Authorization: Basic {token}
```
Where {jobId} should be the job ID for which you want to list the vendors and {token} is the basic authentication token.

Response:
``` json
[{
    "id": 3,
    "name": "vendor 3",
    "location": {
        "id": 10,
        "name": "Fayette",
        "state": "TX"
    },
    "serviceCategories": [{
        "id": 2,
        "name": "Air Conditioning",
        "compliant": true
    }]
}]
```

### Vendors Count for a Given Location and Service Category:
Request:
``` http request
GET http://localhost:8000/api/vendors/count?locationId={locationId}&serviceCategoryId={serviceCategoryId}
```
Where {locationId} and {serviceCategoryId} should be the location ID and service category ID, respectively, for which you want to list the vendors.

Response:
``` json
{
    "totalCount": 3,
    "compliantCount": 1,
    "notCompliantCount": 2
}
```

### Create Vendor:
Request:
``` http request
POST http://localhost:8000/api/vendors

Headers:
Authorization: Basic {token}

Body
{
    "name": "{vendorName}",
    "locationId": {locationId},
    "services": {
        "{serviceCategoryId1}": {compliant1},
        "{serviceCategoryId2}": {compliant2}
    }
}
```
where: 
- {vendorName} is the vendor name.
- {locationId} is the location ID.
- services is a map where {serviceCategoryIdX} is a service category id and {compliantX} is the compliant status (true/false) for that category.
- {token} is the basic authentication token.

Response:
``` json
{
    "id": 6,
    "name": "vendor 6",
    "location": {
        "id": 1,
        "name": "Glades",
        "state": "FL"
    },
    "serviceCategories": [
        {
            "id": 2,
            "name": "Air Conditioning",
            "compliant": true
        },
        {
            "id": 4,
            "name": "Landscaping Maintenance",
            "compliant": true
        }
    ]
}
```

### Create Job:
Request:
``` http request
POST http://localhost:8000/api/jobs

Headers:
Authorization: Basic {token}

Body
{
    "description": "{jobDescription}",
    "locationId": {locationId},
    "serviceCategoryId": {serviceCategoryId}
}
```
where:
- {jobDescription} is the job description.
- {locationId} is the location id.
- {serviceCategoryId} is the service category ID.
- {token} is the basic authentication token.

Response:
``` json
{
    "id": 6,
    "name": "vendor 6",
    "location": {
        "id": 1,
        "name": "Glades",
        "state": "FL"
    },
    "serviceCategories": [
        {
            "id": 2,
            "name": "Air Conditioning",
            "compliant": true
        },
        {
            "id": 4,
            "name": "Landscaping Maintenance",
            "compliant": true
        }
    ]
}
```

### Errors
All the endpoints can return errors in the following format
```json
{
    "message": "The vendor contains errors and cannot not be created.",
    "timestamp": "2024-09-04T02:19:30.883932888",
    "validationErrors": {
        "locationId": "Location not found.",
        "name": "Name cannot be blank."
    }
}
```

# Running the application

## Run on docker
```
docker build -t vs-tech-test .

docker run -dp 127.0.0.1:8000:8000 vs-tech-test 
```

## Run without docker
```
./gradlew run
```

## Run tests
```
./gradlew clean test
```
