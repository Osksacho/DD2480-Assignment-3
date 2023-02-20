package com.github.javaparser.symbolsolver.resolution.typeinference.constraintformulas;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.model.typesystem.NullType;
import com.github.javaparser.resolution.model.typesystem.ReferenceTypeImpl;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.resolution.typeinference.BoundSet;
import com.github.javaparser.symbolsolver.resolution.typeinference.ConstraintFormula;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariable;
import com.github.javaparser.symbolsolver.resolution.typeinference.bounds.SameAsBound;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ForkedIssue14Test {
    @Test
    void TestTypeSameAsTypeEqualNoWildcards() {
        ResolvedType stringType = new ReferenceTypeImpl(new ReflectionTypeSolver().solveType(String.class.getCanonicalName()));
        TypeSameAsType tsat = new TypeSameAsType(stringType, stringType);
        assertEquals(tsat.reduce(null), ConstraintFormula.ReductionResult.trueResult());
    }
    @Test
    void TestTypeSameAsTypeNullType() {
        ResolvedType stringType = new ReferenceTypeImpl(new ReflectionTypeSolver().solveType(String.class.getCanonicalName()));
        // Currently not supported in isProperType ?
        //assertEquals(new TypeSameAsType(NullType.INSTANCE, stringType).reduce(null), ConstraintFormula.ReductionResult.falseResult());
        //assertEquals(new TypeSameAsType(stringType, NullType.INSTANCE).reduce(null), ConstraintFormula.ReductionResult.falseResult());
    }
    @Test
    void TestTypeSameAsTypeInferencePrimitive() {
        ResolvedType objectType = new ReferenceTypeImpl(new ReflectionTypeSolver().solveType(Object.class.getCanonicalName()));
        ResolvedTypeParameterDeclaration typeParameterDeclaration = mock(ResolvedTypeParameterDeclaration.class);
        InferenceVariable inferenceVariable = new InferenceVariable("α", typeParameterDeclaration);
        assertEquals(new TypeSameAsType(objectType, inferenceVariable).reduce(null),
                    ConstraintFormula.ReductionResult.oneBound(new SameAsBound(objectType, inferenceVariable)));
        assertEquals(new TypeSameAsType(inferenceVariable, objectType).reduce(null),
                ConstraintFormula.ReductionResult.oneBound(new SameAsBound(inferenceVariable, objectType)));
    }
    @AfterAll
    static void printReduceCoverage() {
        TypeSameAsType.printReachedBranches();
    }

}
