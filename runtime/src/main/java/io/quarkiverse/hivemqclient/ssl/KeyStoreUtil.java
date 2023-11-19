package io.quarkiverse.hivemqclient.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import com.hivemq.client.internal.util.Checks;

import io.quarkiverse.hivemqclient.exceptions.SSLConfigException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

public class KeyStoreUtil {

    private static final String DEFAULT_CERT_TYPE = "JKS";

    public static TrustManagerFactory trustManagerFromKeystore(final File trustStoreFile, final String trustStorePassword,
            final String truststoreType)
            throws RuntimeException {

        Checks.notNull(trustStoreFile, "Trust store file");
        if (!DEFAULT_CERT_TYPE.equalsIgnoreCase(truststoreType)) {
            throw new SSLConfigException("Currently we only support JKS certificates, provided " + truststoreType);
        }

        try (final FileInputStream fileInputStream = new FileInputStream(trustStoreFile)) {
            final KeyStore keyStore = KeyStore.getInstance(truststoreType);
            keyStore.load(fileInputStream, trustStorePassword.toCharArray());

            final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);
            return tmf;

        } catch (final KeyStoreException | IOException e) {
            throw new SSLConfigException(
                    "Not able to open or read trust store '" + trustStoreFile.getAbsolutePath() + "'", e);
        } catch (final NoSuchAlgorithmException | CertificateException e) {
            throw new SSLConfigException(
                    "Not able to read certificate from trust store '" + trustStoreFile.getAbsolutePath() + "'", e);
        }
    }

    public static KeyManagerFactory keyManagerFromKeystore(
            final File keyStoreFile,
            final String keyStorePassword,
            final String privateKeyPassword,
            final String keystoreType) throws RuntimeException {

        Checks.notNull(keyStoreFile, "Key store file");
        try (final FileInputStream fileInputStream = new FileInputStream(keyStoreFile)) {
            final KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(fileInputStream, keyStorePassword.toCharArray());

            final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, privateKeyPassword.toCharArray());
            return kmf;

        } catch (final UnrecoverableKeyException e) {
            throw new SSLConfigException(
                    "Not able to recover key from key store, please check your private key password and your key store password",
                    e);
        } catch (final KeyStoreException | IOException e) {
            throw new SSLConfigException("Not able to open or read key store '" + keyStoreFile.getAbsolutePath() + "'", e);

        } catch (final NoSuchAlgorithmException | CertificateException e) {
            throw new SSLConfigException(
                    "Not able to read certificate from key store '" + keyStoreFile.getAbsolutePath() + "'", e);
        }
    }
}
