package dare.daremall.controller.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    @DisplayName("[GET] 전체 상품")
    void itemList() throws Exception{

        mockMvc.perform(get("/items")
                    .accept(MediaType.parseMediaType("application/html;charset=UTF-8")))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("[GET] 전체 앨범")
    void albumList() throws Exception {
        mockMvc.perform(get("/items/albums")
                        .accept(MediaType.parseMediaType("application/html;charset=UTF-8")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[GET] 전체 도서")
    void bookList() throws Exception {
        mockMvc.perform(get("/items/books")
                        .accept(MediaType.parseMediaType("application/html;charset=UTF-8")))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[GET] 상품 상세")
    void itemDetailsWithMember() throws Exception {
        mockMvc.perform(get("/items/books")
                        .accept(MediaType.parseMediaType("application/html;charset=UTF-8")))
                .andExpect(status().isOk());
    }
}