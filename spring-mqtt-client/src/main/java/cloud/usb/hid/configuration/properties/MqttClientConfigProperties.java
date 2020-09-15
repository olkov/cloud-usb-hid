package cloud.usb.hid.configuration.properties;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mqtt.client.config")
public class MqttClientConfigProperties {

    @NotBlank
    private String serverUri;
    @NotBlank
    private String userName;
    @NotBlank
    private String password;
    @NotBlank
    private String clientId;
    @NotBlank
    private String defaultTopic;
}
