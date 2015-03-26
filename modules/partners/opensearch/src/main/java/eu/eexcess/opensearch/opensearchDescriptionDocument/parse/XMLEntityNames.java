package eu.eexcess.opensearch.opensearchDescriptionDocument.parse;

/**
 * Node Names explained in <a href="http://www.opensearch.org/Specifications/OpenSearch/1.1">OpenSearch 1.1 draft 5</a>. 
 * @author Raoul Rubien
 */
public class XMLEntityNames {

	public static class OpenSearchDescription {
		public static final String Node = "OpenSearchDescription";

		public static class Attribute {
			public static final String Namespace = "xmlns";
		}
	}

	public static class ShortName {
		public static final String Node = "ShortName";
	}

	public static class Description {
		public final static String Node = "Description";
	}

	public static class Tags {
		public final static String Node = "Tags";
	}

	public static class Contact {
		public static final String Node = "Contact";
	}

	public static class Url {
		public static final String Node = "Url";

		public static class Attribute {
			public static final String Type = "type";
			public static final String Template = "template";
			public static final String IndexOffset = "indexOffset";
			public static final String pageOffset = "pageOffset";
			public static final String Rel = "rel";
		}

		public static class AttributeValue {
			public static final String RelResults = "results";
			public static final String RelSuggestions = "suggestions";
			public static final String RelSelf = "self";
			public static final String RelCollection = "collection";
		}
	}

	public static class LongName {
		public static final String Node = "LongName";
	}

	public static class Image {
		public static final String Node = "Image";

		public static class Attribute {
			public static final String Width = "width";
			public static final String Height = "height";
			public static final String Type = "type";
		}
	}

	public static class Query {
		public static final String Node = "Query";

		public static class Attribute {
			public static final String Role = "role";
			public static final String Title = "title";
			public static final String TotalResults = "totalResults";
			public static final String SearchTerms = "searchTerms";
			public static final String Count = "count";
			public static final String StartIndex = "startIndex";
			public static final String StartPage = "startPage";
			public static final String Language = "language";
			public static final String InputEncoding = "inputEncoding";
			public static final String OutputEncoding = "outputEncoding";
		}

		public static class AttributeValue {
			public static final String RoleRequest = "request";
			public static final String RoleExample = "example";
			public static final String RoleRelated = "related";
			public static final String RoleCorrection = "correction";
			public static final String RoleSubset = "subset";
			public static final String RoleSuperset = "superset";
		}
	}

	public static class Developer {
		public static final String Node = "Developer";
	}

	public static class Attribution {
		public final static String Node = "Attribution";
	}

	public static class SyndicationRight {
		public static final String Node = "SyndicationRight";

		public static class NodeValue {
			public static final String Open = "open";
			public static final String Limited = "limited";
			public static final String Private = "private";
			public static final String Closed = "closed";
		}
	}

	public static class Language {
		public static final String Node = "Language";
	}

	public static class OutputEncoding {
		public static final String Node = "OutputEncoding";
	}

	public static class InputEncoding {
		public static final String Node = "InputEncoding";
	}

	public static class AdultContent {
		public static final String Node = "AdultContent";
	}
}
