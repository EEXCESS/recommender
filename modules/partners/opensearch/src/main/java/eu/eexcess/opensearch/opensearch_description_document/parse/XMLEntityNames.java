package eu.eexcess.opensearch.opensearch_description_document.parse;

/**
 * Node Names explained in <a
 * href="http://www.opensearch.org/Specifications/OpenSearch/1.1">OpenSearch 1.1
 * draft 5</a>.
 * 
 * @author Raoul Rubien
 */
public class XMLEntityNames {

    public static class OpenSearchDescription {
        public static final String NODE = "OpenSearchDescription";

        private OpenSearchDescription() {
        }

        public static class Attribute {
            public static final String NAMESPACE = "xmlns";

            private Attribute() {
            }
        }
    }

    public static class ShortName {
        public static final String NODE = "ShortName";

        private ShortName() {
        }
    }

    public static class Description {
        public static final String NODE = "Description";

        private Description() {
        }
    }

    public static class Tags {
        public static final String NODE = "Tags";

        private Tags() {
        }
    }

    public static class Contact {
        public static final String NODE = "Contact";

        private Contact() {
        }
    }

    public static class Url {
        public static class Attribute {
            public static final String TYPE = "type";
            public static final String TEMPLATE = "template";
            public static final String INDEXOFFSET = "indexOffset";
            public static final String PAGEOFFSET = "pageOffset";
            public static final String REL = "rel";

            private Attribute() {
            }
        }

        public static class AttributeValue {
            public static final String RELRESULTS = "results";
            public static final String RELSUGGESTIONS = "suggestions";
            public static final String RELSELF = "self";
            public static final String RELCOLLECTION = "collection";

            private AttributeValue() {
            }
        }

        public static final String NODE = "Url";

        private Url() {
        }
    }

    public static class LongName {
        public static final String NODE = "LongName";

        private LongName() {
        }
    }

    public static class Image {
        public static class Attribute {
            public static final String WIDTH = "width";
            public static final String HEIGHT = "height";
            public static final String TYPE = "type";

            private Attribute() {
            }
        }

        public static final String NODE = "Image";

        private Image() {
        }
    }

    public static class Query {
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

            private Attribute() {
            }
        }

        public static class AttributeValue {
            public static final String ROLEREQUEST = "request";
            public static final String ROLEEXAMPLE = "example";
            public static final String ROLERELATED = "related";
            public static final String ROLECORRECTION = "correction";
            public static final String ROLESUBSET = "subset";
            public static final String ROLESUPERSET = "superset";

            private AttributeValue() {
            }
        }

        public static final String NODE = "Query";

        private Query() {
        }
    }

    public static class Developer {
        public static final String NODE = "Developer";

        private Developer() {
        }
    }

    public static class Attribution {
        public static final String NODE = "Attribution";

        private Attribution() {
        }
    }

    public static class SyndicationRight {
        public static class NodeValue {
            public static final String OPEN = "open";
            public static final String LIMITED = "limited";
            public static final String PRIVATE = "private";
            public static final String CLOSED = "closed";

            private NodeValue() {
            }
        }

        public static final String NODE = "SyndicationRight";

        private SyndicationRight() {
        }
    }

    public static class Language {
        public static final String NODE = "Language";

        private Language() {
        }
    }

    public static class OutputEncoding {
        public static final String NODE = "OutputEncoding";

        private OutputEncoding() {
        }
    }

    public static class InputEncoding {
        public static final String NODE = "InputEncoding";

        private InputEncoding() {
        }
    }

    public static class AdultContent {
        public static final String NODE = "AdultContent";

        private AdultContent() {
        }
    }

    private XMLEntityNames() {
    }
}
