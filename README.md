# Transpiler for xmls between different data transformation softwares

## User guide
- Please clone the repo to local and import project using Intellij Idea.
- Put input XML files under the project folder directly for reading with relative path, any writing with no absolute path specified will also be produced in the project folder.

## Tasks
- [x] IO: read input xml into lists of vertices (with type and id) and links (with type and id and source and targets).
- [x] IO: output executable xml from intermediate representation by pre-defined templates of pentaho xml.
- [x] IO: output class should be created
- [ ] Mapping: writing an interpreter on input graph and output graph.