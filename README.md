# Transpiler for xmls between different data transformation softwares

## Usage
- Please clone the repo to local and import project using Intellij Idea.
- Put input XML files under the project folder directly for reading with relative path, any writing with no absolute path specified will also be produced in the project folder.

## Tasks
- [x] IO: read input xml into lists of vertices (with type and id) and links (with type and id and source and targets).
- [ ] IO: output executable xml from intermediate representation by pre-defined templates of pentaho xml.
- [ ] Mapping: writing an interpreter on csvInput, csvOutput.