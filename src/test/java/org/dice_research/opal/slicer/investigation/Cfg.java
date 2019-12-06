package org.dice_research.opal.slicer.investigation;

import org.dice_research.opal.slicer.investigation.SparqlSources;

public abstract class Cfg {

	public static final String VBB_LOCAL = "local-vbb";
	private static final String VBB_LOCAL_URL = "http://localhost:3030/vbb/sparql";

	public static final String MCLOUD_LOCAL = "local-mcloud";
	private static final String MCLOUD_LOCAL_URL = "http://localhost:3030/mcloud-2019-06-24/sparql";

	public static final String OPAL_LOCAL = "local-opal";
	private static final String OPAL_LOCAL_URL = "http://localhost:3030/opal-2019-06-24/sparql";

	public static final String OPAL = "opal";
	public static final String OPAL_NAMED_GRAPH = "http://projekt-opal.de";
	private static final String OPAL_URL = "http://opaldata.cs.uni-paderborn.de:3030/opal/sparql";

	public static final String CRAWLER_MCLOUD = "crawler-mcloud";
	private static final String CRAWLER_MCLOUD_URL = "http://spark-hare-1.cs.uni-paderborn.de:3030/mcloud/query";

	public static void createEndpoints() {
		if (SparqlSources.isEmpty()) {
			SparqlSources.getInstance().create(VBB_LOCAL, VBB_LOCAL_URL);
			SparqlSources.getInstance().create(MCLOUD_LOCAL, MCLOUD_LOCAL_URL);
			SparqlSources.getInstance().create(OPAL_LOCAL, OPAL_LOCAL_URL);
			SparqlSources.getInstance().create(OPAL, OPAL_URL, OPAL_NAMED_GRAPH);
			SparqlSources.getInstance().create(CRAWLER_MCLOUD, CRAWLER_MCLOUD_URL);
		}
	}
}