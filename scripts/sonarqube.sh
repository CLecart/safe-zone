#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

check_docker() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        log_error "Docker daemon is not running. Please start Docker."
        exit 1
    fi
    
    log_info "Docker is available"
}

start_sonarqube() {
    log_info "Starting SonarQube..."
    
    cd "$PROJECT_ROOT"
    docker-compose -f docker/docker-compose.sonarqube.yml up -d
    
    log_info "Waiting for SonarQube to be ready..."
    
    MAX_ATTEMPTS=60
    ATTEMPT=1
    
    while [ $ATTEMPT -le $MAX_ATTEMPTS ]; do
        if curl -s http://localhost:9000/api/system/health | grep -q '"health":"GREEN"'; then
            log_info "SonarQube is ready!"
            break
        fi
        
        if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
            log_error "SonarQube failed to start within expected time"
            exit 1
        fi
        
        echo -n "."
        sleep 5
        ATTEMPT=$((ATTEMPT + 1))
    done
    
    echo ""
    log_info "SonarQube is available at http://localhost:9000"
    log_info "Default credentials: admin / admin"
    log_warn "Please change the default password on first login!"
}

stop_sonarqube() {
    log_info "Stopping SonarQube..."
    cd "$PROJECT_ROOT"
    docker-compose -f docker/docker-compose.sonarqube.yml down
    log_info "SonarQube stopped"
}

create_sonar_token() {
    log_info "Creating SonarQube token..."
    
    TOKEN_NAME="safezone-ci-token"
    
    RESPONSE=$(curl -s -u admin:admin -X POST \
        "http://localhost:9000/api/user_tokens/generate" \
        -d "name=$TOKEN_NAME" \
        -d "type=GLOBAL_ANALYSIS_TOKEN")
    
    if echo "$RESPONSE" | grep -q "token"; then
        TOKEN=$(echo "$RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
        log_info "Token created successfully!"
        echo ""
        echo "========================================"
        echo "SONAR_TOKEN=$TOKEN"
        echo "========================================"
        echo ""
        log_info "Save this token in your GitHub repository secrets as SONAR_TOKEN"
        log_info "Also set SONAR_HOST_URL=http://your-sonarqube-server:9000"
    else
        log_error "Failed to create token. Response: $RESPONSE"
        log_warn "Token might already exist or credentials are wrong"
    fi
}

run_analysis() {
    log_info "Running SonarQube analysis..."
    
    cd "$PROJECT_ROOT"
    
    if [ -z "$SONAR_TOKEN" ]; then
        log_warn "SONAR_TOKEN not set. Using default authentication."
        mvn clean verify sonar:sonar \
            -Dsonar.host.url=http://localhost:9000 \
            -Dsonar.login=admin \
            -Dsonar.password=admin
    else
        mvn clean verify sonar:sonar \
            -Dsonar.host.url=http://localhost:9000 \
            -Dsonar.token=$SONAR_TOKEN
    fi
    
    log_info "Analysis complete! View results at http://localhost:9000/dashboard?id=safe-zone"
}

show_status() {
    log_info "Checking SonarQube status..."
    
    if curl -s http://localhost:9000/api/system/health | grep -q '"health":"GREEN"'; then
        log_info "SonarQube is running and healthy"
        
        METRICS=$(curl -s "http://localhost:9000/api/measures/component?component=safe-zone&metricKeys=bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density")
        
        if echo "$METRICS" | grep -q "measures"; then
            echo ""
            log_info "Project Metrics:"
            echo "$METRICS" | python3 -m json.tool 2>/dev/null || echo "$METRICS"
        fi
    else
        log_warn "SonarQube is not running or not healthy"
    fi
}

print_usage() {
    echo "Usage: $0 {start|stop|restart|token|analyze|status}"
    echo ""
    echo "Commands:"
    echo "  start    - Start SonarQube using Docker"
    echo "  stop     - Stop SonarQube"
    echo "  restart  - Restart SonarQube"
    echo "  token    - Generate a new analysis token"
    echo "  analyze  - Run SonarQube analysis on the project"
    echo "  status   - Show SonarQube status and metrics"
}

case "$1" in
    start)
        check_docker
        start_sonarqube
        ;;
    stop)
        stop_sonarqube
        ;;
    restart)
        stop_sonarqube
        sleep 2
        check_docker
        start_sonarqube
        ;;
    token)
        create_sonar_token
        ;;
    analyze)
        run_analysis
        ;;
    status)
        show_status
        ;;
    *)
        print_usage
        exit 1
        ;;
esac
