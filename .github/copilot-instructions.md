# Danfoss Icon Controller Home Assistant Add-on

Always reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.

## Project Overview

This is a Java 21 Maven project that creates a Home Assistant Add-on for integrating Danfoss Icon Master Controller thermostats. The addon provides a web interface for device pairing, MQTT integration for Home Assistant auto-discovery, and REST API endpoints for temperature control.

## Working Effectively

### Prerequisites
- Install Java 21 (required for compilation):
  ```bash
  sudo apt update && sudo apt install -y openjdk-21-jdk
  sudo update-alternatives --set java /usr/lib/jvm/java-21-openjdk-amd64/bin/java
  export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
  ```
- Maven 3.x is required (usually pre-installed)

### Build Commands (NEVER CANCEL - Set 90+ minute timeouts)
Navigate to `danfoss-addon/` directory for all build operations:
```bash
cd danfoss-addon/
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
```

- **Clean Compile**: `mvn clean compile` -- takes ~55 seconds. NEVER CANCEL. Set timeout to 90+ minutes.
- **Run Tests**: `mvn test` -- takes ~23 seconds. NEVER CANCEL. Set timeout to 30+ minutes.
- **Full Build**: `mvn clean package` -- takes ~24 seconds after initial downloads. NEVER CANCEL. Set timeout to 90+ minutes.

### Running the Application
- **Start Application**: `java --enable-preview -jar ha-danfoss-addon-0.0.1.jar`
- **Web UI**: http://localhost:9199/ (device pairing interface)
- **Health Check**: http://localhost:9199/health (should return "OK")
- **Stop**: Ctrl+C or kill the Java process

## Validation Scenarios

### Always Test After Making Changes
1. **Build Validation**:
   ```bash
   cd danfoss-addon/
   export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
   mvn clean package
   ```

2. **Application Startup**:
   ```bash
   java --enable-preview -jar ha-danfoss-addon-0.0.1.jar &
   sleep 10
   curl -s http://localhost:9199/health
   curl -s http://localhost:9199/ | grep "Danfoss Icon Discovery"
   pkill -f "java.*ha-danfoss"
   ```

3. **Unit Tests**: `mvn test` (4 tests must pass)

### Manual Testing Scenarios
- **Web Interface**: Verify the device pairing form loads at http://localhost:9199/
- **REST API**: Test command endpoint with POST to /command (requires specific JSON payload)
- **Configuration**: Check config loading from `/share/danfoss-icon/danfoss_config.json`

## Build Troubleshooting

### Common Issues
- **Java Version Error**: Ensure Java 21 is set as default and JAVA_HOME is configured
- **Preview Features Warning**: Normal - project uses Java 21 preview features with `--enable-preview`
- **Network Timeouts**: Maven downloads dependencies - first build takes longer
- **Docker Build Failures**: Container builds may fail in restricted network environments

### Expected Build Output
- **Compilation**: ~27 Java source files compiled
- **JAR Size**: ~10.6MB final artifact
- **Test Results**: 4 tests (IconRoomTest, HeatingStateTest, IconMasterTest)

## Code Navigation

### Key Directories
- `danfoss-addon/src/main/java/net/soundvibe/hasio/` - Main source code
- `danfoss-addon/src/main/java/net/soundvibe/hasio/danfoss/` - Danfoss protocol implementation
- `danfoss-addon/src/main/java/net/soundvibe/hasio/ha/` - Home Assistant integration
- `danfoss-addon/src/test/java/` - Unit tests
- `danfoss-addon/src/main/resources/` - Web UI resources

### Important Files
- `Application.java` - Main entry point and web server setup
- `Bootstrapper.java` - Application initialization logic
- `pom.xml` - Maven build configuration (Java 21, dependencies)
- `config.yaml` - Home Assistant Add-on configuration
- `Dockerfile` - Container build definition
- `run.sh` - Container startup script

### Configuration Files
- `/data/options.json` - Runtime configuration (port, MQTT settings, log level)
- `/share/danfoss-icon/danfoss_config.json` - Danfoss device pairing data
- `translations/en.yaml` - Configuration parameter descriptions

## Development Workflow

### Making Changes
1. Build and test first to establish baseline: `mvn clean package`
2. Make minimal code changes
3. Compile and test: `mvn compile test`
4. Run application and test affected functionality
5. Build final artifact: `mvn package`

### No Linting/Formatting Tools
- Project does not include automated code formatting (Spotless, Checkstyle, etc.)
- Follow existing Java code style in the project
- No automated pre-commit hooks configured

### Container Development
- **Local JAR Build**: Always build JAR first with `mvn package`
- **Docker Build**: `docker build --build-arg BUILD_FROM=alpine:3.18 .`
- **Note**: Docker builds may fail in restricted network environments due to Alpine package access

## MQTT and Home Assistant Integration

### Testing MQTT Features
- Enable MQTT in configuration: `mqttEnabled: true`
- Configure MQTT broker details in options
- Restart application to connect to broker
- Check Home Assistant for auto-discovered climate entities

### REST API Testing
```bash
# Basic endpoints available without device pairing
curl -s http://localhost:9199/health          # Health check
curl -s http://localhost:9199/                # Web UI

# Device-specific endpoints (only available after pairing via /discover)
# curl -X POST http://localhost:9199/command \
#   -H "Content-Type: application/json" \
#   -d '{"command":"setHomeTemperature","value":22.5,"roomNumber":1}'
# curl -s http://localhost:9199/rooms         # List rooms
```

## Critical Timing and Timeouts

### NEVER CANCEL Build Operations
- **Initial Maven Build**: 2-5 minutes (dependency downloads)
- **Subsequent Builds**: 24-55 seconds  
- **Test Execution**: 23 seconds
- **Java Compilation**: 55 seconds

### Always Use Extended Timeouts
- Build commands: 90+ minutes timeout
- Test commands: 30+ minutes timeout  
- Application startup: 60+ seconds timeout

## Common File Outputs

### Repository Root
```
.
..
.git/
.github/
README.md
danfoss-addon/
repository.json
```

### danfoss-addon Directory
```
.gitignore
.idea/
CHANGELOG.md
DOCS.md
Dockerfile
LICENSE
README.md
config.yaml
ha-danfoss-addon-0.0.1.jar
pom.xml
run.sh
src/
translations/
```

### Build Artifacts
- `target/` - Maven build directory
- `ha-danfoss-addon-0.0.1.jar` - Final executable JAR (copied to project root)
- `dependency-reduced-pom.xml` - Generated by Maven Shade plugin