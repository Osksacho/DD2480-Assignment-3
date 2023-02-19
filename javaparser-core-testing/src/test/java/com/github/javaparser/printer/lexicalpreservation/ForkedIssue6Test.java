package com.github.javaparser.printer.lexicalpreservation;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.junit.jupiter.api.Test;

import static com.github.javaparser.utils.Utils.SYSTEM_EOL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ForkedIssue6Test extends AbstractLexicalPreservingTest {

    @Test
    void statementAddedOnNewLineWithCommentBefore() {


        considerCode("class A {\n"
                + "void foo() {\n"
                + "/** \n"
                +" * Big comment \n"
                + "*/// Small comment \n"
                + "int second = 2;\n"
                + "}\n"
                + "}"
        );

        String expected =
                "class A {\n"
                        + "void foo() {\n"
                        + "/** \n"
                        +" * Big comment \n"
                        + "*/// Small comment \n"
                        + "foo();\n"
                        + "int second = 2;\n"
                        + "}\n"
                        + "}";
        BlockStmt block = cu.findAll(BlockStmt.class).get(0);
        ExpressionStmt newStmt = new ExpressionStmt(new MethodCallExpr("foo"));
        block.addStatement(0, newStmt);
        String print = LexicalPreservingPrinter.print(cu);
        assertEquals(expected, print);
    }
    @Test
    void importAddedAfterCommentTest() {
        String code = "import a.A;" + SYSTEM_EOL +
                "/**" + SYSTEM_EOL +
                " *Class JavaDoc comment 1 coming through" + SYSTEM_EOL +
                " */" + SYSTEM_EOL +
                "/*" + SYSTEM_EOL +
                " * Comment about import a.B" + SYSTEM_EOL +
                " */" + SYSTEM_EOL +
                "import a.B;" + SYSTEM_EOL+
                "public class Foo {" + SYSTEM_EOL +
                "  void mymethod() {" + SYSTEM_EOL +
                "  }" + SYSTEM_EOL +
                "}";

        considerCode(code);
        ImportDeclaration id = cu.getImport(1);
        cu.remove(id);
        cu.addImport(id);
        String print = LexicalPreservingPrinter.print(cu);
        assertEquals(code, print);

    }
}
