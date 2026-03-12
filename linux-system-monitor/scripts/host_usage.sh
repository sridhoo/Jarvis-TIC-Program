set -eu
set -o pipefail

if [ "$#" -ne 5 ]; then
  echo "Usage: $0 psql_host psql_port db_name psql_user psql_password" >&2
  exit 1
fi

psql_host=$1
psql_port=$2
db_name=$3
psql_user=$4
psql_password=$5

export PGPASSWORD="postgres"

hostname_fqdn=$(hostname -f 2>/dev/null || hostname)


vm_line="$(vmstat --unit M 1 2 | tail -n1)"

memory_free="$(echo "$vm_line" | awk '{print $4}' | xargs)"     
cpu_kernel="$(echo "$vm_line" | awk '{print $14}' | xargs)"    
cpu_idle="$(echo "$vm_line"   | awk '{print $15}' | xargs)"     


disk_io="$(vmstat -d | awk 'NR==3 {print $10; exit}')"


disk_available="$(df -BM / | awk 'NR==2 {gsub(/M/,"",$4); print $4}')"

timestamp="$(date -u '+%F %T')"
host_id="(SELECT id FROM host_info WHERE hostname = '$hostname_fqdn')"

insert_stmt="
INSERT INTO host_usage(timestamp, host_id, memory_free, cpu_idle, cpu_kernel, disk_io, disk_available)
VALUES(
  '$timestamp',
  $host_id,
  $memory_free,
  $cpu_idle,
  $cpu_kernel,
  $disk_io,
  $disk_available
);
"

psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "$insert_stmt"
