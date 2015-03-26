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

package eu.eexcess.tree;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ValueTreeNode<T> extends BaseTreeNode<T> {

	private Set<T> values;

	public ValueTreeNode() {
		values = new HashSet<T>();
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

		return "values [" + valuesString + "] children [" + childrenString + "]";
	}
}
