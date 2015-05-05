package eu.eexcess.federatedrecommender;

import eu.eexcess.dataformats.result.DocumentBadge;

public interface DocumentBadgePredicate {

	boolean test(DocumentBadge docs);

}
