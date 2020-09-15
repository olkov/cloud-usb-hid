package cloud.usb.hid.configuration;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import cloud.usb.hid.configuration.properties.MqttClientConfigProperties;

@Slf4j
@Configuration
public class MqttClientConfig {

    @Bean
    public MqttPahoClientFactory mqttClientFactory(MqttClientConfigProperties properties) {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{properties.getServerUri()});
        options.setUserName(properties.getUserName());
        options.setPassword(properties.getPassword().toCharArray());
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttPromiseOutboundChannel")
    public MessageHandler mqttPromiseOutbound(
        MqttClientConfigProperties properties,
        MqttPahoClientFactory mqttClientFactory
    ) {
        MqttPahoMessageHandler messageHandler =
            new MqttPahoMessageHandler(properties.getClientId(), mqttClientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(properties.getDefaultTopic());
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel mqttPromiseOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow mqttIntegrationFlow(MessageProducerSupport mqttInbound) {
        return
            IntegrationFlows.from(mqttInbound)
                .handle((p) -> log.debug("Received payload from MQTT: {}", p))
                .get();
    }

    @Bean
    public MessageProducerSupport mqttInbound(
        MqttClientConfigProperties properties,
        MqttPahoClientFactory mqttClientFactory
    ) {
        MqttPahoMessageDrivenChannelAdapter adapter =
            new MqttPahoMessageDrivenChannelAdapter(
                properties.getClientId() + "-consumer",
                mqttClientFactory,
                properties.getDefaultTopic()
            );
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        return adapter;
    }
}
