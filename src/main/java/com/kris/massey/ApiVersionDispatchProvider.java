package com.kris.massey;


import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.*;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.List;

@Provider
public class ApiVersionDispatchProvider implements ResourceFilterFactory {


    private final String BAD_REQUEST_RESPONSE_TEMPLATE = "{\"message\"{\"versionHeaderName: \"%s\"\", \"headerRequired\":%b,\"minVersion\":%s,\"maxVersion\":%s}}";
    private final ApiVersionMatcher apiVersionMatcher;
    private final String versionHeaderName;

    public ApiVersionDispatchProvider(ApiVersionMatcher apiVersionMatcher, String versionHeaderName) {
        this.apiVersionMatcher = apiVersionMatcher;
        this.versionHeaderName = versionHeaderName;
    }

    public ApiVersionDispatchProvider() {
        this.versionHeaderName = "Version";
        this.apiVersionMatcher = new DefaultApiVersionMatcher(versionHeaderName);
    }

    @Override
    public List<ResourceFilter> create(AbstractMethod am) {
        List<ResourceFilter> response = null;
        // Add VersionFilter for resource methods annotated with @Secure annotation (ignore other resource methods).
        boolean hasApiVersionAnnotation = am.isAnnotationPresent(ApiVersion.class);

        if(hasApiVersionAnnotation) {
            ApiVersion annotation = am.getAnnotation(ApiVersion.class);
            boolean headerRequired = annotation.headerRequired();
            float minVersion = annotation.minVersion();
            float maxVersion = annotation.maxVersion();
            response = Collections.<ResourceFilter>singletonList(new VersionFilter(headerRequired, minVersion, maxVersion));
        }

        return response;
    }

    private class VersionFilter implements ResourceFilter, ContainerRequestFilter {

        private final boolean headerRequired;
        private final float minVersion;
        private final float maxVersion;

        private VersionFilter(boolean headerRequired, float minVersion, float maxVersion) {
            this.headerRequired = headerRequired;
            this.minVersion = minVersion;
            this.maxVersion = maxVersion;
        }

        @Override
        public ContainerRequestFilter getRequestFilter() {
            return this;
        }

        @Override
        public ContainerResponseFilter getResponseFilter() {
            return null;
        }

        @Override
        public ContainerRequest filter(final ContainerRequest request) {

            if(apiVersionMatcher.supportsRequestedVersion(headerRequired, minVersion, maxVersion, request)) {
                return request;
            }

            String minVersionString = minVersion == 0 ? "\"no minimum version\"" : String.valueOf(minVersion);
            String maxVersionString = minVersion == 0 ? "\"no maximum version\"" : String.valueOf(maxVersion);

            Response badRequest = Response.status(Response.Status.BAD_REQUEST)
                    .entity(String.format(BAD_REQUEST_RESPONSE_TEMPLATE, versionHeaderName, headerRequired, minVersionString, maxVersionString))
                    .build();

            throw new WebApplicationException(badRequest);
        }
    }
}