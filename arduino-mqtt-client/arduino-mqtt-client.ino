#include <ESP8266WiFi.h>
#include <PubSubClient.h>

const char* ssid = "ASUS_78_Guest";
const char* password = "12344321";

const char* mqtt_server = "34.89.136.226";
const char* mqtt_client_id = "ESP8266-client";
const char* mqtt_topic = "io-commands-topic";
const char* mqtt_username = "iot";
const char* mqtt_password = "12344321";

WiFiClient wifiClient;
PubSubClient client(wifiClient);

void setup_wifi() {
  delay(10);
  
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(400);
    Serial.print(".");
  }

  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char) payload[i]);
  }
  Serial.println();

  digitalWrite(BUILTIN_LED, LOW);   // Turn the LED off by making the voltage HIGH
  delay(100);
  digitalWrite(BUILTIN_LED, HIGH);  // Turn the LED on (Note that LOW is the voltage level)
}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Attempt to connect
    if (client.connect(mqtt_client_id, mqtt_username, mqtt_password)) {
      Serial.println("connected");
      client.subscribe(mqtt_topic);
      digitalWrite(BUILTIN_LED, HIGH);
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void setup() {
  pinMode(BUILTIN_LED, OUTPUT);     // Initialize the BUILTIN_LED pin as an output
  Serial.begin(115200);
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
}
