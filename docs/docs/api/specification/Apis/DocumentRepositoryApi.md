# DocumentRepositoryApi

All URIs are relative to *http://localhost:1985*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**deleteDocument**](DocumentRepositoryApi.md#deleteDocument) | **DELETE** /api/backend/repository/document/\{uuid\} | Delete a Document |
| [**deleteSiloDocuments**](DocumentRepositoryApi.md#deleteSiloDocuments) | **DELETE** /api/backend/repository/document/silo/\{uuid\} | Delete all Documents from a Silo |
| [**getDocument**](DocumentRepositoryApi.md#getDocument) | **GET** /api/backend/repository/document/\{uuid\} | Retrieve one Document |
| [**listDocumentsOfSilo**](DocumentRepositoryApi.md#listDocumentsOfSilo) | **GET** /api/backend/repository/document/silo/\{uuid\} | List Documents of a Silo |
| [**updateDocument**](DocumentRepositoryApi.md#updateDocument) | **PATCH** /api/backend/repository/document/\{uuid\} | Update a Document |


<a name="deleteDocument"></a>
# **deleteDocument**
> deleteDocument(uuid)

Delete a Document

    Deletes one Document entity from its UUID stored in the database.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **UUID**| Identifier of the Document | [default to null] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

<a name="deleteSiloDocuments"></a>
# **deleteSiloDocuments**
> deleteSiloDocuments(uuid)

Delete all Documents from a Silo

    Deletes all Document entities from a Silo.

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

<a name="getDocument"></a>
# **getDocument**
> Document getDocument(uuid)

Retrieve one Document

    Returns one Document entity from its UUID stored in the database.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **String**| Identifier of the Document | [default to null] |

### Return type

[**Document**](../Models/Document.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="listDocumentsOfSilo"></a>
# **listDocumentsOfSilo**
> List listDocumentsOfSilo(uuid, limit, offset)

List Documents of a Silo

    Returns Documents entities about a Silo.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **String**| Identifier of the Silo | [default to null] |
| **limit** | **Integer**| Limit number of results | [optional] [default to 10] |
| **offset** | **Integer**| Skip x results | [optional] [default to 0] |

### Return type

[**List**](../Models/Document.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="updateDocument"></a>
# **updateDocument**
> Document updateDocument(uuid, request\_body)

Update a Document

    Updates one Document entity in the database. Only the field &#39;allowIndexing&#39; can be updated.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **String**| Identifier of the Document | [default to null] |
| **request\_body** | **Map**|  | |

### Return type

[**Document**](../Models/Document.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*

