import { $t } from '@/locales';
import { transformRecordToOption } from '@/utils/common';

export const vectoreStoreRecord: Record<Api.SiloManage.VectorStoreType, string> = {
  PersistInMemoryEmbeddingStore: 'PERSISTANT MEMORY',
  InMemoryEmbeddingStore: 'MEMORY',
  PGVectorEmbeddingStore: 'PGVECTOR'
};
export const vectorStoreOptions = transformRecordToOption(vectoreStoreRecord);

export const embeddingModelRecord: Record<Api.SiloManage.EmbeddingModelType, string> = {
  BgeSmallEnV15QuantizedEmbeddingModel: 'BGE SMALL EN V15 QUANTIZED',
  OpenAiEmbeddingAda002Model: 'OPENAI EMBEDDING ADA002',
  OpenAiEmbedding3SmallModel: 'OPENAI EMBEDDING 3 SMALL',
  OpenAiEmbedding3LargeModel: 'OPENAI EMBEDDING 3 LARGE'
};
export const embeddingModelOptions = transformRecordToOption(embeddingModelRecord);

export const ingestorLoaderRecord: Record<Api.SiloManage.IngestorLoaderType, string> = {
  None: 'NONE',
  FileSystem: 'FILE SYSTEM',
  URL: 'URL'
};
export const ingestorLoaderOptions = transformRecordToOption(ingestorLoaderRecord);

export const languageModelRecord: Record<Api.FarmManage.LanguageModelType, string> = {
  OpenAiModel: 'OPENAI'
};
export const languageModelOptions = transformRecordToOption(languageModelRecord);

export const chatMemoryStrategyRecord: Record<Api.FarmManage.ChatMemoryStrategyType, string | Api.SelectOptionItem> = {
  MaxMessages: {
    label: 'MAX MESSAGES',
    hint: $t('help.farm.chatMemoryStrategy.maxMessagesHint')
  },
  MaxTokens: {
    label: 'MAX TOKENS',
    hint: $t('help.farm.chatMemoryStrategy.maxTokensHint')
  }
};
export const chatMemoryStrategyOptions = transformRecordToOption(chatMemoryStrategyRecord);

export const queryRouterRecord: Record<Api.FarmManage.QueryRouterType, string> = {
  Default: 'DEFAULT',
  LanguageModel: 'LANGUAGE MODEL'
};
export const queryRouterOptions = transformRecordToOption(queryRouterRecord);

export const granaryEngineTypeRecord: Record<Api.GranaryManage.GranaryEngineType, string> = {
  WebSearchEngine: 'WEB SEARCH ENGINE'
};
export const granaryEngineTypeOptions = transformRecordToOption(granaryEngineTypeRecord);
