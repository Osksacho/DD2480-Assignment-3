package com.github.javaparser.printer.lexicalpreservation;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.type.PrimitiveType;
import org.junit.jupiter.api.Test;

import static com.github.javaparser.GeneratedJavaParserConstants.BYTE;
import static com.github.javaparser.GeneratedJavaParserConstants.JAVADOC_COMMENT;
import static com.github.javaparser.GeneratedJavaParserConstants.MULTI_LINE_COMMENT;
import static com.github.javaparser.GeneratedJavaParserConstants.LONG;
import java.util.Optional;

import static com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter.NODE_TEXT_DATA;
import static org.junit.jupiter.api.Assertions.*;

public class prettyPrintingTextNodeTest extends AbstractLexicalPreservingTest {

    @Test
    void testPrintByteNode() {
        Node node1 = PrimitiveType.byteType();
        NodeText nodeText = new NodeText();
        // tryToFindElement returns -1 if it cant find the element
        assertEquals(-1, nodeText.tryToFindElement(TextElementMatchers.byTokenType(BYTE),0));
        nodeText = LexicalPreservingPrinter.getOrCreateNodeText(node1);
        assertNotEquals(-1, nodeText.tryToFindElement(TextElementMatchers.byTokenType(BYTE),0));
    }

    @Test
    void testPrintLongNode() {
        Node node1 = PrimitiveType.longType();
        NodeText nodeText = new NodeText();

        // tryToFindElement returns -1 if it cant find the element
        assertEquals(-1, nodeText.tryToFindElement(TextElementMatchers.byTokenType(LONG),0));

        nodeText = LexicalPreservingPrinter.getOrCreateNodeText(node1);

        assertNotEquals(-1, nodeText.tryToFindElement(TextElementMatchers.byTokenType(LONG),0));
    }

    @Test
    void testPrintJavadocComment() {
        NodeText nodeText = new NodeText();
        Comment node = new JavadocComment("testing\ntesting");

        assertEquals(-1, nodeText.tryToFindElement(TextElementMatchers.byTokenType(JAVADOC_COMMENT),0));

        nodeText= LexicalPreservingPrinter.getOrCreateNodeText(node);
        int index = nodeText.tryToFindElement(TextElementMatchers.byTokenType(JAVADOC_COMMENT),0);
        String commentText = nodeText.getElements().get(index).expand();

        assertEquals("/**testing\ntesting*/",commentText);
    }

    @Test
    void testPrintBlockComment() {
        NodeText nodeText = new NodeText();
        Comment node = new BlockComment("testing\ntesting");
        assertEquals(-1, nodeText.tryToFindElement(TextElementMatchers.byTokenType(MULTI_LINE_COMMENT),0));

        nodeText= LexicalPreservingPrinter.getOrCreateNodeText(node);
        int index = nodeText.tryToFindElement(TextElementMatchers.byTokenType(MULTI_LINE_COMMENT),0);
        String commentText = nodeText.getElements().get(index).expand();
        assertEquals("/*testing\ntesting*/",commentText);
    }
}
