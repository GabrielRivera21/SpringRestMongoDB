## SpringRestMongoDB
A Spring Restful Micro Service with MongoDB as it's backend.

## Requirements
You will need to download and install correctly the following programs.

- [MongoDB](https://www.mongodb.org/)
- [Eclipse Luna with gradle plugin](https://eclipse.org/luna/)
- [SendGrid Account](https://sendgrid.com)

## Setting up The Project
##### Clone the Repository
On your Terminal or Git Shell execute the following command on the directory where you want to clone the project.

```bash
$ git clone https://github.com/GabrielRivera21/SpringRestMongoDB.git
```

##### Import the Project to Eclipse
1. Click on <b>File</b> -> <b>Import...</b>
2. Select on <b>General</b> -> <b>Existing Projects into Workspace</b>
3. Then specify the directory where you cloned the project on Root Directory
4. Check the Checkbox of the Project
5. Click <b>Finish</b>

#### Setting up the Environment

Go to the `src/main/resources` directory and open `application.properties` input your MongoDb uri or localhost, your database name and SendGrid's username and password.

## Running the Application

_To run the application:

1. (Menu Bar) Run->Run Configurations
2. Under Java Applications, select your run configuration for this app
3. Open the Arguments tab
4. In VM Arguments, provide the following information to use the default keystore provided with the sample code:

-Dkeystore.file=src/main/resources/private/.keystore -Dkeystore.pass=changeit

5. Note, this keystore is highly insecure! If you want more security, you should obtain a real SSL certificate:

http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html

6. This keystore is not secured and should be in a more secure directory -- preferably completely outside of the app for non-test applications -- and with strict permissions on which user accounts can access it

## Accessing the Service

Note: you need to use "https" and port "8443":

https://localhost:8443/users

You will almost certainly see a warning about the site's certificate in your browser. This warning is being generated because the keystore includes a certificate that has not been signed by a certificate authority.

If you try to access the above URL in your browser, the server is going to generate an error that looks something like "An Authentication object was not found in the SecurityContext." If you want to use your browser to test the service, you will need to use a plug-in like Postman and an understanding of how to use it to manually construct and obtain a bearer token.

The UsersSvcClientApiTest shows how to programmatically access the services. You should look at the SecuredRestBuilder class that is used to automatically intercept requests to the UsersSvcApi methods, automatically obtain an OAuth 2.0 bearer token if needed, and add this bearer token to HTTP requests.

###Obtaining the OAuth2 Token

In order to obtain the Token in order to access the token you need to make an HTTP POST using a grant_type of password as seen in the `SecuredRestBuilder` class to the /oauth/token path.

#####Request
```
POST /oauth/token HTTP/1.1
Host: localhost:8443
Content-Type: application/x-www-form-urlencoded

username=youruser&
password=yourpass&
client_id=mobile-spring&
client_secret=&
grant_type=password
```

#####Response
```
{
  "access_token":"7186f8b2-9bae-48b6-90c2-033a4476c0fc",
  "token_type":"bearer",
  "refresh_token":"d7fe8cda-812b-4b3e-9ce7-b15067e001e4",
  "expires_in":298653
}
```

### Accesing API Docs

To access the docs just go this url on your browser:
```
https://localhost:8443/docs
```

and you will see the API with Response and Request Models and Info.

## What to Pay Attention to


In this application, we are using MongoDB to store data. See the Model package and Repository classes to see how the application stores and queries the database through the repository classes. 
See the src/main/resources/application.properties file for configuration
options if you want to connect to a remote MongoDB instance.

