# Specification

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to *http://localhost:1985*

| Class | Method | HTTP request | Description |
|------------ | ------------- | ------------- | -------------|
| *AppInformationApi* | [**version**](Apis/AppInformationApi.md#version) | **GET** /api/app/info/version | Get dRAGon app version |
| *DatabaseCommandApi* | [**exportDatabase**](Apis/DatabaseCommandApi.md#exportdatabase) | **POST** /api/backend/command/database/export | Create a database export |
| *DocumentRepositoryApi* | [**deleteDocument**](Apis/DocumentRepositoryApi.md#deletedocument) | **DELETE** /api/backend/repository/document/\{uuid\} | Delete a Document |
*DocumentRepositoryApi* | [**deleteSiloDocuments**](Apis/DocumentRepositoryApi.md#deletesilodocuments) | **DELETE** /api/backend/repository/document/silo/\{uuid\} | Delete all Documents from a Silo |
*DocumentRepositoryApi* | [**getDocument**](Apis/DocumentRepositoryApi.md#getdocument) | **GET** /api/backend/repository/document/\{uuid\} | Retrieve one Document |
*DocumentRepositoryApi* | [**listDocumentsOfSilo**](Apis/DocumentRepositoryApi.md#listdocumentsofsilo) | **GET** /api/backend/repository/document/silo/\{uuid\} | List Documents of a Silo |
*DocumentRepositoryApi* | [**updateDocument**](Apis/DocumentRepositoryApi.md#updatedocument) | **PATCH** /api/backend/repository/document/\{uuid\} | Update a Document |
| *FarmRepositoryApi* | [**createFarm**](Apis/FarmRepositoryApi.md#createfarm) | **POST** /api/backend/repository/farm/ | Create a new Farm |
*FarmRepositoryApi* | [**deleteFarm**](Apis/FarmRepositoryApi.md#deletefarm) | **DELETE** /api/backend/repository/farm/\{uuid\} | Delete a Farm |
*FarmRepositoryApi* | [**getFarm**](Apis/FarmRepositoryApi.md#getfarm) | **GET** /api/backend/repository/farm/\{uuid\} | Retrieve one Farm |
*FarmRepositoryApi* | [**listFarms**](Apis/FarmRepositoryApi.md#listfarms) | **GET** /api/backend/repository/farm/ | List all Farms |
*FarmRepositoryApi* | [**updateFarm**](Apis/FarmRepositoryApi.md#updatefarm) | **PATCH** /api/backend/repository/farm/\{uuid\} | Update a Farm |
| *HealthApi* | [**health**](Apis/HealthApi.md#health) | **GET** /api/app/health | Check dRAGon app health |
| *RaaGApi* | [**chatCompletions**](Apis/RaaGApi.md#chatcompletions) | **POST** /api/raag/v1/chat/completions | Creates a chat completion |
*RaaGApi* | [**completions**](Apis/RaaGApi.md#completions) | **POST** /api/raag/v1/completions | Creates a completion |
*RaaGApi* | [**models**](Apis/RaaGApi.md#models) | **GET** /api/raag/v1/models | List models |
| *SearchApi* | [**searchDocumentsInSilo**](Apis/SearchApi.md#searchdocumentsinsilo) | **POST** /api/rag/search/documents/silo/\{uuid\} | Search documents inside a Silo |
| *SiloCommandApi* | [**rebuildSilo**](Apis/SiloCommandApi.md#rebuildsilo) | **POST** /api/backend/command/silo/rebuild/\{uuid\} | Rebuild Silo |
*SiloCommandApi* | [**startSiloIngestor**](Apis/SiloCommandApi.md#startsiloingestor) | **POST** /api/backend/command/silo/ingest/\{uuid\} | Start Silo Ingestor |
| *SiloRepositoryApi* | [**createSilo**](Apis/SiloRepositoryApi.md#createsilo) | **POST** /api/backend/repository/silo/ | Create a new Silo |
*SiloRepositoryApi* | [**deleteSilo**](Apis/SiloRepositoryApi.md#deletesilo) | **DELETE** /api/backend/repository/silo/\{uuid\} | Delete a Silo |
*SiloRepositoryApi* | [**getSilo**](Apis/SiloRepositoryApi.md#getsilo) | **GET** /api/backend/repository/silo/\{uuid\} | Retrieve one Silo |
*SiloRepositoryApi* | [**listSilos**](Apis/SiloRepositoryApi.md#listsilos) | **GET** /api/backend/repository/silo/ | List all Silos |
*SiloRepositoryApi* | [**updateSilo**](Apis/SiloRepositoryApi.md#updatesilo) | **PATCH** /api/backend/repository/silo/\{uuid\} | Update a Silo |


<a name="documentation-for-models"></a>
## Documentation for Models

 - [Document](./Models/Document.md)
 - [EmbeddingMatchResponse](./Models/EmbeddingMatchResponse.md)
 - [Farm](./Models/Farm.md)
 - [OpenAiChatCompletionChoice](./Models/OpenAiChatCompletionChoice.md)
 - [OpenAiChatCompletionRequest](./Models/OpenAiChatCompletionRequest.md)
 - [OpenAiChatCompletionResponse](./Models/OpenAiChatCompletionResponse.md)
 - [OpenAiCompletionChoice](./Models/OpenAiCompletionChoice.md)
 - [OpenAiCompletionMessage](./Models/OpenAiCompletionMessage.md)
 - [OpenAiCompletionRequest](./Models/OpenAiCompletionRequest.md)
 - [OpenAiCompletionResponse](./Models/OpenAiCompletionResponse.md)
 - [OpenAiCompletionUsage](./Models/OpenAiCompletionUsage.md)
 - [OpenAiModel](./Models/OpenAiModel.md)
 - [OpenAiModelsReponse](./Models/OpenAiModelsReponse.md)
 - [Silo](./Models/Silo.md)


<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
