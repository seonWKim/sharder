package io.github.seonWKim.query.state.expr;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.github.seonWKim.Expression;
import io.github.seonWKim.TokenTypeCategory;
import io.github.seonWKim.Nullable;
import io.github.seonWKim.Token;
import io.github.seonWKim.TokenType;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

/**
 * Represents a condition expression in the query.
 */
@Getter
public class ConditionExpression extends Expression {

    private static final Set<TokenType> supportedOperators = Set.of(
            TokenType.EQUAL, TokenType.NOT_EQUAL, TokenType.GREATER_THAN,
            TokenType.GREATER_THAN_OR_EQUAL, TokenType.LESS_THAN, TokenType.LESS_THAN_OR_EQUAL);

    private final ConditionNodeTree tree;

    public ConditionExpression(List<Token> tokens) {
        this.tree = buildTree(tokens, 0, tokens.size());
    }

    private ConditionNodeTree buildTree(List<Token> tokens, int start, int end) {
        Deque<ConditionNode> deque = new ArrayDeque<>();
        for (int i = start; i < end; i++) {
            Token curToken = tokens.get(i);
            ConditionNode curNode = ConditionNode.of(curToken);

            if (isLogicalOperator(curToken)) {
                deque.push(curNode);
            } else if (isOperator(curToken)) {
                Token rightToken = tokens.get(i + 1);
                if (rightToken.type() != TokenType.LEFT_PAREN) {
                    ConditionNode leftNode = deque.pop();
                    ConditionNode rightNode = ConditionNode.of(rightToken);
                    deque.push(ConditionNode.builder()
                                            .left(leftNode)
                                            .token(curToken)
                                            .right(rightNode)
                                            .build());
                    i++;
                } else {
                    deque.push(curNode);
                }
            } else if (curToken.type() == TokenType.LEFT_PAREN) {
                i = handleParentheses(tokens, i, deque);
            } else {
                deque.push(curNode);
            }
        }

        while (deque.size() > 1) {
            ConditionNode right = deque.pop();
            ConditionNode operator = deque.pop();
            ConditionNode left = deque.pop();
            deque.push(ConditionNode.builder()
                                    .left(left)
                                    .token(operator.token)
                                    .right(right)
                                    .build());
        }

        return ConditionNodeTree.builder()
                                .root(deque.pop())
                                .tokens(tokens.subList(start, end))
                                .build();
    }

    private int handleParentheses(List<Token> tokens, int start, Deque<ConditionNode> stack) {
        int countLeftParen = 1;
        int countRightParen = 0;
        int i = start + 1;

        while (i < tokens.size()) {
            Token token = tokens.get(i);
            if (token.type() == TokenType.LEFT_PAREN) {
                countLeftParen++;
            } else if (token.type() == TokenType.RIGHT_PAREN) {
                countRightParen++;
            }

            if (countLeftParen == countRightParen) {
                break;
            }

            i++;
        }

        ConditionNodeTree subTree = buildTree(tokens, start + 1, i);
        stack.push(subTree.root);
        return i;
    }

    private boolean isOperator(Token token) {
        return token.type().getCategory() == TokenTypeCategory.OPERATOR;
    }

    private boolean isLogicalOperator(Token token) {
        return token.type() == TokenType.AND || token.type() == TokenType.OR;
    }

    /**
     * Represents a condition node tree.
     */
    @Builder
    @Value
    public static class ConditionNodeTree {
        ConditionNode root;
        List<Token> tokens;

        public List<ConditionNode> preOrderTraversal() {
            List<ConditionNode> container = new LinkedList<>();
            preOrderTraversal(root, container);
            return container;
        }

        private void preOrderTraversal(@Nullable ConditionExpression.ConditionNode node,
                                       List<ConditionNode> container) {
            if (node == null) {
                return;
            }

            preOrderTraversal(node.left, container);
            container.add(node);
            preOrderTraversal(node.right, container);
        }
    }

    /**
     * Represents a condition node in the tree.
     */
    @Builder
    @Value
    public static class ConditionNode {
        @Builder.Default
        ConditionNode left = null;
        Token token;
        @Builder.Default
        ConditionNode right = null;

        public static ConditionNode of(Token token) {
            return ConditionNode.builder().token(token).build();
        }

        public boolean isLogicalOperator() {
            return token.type() == TokenType.AND || token.type() == TokenType.OR;
        }

        public boolean isSupportedOperator() {
            return supportedOperators.contains(token.type());
        }
    }
}
