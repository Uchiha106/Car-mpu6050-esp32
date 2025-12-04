#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266HTTPClient.h>

#include <Wire.h>
const char* ssid = "anhtoan";
const char* password = "22032002";

const int MPU_addr = 0x68;
int16_t AcX, AcY, AcZ, GyX, GyY, GyZ;

int minVal = 265;
int maxVal = 402;

double Acc_X;
double Acc_Y;
double Acc_Z;
double Gyro_X;
double Gyro_Y;
double Gyro_Z;

String URL = "http://172.20.10.2/ANDROID/process_6050.php";


ESP8266WebServer server(80);

void setup() {
  Serial.begin(115200);

  Wire.begin();
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x6B);
  Wire.write(0);
  Wire.endTransmission(true);
  connectWiFi();
    server.begin();
  Serial.println("HTTP server started");
  /*
    Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 14, true);
  AcX = Wire.read() << 8 | Wire.read();
  AcY = Wire.read() << 8 | Wire.read();
  AcZ = Wire.read() << 8 | Wire.read();
  GyX = Wire.read() << 8 | Wire.read();
  GyY = Wire.read() << 8 | Wire.read();
  GyZ = Wire.read() << 8 | Wire.read();

  int xAng = map(AcX, minVal, maxVal, -90, 90);
  int yAng = map(AcY, minVal, maxVal, -90, 90);
  int zAng = map(AcZ, minVal, maxVal, -90, 90);

  Acc_X = RAD_TO_DEG * (atan2(-yAng, -zAng) + PI);
  Acc_Y = RAD_TO_DEG * (atan2(-xAng, -zAng) + PI);
  Acc_Z = RAD_TO_DEG * (atan2(-yAng, -xAng) + PI);

  Gyro_X = GyX / 131.0;
  Gyro_Y = GyY / 131.0;
  Gyro_Z = GyZ / 131.0; */

}

void loop() {
  server.handleClient();
  if (WiFi.status() != WL_CONNECTED) {
    connectWiFi();
  }
  Wire.beginTransmission(MPU_addr);
  Wire.write(0x3B);
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_addr, 14, true);
  AcX = Wire.read() << 8 | Wire.read();
  AcY = Wire.read() << 8 | Wire.read();
  AcZ = Wire.read() << 8 | Wire.read();
  GyX = Wire.read() << 8 | Wire.read();
  GyY = Wire.read() << 8 | Wire.read();
  GyZ = Wire.read() << 8 | Wire.read();

  int xAng = map(AcX, minVal, maxVal, -90, 90);
  int yAng = map(AcY, minVal, maxVal, -90, 90);
  int zAng = map(AcZ, minVal, maxVal, -90, 90);

  Acc_X = RAD_TO_DEG * (atan2(-yAng, -zAng) + PI);
  Acc_Y = RAD_TO_DEG * (atan2(-xAng, -zAng) + PI);
  Acc_Z = RAD_TO_DEG * (atan2(-yAng, -xAng) + PI);

  Gyro_X = GyX / 131.0;
  Gyro_Y = GyY / 131.0;
  Gyro_Z = GyZ / 131.0;

  String postData = "Acc_X=" + String(Acc_X) + "&Acc_Y=" + String(Acc_Y)+ "&Acc_Z=" + String(Acc_Z)+ "&Gyro_X=" + String(Gyro_X)+ "&Gyro_Y=" + String(Gyro_Y)+ "&Gyro_Z=" + String(Gyro_Z);
  HTTPClient http;
  WiFiClient client; // Tạo một đối tượng WiFiClient
  http.begin(client, URL); // Truyền đối tượng WiFiClient vào hàm begin()

  http.addHeader("Content-Type", "application/x-www-form-urlencoded");

  int httpCode = http.POST(postData);
  String payload = "";
  
  server.on("/get_data", HTTP_GET, []() {
  String data = String(Acc_X) + "," + String(Acc_Y)+ "," + String(Acc_Z)+ "," + String(Gyro_X)+ "," + String(Gyro_Y)+ "," + String(Gyro_Z);
  server.send(200, "text/plain", data);
  });
  
  if (httpCode > 0) {
    // File được tìm thấy trên máy chủ
    if (httpCode == HTTP_CODE_OK) {
      payload = http.getString();
      Serial.println(payload);
    } else {

      Serial.printf("[HTTP] GET... code: %d\n", httpCode);
    }
  } else {
    Serial.printf("[HTTP] GET... failed, error: %s\n", http.errorToString(httpCode).c_str());
  }

  http.end(); 
  Serial.print("URL : ");
  Serial.println(URL);
  Serial.print("Data: ");
  Serial.println(postData);
  Serial.print("httpCode: ");
  Serial.println(httpCode);
  Serial.print("payload : ");
  Serial.println(payload);
  Serial.println("--------------------------------------------------");
  delay(5000);
}
void connectWiFi() {
  WiFi.mode(WIFI_OFF);
  delay(1000);
  // Ẩn chế độ phát sóng của ESP như là một điểm truy cập wifi
  WiFi.mode(WIFI_STA);

  WiFi.begin(ssid, password);
  Serial.println("Connecting to WiFi");

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  Serial.print("Connected to: ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}
