# ══════════════════════════════════════════════════════════
# STAGE 1: BUILD
# Goal: Compile Java source code → produce a JAR file
# Tools needed: JDK + Maven
# ══════════════════════════════════════════════════════════

# Start with JDK 21 on tiny Alpine Linux
# "AS build" names this stage so we can reference it later
FROM eclipse-temurin:21-jdk-alpine AS build

# Set working directory inside this container
# Every subsequent command runs from /app
WORKDIR /app

# ── Dependency Layer (cached if pom.xml doesn't change) ──

# Copy the Maven wrapper (so we don't need Maven installed)
COPY mvnw .
COPY .mvn .mvn

# Copy just pom.xml first (not source code yet!)
COPY pom.xml .

# Make the Maven wrapper executable
# (Linux requires explicit permission to run scripts)
RUN chmod +x ./mvnw

# Download all dependencies declared in pom.xml
# -B = batch mode (no interactive prompts)
# If pom.xml hasn't changed → Docker uses cached layer → fast!
RUN ./mvnw dependency:go-offline -B

# ── Source Code Layer (rebuilds when code changes) ──

# NOW copy source code (after dependencies are downloaded)
COPY src src

# Compile + package into a JAR file
# -DskipTests: tests will run in CI pipeline, not here
RUN ./mvnw clean package -DskipTests -B

# ══════════════════════════════════════════════════════════
# STAGE 2: RUNTIME
# Goal: Run the JAR with minimum possible image size
# Tools needed: JRE only (no compiler, no Maven)
# Stage 1 is completely discarded after this
# ══════════════════════════════════════════════════════════

# Start completely fresh with just JRE (smaller than JDK)
FROM eclipse-temurin:21-jre-alpine AS runtime

# Security: Never run as root in production
# Create a non-root user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy ONLY the JAR from Stage 1 (build stage)
# Everything else from Stage 1 is thrown away
# The * wildcard matches: ChatBot-0.0.1-SNAPSHOT.jar
COPY --from=build /app/target/*.jar app.jar

# Switch to non-root user for security
# From here on, the container runs as 'appuser' not 'root'
USER appuser

# Tell Docker "this container listens on port 8080"
# This is documentation — doesn't actually open the port
# The actual port mapping happens when you run the container
EXPOSE 8080

# Health check: Docker uses this to know if your app is alive
# --interval=30s:     Check every 30 seconds
# --timeout=10s:      If no response in 10s = failure
# --start-period=60s: Give app 60s to start before checking
# --retries=3:        3 failures in a row = container is unhealthy
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget -q -O- http://localhost:8080/api/health || exit 1

# Command to run when container starts
# Using array format (recommended): each part is a separate argument
# -XX:+UseContainerSupport: JVM respects Docker memory limits
# -XX:MaxRAMPercentage=75.0: Use max 75% of container's RAM for JVM heap
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-jar", \
    "app.jar"]