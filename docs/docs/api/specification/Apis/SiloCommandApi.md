# SiloCommandApi

All URIs are relative to *http://localhost:1985*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**rebuildSilo**](SiloCommandApi.md#rebuildSilo) | **POST** /api/backend/command/silo/rebuild/\{uuid\} | Rebuild Silo |


<a name="rebuildSilo"></a>
# **rebuildSilo**
> rebuildSilo(uuid)

Rebuild Silo

    This will recompute the embeddings of the Silo.

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

