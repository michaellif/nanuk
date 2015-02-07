package com.nanukreader.client.deflate;

import java.util.ArrayList;
import java.util.List;

/**
 * A binary tree where each leaf codes a symbol, for representing Huffman codes. Immutable.
 * 
 * There are two main uses of a CodeTree:
 * - Read the 'root' field and walk through the tree to extract the desired information.
 * - Call getCode() to get the code for a symbol, provided that the symbol has a code.
 * 
 * The path to a leaf node determines the leaf's symbol's code. Starting from the root, going to the left child represents a 0, and going to the right child
 * represents a 1.
 * Constraints:
 * - The tree must be complete, i.e. every leaf must have a symbol.
 * - No symbol occurs in two leaves.
 * - But not every symbol needs to be in the tree.
 * - The root must not be a leaf node.
 * Example:
 * Huffman codes:
 * 0: Symbol A
 * 10: Symbol B
 * 110: Symbol C
 * 111: Symbol D
 * Code tree:
 * .
 * / \
 * A .
 * / \
 * B .
 * / \
 * C D
 */
public class CodeTree {

    public final Node root; // Not null

    // Stores the code for each symbol, or null if the symbol has no code.
    // For example, if symbol 5 has code 10011, then codes.get(5) is the list [1, 0, 0, 1, 1].
    private final List<List<Integer>> codes;

    // Every symbol in the tree 'root' must be strictly less than 'symbolLimit'.
    public CodeTree(Node root, int symbolLimit) {
        if (root == null)
            throw new NullPointerException("Argument is null");
        this.root = root;

        codes = new ArrayList<List<Integer>>(); // Initially all null
        for (int i = 0; i < symbolLimit; i++)
            codes.add(null);
        buildCodeList(root, new ArrayList<Integer>()); // Fills 'codes' with appropriate data
    }

    private void buildCodeList(Node node, List<Integer> prefix) {
        if (!node.isLeaf()) {
            prefix.add(0);
            buildCodeList(node.leftChild, prefix);
            prefix.remove(prefix.size() - 1);

            prefix.add(1);
            buildCodeList(node.rightChild, prefix);
            prefix.remove(prefix.size() - 1);

        } else {
            if (node.symbol >= codes.size())
                throw new IllegalArgumentException("Symbol exceeds symbol limit");
            if (codes.get(node.symbol) != null)
                throw new IllegalArgumentException("Symbol has more than one code");
            codes.set(node.symbol, new ArrayList<Integer>(prefix));

        }
    }

    public List<Integer> getCode(int symbol) {
        if (symbol < 0)
            throw new IllegalArgumentException("Illegal symbol");
        else if (codes.get(symbol) == null)
            throw new IllegalArgumentException("No code for given symbol");
        else
            return codes.get(symbol);
    }

    // Returns a string showing all the codes in this tree. The format is subject to change. Useful for debugging.
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString("", root, sb);
        return sb.toString();
    }

    private static void toString(String prefix, Node node, StringBuilder sb) {
        if (!node.isLeaf()) {
            toString(prefix + "0", node.leftChild, sb);
            toString(prefix + "1", node.rightChild, sb);
        } else {
            sb.append("Code " + prefix + ": Symbol" + node.symbol);
        }
    }

    /**
     * A node in a code tree. This class has two and only two subclasses: InternalNode, Leaf.
     */
    public static class Node {

        private Node leftChild;

        private Node rightChild;

        private int symbol;

        private final boolean leaf;

        public Node(Node leftChild, Node rightChild) {
            this.leaf = false;
            if (leftChild == null || rightChild == null) {
                throw new NullPointerException("Argument is null");
            }
            this.leftChild = leftChild;
            this.rightChild = rightChild;
        }

        public Node(int symbol) {
            this.leaf = true;
            if (symbol < 0) {
                throw new IllegalArgumentException("Illegal symbol value");
            }
            this.symbol = symbol;
        }

        public Node getLeftChild() {
            return leftChild;
        }

        public Node getRightChild() {
            return rightChild;
        }

        public int getSymbol() {
            return symbol;
        }

        public boolean isLeaf() {
            return leaf;
        }
    }

}
