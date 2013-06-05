#RDFSlice

In the last years an increasing number of structured data was published on the Web as Linked Open Data (LOD).Despite recent advances, consuming and using Linked Open Data within an organization is still a substantial challenge. Many of the LOD datasets are quite large and despite progress in RDF data management their loading and querying within a triple store is extremely time-consuming and resource-demanding. To overcome this consumption obstacle, we propose a process inspired by the classical Extract-Transform-Load (ETL) paradigm, RDF dataset slicing.

- - -
##HOW TO

Download the [RDFSlice](https://bitbucket.org/emarx/rdfslice/downloads/slice.jar) and execute by command line using the fallow sintax: 

     java -jar slice.jar <fileList> <query> <fileDest> <order> <debugGraphSize>
	 
, where:

fileList - is a file containing a list of target dump files e.g. http://downloads.dbpedia.org/3.8/en/contents-ttl.txt

query - desired query, e.g. "Select * where {?s ?p ?o}"

fileDest - destiny file, e.g. out.nt

order - order of the content in the source files, where: S is subject order; SS is subject segmentated and; N no order.

debugGraphSiye: the size of the graph to register. When the RDFSlice reach this number a debug statistic register is generated, e.g. 1024, 1048576.

- - -
##Evaluation

We evaluate RDFSlice against several datasets and type of queries.

You can download the [evaluation](https://bitbucket.org/emarx/rdfslice/downloads/evaluation.rar) to see the results or the [paper](https://bitbucket.org/emarx/rdfslice/downloads/slice_v1.1.pdf) for more details.

