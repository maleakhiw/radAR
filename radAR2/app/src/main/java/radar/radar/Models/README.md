# Models
Data models for the application. Also part of the Model in Model-View-Presenter.

## Motivation
The application should communicate in terms of common objects, for consistency of code and ease of inter-class communication.

## Directories
- Android: Android-only objects, used to support inter-object communication
- Domain: represents domain objects, most of which having counterparts in `backend/models`. These are POJO (plain-old Java objects), which the GSON library serialises from and to JSON.
- Requests: represents objects used as the body for HTTP requests. As above, GSON serialises from these objects to JSON.
- Responses: represents objects used as the response body for HTTP requests. As above, GSON deserialises from JSON to these POJOs.

Details on Domain, Requests and Responses can also be seen in the backend code or our API documentation.
