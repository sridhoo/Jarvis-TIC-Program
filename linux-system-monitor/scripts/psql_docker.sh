set -euo pipefail

CONTAINER="${CONTAINER:-jrvs-psql}"
VOLUME="${VOLUME:-pgdata}"
HOST_PORT="${HOST_PORT:-5432}"
IMAGE="${IMAGE:-postgres:16-alpine}"

ensure_docker() {
  if ! sudo systemctl is-active --quiet docker; then
    echo "Starting Docker..."
    sudo systemctl start docker
  fi
}
container_exists() { docker inspect "$CONTAINER" >/dev/null 2>&1; }
container_status() { docker inspect -f '{{.State.Status}}' "$CONTAINER" 2>/dev/null || echo "not_created"; }

create_container() {
  local db_user="$1" db_pass="$2"
  if container_exists; then echo "Error: Container '$CONTAINER' already exists."; exit 1; fi
  docker volume create "$VOLUME" >/dev/null
  docker pull "$IMAGE" >/dev/null
  docker run --name "$CONTAINER" \
    -e POSTGRES_USER="$db_user" \
    -e POSTGRES_PASSWORD="$db_pass" \
    -e PGDATA=/var/lib/postgresql/data/pgdata \
    -v "$VOLUME":/var/lib/postgresql/data:Z \
    -p "$HOST_PORT":5432 \
    -d "$IMAGE" >/dev/null
  echo "Status: $(container_status)"
}

start_or_stop() {
  local action="$1"
  if ! container_exists; then echo "Error: Container '$CONTAINER' not created. Run: $0 create <user> <pass>"; exit 1; fi
  docker container "$action" "$CONTAINER"
  echo "Status: $(container_status)"
  docker ps -a -f name="$CONTAINER"
}

usage() {
  cat <<U
Usage:
  $0 create <db_username> <db_password>
  $0 start
  $0 stop
Env (optional): CONTAINER, VOLUME, HOST_PORT, IMAGE
U
}

cmd="${1:-}"; case "$cmd" in
  create) [ $# -eq 3 ] || { echo "Error: create requires <db_username> <db_password>"; usage; exit 1; }
          ensure_docker; create_container "$2" "$3" ;;
  start)  ensure_docker; start_or_stop start ;;
  stop)   ensure_docker; start_or_stop stop ;;
  *)      echo "Illegal command: '${cmd:-<empty>}'"; usage; exit 1 ;;
esac
