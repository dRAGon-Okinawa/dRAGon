# SearchApi

All URIs are relative to *http://localhost:1985*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**searchDocumentsInSilo**](SearchApi.md#searchDocumentsInSilo) | **POST** /api/rag/search/documents/silo/\{uuid\} | Search documents inside a Silo |


<a name="searchDocumentsInSilo"></a>
# **searchDocumentsInSilo**
> List searchDocumentsInSilo(uuid, body, maxResults)

Search documents inside a Silo

    Search documents from the Silo.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **UUID**| Identifier of the Silo | [default to null] |
| **body** | **String**|  | |
| **maxResults** | **Integer**| Max results to return | [optional] [default to 10] |

### Return type

[**List**](../Models/EmbeddingMatchResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*

