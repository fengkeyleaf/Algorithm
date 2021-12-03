package myLibraries.util.graph.elements;

/*
 * DecisionTreeNode.java
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0$
 */

import myLibraries.util.Node;

import java.util.HashMap;
import java.util.List;

/**
 * Data structure of a Decision Tree Node
 *
 * @author       Xiaoyu Tongyang
 */

public class DecisionTreeNode extends Node {
    // question to ask for this node
    public final String question;
    // leaf node has an answer; otherwise, doesn't
    public final String answerString;
    public final float answerNumerical; // [ -1.0, 1.0 ]
    // child nodes
    public final
    HashMap<String, DecisionTreeNode> children = new HashMap<>();

    // unnecessary variables, for testing purpose
    // classification of answers with this node/question
    public final HashMap<String, Integer> answerCount;
    // classification of examples with this node/question
    public final HashMap<String, List<List<String>>> classifications;

    /**
     * constructs to create an instance of DecisionTreeNode
     * */

    public DecisionTreeNode( int ID, String question,
                             String answerString, float answerNumerical,
                             HashMap<String, Integer> answerCount,
                             HashMap<String, List<List<String>>> classifications ) {
        super( ID );
        this.question = question;
        this.answerString = answerString;
        this.answerNumerical = answerNumerical;
        this.answerCount = answerCount;
        this.classifications = classifications;
    }

    public DecisionTreeNode( int ID, Node parent, String question,
                             String answerString, float answerNumerical,
                             HashMap<String, Integer> answerCount,
                             HashMap<String, List<List<String>>> classifications ) {
        this( ID, question, answerString, answerNumerical, answerCount, classifications );
        this.parent = parent;
    }

    @Override
    public String toString() {
        String question = ( this.question == null ? "" : this.question );
        String answer = ( this.answerString == null ? "" : this.answerString );
        return "Q: " + question + " " + "A: " + answer;
    }
}
