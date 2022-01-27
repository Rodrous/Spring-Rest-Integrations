# Spring Boot Denodo to Collibra Integration

A Spring Boot integration that parses Denodo metadata, transforms and upserts it to a Collibra Platform instance as assets and complex relations using the Collibra Integration Library. This integration also contains logic for marking old assets as obsolete.

The metadata includes:
- Database
- BI Folder
- Table (having various table types)
- Database View
- Column