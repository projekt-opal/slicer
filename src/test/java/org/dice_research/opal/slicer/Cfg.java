package org.dice_research.opal.slicer;

public abstract class Cfg {

	public static final String VBB_LOCAL = "local-vbb";
	private static final String VBB_LOCAL_URL = "http://localhost:3030/vbb/sparql";

	public static final String OPAL = "opal";
	private static final String OPAL_URL = "http://opaldata.cs.uni-paderborn.de:3030/opal/sparql";

	public static void createEndpoints() {
		if (Endpoints.isEmpty()) {
			Endpoints.getInstance().create(VBB_LOCAL, VBB_LOCAL_URL);
			Endpoints.getInstance().create(OPAL, OPAL_URL);
		}
	}
}