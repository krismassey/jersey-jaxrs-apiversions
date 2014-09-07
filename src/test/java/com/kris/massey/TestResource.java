package com.kris.massey;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class TestResource {

    @GET
    @ApiVersion
    @Path("/required/noMinOrMax")
    public void requiredNoMinOrMax() {
    }

    @GET
    @ApiVersion(minVersion = 2, maxVersion = 3)
    @Path("/required/min/two/max/three")
    public void requiredMinTwoMaxThree() {
    }

    @GET
    @ApiVersion(headerRequired = false, minVersion = 1.2f, maxVersion = 2.0f)
    @Path("/notRequired/minAndMaxSet")
    public void minAndMaxButNotRequired() {
    }


    @GET
    @ApiVersion(headerRequired = false, minVersion = 2, maxVersion = 3)
    @Path("/notRequired/min/two/max/three")
    public void notSet() {
    }
}
