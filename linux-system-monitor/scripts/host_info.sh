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

hostname_fqdn=$(hostname -f || hostname)
lscpu_out="$(lscpu)"

get_val () { echo "$lscpu_out" | awk -F: -v k="$1" '$1 ~ k {sub(/^ +/,"",$2); print $2; exit}'; }

cpu_number="$(echo "$lscpu_out" | awk -F: '/^CPU\(s\):/ {gsub(/ /,"",$2); print $2; exit}')"
cpu_architecture="$(get_val "^Architecture")"
cpu_model="$(get_val "^Model name")"
cpu_mhz="$(get_val "^CPU MHz")"
[ -z "${cpu_mhz:-}" ] && cpu_mhz="$(get_val "^CPU max MHz")"
[ -z "${cpu_mhz:-}" ] && cpu_mhz="0"

l2_cache_raw="$(get_val "^L2 cache")"
l2_cache="$(echo "$l2_cache_raw" | tr -d ' ' | sed 's/([^)]*)//g' | sed 's/[Kk]$//' )"

[ -z "${l2_cache:-}" ] && l2_cache="0"

total_mem="$(free -m | awk '/^Mem:/ {print $2}')"
timestamp="$(date -u '+%F %T')"

insert_stmt="
INSERT INTO host_info(hostname, cpu_number, cpu_architecture, cpu_model, cpu_mhz, l2_cache, total_mem, created_at)
SELECT
  '$hostname_fqdn',
  $cpu_number,
  '$cpu_architecture',
  \$\$${cpu_model}\$\$,
  $cpu_mhz,
  $l2_cache,
  $total_mem,
  '$timestamp'
WHERE NOT EXISTS (SELECT 1 FROM host_info WHERE hostname = '$hostname_fqdn');
"

psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "$insert_stmt"
psql -h "$psql_host" -p "$psql_port" -d "$db_name" -U "$psql_user" -c "SELECT * FROM host_info WHERE hostname = '$hostname_fqdn';"
