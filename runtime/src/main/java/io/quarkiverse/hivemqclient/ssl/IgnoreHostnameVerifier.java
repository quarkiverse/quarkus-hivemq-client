package io.quarkiverse.hivemqclient.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class IgnoreHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        // Ignore hostname verification and always return true
        return true;
    }
}
