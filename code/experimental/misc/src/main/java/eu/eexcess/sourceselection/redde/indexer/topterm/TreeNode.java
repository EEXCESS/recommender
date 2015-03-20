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

package eu.eexcess.sourceselection.redde.indexer.topterm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class TreeNode<T> implements Iterable<TreeNode<T>> {

	public interface NodeInspector<E> {
		void invoke(TreeNode<E> n);
	}

	private Set<TreeNode<T>> children;
	private Set<T> values;
	private String name;

	public TreeNode() {
		children = new HashSet<TreeNode<T>>();
		values = new HashSet<T>();
	}

	public boolean addChild(TreeNode<T> n) {
		return children.add(n);
	}

	public Set<TreeNode<T>> getChildren() {
		return children;
	}

	public boolean removeChild(TreeNode<T> n) {
		return children.remove(n);
	}

	public Iterator<TreeNode<T>> iterator() {
		return children.iterator();
	}

	public Set<T> getValues() {
		return values;
	}

	public void addValue(T value) {
		values.add(value);
	}

	public void addValues(Set<T> valueSet) {
		values.addAll(valueSet);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder childrenString = new StringBuilder();
		boolean isFirstChild = true;
		Iterator<TreeNode<T>> iterator = iterator();

		while (iterator.hasNext()) {
			TreeNode<T> next = iterator.next();
			if (!isFirstChild) {
				childrenString.append(", ");
			}
			childrenString.append("name [" + next.getName() + "]");
			isFirstChild = false;
		}

		StringBuilder valuesString = new StringBuilder();
		boolean isFirstValue = true;
		for (T value : values) {

			if (!isFirstValue) {
				valuesString.append(", ");
			}
			valuesString.append(value.toString());
			isFirstValue = false;
		}

		return "name [" + name + "] values [" + valuesString + "] #children [" + children.size() + "] children ["
						+ childrenString + "]";
	}

	/**
	 * depth first search for node having requested case sensitive name
	 * 
	 * @param nodeName
	 *            name to look for
	 * @param root
	 *            where to start search
	 * 
	 * @param resultCollector
	 *            set containing the zero or one result nodes
	 */
	public static <E> void findFirstNode(String nodeName, TreeNode<E> root, Set<TreeNode<E>> resultCollector) {

		if (resultCollector.size() > 0) {
			return;
		}

		if (root.getName().compareTo(nodeName) == 0) {
			resultCollector.add(root);
			return;
		}

		for (TreeNode<E> child : root.getChildren()) {
			findFirstNode(nodeName, child, resultCollector);
		}
	}

	public static <E> void depthFirstTraverser(TreeNode<E> root, NodeInspector<E> operator) {
		for (TreeNode<E> child : root.getChildren()) {
			operator.invoke(child);
			depthFirstTraverser(child, operator);
		}
	}

}
