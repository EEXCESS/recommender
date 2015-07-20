package eu.eexcess.opensearch.opensearchDescriptionDocument.parse;

/**
 * Node Names explained in <a href="http://www.opensearch.org/Specifications/OpenSearch/1.1">OpenSearch 1.1 draft 5</a>. 
 * @author Raoul Rubien
 */
public class XMLEntityNames {

	public static class OpenSearchDescription {
		public static final String NODE = "OpenSearchDescription";

		public static class Attribute {
			public static final String NAMESPACE = "xmlns";
		}
	}

	public static class ShortName {
		public static final String NODE = "ShortName";
	}

	public static class Description {
		public final static String NODE = "Description";
	}

	public static class Tags {
		public final static String NODE = "Tags";
	}

	public static class Contact {
		public static final String NODE = "Contact";
	}

	public static class Url {
		public static final String NODE = "Url";

		public static class Attribute {
			public static final String TYPE = "type";
			public static final String TEMPLATE = "template";
			public static final String INDEXOFFSET = "indexOffset";
			public static final String PAGEOFFSET = "pageOffset";
			public static final String REL = "rel";
		}

		public static class AttributeValue {
			public static final String RELRESULTS = "results";
			public static final String RELSUGGESTIONS = "suggestions";
			public static final String RELSELF = "self";
			public static final String RELCOLLECTION = "collection";
		}
	}

	public static class LongName {
		public static final String NODE = "LongName";
	}

	public static class Image {
		public static final String NODE = "Image";

		public static class Attribute {
			public static final String WIDTH = "width";
			public static final String HEIGHT = "height";
			public static final String TYPE = "type";
		}
	}

	public static class Query {
		public static final String Node = "Query";

		public static class Attribute {
			public static final String ROLE = "role";
			public static final String TITLE = "title";
			public static final String TOTALRESULTS = "totalResults";
			public static final String SEARCHTERMS = "searchTerms";
			public static final String COUNT = "count";
			public static final String STARTINDEX = "startIndex";
			public static final String STARTPAGE = "startPage";
			public static final String LANGUAGE = "language";
			public static final String INPUTENCODING = "inputEncoding";
			public static final String OUTPUTENCODING = "outputEncoding";
		}

		public static class AttributeValue {
			public static final String ROLEREQUEST = "request";
			public static final String ROLEEXAMPLE = "example";
			public static final String ROLERELATED = "related";
			public static final String ROLECORRECTION = "correction";
			public static final String ROLESUBSET = "subset";
			public static final String ROLESUPERSET = "superset";
		}
	}

	public static class Developer {
		public static final String NODE = "Developer";
	}

	public static class Attribution {
		public final static String NODE = "Attribution";
	}

	public static class SyndicationRight {
		public static final String NODE = "SyndicationRight";

		public static class NodeValue {
			public static final String OPEN = "open";
			public static final String LIMITED = "limited";
			public static final String PRIVATE = "private";
			public static final String CLOSED = "closed";
		}
	}

	public static class Language {
		public static final String NODE = "Language";
	}

	public static class OutputEncoding {
		public static final String NODE = "OutputEncoding";
	}

	public static class InputEncoding {
		public static final String NODE = "InputEncoding";
	}

	public static class AdultContent {
		public static final String NODE = "AdultContent";
	}
}
