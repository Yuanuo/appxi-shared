package org.appxi.util.ext;

import org.appxi.holder.BoolHolder;

import java.util.*;
import java.util.function.BiFunction;

public final class LookupExpression {
    private final Grouped root = new Grouped(null);

    private LookupExpression() {
    }

    public double score(Object data) {
        return root.score(data);
    }

    public Set<Keyword> keywords() {
        return root.keywords();
    }

    public String toString() {
        return root.toString();
    }

    public static abstract class Expression {
        protected final Grouped parent;
        protected boolean mandatory;

        private Expression(Grouped parent) {
            this.parent = parent;
            if (null != parent) parent.children.add(this);
        }

        public abstract double score(Object data);
    }

    public static final class Grouped extends Expression {
        private final List<Expression> children = new ArrayList<>();

        private Grouped(Grouped parent) {
            super(parent);
        }

        @Override
        public double score(Object data) {
            double result = 0;
            final int size = this.children.size();
            double mandatoryScore = 0;
            for (int i = 0; i < size; i++) {
                Expression expression = this.children.get(i);
                double score = expression.score(data);
                if (expression.mandatory) {
                    if (score > 0) mandatoryScore += score; //added
                    else {
                        mandatoryScore = 0; //discard
                        for (i++; i < size; i++) { //skip next
                            if (this.children.get(i).mandatory) continue;
                            i--;
                            break;
                        }
                    }
                } else {
                    if (mandatoryScore > 0) {
                        result += mandatoryScore;
                        mandatoryScore = 0; //reset
                    }
                    if (score > 0) result += score;
                }
            }
            if (mandatoryScore > 0) result += mandatoryScore;
            return result;
        }

        @Override
        public String toString() {
            return this.toString(0);
        }

        private String toString(int indent) {
            final StringBuilder buff = new StringBuilder();
            final String indentStr = " ".repeat(indent);
            if (null != parent) {
                buff.append("(").append("\n");
            }
            for (Expression expression : this.children) {
                buff.append(indentStr).append(expression.mandatory ? "AND: " : " OR: ");
                if (expression instanceof Grouped grouped)
                    buff.append(grouped.toString(indent + 4));
                else if (expression instanceof Keyword keyword)
                    buff.append(keyword.keyword);
                else buff.append(expression);
                buff.append("\n");
            }
            if (null != parent)
                buff.append(indentStr).append(")");
            return buff.toString();
        }

        public Set<Keyword> keywords() {
            Set<Keyword> result = new LinkedHashSet<>();
            this.children.forEach(expr -> {
                if (expr instanceof Keyword keyword)
                    result.add(keyword);
                else if (expr instanceof Grouped grouped)
                    result.addAll(grouped.keywords());
            });
            return result;
        }
    }

    public static class Keyword extends Expression {
        private String keyword;
        private boolean asciiKeyword;

        public Keyword(Grouped parent, String keyword) {
            super(parent);
            this.setKeyword(keyword);
        }

        @Override
        public double score(Object data) {
            final String text = null == data ? "" : data.toString();
            double score = 0;
            if (text.equals(keyword)) score += 1.;
            else {
                if (text.startsWith(keyword)) score += .6;
                else if (text.endsWith(keyword)) score += .2;
                else if (text.contains(keyword)) score += .4;
            }
            return score;
        }

        @Override
        public String toString() {
            return this.keyword;
        }

        protected void setKeyword(String text) {
            this.keyword = text;
            //
            this.asciiKeyword = this.keyword.matches("[a-zA-Z0-9\s]+");
        }

        public final String keyword() {
            return keyword;
        }

        public final boolean isAsciiKeyword() {
            return asciiKeyword;
        }
    }

    public static Optional<LookupExpression> of(String expression) {
        return of(expression, Keyword::new);
    }

    public static Optional<LookupExpression> of(String expression, BiFunction<Grouped, String, Keyword> keywordSupplier) {
        try {
            if (null == expression || expression.isEmpty()) return Optional.empty();
            expression = expression.replaceAll("[“”\"]+", "\""); //
            expression = expression.replaceAll("[（(]", "( ");
            expression = expression.replaceAll("[）)]", " )");
            expression = expression.replaceAll("[+]+", "+");
            expression = expression.replaceAll("\s+", " ");
            //
            LookupExpression result = new LookupExpression();
            Expression node = result.root;
            final BoolHolder mandatoryState = new BoolHolder(false);
            final StringTokenizer tokenizer = new StringTokenizer(expression);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().strip();
//                System.out.println("token : " + token);
                if (token.isEmpty()) continue;
                if ("AND".equals(token) || "&&".equals(token) || token.charAt(0) == '+' && token.length() == 1) {
                    mandatoryState.value = true;
                    continue;
                }
                if ("OR".equals(token) || "||".equals(token)) {
                    mandatoryState.value = false;
                    continue;
                }
                //
                if (token.charAt(0) == '+') {
                    mandatoryState.value = true;
                    token = token.substring(1);
                }
                //
                if (token.charAt(0) == '\"') {
                    if (token.length() == 1) continue;
                    token = token.substring(1);

                    boolean closed = false;
                    if (token.charAt(token.length() - 1) == '\"') {
                        if (token.length() == 1) continue;
                        closed = true;
                        token = token.substring(0, token.length() - 1);
                    }

                    assert node instanceof Grouped;
                    Keyword keyword = keywordSupplier.apply((Grouped) node, token);
                    setMandatoryState(keyword, mandatoryState);
                    if (!closed)
                        node = keyword; // need close
                } else if (token.charAt(token.length() - 1) == '\"') {
                    token = token.substring(0, token.length() - 1);
                    if (node instanceof Keyword keyword)
                        keyword.setKeyword(keyword.keyword + " " + token);
                    node = node.parent; // close node
                } else if (token.charAt(0) == '(') {
                    assert node instanceof Grouped;
                    node = new Grouped((Grouped) node);
                    setMandatoryState(node, mandatoryState);
                } else if (token.charAt(0) == ')') {
                    node = node.parent; // close node
                } else {
                    if (node instanceof Grouped grouped) {
                        Keyword keyword = keywordSupplier.apply(grouped, token);
                        setMandatoryState(keyword, mandatoryState);
                    } else {
                        Keyword keyword = (Keyword) node;
                        keyword.setKeyword(keyword.keyword + " " + token.substring(0, token.length() - 1));
                    }
                }
            }
            return Optional.of(result);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }

    private static void setMandatoryState(Expression expression, BoolHolder mandatoryState) {
        if (mandatoryState.value) {
            expression.mandatory = true;
            // overwrite previous one
            if (expression.parent != null && expression.parent.children.size() > 1) {
                expression.parent.children.get(expression.parent.children.size() - 2).mandatory = true;
            }
            // reset for next
            mandatoryState.value = false;
        }
    }
}