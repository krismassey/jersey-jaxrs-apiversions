package com.kris.massey;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;


public class ApiVersionDispatchProviderTest extends JerseyTest {


    public static final String VERSION_HEADER_NAME = "Version";
    public static final String INVALID_VERSION_HEADER = "INVALID";

    public ApiVersionDispatchProviderTest()throws Exception {
        super(new WebAppDescriptor.Builder()
                .initParam(PackagesResourceConfig.PROPERTY_PACKAGES, "com.kris.massey")
                .initParam(PackagesResourceConfig.PROPERTY_RESOURCE_FILTER_FACTORIES, "com.kris.massey.ApiVersionDispatchProvider")
                .build());
    }

    @Test
    public void headerRequiredButNotSentResultsInBadRequired() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/required/noMinOrMax")
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
        String responseBody = response.getEntity(String.class);
        assertThat(responseBody, equalTo("{\"message\"{\"versionHeaderName: \"" + VERSION_HEADER_NAME + "\"\", \"headerRequired\":true,\"minVersion\":\"no minimum version\",\"maxVersion\":\"no maximum version\"}}"));
    }

    @Test
    public void headerRequiredAndIsSameAsMinSupportedVersion() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/required/min/two/max/three")
                .header(VERSION_HEADER_NAME, 2)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void headerRequiredAndIsBetweenMinAndMaxSupportedVersion() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/required/min/two/max/three")
                .header(VERSION_HEADER_NAME, 2.5)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void headerRequiredAndIsSameAsMaxSupportedVersion() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/required/min/two/max/three")
                .header(VERSION_HEADER_NAME, 3)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void headerRequiredAndIsUnderMinSupportedVersion() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/required/min/two/max/three")
                .header(VERSION_HEADER_NAME, 1)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void headerRequiredAndIsLoverMaxSupportedVersion() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/required/min/two/max/three")
                .header(VERSION_HEADER_NAME, 4)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void headerNotRequiredAcceptsRequestWithNoHeaderIgnoresAnyHeaderValue() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/notRequired/min/two/max/three")
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void headerNotRequiredRejectsVersionUnderMin() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/notRequired/min/two/max/three")
                .header(VERSION_HEADER_NAME, 1)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void headerNotRequiredRejectsVersionOverMax() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/notRequired/min/two/max/three")
                .header(VERSION_HEADER_NAME, 4)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
     public void headerNotRequiredValidatesWithinMinAndMax() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/notRequired/min/two/max/three")
                .header(VERSION_HEADER_NAME, 2.5)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.NO_CONTENT.getStatusCode()));
    }

    @Test
    public void notRequiredButInvalidHeaderIsRejected() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/notRequired/min/two/max/three")
                .header(VERSION_HEADER_NAME, INVALID_VERSION_HEADER)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void requiredButInvalidHeaderIsRejected() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/required/min/two/max/three")
                .header(VERSION_HEADER_NAME, INVALID_VERSION_HEADER)
                .get(ClientResponse.class);

        assertThat(response.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    }

    @Test
    public void badRequestContainsVersionHeaderNameInResponseJson() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/required/noMinOrMax")
                .get(ClientResponse.class);

        String responseBody = response.getEntity(String.class);
        assertThat(responseBody, containsString("\"versionHeaderName: \"" + VERSION_HEADER_NAME + "\""));
    }

    @Test
    public void badRequestContainsHeaderNotRequiredIfApiDoesNtoRequireVersionHeader() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/notRequired/min/two/max/three")
                .header(VERSION_HEADER_NAME, INVALID_VERSION_HEADER)
                .get(ClientResponse.class);

        String responseBody = response.getEntity(String.class);
        assertThat(responseBody, containsString("\"headerRequired\":false"));
    }

    @Test
    public void badRequestContainsHeaderRequiredIfApiRequiredVersionHeaderToBeSet() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/required/noMinOrMax")
                .get(ClientResponse.class);

        String responseBody = response.getEntity(String.class);
        assertThat(responseBody, containsString("\"headerRequired\":true"));
    }

    @Test
    public void badRequestContainsNoMinVersionInResponseJsonIfApiDoesNotHaveVersionSet() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/required/noMinOrMax")
                .get(ClientResponse.class);

        String responseBody = response.getEntity(String.class);
        assertThat(responseBody, containsString("\"minVersion\":\"no minimum version\""));
    }

    @Test
    public void badRequestContainsNoMaxVersionInResponseJsonIfApiDoesNotHaveVersionSet() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/required/noMinOrMax")
                .get(ClientResponse.class);

        String responseBody = response.getEntity(String.class);
        assertThat(responseBody, containsString("\"maxVersion\":\"no maximum version\""));
    }

    @Test
    public void badRequestContainsMinVersionNumber() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/notRequired/min/two/max/three")
                .header(VERSION_HEADER_NAME, INVALID_VERSION_HEADER)
                .get(ClientResponse.class);

        String responseBody = response.getEntity(String.class);
        assertThat(responseBody, containsString("\"minVersion\":2"));
    }

    @Test
    public void badRequestContainsMaxVersionNumber() {
        WebResource webResource = resource();

        ClientResponse response = webResource
                .path("/notRequired/min/two/max/three")
                .header(VERSION_HEADER_NAME, INVALID_VERSION_HEADER)
                .get(ClientResponse.class);

        String responseBody = response.getEntity(String.class);
        assertThat(responseBody, containsString("\"maxVersion\":3"));
    }

}
