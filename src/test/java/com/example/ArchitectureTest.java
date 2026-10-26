package com.example;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private final ClassFileImporter importer = new ClassFileImporter();

    @Test
    void domain_should_not_depend_on_application_or_adapters() {
        ArchRule rule = noClasses()
                .that()
                .resideInAnyPackage("com.example.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "com.example.application..",
                        "com.example.adapters..");

        rule.check(importer.importPackages("com.example"));
    }

    @Test
    void application_should_not_depend_on_adapters() {
        ArchRule rule = noClasses()
                .that()
                .resideInAnyPackage("com.example.application..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.example.adapters..");

        rule.check(importer.importPackages("com.example"));
    }

    @Test
    void ports_should_not_depend_on_adapters() {
        ArchRule rule = noClasses()
                .that()
                .resideInAnyPackage("com.example.ports..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.example.adapters..");

        rule.check(importer.importPackages("com.example"));
    }

    @Test
    void inner_layers_should_not_depend_on_hibernate_jdbc_or_postgresql() {
        ArchRule rule = noClasses()
                .that()
                .resideInAnyPackage(
                        "com.example.domain..",
                        "com.example.application..",
                        "com.example.ports..",
                        "com.example.adapters.inbound..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(
                        "org.hibernate..",
                        "jakarta.persistence..",
                        "java.sql..",
                        "org.postgresql..");

        rule.check(importer.importPackages("com.example"));
    }
}
