#RDFSlice

In the last years an increasing number of structured data was published on the Web as Linked Open Data (LOD).Despite recent advances, consuming and using Linked Open Data within an organization is still a substantial challenge. Many of the LOD datasets are quite large and despite progress in RDF data management their loading and querying within a triple store is extremely time-consuming and resource-demanding. To overcome this consumption obstacle, we propose a process inspired by the classical Extract-Transform-Load (ETL) paradigm, RDF dataset slicing.

- - -
##HOW TO

Download [RDFSlice](https://bitbucket.org/emarx/rdfslice/downloads/rdfslice_1.5.jar) and execute the command line using the following syntax: 

     java -jar rdfslice.jar -source <fileList>|<path> -patterns <graphPatterns> -out <fileDest> -order <order> -debug <debugGraphSize>
	 
Where:

<fileList>       - is a file containing a list of target dump files e.g. http://downloads.dbpedia.org/3.8/en/contents-nt.txt

<path>           - is the path where the files are e.g. ./files/*.nt

<graphPatterns>  - desired query, e.g. "SELECT * WHERE {?s ?p ?o}" or graph pattern e.g. "{?s ?p ?o}"

<fileDest>       - destination file, e.g. out.nt

<order>          - order of the content in the source files, where: "S" means that the file is alphabetically ordered by subjects; "IS" means that all triples concerning one subject are grouped together but subjects are not in alphabetic order and; N no order.

<debugGraphSize> - the size of the graph to register. When RDFSlice reach this number a debug statistic register is generated, e.g. 1024, 1048576.
- - -
##TIP

Use [-Xmx](http://publib.boulder.ibm.com/infocenter/javasdk/tools/index.jsp?topic=%2Fcom.ibm.java.doc.igaa%2F_1vg000139b8b453-11951f1e7ff-8000_1001.html) in java command line to increase the amount of memory used to slice.

e.g. using 2 Gigabytes


     java -jar -Xmx2G rdfslice.jar -source <fileList>|<path> -patterns <graphPatterns> -out <fileDest> -order <order> -debug <debugGraphSize>
- - -
##Evaluation

We evaluate RDFSlice against several datasets and type of queries.

You can download the evaluations ([evaluation 1](https://bitbucket.org/emarx/rdfslice/downloads/evaluation.rar),[evaluation 2](https://bitbucket.org/emarx/rdfslice/downloads/evaluation2.rar)) to see the results or the [paper](https://www.researchgate.net/publication/262764115_Towards_an_Efficient_RDF_Dataset_Slicing) for more details.

- - -
##NEWS

###29.11.2014

####Version 1.6 released - [Download](https://bitbucket.org/emarx/rdfslice/downloads/rdfslice_1.6.jar)

#####Bugs

- ######Subjects and objects with special characters

###10.11.2014

####Version 1.5 released - [Download](https://bitbucket.org/emarx/rdfslice/downloads/rdfslice_1.5.jar)

#####Bugs

- ######Subject Subject pattern with variable
- ######Recognition of BAD URIs (#2)

#####Features

- ######Memory optimization for slicing (use the available free RAM to optimize the slicing)
- ######Reconnectable streamer for remote files

###06.06.2014

####Version 1.42 released

#####Bugs

- ######Single Pattern Match

###07.04.2014

####Version 1.41 released

#####Bugs

- ######Cache pattern match

- ######URL serialization

###10.12.2013

####Version 1.4 released

#####Features

- ######Optimizations(cache, domain and parallelization)

#####Bugs

- ######Reading from bzip files

- ######Ordering restrictive triple patterns