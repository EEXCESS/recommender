/**
 * Copyright (C) 2015
 * "Kompetenzzentrum fuer wissensbasierte Anwendungen Forschungs- und EntwicklungsgmbH" 
 * (Know-Center), Graz, Austria, office@know-center.at.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Raoul Rubien
 */

package eu.eexcess.federatedrecommender.utils.tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class BaseTreeNode implements Iterable<TreeNode>, TreeNode {

    private Set<TreeNode> children;
    private String name;

    public BaseTreeNode(String name) {
        this();
        this.name = name;
    }

    public BaseTreeNode() {
        children = new HashSet<TreeNode>();
    }

    @Override
    public boolean addChild(TreeNode n) {
        return children.add(n);
    }

    public boolean addChildren(Set<? extends TreeNode> nodes) {
        return children.addAll(nodes);
    }

    @Override
    public Set<TreeNode> getChildren() {
        return new HashSet<TreeNode>(children);
    }

    public boolean removeChild(TreeNode n) {
        return children.remove(n);
    }

    public void removeChildren() {
        children.clear();
    }

    @Override
    public Iterator<TreeNode> iterator() {
        return children.iterator();
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder childrenString = new StringBuilder();
        boolean isFirstChild = true;
        Iterator<TreeNode> iterator = iterator();

        while (iterator.hasNext()) {
            TreeNode next = iterator.next();
            if (!isFirstChild) {
                childrenString.append(", ");
            }
            childrenString.append("name [" + next.getName() + "]");
            isFirstChild = false;
        }

        return "name [" + name + "] #children [" + children.size() + "] children [" + childrenString + "]";
    }

    /**
     * depth first search for node having requested case sensitive name
     * 
     * @param node
     *            node to look for
     * @param root
     *            where to start search
     * 
     * @param resultCollector
     *            set containing the zero or one result nodes
     */
    public static void findFirstNode(TreeNode node, TreeNode root, Set<TreeNode> resultCollector) {

        if (resultCollector.size() > 0) {
            return;
        }

        if (root.equals(node)) {
            resultCollector.add(root);
            return;
        }

        for (TreeNode child : root.getChildren()) {
            findFirstNode(node, child, resultCollector);
        }
    }

    /**
     * Invokes the operator and traverses (depth first) the nodes until invocation returns true (abort traversal) or all nodes are traversed.
     * @param root node where to start from
     * @param operator the node visitor
     */
    public static <E> void depthFirstTraverser(TreeNode root, NodeInspector operator) {
        if (operator.invoke(root)) {
            return;
        }
        for (TreeNode child : root.getChildren()) {
            BaseTreeNode c = (BaseTreeNode) child;
            depthFirstTraverser(c, operator);
        }
    }

    /**
     * note on equality: if {@link #name} equals other's name it is considered
     * as equal (do not consider node's children)
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * note on equality: if {@link #name} equals other's name it is considered
     * as equal (do not consider node's children)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseTreeNode other = (BaseTreeNode) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}


///**
// * A tree node implementation that can store it's name.
// * @author Raoul Rubien
// *
// * @param <T> id of the node; i.e. String name 
// */
//
//public class BaseTreeNode<T> implements Iterable<TreeNode<T>>, TreeNode<T> {
//
//    private Set<TreeNode<T>> children;
//    private String name;
//
//    public BaseTreeNode(String name) {
//        this();
//        this.name = name;
//    }
//
//    public BaseTreeNode() {
//        children = new HashSet<TreeNode<T>>();
//    }
//
//    @Override
//    public boolean addChild(TreeNode<T> n) {
//        return children.add(n);
//    }
//
//    public boolean addChildren(Set<? extends TreeNode<T>> nodes) {
//        return children.addAll(nodes);
//    }
//
//    @Override
//    public Set<TreeNode<T>> getChildren() {
//        return new HashSet<TreeNode<T>>(children);
//    }
//
//    public boolean removeChild(TreeNode<T> n) {
//        return children.remove(n);
//    }
//
//    public void removeChildren() {
//        children.clear();
//    }
//
//    @Override
//    public Iterator<TreeNode<T>> iterator() {
//        return children.iterator();
//    }
//
//    @Override
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder childrenString = new StringBuilder();
//        boolean isFirstChild = true;
//        Iterator<TreeNode<T>> iterator = iterator();
//
//        while (iterator.hasNext()) {
//            TreeNode<T> next = iterator.next();
//            if (!isFirstChild) {
//                childrenString.append(", ");
//            }
//            childrenString.append("name [" + next.getName() + "]");
//            isFirstChild = false;
//        }
//
//        return "name [" + name + "] #children [" + children.size() + "] children [" + childrenString + "]";
//    }
//
//    /**
//     * depth first search for node having requested case sensitive name
//     * 
//     * @param node
//     *            node to look for
//     * @param root
//     *            where to start search
//     * 
//     * @param resultCollector
//     *            set containing the zero or one result nodes
//     */
//    public static <E> void findFirstNode(TreeNode<E> node, TreeNode<E> root, Set<TreeNode<E>> resultCollector) {
//
//        if (resultCollector.size() > 0) {
//            return;
//        }
//
//        if (root.equals(node)) {
//            resultCollector.add(root);
//            return;
//        }
//
//        for (TreeNode<E> child : root.getChildren()) {
//            findFirstNode(node, child, resultCollector);
//        }
//    }
//
//    /**
//     * Invokes the operator and traverses (depth first) the nodes until invocation returns true (abort traversal) or all nodes are traversed.
//     * @param root node where to start from
//     * @param operator the node visitor
//     */
//    public static <E> void depthFirstTraverser(TreeNode<E> root, NodeInspector<E> operator) {
//        if (operator.invoke(root)) {
//            return;
//        }
//        for (TreeNode<E> child : root.getChildren()) {
//            BaseTreeNode<E> c = (BaseTreeNode<E>) child;
//            depthFirstTraverser(c, operator);
//        }
//    }
//
//    /**
//     * note on equality: if {@link #name} equals other's name it is considered
//     * as equal (do not consider node's children)
//     */
//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((name == null) ? 0 : name.hashCode());
//        return result;
//    }
//
//    /**
//     * note on equality: if {@link #name} equals other's name it is considered
//     * as equal (do not consider node's children)
//     */
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj)
//            return true;
//        if (obj == null)
//            return false;
//        if (getClass() != obj.getClass())
//            return false;
//        @SuppressWarnings("unchecked")
//        BaseTreeNode<T> other = (BaseTreeNode<T>) obj;
//        if (name == null) {
//            if (other.name != null)
//                return false;
//        } else if (!name.equals(other.name))
//            return false;
//        return true;
//    }
//
//}

