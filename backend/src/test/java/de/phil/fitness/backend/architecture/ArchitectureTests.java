package de.phil.fitness.backend.architecture;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

/**
 * Dependency rules between the slices under {@code de.phil.fitness.backend}. The diagram in docs/architecture.md shows
 * the same structure.
 */
class ArchitectureTests {

    private static final String BASE = "de.phil.fitness.backend";

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(BASE);

    @Test
    @DisplayName("No cycles between the slices")
    void slicesAreFreeOfCycles() {
        slices().matching(BASE + ".(*)..")
                .should().beFreeOfCycles()
                .check(CLASSES);
    }

    @Test
    @DisplayName("Domain slices only depend downwards: workout -> program -> routine -> exercise")
    void domainSlicesDependDownwards() {
        layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("workout").definedBy(BASE + ".workout..")
                .layer("program").definedBy(BASE + ".program..")
                .layer("routine").definedBy(BASE + ".routine..")
                .layer("exercise").definedBy(BASE + ".exercise..")
                .whereLayer("workout").mayNotBeAccessedByAnyLayer()
                .whereLayer("program").mayOnlyBeAccessedByLayers("workout")
                .whereLayer("routine").mayOnlyBeAccessedByLayers("program", "workout")
                .whereLayer("exercise").mayNotAccessAnyLayer()
                .check(CLASSES);
    }

    @Test
    @DisplayName("common depends on no other slice")
    void commonDependsOnNoOtherSlice() {
        noClasses().that().resideInAPackage(BASE + ".common..")
                .should().dependOnClassesThat(resideInAPackage(BASE + "..")
                        .and(not(resideInAPackage(BASE + ".common.."))))
                .check(CLASSES);
    }
}
