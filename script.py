import paho.mqtt.client as mqtt
import psycopg2
from psycopg2 import sql
import json

DB_HOST = "localhost"
DB_NAME = "postgres"
DB_USER = "postgres"
DB_PASSWORD = "postgres"

MQTT_BROKER = "BROKER"
MQTT_PORT = 1883
MQTT_TOPIC = "TOPIC"

conn = psycopg2.connect(host=DB_HOST, dbname=DB_NAME, user=DB_USER, password=DB_PASSWORD)
cursor = conn.cursor()

def on_connect(client, userdata, flags, rc):
    print(f"Connected with result code {rc}")
    client.subscribe(MQTT_TOPIC)

def on_message(client, userdata, msg):
    print(f"Message received: {msg.payload.decode()}")
    try:
        data = json.loads(msg.payload.decode())
        insert_query = sql.SQL(
            """
            INSERT INTO sensor_data (date, temperature, humidity, brightness, flame, motion, mac)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            """
        )
        cursor.execute(insert_query, (
            data['date'], data['temperature'], data['humidity'],
            data['brightness'], data['flame'], data['motion'], data['mac']
        ))
        conn.commit()
        print("Data inserted successfully")
    except (Exception, psycopg2.DatabaseError) as error:
        print(f"Error inserting data: {error}")

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect(MQTT_BROKER, MQTT_PORT, 60)

client.loop_forever()
cursor.close()
conn.close()
