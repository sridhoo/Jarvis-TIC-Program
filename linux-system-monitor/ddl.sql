CREATE DATABASE IF NOT EXISTS host_agent;

\c host_agent;

CREATE TABLE IF NOT EXISTS host_info (
  id SERIAL PRIMARY KEY,
  hostname VARCHAR(255) UNIQUE NOT NULL,
  cpu_number INT NOT NULL,
  cpu_architecture VARCHAR(64) NOT NULL,
  cpu_model VARCHAR(255) NOT NULL,
  cpu_mhz DECIMAL(10,2) NOT NULL,
  l2_cache INT NOT NULL,
  total_mem INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS host_usage (
  timestamp TIMESTAMP NOT NULL,
  host_id INT NOT NULL REFERENCES host_info(id),
  memory_free INT NOT NULL,
  cpu_idle INT NOT NULL,
  cpu_kernel INT NOT NULL,
  disk_io BIGINT NOT NULL,
  disk_available INT NOT NULL
);
