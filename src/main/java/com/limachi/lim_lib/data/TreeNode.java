package com.limachi.lim_lib.data;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class TreeNode<T> {
    private TreeNode<T> parent = null;
    private TreeNode<T> child = null;
    private TreeNode<T> next = null;
    private TreeNode<T> prev = null;
    private T content;
    private boolean hasPriority = false;

    public TreeNode(T content) {
        this.content = content;
    }

    /**
     * Run a function on all nodes below this one (children) (skip the calling node).
     * @param run Predicate: if this return true, stop the iteration.
     * @param parentFirst: set to true to change the iteration order from child/next/self to self/next/child.
     * @param depth: how deep this should run, set to -1 for unlimited.
     * @return this (the node calling the method).
     */
    public boolean propagateDown(Predicate<TreeNode<T>> run, boolean parentFirst, int depth) {
        if (child != null) {
            if (parentFirst)
                return child.propagateDownPF(run, depth < 0 ? Integer.MAX_VALUE : depth);
            else
                return child.propagateDownCF(run, depth < 0 ? Integer.MAX_VALUE : depth);
        }
        return false;
    }

    public TreeNode<T> propagateDown(Consumer<T> runOnAllContents, int depth, boolean parentFirst) {
        propagateDown(n->{runOnAllContents.accept(n.getContent()); return false;}, parentFirst, depth);
        return this;
    }

    public boolean propagateDown(Predicate<T> runOnContents, int depth, boolean parentFirst) {
        return propagateDown(n->runOnContents.test(n.getContent()), parentFirst, depth);
    }

    private boolean propagateDownPF(Predicate<TreeNode<T>> run, int depth) {
        return depth > 0 && (run.test(this) || (next != null && next.propagateDownPF(run, depth) || (child != null && child.propagateDownPF(run, depth - 1))));
    }

    private boolean propagateDownCF(Predicate<TreeNode<T>> run, int depth) {
        if (hasPriority)
            return depth > 0 && (run.test(this) || (child != null && child.propagateDownCF(run, depth - 1)) || (next != null && next.propagateDownCF(run, depth)));
        return depth > 0 && ((child != null && child.propagateDownCF(run, depth - 1)) || (next != null && next.propagateDownCF(run, depth)) || run.test(this));
    }

    /**
     * Run a function on all parent nodes (skip the calling node).
     * @param run Predicate: if this return true, stop the iteration.
     * @param parentFirst: if set to true, will run from top parent to node, false from node to top parent.
     * @return this (the node calling the method).
     */
    public boolean propagateUp(Predicate<TreeNode<T>> run, boolean parentFirst) {
        if (parent != null) {
            if (parentFirst)
                return parent.propagateUpPF(run);
            else
                return parent.propagateUpCF(run);
        }
        return false;
    }

    private boolean propagateUpPF(Predicate<TreeNode<T>> run) {
        return (parent != null && parent.propagateUpPF(run)) || run.test(this);
    }

    private boolean propagateUpCF(Predicate<TreeNode<T>> run) {
        return run.test(this) || (parent != null && parent.propagateUpCF(run));
    }

    /**
     * Run a function on all neighbours (next/prev nodes). Will start from the first node, not the one calling the method.
     * @param run Predicate: if this return true, stop the iteration.
     * @param includeSelf: if set to false, will skip the calling node.
     * @return this (the node calling the method).
     */
    public TreeNode<T> propagateToNeighbours(Predicate<TreeNode<T>> run, boolean includeSelf) {
        TreeNode<T> n = parent != null ? parent.child : this;
        while (((!includeSelf && n == this) || !run.test(n)) && n.next != null)
            n = n.next;
        return this;
    }

    /**
     * Add a child to the node, pushing it first in the iteration order and updating the parent's child reference.
     * @param newChild will have its parent and next references updated.
     * @return this (the node calling the method).
     */
    public TreeNode<T> addChild(TreeNode<T> newChild) {
        newChild.parent = this;
        if (child != null) {
            newChild.next = child;
            child.prev = newChild;
        }
        child = newChild;
        return this;
    }

    /**
     * Remove (detach) the calling node itself from its tree.
     * Child nodes will still be linked (meaning this node will be the new root of its children).
     * 'hasPriority' flags and node order will not be changed.
     * On a root node, this is basically a no op.
     * @return this (the node calling the method).
     */
    public TreeNode<T> removeNode() {
        if (next != null)
            next.prev = prev;
        if (prev != null)
            prev.next = next;
        if (parent != null && parent.child == this)
            parent.child = next;
        parent = null;
        next = null;
        prev = null;
        return this;
    }

    /**
     * Change the order of the parent(s) so that an iteration will go through this node first (in child first mode).
     * Will also update the 'hasPriority' flag in the node, its children and the parents.
     * @return this (the node calling the method).
     */
    public TreeNode<T> prioritize() {
        if (parent == null || parent.child == this) {
            hasPriority = true;
            return this;
        }
        propagateDown(n->n.hasPriority = false, true, -1);
        propagateUp(n->n.hasPriority = false, false);
        hasPriority = true;
        if (prev != null)
            prev.next = next;
        if (next != null)
            next.prev = prev;
        parent.child.prev = this;
        next = parent.child;
        parent.child = this;
        parent.prioritize();
        return this;
    }

    /**
     * Clear ALL the priorities of the tree, starting from the root node.
     * @return this (the node calling the method).
     */
    public TreeNode<T> clearAllPriorities() {
        getRoot().propagateDown(n->n.hasPriority = false, true, -1);
        return this;
    }

    /**
     * Search and return the root node (iterate parents until a dangling one is found).
     * @return the top most parent (aka root).
     */
    public TreeNode<T> getRoot() {
        TreeNode<T> n = this;
        while (n.parent != null)
            n = n.parent;
        return n;
    }

    public <O> O safeParentCall(Function<TreeNode<T>, O> run, O onFail) {
        if (parent != null)
            return run.apply(parent);
        return onFail;
    }

    public TreeNode<T> getParent() { return parent; }

    public TreeNode<T> getChild() { return child; }

    public TreeNode<T> getNext() { return next; }

    public TreeNode<T> getPrev() { return prev; }

    public T getContent() { return content; }
}
