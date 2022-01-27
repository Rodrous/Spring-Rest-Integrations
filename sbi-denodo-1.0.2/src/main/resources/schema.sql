DROP ALL OBJECTS; 

CREATE SCHEMA h2MockDatabase;
USE h2MockDatabase;

CREATE ALIAS CATALOG_VDP_METADATA_VIEWS FOR "com.collibra.marketplace.denodo.mock.DenodoMockData.getCatalogVdpMetadataViews";
CREATE ALIAS VIEW_DEPENDENCIES FOR "com.collibra.marketplace.denodo.mock.DenodoMockData.getViewDependencies";
CREATE ALIAS GET_VIEWS FOR "com.collibra.marketplace.denodo.mock.DenodoMockData.getViews";

