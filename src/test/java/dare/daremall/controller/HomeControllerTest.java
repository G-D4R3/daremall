package dare.daremall.controller;

import dare.daremall.domain.ad.MainAd;
import dare.daremall.service.AdService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HomeControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired AdService adService;

    @Test
    @DisplayName("[GET] 메인 화면")
    public void home() throws Exception {

        mockMvc.perform(get("/")
                .accept(MediaType.parseMediaType("application/html;charset=UTF-8")))
                /*.andExpect(model().attribute("ads", hasSize(2)))
                .andExpect(model().attribute("ads", hasItem(
                        allOf(
                            hasProperty("imagePath", is("/images/ad/main/summer.png")),
                            hasProperty("href", is("/items/detail?itemId=82"))
                        )
                )))
                .andExpect(model().attribute("ads", hasItem(
                        allOf(
                                hasProperty("imagePath", is("/images/ad/main/agile.png")),
                                hasProperty("href", is("/items/detail?itemId=981"))
                                )
                )))*/
                .andExpect(view().name("home"));
    }

}