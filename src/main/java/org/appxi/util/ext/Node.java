package org.appxi.util.ext;

import org.appxi.holder.RawHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Node<T> implements Serializable {
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

    public List<Node<T>> children() {
        return children;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public Node<T> add(T value) {
        return new Node<>(this, value);
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
        this.children.forEach(child -> child.traverse(level + 1, consumer));
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void printTree(BiFunction<Node<T>, T, String> format) {
        this.traverse((level, node, val) -> {
            final String str = null != format ? format.apply(node, val) : String.valueOf(val);
            System.out.println("\t".repeat(level) + str);
        });
    }

    public Node<T> merge(Node<T> other) {
        other.children.forEach(c -> c.parent = this);
        this.children.addAll(other.children);
        return this;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private static final Object AK_LINKED_PREV = new Object();
    private static final Object AK_LINKED_NEXT = new Object();
    private Node<T> linkedPrevious, linkedNext;

    public final void relinkChildren() {
        relinkChildren((l, n, v) -> !n.hasChildren());
    }

    public final void relinkChildren(FiPredicateX3<Integer, Node<T>, T> nodeAcceptor) {
        final RawHolder<Node<T>> ctx = new RawHolder<>();
        this.traverse((level, node, val) -> {
            if (null != nodeAcceptor && nodeAcceptor.test(level, node, val)) {
                if (null != ctx.value) {
                    ctx.value.linkedNext = node;
                    node.linkedPrevious = ctx.value;
//                ctx.value.attr(AK_LINKED_NEXT, node);
//                node.attr(AK_LINKED_PREV, ctx.value);
                }
                ctx.value = node; // update to last one
            }
        });
    }

    public final Node<T> getLinkedPrevious() {
//        return this.attr(AK_LINKED_PREV);
        return linkedPrevious;
    }

    public final Node<T> getLinkedNext() {
//        return this.attr(AK_LINKED_NEXT);
        return linkedNext;
    }
}
