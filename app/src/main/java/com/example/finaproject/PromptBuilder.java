package com.example.finaproject;

public class PromptBuilder {

    public static String buildReportPrompt(String userText) {
        String prompt =
                "You are a municipal infrastructure assistant.\n\n" +
                        "A citizen reported the following issue:\n" +
                        "\"" + userText + "\"\n\n" +
                        "Your tasks:\n" +
                        "1. Identify the type of problem.\n" +
                        "2. Rewrite the report professionally.\n" +
                        "3. Suggest a solution.\n" +
                        "4. Define urgency level.\n";

        return prompt;
    }
}