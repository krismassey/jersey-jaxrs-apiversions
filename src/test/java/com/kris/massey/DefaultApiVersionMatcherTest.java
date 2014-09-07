package com.kris.massey;

import com.sun.jersey.spi.container.ContainerRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultApiVersionMatcherTest {

    private static final boolean HEADER_REQUIRED_FOR_VALID_REQUEST = true;
    private static final boolean HEADER_NOT_REQUIRED_FOR_VALID_REQUEST = false;
    private static final String VERSION_HEADER_NAME = "Version";
    private DefaultApiVersionMatcher defaultApiVersionMatcher;

    @Mock
    private ContainerRequest mockContainerRequest;

    @Before
    public void setUp() throws Exception {
        this.defaultApiVersionMatcher = new DefaultApiVersionMatcher(VERSION_HEADER_NAME);
    }

    @Test
    public void minVersionAndMaxVersionNotSetResultsInIsSupported() {
        boolean isVersionSupported = defaultApiVersionMatcher.supportsRequestedVersion(
                HEADER_NOT_REQUIRED_FOR_VALID_REQUEST,
                ApiVersion.ALL_VERSION_SUPPORTED,
                ApiVersion.ALL_VERSION_SUPPORTED,
                mockContainerRequest);

        assertThat(isVersionSupported, is(true));
    }

    @Test
    public void versionLowerThanMinVersionIsNotSupported() {
        int minVersionSupported = 2;

        when(mockContainerRequest.getHeaderValue(VERSION_HEADER_NAME)).thenReturn("1");

        boolean isVersionSupported = defaultApiVersionMatcher.supportsRequestedVersion(
                HEADER_NOT_REQUIRED_FOR_VALID_REQUEST,
                minVersionSupported,
                ApiVersion.ALL_VERSION_SUPPORTED,
                mockContainerRequest);

        assertThat(isVersionSupported, is(false));
    }

    @Test
    public void ifNoVersionHeaderSupportAllVersions() {
        int minVersionSupported = 2;

        when(mockContainerRequest.getHeaderValue(VERSION_HEADER_NAME)).thenReturn(String.valueOf(minVersionSupported));

        boolean isVersionSupported = defaultApiVersionMatcher.supportsRequestedVersion(
                HEADER_NOT_REQUIRED_FOR_VALID_REQUEST,
                minVersionSupported,
                ApiVersion.ALL_VERSION_SUPPORTED,
                mockContainerRequest);

        assertThat(isVersionSupported, is(true));
    }

    @Test
    public void ifNoVersionHeaderRequestIsNotSupported() {
        int minVersionSupported = 2;

        when(mockContainerRequest.getHeaderValue(VERSION_HEADER_NAME)).thenReturn(String.valueOf(minVersionSupported));

        boolean isVersionSupported = defaultApiVersionMatcher.supportsRequestedVersion(
                HEADER_REQUIRED_FOR_VALID_REQUEST,
                minVersionSupported,
                ApiVersion.ALL_VERSION_SUPPORTED,
                mockContainerRequest);

        assertThat(isVersionSupported, is(true));
    }

    @Test
    public void versionSameAsMinVersionIsSupported() {
        int minVersionSupported = 2;

        when(mockContainerRequest.getHeaderValue(VERSION_HEADER_NAME)).thenReturn(String.valueOf(minVersionSupported));

        boolean isVersionSupported = defaultApiVersionMatcher.supportsRequestedVersion(
                HEADER_NOT_REQUIRED_FOR_VALID_REQUEST,
                minVersionSupported,
                ApiVersion.ALL_VERSION_SUPPORTED,
                mockContainerRequest);

        assertThat(isVersionSupported, is(true));
    }

    @Test
    public void versionSameAsMaxVersionIsSupported() {
        int maxVersionSupported = 2;

        when(mockContainerRequest.getHeaderValue(VERSION_HEADER_NAME)).thenReturn(String.valueOf(maxVersionSupported));

        boolean isVersionSupported = defaultApiVersionMatcher.supportsRequestedVersion(
                HEADER_NOT_REQUIRED_FOR_VALID_REQUEST,
                ApiVersion.ALL_VERSION_SUPPORTED,
                maxVersionSupported,
                mockContainerRequest);

        assertThat(isVersionSupported, is(true));
    }

    @Test
    public void versionGreaterThanMaxVersionIsNotSupported() {
        int maxVersionSupported = 2;

        when(mockContainerRequest.getHeaderValue(VERSION_HEADER_NAME)).thenReturn("3");

        boolean isVersionSupported = defaultApiVersionMatcher.supportsRequestedVersion(
                HEADER_NOT_REQUIRED_FOR_VALID_REQUEST,
                ApiVersion.ALL_VERSION_SUPPORTED,
                maxVersionSupported,
                mockContainerRequest);

        assertThat(isVersionSupported, is(false));
    }
}
