package com.safezone.quality;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * Tests to enforce security policy requirements and Sonar S4502 justification.
 * <p>
 * These tests intentionally read source files to ensure that CSRF exceptions
 * are
 * justified with S4502 comments and that CORS credentials are not allowed.
 * </p>
 */
class SecurityPolicyTest {

    private static final Path REPO_ROOT = findRepoRoot();

    private static Path findRepoRoot() {
        Path cur = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
        // Climb up to find repository root (multi-module parent). Stop after a few
        // levels.
        for (int i = 0; i < 6; i++) {
            if (Files.exists(cur.resolve("pom.xml")) && Files.exists(cur.resolve("api-gateway"))) {
                return cur;
            }
            cur = cur.getParent();
            if (cur == null) {
                break;
            }
        }
        return Paths.get(System.getProperty("user.dir")).toAbsolutePath();
    }

    @Test
    void csrfIgnoredEndpointsMustBeDocumentedWithS4502() throws IOException {
        // Look for SecurityConfig files that ignore CSRF and ensure S4502 is present
        List<Path> securityConfigs;
        try (Stream<Path> s = Files.walk(REPO_ROOT)) {
            // Exclude test sources to avoid self-detection and modules' test files
            securityConfigs = s.filter(p -> p.getFileName().toString().equals("SecurityConfig.java"))
                    .filter(p -> !p.toString().contains("/src/test/"))
                    .toList();
        }
        assertThat(securityConfigs).as("Found SecurityConfig files").isNotEmpty();

        boolean anyMissing = false;
        StringBuilder missingFiles = new StringBuilder();
        for (Path cfg : securityConfigs) {
            String content = Files.readString(cfg);
            // If the file ignores CSRF, require an explicit S4502 justification nearby
            if (content.contains("ignoringRequestMatchers(") && !(content.contains("S4502")
                    || content.toLowerCase().contains("sonarqube s4502") || content.contains("SonarQube S4502"))) {
                anyMissing = true;
                missingFiles.append(cfg.toString()).append("\n");
            }
        }

        assertThat(anyMissing)
                .withFailMessage("SecurityConfig files ignore CSRF but lack S4502 justification:\n" + missingFiles)
                .isFalse();
    }

    @Test
    void noCsrfDisableCallShouldExist() throws IOException {
        // Disallow global .csrf(AbstractHttpConfigurer::disable) usage
        try (Stream<Path> s = Files.walk(REPO_ROOT)) {
            List<String> hits = s.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.toString()
                            .contains(File.separator + "src" + File.separator + "test" + File.separator))
                    .map(Path::toString)
                    .filter(p -> {
                        try {
                            String c = Files.readString(Paths.get(p));
                            return c.contains(".csrf(AbstractHttpConfigurer::disable)");
                        } catch (IOException e) {
                            return false;
                        }
                    }).toList();
            assertThat(hits)
                    .withFailMessage(
                            "Found usages of .csrf(AbstractHttpConfigurer::disable) in:\n" + String.join("\n", hits))
                    .isEmpty();
        }
    }

    @Test
    void corsMustNotAllowCredentials() throws IOException {
        try (Stream<Path> s = Files.walk(REPO_ROOT)) {
            List<String> hits = s.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.toString()
                            .contains(File.separator + "src" + File.separator + "test" + File.separator))
                    .map(Path::toString)
                    .filter(p -> {
                        try {
                            String c = Files.readString(Paths.get(p));
                            // Remove block comments (/* ... */) and line comments (// ...)
                            String withoutBlockComments = c.replaceAll("(?s)/\\*.*?\\*/", "");
                            String withoutComments = withoutBlockComments.replaceAll("(?m)//.*$", "");
                            // Only consider code outside comments when searching for disallowed usage
                            return withoutComments.contains("setAllowCredentials(true)");
                        } catch (IOException e) {
                            return false;
                        }
                    }).toList();
            assertThat(hits).withFailMessage("Found CORS allowing credentials in:\n" + String.join("\n", hits))
                    .isEmpty();
        }
    }

    @Test
    void corsWildcardMustBeJustifiedWithS5122() throws IOException {
        try (Stream<Path> s = Files.walk(REPO_ROOT)) {
            List<Path> files = s.filter(p -> p.toString().endsWith(".java"))
                    .filter(p -> !p.toString()
                            .contains(File.separator + "src" + File.separator + "test" + File.separator))
                    .filter(p -> {
                        try {
                            String c = Files.readString(p);
                            return c.contains("setAllowedOriginPatterns(") || c.contains("setAllowedOrigins(");
                        } catch (IOException e) {
                            return false;
                        }
                    }).toList();

            boolean anyMissing = false;
            StringBuilder missing = new StringBuilder();
            for (Path f : files) {
                String c = Files.readString(f);
                if ((c.contains("setAllowedOriginPatterns(\"*\")") || c.contains("setAllowedOrigins(\"*\")")
                        || c.contains("setAllowedOriginPatterns(List.of(\"*\"))")
                        || c.contains("setAllowedOrigins(List.of(\"*\"))"))
                        && !(c.contains("S5122") || c.toLowerCase().contains("sonarqube s5122")
                                || c.contains("Sonar S5122"))) {
                    anyMissing = true;
                    missing.append(f.toString()).append("\n");
                }
            }
            assertThat(anyMissing)
                    .withFailMessage("Wildcard CORS origins found without S5122 justification in:\n" + missing)
                    .isFalse();
        }
    }

    // Fin du test SecurityPolicyTest — commentaire inoffensif pour forcer une merge
    // propre
}
