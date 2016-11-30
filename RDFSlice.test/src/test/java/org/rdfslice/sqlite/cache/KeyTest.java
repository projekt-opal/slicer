package org.rdfslice.sqlite.cache;

import junit.framework.Assert;

import org.junit.Test;
import org.rdfslice.model.Triple;

public class KeyTest {
	
	@Test
	public void keyTest() {
		//<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1493>	
		//<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseaseSubtypeOf>	
		//<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/114>
		Triple triple1 = new Triple();
		triple1.setSubject("http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/1493");
		triple1.setObject("http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/114");
		
		//<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/114>	
		//<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>	
		//<http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseases>
		Triple triple2 = new Triple();
		triple2.setSubject("http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseases/114");
		triple2.setObject("http://www4.wiwiss.fu-berlin.de/diseasome/resource/diseasome/diseases");
		
		Key entity1 = new Key(triple1, 0, 33);
		Key entity2 = new Key(triple2, 0, 34);
		
		entity1.hashCode();
		entity2.hashCode();
		
		Assert.assertEquals(entity1.hashCode(), entity2.hashCode());
	}
}
