package com.sharder.query.state.expr;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.sharder.Expression;
import com.sharder.ExpressionType;
import com.sharder.Token;
import com.sharder.TokenType;
import com.sharder.TokenTypeCategory;

import com.sharder.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Getter
public class ConditionExpression extends Expression {

    private final ConditionNodeTree tree;

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

        private void preOrderTraversal(@Nullable ConditionExpression.ConditionNode node, List<ConditionNode> container) {
            if (node == null) {
                return;
            }

            preOrderTraversal(node.left, container);
            container.add(node);
            preOrderTraversal(node.right, container);
        }
    }

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
            return token.type() == TokenType.EQUAL || token.type() == TokenType.NOT_EQUAL;
        }
    }

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

    @Override
    protected <R> R accept(Visitor<R> visitor) {
        return null;
    }

    @Override
    public ExpressionType getExpressionType() {
        return ExpressionType.CONDITION;
    }
}
