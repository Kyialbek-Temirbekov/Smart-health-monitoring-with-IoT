package kg.edu.manas.cloud.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kg.edu.manas.cloud.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Recommendation Service")
@RestController
@RequestMapping("/recommendation")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;

    @GetMapping
    public List<?> getRecommendations() {
        return recommendationService.getRecommendations();
    }
}
