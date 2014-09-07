package com.kris.massey;

import com.sun.jersey.spi.container.ContainerRequest;

public interface ApiVersionMatcher {

    public boolean supportsRequestedVersion(boolean headerRequired, float minVersion, float maxVersion, ContainerRequest request);
}
