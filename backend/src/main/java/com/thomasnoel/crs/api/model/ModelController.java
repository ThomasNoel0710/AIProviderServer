package com.thomasnoel.crs.api.model;
import com.thomasnoel.crs.ai.ChatModelDefinition;
import java.util.List;
import com.thomasnoel.crs.ai.ModelCatalog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/models")
public class ModelController {
    private final ModelCatalog modelCatalog;

    public ModelController(ModelCatalog modelCatalog) {
        this.modelCatalog = modelCatalog;
    }

    @GetMapping
    public List<ChatModelDefinition> getModels() {
        return modelCatalog.getModels();
    }
}
