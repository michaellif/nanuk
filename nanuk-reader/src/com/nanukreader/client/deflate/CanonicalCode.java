package com.nanukreader.client.deflate;

import java.util.ArrayList;
import java.util.List;

import com.nanukreader.client.deflate.CodeTree.Node;

final class CanonicalCode {

    private final int[] codeLengths;

    // The constructor does not check that the array of code lengths results in a complete Huffman tree, being neither underfilled nor overfilled.
    public CanonicalCode(int[] codeLengths) {
        if (codeLengths == null) {
            throw new NullPointerException("Argument is null");
        }
        this.codeLengths = new int[codeLengths.length];

        for (int i = 0; i < codeLengths.length; i++) {
            this.codeLengths[i] = codeLengths[i];
        }

        for (int x : codeLengths) {
            if (x < 0)
                throw new IllegalArgumentException("Illegal code length");
        }

    }

    // Builds a canonical code from the given code tree.
    public CanonicalCode(CodeTree tree, int symbolLimit) {
        codeLengths = new int[symbolLimit];
        buildCodeLengths(tree.root, 0);
    }

    private void buildCodeLengths(Node node, int depth) {
        if (!node.isLeaf()) {
            buildCodeLengths(node.getLeftChild(), depth + 1);
            buildCodeLengths(node.getRightChild(), depth + 1);
        } else {
            int symbol = node.getSymbol();
            if (codeLengths[symbol] != 0)
                throw new AssertionError("Symbol has more than one code"); // Because CodeTree has a checked constraint that disallows a symbol in multiple leaves
            if (symbol >= codeLengths.length)
                throw new IllegalArgumentException("Symbol exceeds symbol limit");
            codeLengths[symbol] = depth;
        }
    }

    public int getSymbolLimit() {
        return codeLengths.length;
    }

    public int getCodeLength(int symbol) {
        if (symbol < 0 || symbol >= codeLengths.length)
            throw new IllegalArgumentException("Symbol out of range");
        return codeLengths[symbol];
    }

    public CodeTree toCodeTree() {
        List<Node> nodes = new ArrayList<Node>();
        for (int i = max(codeLengths); i >= 1; i--) { // Descend through positive code lengths
            List<Node> newNodes = new ArrayList<Node>();

            // Add leaves for symbols with code length i
            for (int j = 0; j < codeLengths.length; j++) {
                if (codeLengths[j] == i)
                    newNodes.add(new Node(j));
            }

            // Merge nodes from the previous deeper layer
            for (int j = 0; j < nodes.size(); j += 2)
                newNodes.add(new Node(nodes.get(j), nodes.get(j + 1)));

            nodes = newNodes;
            if (nodes.size() % 2 != 0)
                throw new IllegalStateException("This canonical code does not represent a Huffman code tree");
        }

        if (nodes.size() != 2)
            throw new IllegalStateException("This canonical code does not represent a Huffman code tree");
        return new CodeTree(new Node(nodes.get(0), nodes.get(1)), codeLengths.length);
    }

    private static int max(int[] array) {
        int result = array[0];
        for (int x : array)
            result = Math.max(x, result);
        return result;
    }

}
