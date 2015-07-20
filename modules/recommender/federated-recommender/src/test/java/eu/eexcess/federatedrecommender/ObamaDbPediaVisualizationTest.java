//package eu.eexcess.federatedrecommender;
//
//import eu.eexcess.config.FederatedRecommenderConfiguration;
//import eu.eexcess.dataformats.userprofile.ContextKeyword;
//import eu.eexcess.federatedrecommender.dbpedia.DbPediaGraph;
//import eu.eexcess.federatedrecommender.dbpedia.DbPediaSolrIndex;
//import eu.eexcess.federatedrecommender.utils.FederatedRecommenderException;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Rectangle;
//import java.awt.geom.Rectangle2D;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.swing.JApplet;
//import javax.swing.JScrollPane;
//
//import org.jgraph.JGraph;
//import org.jgraph.graph.DefaultEdge;
//import org.jgraph.graph.DefaultGraphCell;
//import org.jgraph.graph.GraphConstants;
//import org.jgrapht.ext.JGraphModelAdapter;
//import org.jgrapht.graph.SimpleWeightedGraph;
//
//import com.jgraph.layout.JGraphFacade;
//import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;
//
///**
// * Adapted from: http://jgrapht.org/visualizations.html
// * @author hziak
// */
//
//public class ObamaDbPediaVisualizationTest extends JApplet {
// 
//	private static final long serialVersionUID = -7869718395012820223L;
//	private static final Color     DEFAULT_BG_COLOR = Color.decode( "#FAFBFF" );
//    private static final Dimension DEFAULT_SIZE = new Dimension(20000, 20000);
//    private static final Dimension WINDOW_SIZE = new Dimension(800	,600);
//	private static final int HITS_LIMIT = 10;
//	private static final int DEPTH_LIMIT = 5;
//
//    // 
//    private JGraphModelAdapter<String, DefaultEdge> m_jgAdapter;
//
//    /**
//     * @see java.applet.Applet#init().
//     */
//    public void init(  ) {
//    	FederatedRecommenderConfiguration fConfiguration = new FederatedRecommenderConfiguration();
//    	fConfiguration.solrServerUri ="http://localhost:8983/solr/";
//    	DbPediaSolrIndex dbPediaSolrIndex = new DbPediaSolrIndex(fConfiguration);
//    	DbPediaGraph dbPediaGraph = new DbPediaGraph(dbPediaSolrIndex); 
//		List<ContextKeyword> keywordList = new ArrayList<ContextKeyword>();
////		keywordList.add("graz");
////		keywordList.add("vienna");
//		keywordList.add(new ContextKeyword("U.S.A"));
//		keywordList.add(new ContextKeyword("Obama"));
//		keywordList.add(new ContextKeyword("Clinton"));
//		List<String> keyNodes = new ArrayList<String>();
//		SimpleWeightedGraph<String, org.jgraph.graph.DefaultEdge> graphA = null;
//		try {
//			graphA = dbPediaGraph.getFromKeywords(keywordList , keyNodes , HITS_LIMIT, DEPTH_LIMIT);
//		} catch (FederatedRecommenderException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//        // create a visualization using JGraph, via an adapter
//        m_jgAdapter = new JGraphModelAdapter<String, DefaultEdge>( graphA );
//
//        JGraph jgraph = new JGraph( m_jgAdapter );
//
//        adjustDisplaySettings( jgraph );
//        getContentPane(  ).add( jgraph );
//        resize( DEFAULT_SIZE );
//        
//       
//       
//         
//      
//        
//		JGraphHierarchicalLayout hir = new JGraphHierarchicalLayout();
//		JGraphFacade graphFacade = new JGraphFacade(jgraph);      
//		 JScrollPane scrollPane = new JScrollPane();
//	   
//	        setPreferredSize(new Dimension(450, 110));
//	        
//	        add(scrollPane, BorderLayout.CENTER);
//	        
//	hir.run(graphFacade);
//	    final Map<?, ?> nestedMap = graphFacade.createNestedMap(true, true);
//	    jgraph.getGraphLayoutCache().edit(nestedMap);
//
//    }
//
//
//    private void adjustDisplaySettings( JGraph jg ) {
//    	
//        jg.setPreferredSize( WINDOW_SIZE );
//        
//        jg.revalidate();
//        Color  c        = DEFAULT_BG_COLOR;
//        String colorStr = null;
//
//        try {
//            colorStr = getParameter( "bgcolor" );
//        }
//         catch( Exception e ) {}
//
//        if( colorStr != null ) {
//            c = Color.decode( colorStr );
//        }
//
//        jg.setBackground( c );
//    }
//
//
//    
//    @SuppressWarnings("unused")
//	private void positionVertexAt( Object vertex, int x, int y ) {
//        DefaultGraphCell cell = m_jgAdapter.getVertexCell( vertex );
//        Map<?, ?>              attr = cell.getAttributes(  );
//        Rectangle2D      b    =  GraphConstants.getBounds(attr);
//
//        GraphConstants.setBounds( attr, new Rectangle( x, y, b.getBounds().width, b.getBounds().height ) );
//        
//        Map<DefaultGraphCell, Map<?, ?>> cellAttr = new HashMap<DefaultGraphCell, Map<?, ?>>(  );
//        cellAttr.put( cell, attr );
//        m_jgAdapter.edit( cellAttr, null, null, null );
//    }
//}
