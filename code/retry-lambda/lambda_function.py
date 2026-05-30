import json
import logging
import os

from chromadb import logger
import psycopg2
from kafka import KafkaProducer

KAFKA_BOOTSTRAP_SERVERS = os.environ["KAFKA_BOOTSTRAP_SERVERS"]

TOPIC_MAP = {
    "HIGH": "notification.priority.high",
    "MEDIUM": "notification.priority.medium",
    "LOW": "notification.priority.low",
}

RETRY_QUERY = """
    SELECT id, recipient, channel, priority, subject, content,
           template_name, parameters, retry_count, max_retries,
           mode, base_delay_seconds
    FROM notification
    WHERE status = 'RETRY'
      AND next_retry_at <= NOW()
"""

UPDATE_QUERY = "UPDATE notification SET status = 'PENDING' WHERE id = %s"

producer = KafkaProducer(bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS)


def _get_connection():

    return psycopg2.connect(
        host=os.environ["DB_HOST"],
        port=os.environ["DB_PORT"],
        dbname=os.environ["DB_NAME"],
        user=os.environ["DB_USER"],
        password=os.environ["DB_PASSWORD"],
    )


conn = _get_connection()


def _build_event(row):

    return {
        "id": str(row[0]),
        "recipient": row[1],
        "channel": row[2],
        "priority": row[3],
        "subject": row[4],
        "content": row[5],
        "templateName": row[6],
        "parameters": row[7],
        "retryCount": row[8],
        "maxRetries": row[9],
        "mode": row[10],
        "baseDelaySeconds": row[11],
    }


def handler(event, context):

    global conn

    if conn.closed:
        conn = _get_connection()

    with conn.cursor() as cursor:
        cursor.execute(RETRY_QUERY)
        rows = cursor.fetchall()

    processed = 0

    for row in rows:
        notification_event = _build_event(row)
        notification_id = row[0]
        priority = row[3]

        topic = TOPIC_MAP.get(priority, "notification.priority.low")

        producer.send(
            topic,
            key=str(notification_id).encode("utf-8"),
            value=json.dumps(notification_event).encode("utf-8"),
        )

        with conn.cursor() as cursor:
            cursor.execute(UPDATE_QUERY, (notification_id,))

        conn.commit()
        processed += 1

    producer.flush()

    return {"processed": processed}
