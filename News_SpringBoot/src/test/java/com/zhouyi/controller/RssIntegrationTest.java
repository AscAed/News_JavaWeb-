package com.zhouyi.controller;

import com.zhouyi.service.RssService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:rsstestdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "mybatis.mapper-locations=classpath:mappers/*xml",
        "mybatis.configuration.map-underscore-to-camel-case=true",
        "spring.profiles.active=test"
})
public class RssIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RssService rssService;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testFetchRssContentSuccess() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("fetched_count", 10);
        mockResult.put("new_articles", 5);

        when(rssService.fetchAndSave(anyLong())).thenReturn(mockResult);

        mockMvc.perform(post("/api/v1/rss-subscriptions/1/fetch")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.fetched_count").value(10))
                .andExpect(jsonPath("$.data.new_articles").value(5));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testFetchRssContentForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/rss-subscriptions/1/fetch")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(403));
    }
}
