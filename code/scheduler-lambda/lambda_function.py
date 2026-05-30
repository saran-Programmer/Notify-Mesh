import json
import os

from kafka import KafkaProducer
from kafka.errors import KafkaError

TOPIC_MAP = {
    "HIGH": "notification.priority.high",
    "MEDIUM": "notification.priority.medium",
    "LOW": "notification.priority.low",
}

bootstrap_servers = os.environ["KAFKA_BOOTSTRAP_SERVERS"]
producer = KafkaProducer(bootstrap_servers=bootstrap_servers)


def handler(event, _context):
    notification_id = event.get("id")
    priority = event.get("priority", "").upper()

    topic = TOPIC_MAP.get(priority)
    if not topic:
        print(f"Unknown priority '{priority}' for notification id={notification_id}")
        return {"statusCode": 400, "body": f"Unknown priority: {priority}"}

    try:
        future = producer.send(
            topic,
            key=str(notification_id).encode("utf-8"),
            value=json.dumps(event).encode("utf-8"),
        )
        future.get(timeout=5)
        print(f"Published notification id={notification_id} to topic={topic}")
        return {"statusCode": 200, "body": f"Published to {topic}"}
    except KafkaError as e:
        print(f"Failed to publish notification id={notification_id}: {e}")
        raise
