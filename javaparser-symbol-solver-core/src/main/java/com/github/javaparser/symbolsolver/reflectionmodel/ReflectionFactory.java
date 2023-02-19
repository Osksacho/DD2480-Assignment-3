/*
 * Copyright (C) 2015-2016 Federico Tomassetti
 * Copyright (C) 2017-2023 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */

package com.github.javaparser.symbolsolver.reflectionmodel;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedTypeParameterDeclaration;
import com.github.javaparser.resolution.model.typesystem.ReferenceTypeImpl;
import com.github.javaparser.resolution.types.*;
import com.github.javaparser.*;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Federico Tomassetti
 */
public class ReflectionFactory {
    
    private static String JAVA_LANG_OBJECT = Object.class.getCanonicalName();

    public static ResolvedReferenceTypeDeclaration typeDeclarationFor(Class<?> clazz, TypeSolver typeSolver) {
        if (clazz.isArray()) {
            throw new IllegalArgumentException("No type declaration available for an Array");
        } else if (clazz.isPrimitive()) {
            throw new IllegalArgumentException();
        } else if (clazz.isAnnotation()) {
            return new ReflectionAnnotationDeclaration(clazz, typeSolver);
        } else if (clazz.isInterface()) {
            return new ReflectionInterfaceDeclaration(clazz, typeSolver);
        } else if (clazz.isEnum()) {
            return new ReflectionEnumDeclaration(clazz, typeSolver);
        } else {
            return new ReflectionClassDeclaration(clazz, typeSolver);
        }
    }

    public static ResolvedType typeUsageFor(java.lang.reflect.Type type, TypeSolver typeSolver) {
        if (type instanceof java.lang.reflect.TypeVariable) {
            StaticJavaParser.TUFbranchReached[0] = true;
            java.lang.reflect.TypeVariable<?> tv = (java.lang.reflect.TypeVariable<?>) type;
            boolean declaredOnClass = tv.getGenericDeclaration() instanceof java.lang.reflect.Type;
            ResolvedTypeParameterDeclaration typeParameter = new ReflectionTypeParameter(tv, declaredOnClass, typeSolver);
            return new ResolvedTypeVariable(typeParameter);
        } else if (type instanceof ParameterizedType) {
            StaticJavaParser.TUFbranchReached[1] = true;
            ParameterizedType pt = (ParameterizedType) type;
            ResolvedReferenceType rawType = typeUsageFor(pt.getRawType(), typeSolver).asReferenceType();
            List<java.lang.reflect.Type> actualTypes = new ArrayList<>();
            actualTypes.addAll(Arrays.asList(pt.getActualTypeArguments()));
            // we consume the actual types
            rawType = rawType.transformTypeParameters(tp -> typeUsageFor(actualTypes.remove(0), typeSolver)).asReferenceType();
            return rawType;
        } else if (type instanceof Class) {
            StaticJavaParser.TUFbranchReached[2] = true;
            Class<?> c = (Class<?>) type;
            if (c.isPrimitive()) {
                StaticJavaParser.TUFbranchReached[3] = true;
                if (c.getName().equals(Void.TYPE.getName())) {
                    StaticJavaParser.TUFbranchReached[4] = true;
                    return ResolvedVoidType.INSTANCE;
                } else {
                    StaticJavaParser.TUFbranchReached[5] = true;
                    return ResolvedPrimitiveType.byName(c.getName());
                }
            } else if (c.isArray()) {
                StaticJavaParser.TUFbranchReached[6] = true;
                return new ResolvedArrayType(typeUsageFor(c.getComponentType(), typeSolver));
            } else {
                StaticJavaParser.TUFbranchReached[7] = true;
                return new ReferenceTypeImpl(typeDeclarationFor(c, typeSolver));
            }
        } else if (type instanceof GenericArrayType) {
            StaticJavaParser.TUFbranchReached[8] = true;
            GenericArrayType genericArrayType = (GenericArrayType) type;
            return new ResolvedArrayType(typeUsageFor(genericArrayType.getGenericComponentType(), typeSolver));
        } else if (type instanceof WildcardType) {
            StaticJavaParser.TUFbranchReached[9] = true;
            WildcardType wildcardType = (WildcardType) type;
            if (wildcardType.getLowerBounds().length > 0 && wildcardType.getUpperBounds().length > 0) {
                StaticJavaParser.TUFbranchReached[10] = true;
                if (wildcardType.getUpperBounds().length == 1 && wildcardType.getUpperBounds()[0].getTypeName().equals(JAVA_LANG_OBJECT)) {
                    StaticJavaParser.TUFbranchReached[11] = true;
                    // ok, it does not matter
                }
            }
            if (wildcardType.getLowerBounds().length > 0) {
                StaticJavaParser.TUFbranchReached[12] = true;
                if (wildcardType.getLowerBounds().length > 1) {
                    StaticJavaParser.TUFbranchReached[13] = true;
                    throw new UnsupportedOperationException();
                }
                return ResolvedWildcard.superBound(typeUsageFor(wildcardType.getLowerBounds()[0], typeSolver));
            }
            if (wildcardType.getUpperBounds().length > 0) {
                StaticJavaParser.TUFbranchReached[14] = true;
                if (wildcardType.getUpperBounds().length > 1) {
                    StaticJavaParser.TUFbranchReached[15] = true;
                    throw new UnsupportedOperationException();
                }
                return ResolvedWildcard.extendsBound(typeUsageFor(wildcardType.getUpperBounds()[0], typeSolver));
            }
            return ResolvedWildcard.UNBOUNDED;
        } else {
            StaticJavaParser.TUFbranchReached[16] = true;
            throw new UnsupportedOperationException(type.getClass().getCanonicalName() + " " + type);
        }
    }

    static AccessSpecifier modifiersToAccessLevel(final int modifiers) {
        if (Modifier.isPublic(modifiers)) {
            return AccessSpecifier.PUBLIC;
        } else if (Modifier.isProtected(modifiers)) {
            return AccessSpecifier.PROTECTED;
        } else if (Modifier.isPrivate(modifiers)) {
            return AccessSpecifier.PRIVATE;
        } else {
            return AccessSpecifier.NONE;
        }
    }
}
