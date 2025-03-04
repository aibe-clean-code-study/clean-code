package soheeGit;//import before.llm.*;
//import before.slack.Slack;

import after.llm.*;
import after.slack.Slack;

public class Solution {
    public static void main(String[] args) {
        LLMImpl llm = new LLMImpl();
        String prompt = System.getenv("GEMINI_PROMPT");
        String result = llm.callAPI(LLMImpl.LLMModel.GEMINI_2_0_FLASH, """
                {
                  "contents": [
                    {
                      "role": "user",
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ],
                }
                """.formatted(prompt));
        // 여러분들의 과제 (시간을 들여서 해보세요)
        Slack slack = new Slack();
    }
}