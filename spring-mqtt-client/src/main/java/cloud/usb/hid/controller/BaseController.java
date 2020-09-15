package cloud.usb.hid.controller;

import cloud.usb.hid.gateway.MqttPromiseGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/send")
public class BaseController {

    @Autowired
    private MqttPromiseGateway mqttPromiseGateway;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<?> send(@RequestBody Map<String, Object> data)
        throws JsonProcessingException {

        mqttPromiseGateway.send(objectMapper.writeValueAsString(data));

        return ResponseEntity.ok().build();
    }
}
