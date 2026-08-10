package com.thomasnoel.crs;

import java.util.List;

import com.thomasnoel.crs.ai.ChatModelMessage;
import com.thomasnoel.crs.ai.ChatModelRole;
import com.thomasnoel.crs.ai.deepseek.DeepSeekClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@EnabledIfEnvironmentVariable(
        named = "RUN_DEEPSEEK_IT",
        matches = "true"
)
class DeepSeekConnectionTest {

    @Autowired
    private DeepSeekClient deepSeekClient;

    @Test
    void connectsToDeepSeek() throws Exception {
        String answer = deepSeekClient.chat(
                "deepseek-v4-flash",
                List.of(
                        new ChatModelMessage(
                                ChatModelRole.USER,
                                "Give me some description of Vancouver"
                        )
                )
        );

        System.out.println("DeepSeek answer: " + answer);

        assertFalse(
                answer.isBlank(),
                "DeepSeek returned an empty answer"
        );
    }
}
