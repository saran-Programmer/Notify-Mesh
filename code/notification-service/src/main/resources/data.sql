INSERT INTO channel_type (name, default_retry_count, rate_limit_per_min, retry_default_multiplier, base_retry_delay_seconds, created_at, last_modified_at)
VALUES
    ('EMAIL',    3, 60, 2, 5, NOW(), NOW()),
    ('DISCORD',  3, 50, 2, 5, NOW(), NOW()),
    ('SLACK',    3, 60, 2, 5, NOW(), NOW()),
    ('TELEGRAM', 3, 30, 2, 5, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

INSERT INTO priority (name, value, max_delay_seconds, base_delay_seconds, created_at, last_modified_at)
VALUES
    ('HIGH',   1,  60,  5, NOW(), NOW()),
    ('MEDIUM', 2, 300, 15, NOW(), NOW()),
    ('LOW',    3, 900, 30, NOW(), NOW())
ON CONFLICT (name) DO NOTHING;
