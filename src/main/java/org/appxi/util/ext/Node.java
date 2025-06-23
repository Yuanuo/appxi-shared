package org.appxi.util.ext;

import org.appxi.holder.RawHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Node<T> extends Attributes implements Serializable {
    private Node<T> parent;
    public final List<Node<T>> children = new ArrayList<>();
    public T value;

    public Node() {
    }

    public Node(Node<T> parent) {
        this(parent, null);
    }

    public Node(T value) {
        this(null, value);
    }

    public Node(Node<T> parent, T value) {
        this.parent = parent;
        this.value = value;

        if (null != parent)
            parent.children.add(this);
    }

    public Node<T> setParent(Node<T> parent) {
        // unlink
        if (null != this.parent)
            this.parent.children.remove(this);

        // link
        this.parent = parent;
        if (null != parent && !parent.children.contains(this))
            parent.children.add(this);
        return this;
    }

    public Node<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public T getValue() {
        return value;
    }

    public final Node<T> root() {
        return null == this.parent ? this : this.parent.root();
    }

    public final Node<T> parent() {
        return this.parent;
    }

    public final Node<T> previous() {
        final RawHolder<Node<T>> ctx = new RawHolder<>();
        final Node<T> stop = this;
        root().filter((level, node, val) -> {
            if (node == stop)
                return true; // break walk
            if (!node.hasChildren())
                ctx.value = node; // keep the last one leaf
            return false;
        });
        return ctx.value;
    }

    public final Node<T> next() {
        final RawHolder<Node<T>> ctx = new RawHolder<>();
        final Node<T> stop = this;
        root().filterReversed((level, node, val) -> {
            if (node == stop)
                return true; // break walk
            if (!node.hasChildren())
                ctx.value = node; // keep the last one leaf
            return false;
        });
        return ctx.value;
    }

    public final Node<T> previousSibling() {
        if (null == parent) {
            return null;
        }
        int idx = parent.children.indexOf(this) - 1;
        return idx < 0 ? null : parent.children.get(idx);
    }

    public final Node<T> nextSibling() {
        if (null == parent) {
            return null;
        }
        int idx = parent.children.indexOf(this) + 1;
        return idx > 0 && idx < parent.children.size() ? parent.children.get(idx) : null;
    }

    public List<Node<T>> children() {
        return children;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public void addChildren(Collection<Node<T>> nodes) {
        nodes.forEach(node -> node.setParent(this));
    }

    public Node<T> add(Object value) {
        return new Node<>(this, (T) value);
    }

    public final int level() {
        return this.parents().size();
    }

    public List<Node<T>> parents() {
        final List<Node<T>> paths = new ArrayList<>(this.paths());
        if (paths.size() > 0)
            paths.remove(paths.size() - 1);
        return paths;
    }

    public List<Node<T>> paths() {
        final List<Node<T>> result = new ArrayList<>();
        Node<T> node = this;
        while (null != node) {
            result.add(0, node);
            node = node.parent;
        }
        return result;
    }

    public void traverse(FiConsumerX3<Integer, Node<T>, T> consumer) {
        this.traverse(this.level(), consumer);
    }

    private void traverse(int level, FiConsumerX3<Integer, Node<T>, T> consumer) {
        if (null != this.parent) // skip root
            consumer.accept(level, this, this.value);
        for (int i = 0; i < this.children.size(); i++) {
            this.children.get(i).traverse(level + 1, consumer);
        }
    }

    public void traverseReversed(FiConsumerX3<Integer, Node<T>, T> consumer) {
        traverseReversed(this.level(), consumer);
    }

    private void traverseReversed(int level, FiConsumerX3<Integer, Node<T>, T> consumer) {
        for (int i = this.children.size() - 1; i > -1; i--)
            this.children.get(i).traverseReversed(level + 1, consumer);
        if (null != this.parent) // skip root
            consumer.accept(level, this, this.value);
    }

    public Node<T> filter(FiPredicateX3<Integer, Node<T>, T> predicate) {
        return this.filter(this.level(), predicate);
    }

    private Node<T> filter(int level, FiPredicateX3<Integer, Node<T>, T> predicate) {
        if (null != this.parent) // skip root
            if (predicate.test(level, this, this.value))
                return this;
        Node<T> breakingNode;
        for (Node<T> child : this.children) {
            breakingNode = child.filter(level + 1, predicate);
            if (null != breakingNode)
                return breakingNode;
        }
        return null;
    }

    public Node<T> filterReversed(FiPredicateX3<Integer, Node<T>, T> predicate) {
        return filterReversed(this.level(), predicate);
    }

    private Node<T> filterReversed(int level, FiPredicateX3<Integer, Node<T>, T> predicate) {
        Node<T> breakingNode;
        for (int i = this.children.size() - 1; i > -1; i--) {
            breakingNode = this.children.get(i).filterReversed(level + 1, predicate);
            if (null != breakingNode)
                return breakingNode;
        }
        if (null != this.parent) // skip root
            if (predicate.test(level, this, this.value))
                return this;
        return null;
    }

    public Node<T> findParent(Predicate<Node<T>> predicate) {
        if (predicate.test(this))
            return this;
        return null == this.parent ? null : this.parent.findParent(predicate);
    }

    public Node<T> findFirst(Predicate<Node<T>> predicate) {
        return filter((level, node, val) -> predicate.test(node));
    }

    public Node<T> findLast(Predicate<Node<T>> predicate) {
        return filterReversed((level, node, val) -> predicate.test(node));
    }

    public T findFirstData(Predicate<Node<T>> predicate) {
        final Node<T> node = findFirst(predicate);
        return null == node ? null : node.value;
    }

    public T findLastData(Predicate<Node<T>> predicate) {
        final Node<T> node = findLast(predicate);
        return null == node ? null : node.value;
    }

    /// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void printTree(BiFunction<Node<T>, T, String> format) {
        this.traverse((level, node, val) -> {
            final String str = null != format ? format.apply(node, val) : String.valueOf(val);
            System.out.println("\t".repeat(level) + str);
        });
    }

    public Node<T> merge(Node other) {
        other.children.forEach(c -> ((Node) c).parent = this);
        this.children.addAll(other.children);
        return this;
    }

    /// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final Object AK_LINKED_PREV = new Object();
    private static final Object AK_LINKED_NEXT = new Object();

    public final void relinkChildren() {
        relinkChildren((l, n, v) -> !n.hasChildren());
    }

    public final void relinkChildren(FiPredicateX3<Integer, Node<T>, T> nodeAcceptor) {
        final RawHolder<Node<T>> ctx = new RawHolder<>();
        this.traverse((level, node, val) -> {
            if (null != nodeAcceptor && nodeAcceptor.test(level, node, val)) {
                if (null != ctx.value) {
                    ctx.value.attr(AK_LINKED_NEXT, node);
                    node.attr(AK_LINKED_PREV, ctx.value);
                }
                ctx.value = node; // update to last one
            }
        });
    }

    public final Node<T> getLinkedPrevious() {
        return this.attr(AK_LINKED_PREV);
    }

    public final Node<T> getLinkedNext() {
        return this.attr(AK_LINKED_NEXT);
    }

    /// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void walk(Walker<T> walker) {
        _walk(walker, 1, this);
    }

    private void _walk(Walker<T> walker, int depth, Node<T> node) {
        walker.head(depth, node, node.value);
        //
        int nextDepth = depth + 1;
        for (int i = 0; i < node.children.size(); i++) {
            _walk(walker, nextDepth, node.children.get(i));
        }
        //
        walker.tail(depth, node, node.value);
    }

    public void wrap(BiPredicate<Node<T>, T> start, BiPredicate<Node<T>, T> end, Function<List<Node<T>>, T> convertor) {
        List<Node<T>> newChildren = select(start, end);
        if (!newChildren.isEmpty()) {
            int insertIdx = children.indexOf(newChildren.getFirst());
            children.removeAll(newChildren);

            Node<T> newNode = new Node<>(convertor.apply(newChildren));
            children.add(insertIdx, newNode);
            newNode.setParent(this);

            newNode.addChildren(newChildren);
        }
    }

    public List<Node<T>> select(BiPredicate<Node<T>, T> start, BiPredicate<Node<T>, T> end) {
        int startIdx = -1;
        int endIdx = -1;
        for (int i = 0; i < children.size(); i++) {
            Node<T> child = children.get(i);
            if (startIdx == -1) {
                if (start == null || start.test(child, child.value)) {
                    startIdx = i;
                }
                continue;
            }
            if (end != null && end.test(child, child.value)) {
                endIdx = i + 1;
                break;
            }
        }
        if (endIdx == -1) {
            endIdx = children.size();
        }
        if (startIdx == endIdx) {
            return new ArrayList<>(children.subList(startIdx, startIdx + 1));
        }
        return startIdx < 0 ? new ArrayList<>() : new ArrayList<>(children.subList(startIdx, endIdx));
    }

    public void unwarp(BiConsumer<Node<T>, Node<T>> consumer) {
        if (this.children.isEmpty()) {
            return;
        }
        final int idxInParents = this.index();

        final List<Node<T>> newChildren = new ArrayList<>(this.children);
        this.children.clear();
        this.parent.children.addAll(idxInParents + 1, newChildren);
        newChildren.forEach(c -> c.setParent(parent));

        if (consumer != null) {
            consumer.accept(this, newChildren.getFirst());
        }
    }

    public int index() {
        return this.parent == null ? -1 : this.parent.children.indexOf(this);
    }

    public void remove() {
        this.setParent(null);
    }

    public void empty() {
        List.copyOf(this.children).forEach(Node::remove);
    }

    public interface Walker<T> {
        default void head(int depth, Node<T> node, T nodeVal) {
        }

        default void tail(int depth, Node<T> node, T nodeVal) {
        }
    }
}
