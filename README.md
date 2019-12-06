# OPAL Slicer

The OPAL data selection component uses patterns in SPARQL format to extract subsets of knowledge graphs.


## Usage example:

To extract all dataset URIs, use the Slicer class and the following parameters:

```
 -source, input.ttl,
 -patterns, Select * where {?p ?k <http://www.w3.org/ns/dcat#Dataset>}
 -out, datasets.ttl]
```


## Note

This component is mainly based on [RDFSlice](http://aksw.org/Projects/RDFSlice.html).


## Credits

[Data Science Group (DICE)](https://dice-research.org/) at [Paderborn University](https://www.uni-paderborn.de/)

This work has been supported by the German Federal Ministry of Transport and Digital Infrastructure (BMVI) in the project [Open Data Portal Germany (OPAL)](http://projekt-opal.de/) (funding code 19F2028A).