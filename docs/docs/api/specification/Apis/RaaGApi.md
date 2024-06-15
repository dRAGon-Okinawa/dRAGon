# RaaGApi

All URIs are relative to *http://localhost:1985*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**chatCompletions**](RaaGApi.md#chatCompletions) | **POST** /api/raag/v1/chat/completions | Creates a chat completion |
| [**completions**](RaaGApi.md#completions) | **POST** /api/raag/v1/completions | Creates a completion |
| [**models**](RaaGApi.md#models) | **GET** /api/raag/v1/models | List models |


<a name="chatCompletions"></a>
# **chatCompletions**
> OpenAiChatCompletionResponse chatCompletions(OpenAiChatCompletionRequest)

Creates a chat completion

    Creates a chat completion for the provided prompt and parameters.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **OpenAiChatCompletionRequest** | [**OpenAiChatCompletionRequest**](../Models/OpenAiChatCompletionRequest.md)|  | |

### Return type

[**OpenAiChatCompletionResponse**](../Models/OpenAiChatCompletionResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*

<a name="completions"></a>
# **completions**
> OpenAiCompletionResponse completions(OpenAiCompletionRequest)

Creates a completion

    Creates a completion for the provided prompt and parameters.

### Parameters

|Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **OpenAiCompletionRequest** | [**OpenAiCompletionRequest**](../Models/OpenAiCompletionRequest.md)|  | |

### Return type

[**OpenAiCompletionResponse**](../Models/OpenAiCompletionResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: */*

<a name="models"></a>
# **models**
> OpenAiModelsReponse models()

List models

    Lists available models.

### Parameters
This endpoint does not need any parameter.

### Return type

[**OpenAiModelsReponse**](../Models/OpenAiModelsReponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: */*

