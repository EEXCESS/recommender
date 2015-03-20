package eu.eexcess.opensearch.datalayer;

import org.w3c.dom.Document;

import com.hp.hpl.jena.query.QuerySolution;
import com.sun.istack.logging.Logger;

import eu.eexcess.dataformats.result.Result;
import eu.eexcess.dataformats.result.ResultList;
import eu.eexcess.partnerdata.api.ITransformer;
import eu.eexcess.partnerdata.reference.Transformer;

public class OpensearchTransformer extends Transformer implements ITransformer {

	Logger logger = Logger.getLogger(OpensearchTransformer.class);

	@Override
	protected ResultList postProcessResults(Document orgPartnerResult, ResultList resultList) {
		return super.postProcessResults(orgPartnerResult, resultList);
	}

	@Override
	protected Result postProcessResult(Document orgPartnerResult, Result result, QuerySolution querySol) {
		return super.postProcessResult(orgPartnerResult, result, querySol);
	}

}
