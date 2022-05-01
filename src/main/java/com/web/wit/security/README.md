## Security package
### Authentication & authorization
Method used for auth is **JWT**. 

**access token** expiration time: **15** minutes

**refresh token** expiration time: **24** hours

### ID as Username
Spring Security is using **username** as unique identification for **User**.

In this project **User** has both **ID** and **username**, but only **ID** is unique, 
so we are using user ID in some **"Username"** fields in Spring security.

Example:
```java
String id = request.getParameter("id");
String password = request.getParameter("password");
// IMPORTANT NOTE: UsernamePasswordAuthenticationToken takes user ID as username
UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);
```