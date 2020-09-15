package cloud.usb.hid.gateway;

import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = "mqttPromiseOutboundChannel")
public interface MqttPromiseGateway {

    void send(String payload);
}