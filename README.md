jersey-jaxrs-apiversions
========================

Easily specify versions on JAX-RS apis. 


# Getting Started

This package is designed to allow JAX-RS APIs to be easily versioned. This project allows you to version an API through the use of an
@ApiVersion annotation. By default the API versioning is achieved through the use of a request header named 'Version'.

```java
@GET
@ApiVersion
@Path("/path")
public void processPath() {
    //IMPLEMENTATION
}
```

The ApiVersion annotation has 3 optional fields:

* headerRequired
    * Default value: true
* minVersion
    * Default value: 0 - Version 0 denotes all versions supported
* maxVersion
    * Default value: 0 - Version 0 denotes all versions supported

# Example Use

If you don't want the user to specify a version you can make the 'Version' header optional by specifying it within the
annotation as below.

```java
@ApiVersion(headerRequired = false)
```

If an API is only supported from a specific version, you can use the 'minVersion' annotation parameter.

```java
@ApiVersion(minVersion = 1)
```

If an API is only supported up until a specific version, you can use the 'maxVersion' annotation parameter.

```java
@ApiVersion(maxVersion = 2.5)
```

# Version formatting
The @ApiVersion annotation will support full and point version, as long as the provided number is a valid Float it is
supported.

# Error Response
When the version header provided buy the client does not met the API version requirements, a 400 Bad Request along with
an over view of the issue within the response body. An example of this is below:

```json
{
    "message": {
        "versionHeaderName": "Version",
        "headerRequired": true,
        "minVersion": 1,
        "maxVersion": 2.5
    }
}
```

## Default versioning strategy

The constructor of the **ApiVersionResourceFilterFactory** class allows two parameters, the first is a custom
**ApiVersionMatcher**, and the second is a custom name for the **versionHeader**. By default the **DefaultApiVersionMatcher**
 is used and the default versioning header name 'Version'.

## DefaultApiVersionMatcher

 The default implementation for versioning matching is within the [DefaultApiVersionMatcher](https://github.com/krismassey/jersey-jaxrs-apiversions/blob/master/src/main/java/com/kris/massey/DefaultApiVersionMatcher.java).