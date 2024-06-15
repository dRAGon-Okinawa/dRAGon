# Farm
## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
| **uuid** | **UUID** | Identifier of the Farm | [default to null] |
| **name** | **String** | Name of the Farm. Must be unique. | [default to null] |
| **raagIdentifier** | **String** | Identifier for the &#39;Raag Model&#39; to be used for the RaaG API. Must be unique | [optional] [default to null] |
| **silos** | **List** | List of Silo UUIDs to be linked to the Farm. A Farm is a collection of Silos, which each Silo is a collection of Documents. | [optional] [default to null] |
| **languageModel** | **String** | Language Model to be used for the RaaG API | [optional] [default to null] |
| **languageModelSettings** | **List** | Settings to be linked to the Farm&#39;s Language Model in the form of &#x60;key &#x3D; value&#x60; pairs. | [optional] [default to null] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)

