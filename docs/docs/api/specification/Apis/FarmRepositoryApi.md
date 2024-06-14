# FarmRepositoryApi

All URIs are relative to *http://localhost:1985*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createFarm**](FarmRepositoryApi.md#createFarm) | **POST** /api/backend/repository/farm/ | Create a new Farm |
| [**deleteFarm**](FarmRepositoryApi.md#deleteFarm) | **DELETE** /api/backend/repository/farm/\{uuid\} | Delete a Farm |
| [**getFarm**](FarmRepositoryApi.md#getFarm) | **GET** /api/backend/repository/farm/\{uuid\} | Retrieve one Farm |
| [**listFarms**](FarmRepositoryApi.md#listFarms) | **GET** /api/backend/repository/farm/ | List all Farms |
| [**updateFarm**](FarmRepositoryApi.md#updateFarm) | **PATCH** /api/backend/repository/farm/\{uuid\} | Update a Farm |


<a name="createFarm"></a>
# **createFarm**
> Farm createFarm()

Create a new Farm

    Creates one Farm entity in the database.

### Parameters
This endpoint does not need any parameter.

### Return type

[**Farm**](../Models/Farm.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="deleteFarm"></a>
# **deleteFarm**
> deleteFarm(uuid)

Delete a Farm

    Deletes one Farm entity from its UUID stored in the database.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **String**| Identifier of the Farm | [default to null] |

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined

<a name="getFarm"></a>
# **getFarm**
> Farm getFarm(uuid)

Retrieve one Farm

    Returns one Farm entity from its UUID stored in the database.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **String**| Identifier of the Farm | [default to null] |

### Return type

[**Farm**](../Models/Farm.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="listFarms"></a>
# **listFarms**
> List listFarms()

List all Farms

    Returns all Farm entities stored in the database.

### Parameters
This endpoint does not need any parameter.

### Return type

[**List**](../Models/Farm.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

<a name="updateFarm"></a>
# **updateFarm**
> Farm updateFarm(uuid, request\_body)

Update a Farm

    Updates one Farm entity in the database.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **uuid** | **String**| Identifier of the Farm | [default to null] |
| **request\_body** | **Map**|  | |

### Return type

[**Farm**](../Models/Farm.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*

