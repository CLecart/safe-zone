#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
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

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

print_banner() {
    echo ""
    echo "╔══════════════════════════════════════════════════════════════╗"
    echo "║           SafeZone E-Commerce Platform Setup                 ║"
    echo "╚══════════════════════════════════════════════════════════════╝"
    echo ""
}

check_prerequisites() {
    log_step "Checking prerequisites..."
    
    local missing=0
    
    if ! command -v java &> /dev/null; then
        log_error "Java is not installed"
        missing=1
    else
        JAVA_VERSION=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$JAVA_VERSION" -lt 17 ]; then
            log_error "Java 17 or higher is required. Found: $JAVA_VERSION"
            missing=1
        else
            log_info "Java $JAVA_VERSION found"
        fi
    fi
    
    if ! command -v mvn &> /dev/null; then
        log_error "Maven is not installed"
        missing=1
    else
        log_info "Maven found: $(mvn -v | head -n1)"
    fi
    
    if ! command -v docker &> /dev/null; then
        log_warn "Docker is not installed (required for SonarQube)"
    else
        log_info "Docker found"
    fi
    
    if ! command -v git &> /dev/null; then
        log_error "Git is not installed"
        missing=1
    else
        log_info "Git found"
    fi
    
    if [ $missing -eq 1 ]; then
        log_error "Missing prerequisites. Please install required tools."
        exit 1
    fi
    
    log_info "All prerequisites met!"
}

setup_git_hooks() {
    log_step "Setting up Git hooks..."
    
    cd "$PROJECT_ROOT"
    
    mkdir -p .git/hooks
    
    cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash

echo "Running pre-commit checks..."

mvn spotless:check -q 2>/dev/null || {
    echo "Code style issues found. Run 'mvn spotless:apply' to fix."
    exit 1
}

mvn test -q -Dtest=*UnitTest 2>/dev/null || {
    echo "Unit tests failed. Please fix before committing."
    exit 1
}

echo "Pre-commit checks passed!"
EOF
    
    chmod +x .git/hooks/pre-commit
    
    cat > .git/hooks/pre-push << 'EOF'
#!/bin/bash

echo "Running pre-push checks..."

mvn verify -q || {
    echo "Build verification failed. Please fix before pushing."
    exit 1
}

echo "Pre-push checks passed!"
EOF
    
    chmod +x .git/hooks/pre-push
    
    log_info "Git hooks configured"
}

build_project() {
    log_step "Building project..."
    
    cd "$PROJECT_ROOT"
    mvn clean install -DskipTests
    
    log_info "Project built successfully"
}

run_tests() {
    log_step "Running tests..."
    
    cd "$PROJECT_ROOT"
    mvn test
    
    log_info "Tests completed"
}

setup_sonarqube() {
    log_step "Setting up SonarQube..."
    
    if ! command -v docker &> /dev/null; then
        log_warn "Docker not available. Skipping SonarQube setup."
        log_info "To set up SonarQube later, run: ./scripts/sonarqube.sh start"
        return
    fi
    
    chmod +x "$PROJECT_ROOT/scripts/sonarqube.sh"
    "$PROJECT_ROOT/scripts/sonarqube.sh" start
    
    log_info "SonarQube is running at http://localhost:9000"
}

print_next_steps() {
    echo ""
    echo "╔══════════════════════════════════════════════════════════════╗"
    echo "║                    Setup Complete!                           ║"
    echo "╚══════════════════════════════════════════════════════════════╝"
    echo ""
    echo "Next steps:"
    echo ""
    echo "1. Start SonarQube (if not already running):"
    echo "   ./scripts/sonarqube.sh start"
    echo ""
    echo "2. Access SonarQube and change default password:"
    echo "   URL: http://localhost:9000"
    echo "   Default: admin / admin"
    echo ""
    echo "3. Generate a SonarQube token:"
    echo "   ./scripts/sonarqube.sh token"
    echo ""
    echo "4. Configure GitHub secrets:"
    echo "   - SONAR_TOKEN: Your generated token"
    echo "   - SONAR_HOST_URL: http://your-sonarqube-server:9000"
    echo ""
    echo "5. Run SonarQube analysis:"
    echo "   ./scripts/sonarqube.sh analyze"
    echo ""
    echo "6. Start microservices:"
    echo "   mvn spring-boot:run -pl product-service"
    echo "   mvn spring-boot:run -pl order-service"
    echo "   mvn spring-boot:run -pl user-service"
    echo "   mvn spring-boot:run -pl api-gateway"
    echo ""
    echo "For more information, see README.md"
    echo ""
}

main() {
    print_banner
    check_prerequisites
    setup_git_hooks
    build_project
    run_tests
    setup_sonarqube
    print_next_steps
}

case "${1:-all}" in
    all)
        main
        ;;
    check)
        check_prerequisites
        ;;
    build)
        build_project
        ;;
    test)
        run_tests
        ;;
    hooks)
        setup_git_hooks
        ;;
    sonar)
        setup_sonarqube
        ;;
    *)
        echo "Usage: $0 {all|check|build|test|hooks|sonar}"
        exit 1
        ;;
esac
