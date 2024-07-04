# SiloCommandApi

All URIs are relative to *http://localhost:1985*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**rebuildSilo**](SiloCommandApi.md#rebuildSilo) | **POST** /api/backend/command/silo/rebuild/\{uuid\} | Rebuild Silo |
| [**startSiloIngestor**](SiloCommandApi.md#startSiloIngestor) | **POST** /api/backend/command/silo/ingest/\{uuid\} | Start Silo Ingestor |


<a name="rebuildSilo"></a>
# **rebuildSilo**
> rebuildSilo(uuid)

Rebuild Silo

    This will erase all embeddings of the Silo and re-ingest all documents.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **UUID**| Identifier of the Silo | [default to null] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

<a name="startSiloIngestor"></a>
# **startSiloIngestor**
> startSiloIngestor(uuid)

Start Silo Ingestor

    This will re-ingest the documents of the Silo.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **UUID**| Identifier of the Silo | [default to null] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

