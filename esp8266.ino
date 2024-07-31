#include <WiFiManager.h> 
#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <DHTesp.h>
#include <NTPClient.h>
#include <WiFiUdp.h>

#define DHT_PIN D0

const char *mqtt_broker = "BROKER";
const char *topic = "TOPIC";
const int mqtt_port = 1883;
const int DHTpin = D0;
const int LDRpin = D1;
const int FDpin = D2;
const int PIRpin = D3;
const int BUZZERpin = D8;
DHTesp dht;
String jsonstring;
int LDRvalue;
int FDvalue;
int PIRvalue;
String payloadString;
float temperature = 0.0;
float humidity = 0.0;
int flameStatus;
String jsonString;
String formattedDate;
String dayStamp;
String timeStamp;
String currentDate;

WiFiClient espClient;
PubSubClient client(espClient);
StaticJsonDocument<300> doc;
JsonDocument income;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");

void setup() {

    Serial.begin(115200);

    dht.setup(DHTpin, DHTesp::DHT11); 
    pinMode(DHTpin, INPUT);
    pinMode(LDRpin, INPUT);
    pinMode(FDpin, INPUT);
    pinMode(PIRpin, INPUT);
    pinMode(BUZZERpin, OUTPUT);
    digitalWrite(BUZZERpin, LOW);
    pinMode(LED_BUILTIN, OUTPUT);
    digitalWrite(LED_BUILTIN, HIGH);

    WiFi.mode(WIFI_STA); 

    WiFiManager wm;

    bool res;

    res = wm.autoConnect("TermosenseAP","");

    if(!res) {
        Serial.println("Failed to connect");
        ESP.restart();
    } 
    else {
        Serial.println("Connected to WIFI");
    }
  String clientID = WiFi.macAddress();
  client.setServer(mqtt_broker, mqtt_port);
  client.setCallback(callback);
  if (!client.connect(clientID.c_str())) {
    Serial.println("MQTT connection failed");
  } else {
    Serial.println("MQTT connected");
  }
  client.subscribe(topic);
  timeClient.begin();
  timeClient.setTimeOffset(10800);
}

void loop() {
    client.loop();
    timeClient.update();
    String formattedTime = timeClient.getFormattedTime();
    time_t epochTime = timeClient.getEpochTime();
    struct tm *ptm = gmtime ((time_t *)&epochTime);
    int monthDay = ptm->tm_mday;
    int currentMonth = ptm->tm_mon+1;
    int currentYear = ptm->tm_year+1900;
    currentDate = String(currentYear) + "-" + String(currentMonth) + "-" + String(monthDay) + " " + formattedTime;
    sensorread();
    convertJSON();
}

void callback(char *topic, byte *payload, unsigned int length) {
  payloadString = "";
  Serial.print("Message arrived in topic: ");
  Serial.println(topic);
  Serial.print("Message:");
  for (int i = 0; i < length; i++) {
      Serial.print((char) payload[i]);
      payloadString += (char) payload[i];
  }
  Serial.println("");
  Serial.println("-----------------------");
  DeserializationError error = deserializeJson(income, payloadString);
  if (error) {
    Serial.print("deserializeJson() failed: ");
    Serial.println(error.f_str());
    return;
  }
  String alarmstat = income["alarm"];
  if(alarmstat == "1"){
    digitalWrite(LED_BUILTIN, LOW);
    digitalWrite(BUZZERpin, HIGH);
    Serial.println("alarm on");  
    delay(5000);
    digitalWrite(BUZZERpin, LOW);
    digitalWrite(LED_BUILTIN, HIGH);
  }
  if(alarmstat == "0"){
    digitalWrite(LED_BUILTIN, HIGH);
    digitalWrite(BUZZERpin, LOW);
    Serial.println("alarm off");
    delay(5000);
  }
}

void sensorread(){
    humidity = dht.getHumidity();
    temperature = dht.getTemperature();
    LDRvalue = digitalRead(LDRpin);
    FDvalue = digitalRead(FDpin);
    PIRvalue = digitalRead(PIRpin);
}

void convertJSON(){
  if(FDvalue ==0){
    flameStatus=1;
    digitalWrite(BUZZERpin, HIGH);
  }else{
    flameStatus=0;
    digitalWrite(BUZZERpin, LOW);
  }
    Serial.println(temperature);
    doc["date"] = currentDate;
    doc["temperature"] = temperature;
    doc["humidity"] = humidity;
    doc["brightness"] = LDRvalue;
    doc["flame"] = flameStatus;
    doc["motion"] = PIRvalue;
    serializeJson(doc, jsonString);
    char message[300];
    jsonString.toCharArray(message, (jsonString.length()+1));
    client.publish(topic, message);
    Serial.println("mqtt message sent.");
    Serial.println(message);
    delay(1000);
}