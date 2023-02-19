package com.github.javaparser.symbolsolver.resolution.typeinference.constraintformulas;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.model.typesystem.ReferenceTypeImpl;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.resolution.typeinference.BoundSet;
import com.github.javaparser.symbolsolver.resolution.typeinference.ConstraintFormula;
import com.github.javaparser.symbolsolver.resolution.typeinference.InferenceVariable;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
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
        TypeSameAsType tsat1 = new TypeSameAsType(null, stringType);
        TypeSameAsType tsat2 = new TypeSameAsType(stringType, null);
        assertEquals(tsat1.reduce(null), ConstraintFormula.ReductionResult.falseResult());
        assertEquals(tsat2.reduce(null), ConstraintFormula.ReductionResult.falseResult());
    }

    @Test
    void TestTypeSameAsTypePrimitiveAndInference() {
        ResolvedTypeParameterDeclaration tp = mock(ResolvedTypeParameterDeclaration.class);
        Expression e = new StringLiteralExpr("hi");
        InferenceVariable inferenceVariable = new InferenceVariable("α", tp);
        ResolvedType stringType = new ReferenceTypeImpl(new ReflectionTypeSolver().solveType(String.class.getCanonicalName()));
        TypeSameAsType tsat1 = new TypeSameAsType(null, stringType);
        TypeSameAsType tsat2 = new TypeSameAsType(stringType, null);
        assertEquals(tsat1.reduce(null), ConstraintFormula.ReductionResult.falseResult());
        assertEquals(tsat2.reduce(null), ConstraintFormula.ReductionResult.falseResult());
    }

}
