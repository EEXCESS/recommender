package eu.eexcess.opensearch.recommender.searchLink;

import java.util.LinkedList;
import java.util.List;

import eu.eexcess.opensearch.opensearch_description_document.documentFields.Url;

/**
 * Selects links from a list applying {@link SearchLinkFilter}.
 * 
 * @author Raoul Rubien
 */
public class SearchLinkSelector {

	/**
	 * @return a list of candidates from {@code searchLinks} applying the filter
	 *         {@code filter}
	 */
	public List<Url> select(List<Url> searchLinks, SearchLinkFilter filter) {
		List<Url> selection = new LinkedList<Url>();

		if (filter == null) {
			filter = new SearchLinkFilter();
		}

		for (Url searchLink : searchLinks) {
			if (true == isCandidate(searchLink, filter)) {
				selection.add(searchLink);
			}
		}

		return selection;
	}

	/**
	 * Applies a {@code filter} on a single {@code link}.
	 * 
	 * @return true if link accomplishes the filter requirements
	 */
	private boolean isCandidate(Url link, SearchLinkFilter filter) {
		boolean isCandidate = true;

//		if (filter.isMethodActive()) {
//			if (filter.getMethod().compareTo(link.method) != 0) {
//				isCandidate = false;
//			}
//		}

		if (filter.isTypeActive()) {
			if (filter.getType().compareTo(link.type) != 0) {
				isCandidate = false;
			}
		}

		return isCandidate;
	}
}
