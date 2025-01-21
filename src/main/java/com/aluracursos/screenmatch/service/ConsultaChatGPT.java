package com.aluracursos.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;



    public class ConsultaChatGPT {
        public static String obtenerTraduccion(String texto) {
            OpenAiService service = new OpenAiService("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");

            CompletionRequest requisicion = CompletionRequest.builder()
                    .model("gpt-3.5-turbo-instruct")
                    .prompt("traduce a español el siguiente texto: " + texto)
                    .maxTokens(1000)
                    .temperature(0.7)
                    .build();

            var respuesta = service.createCompletion(requisicion);
            return respuesta.getChoices().get(0).getText();
        }
    }

