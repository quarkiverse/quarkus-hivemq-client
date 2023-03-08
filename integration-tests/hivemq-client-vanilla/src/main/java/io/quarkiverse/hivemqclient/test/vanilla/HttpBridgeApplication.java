package io.quarkiverse.hivemqclient.test.vanilla;

import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition(info = @Info(title = "Http / HiveMQ bridge API example", version = "1.0.0", contact = @Contact(name = "Http / HiveMQ bridge API Support", url = "http://exampleurl.com/contact", email = "fake-support-examples@hivemq.com"), license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html")))
public class HttpBridgeApplication extends Application {
}
