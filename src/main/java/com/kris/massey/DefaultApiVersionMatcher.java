package com.kris.massey;

import com.sun.jersey.spi.container.ContainerRequest;

public class DefaultApiVersionMatcher implements ApiVersionMatcher {

    private final String versionHeaderName;

    public DefaultApiVersionMatcher(String versionHeaderName) {
        this.versionHeaderName = versionHeaderName;
    }

    @Override
    public boolean supportsRequestedVersion(boolean headerRequired, float minVersion, float maxVersion, ContainerRequest request) {
        boolean isSupported = true;

        String versionHeader = request.getHeaderValue(versionHeaderName);

        if (headerRequiredButNotPresent(headerRequired, versionHeader) || headerPresentButNotSupported(minVersion, maxVersion, versionHeader)) {
            isSupported = false;
        }

        return isSupported;
    }

    private boolean headerIsPresent(String versionHeader) {
        return versionHeader != null && !versionHeader.isEmpty();
    }

    private boolean headerIsNotPresent(String versionHeader) {
        return !headerIsPresent(versionHeader);
    }

    private boolean headerRequiredButNotPresent(boolean headerRequired, String versionHeader) {
        return headerRequired && headerIsNotPresent(versionHeader);
    }

    private boolean headerPresentButNotSupported(float minVersion, float maxVersion, String versionHeader) {
        return headerIsPresent(versionHeader) && isInvalidOrNotSupported(minVersion, maxVersion, versionHeader);
    }

    private boolean isInvalidOrNotSupported(float minVersion, float maxVersion, String versionHeader) {
        return versionHeaderIsInvalidNumber(versionHeader) || !versionIsSupported(minVersion, maxVersion, Float.valueOf(versionHeader));
    }

    private boolean versionHeaderIsInvalidNumber(String versionHeader) {
        boolean invalid = false;

        try {
            Float.valueOf(versionHeader);
        } catch (NumberFormatException e) {
            invalid = true;
        }

        return invalid;
    }

    private boolean versionIsSupported(float minVersion, float maxVersion, float requestVersion) {
        return versionIsWithinMinLimit(minVersion, requestVersion) && versionIsWithinMaxLimit(maxVersion, requestVersion);
    }

    private boolean versionIsWithinMaxLimit(float maxVersion, float requestVersion) {
        return requestVersion <= maxVersion || maxVersion == ApiVersion.ALL_VERSION_SUPPORTED;
    }

    private boolean versionIsWithinMinLimit(float minVersion, float requestVersion) {
        return requestVersion >= minVersion || minVersion == ApiVersion.ALL_VERSION_SUPPORTED;
    }
}
