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

package eu.eexcess.domaindetection.wikipedia.schools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.eexcess.sourceselection.redde.tree.BaseTreeNode;
import eu.eexcess.sourceselection.redde.tree.ValueTreeNode;

public class WikipediaSchoolsDomainTreeIndexerTest {

    @Test
    public void parseDomainFilesToTree_expectCorrectTree() {

        List<File> domainFiles = new ArrayList<File>();
        domainFiles.add(new File("root.htm"));
        domainFiles.add(new File("root.cars.htm"));
        domainFiles.add(new File("root.cars.wheels.htm"));
        domainFiles.add(new File("root.cars.porsche.htm"));
        domainFiles.add(new File("root.planes.htm"));
        domainFiles.add(new File("root.helicopter.htm"));
        domainFiles.add(new File("root.helicopter.black_hawk.htm"));

        ValueTreeNode<String> tree = WikipediaSchoolsDomainTreeIndexer.parseDomainFilesToTree(domainFiles);

        BaseTreeNode.depthFirstTraverser(tree, n -> {
            System.out.println(n.getName());
            return false;
        });
    }
}
