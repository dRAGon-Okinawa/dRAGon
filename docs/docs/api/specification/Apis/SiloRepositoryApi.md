# SiloRepositoryApi

All URIs are relative to *http://localhost:1985*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createSilo**](SiloRepositoryApi.md#createSilo) | **POST** /api/backend/repository/silo/ | Create a new Silo |
| [**deleteSilo**](SiloRepositoryApi.md#deleteSilo) | **DELETE** /api/backend/repository/silo/\{uuid\} | Delete a Silo |
| [**getSilo**](SiloRepositoryApi.md#getSilo) | **GET** /api/backend/repository/silo/\{uuid\} | Retrieve one Silo |
| [**listSilos**](SiloRepositoryApi.md#listSilos) | **GET** /api/backend/repository/silo/ | List all Silos |
| [**updateSilo**](SiloRepositoryApi.md#updateSilo) | **PATCH** /api/backend/repository/silo/\{uuid\} | Update a Silo |


<a name="createSilo"></a>
# **createSilo**
> Silo createSilo()

Create a new Silo

    Creates one Silo entity in the database.

### Parameters
This endpoint does not need any parameter.

### Return type

[**Silo**](../Models/Silo.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="deleteSilo"></a>
# **deleteSilo**
> deleteSilo(uuid)

Delete a Silo

    Deletes one Silo entity from its UUID stored in the database.

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

<a name="getSilo"></a>
# **getSilo**
> Silo getSilo(uuid)

Retrieve one Silo

    Returns one Silo entity from its UUID stored in the database.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **String**| Identifier of the Silo | [default to null] |

### Return type

[**Silo**](../Models/Silo.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="listSilos"></a>
# **listSilos**
> List listSilos()

List all Silos

    Returns all Silo entities stored in the database.

### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/Silo.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="updateSilo"></a>
# **updateSilo**
> Silo updateSilo(uuid, request\_body)

Update a Silo

    Updates one Silo entity in the database.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **String**| Identifier of the Silo | [default to null] |
| **request\_body** | **Map**|  | |

### Return type

[**Silo**](../Models/Silo.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*

