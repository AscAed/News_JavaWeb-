package com.zhouyi.service;

import com.zhouyi.common.result.Result;
import com.zhouyi.dto.HeadlinePublishDTO;
import com.zhouyi.entity.Headline;
import com.zhouyi.entity.User;
import com.zhouyi.entity.mongodb.NewsContent;
import com.zhouyi.entity.elasticsearch.HeadlineEsEntity;
import com.zhouyi.mapper.HeadlineMapper;
import com.zhouyi.repository.elasticsearch.HeadlineEsRepository;
import com.zhouyi.service.impl.HeadlineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HeadlineServiceTransactionTest {

    @Mock
    private HeadlineMapper headlineMapper;

    @Mock
    private UserService userService;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private HeadlineEsRepository headlineEsRepository;

    @Mock
    private TransactionStatus transactionStatus;

    @InjectMocks
    private HeadlineServiceImpl headlineService;

    private HeadlinePublishDTO publishDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        publishDTO = new HeadlinePublishDTO();
        publishDTO.setTitle("Test Title");
        publishDTO.setType(1);
        publishDTO.setArticle("Test content article");
        publishDTO.setSummary("Test summary");
        publishDTO.setTags("tag1,tag2");

        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
    }

    @Test
    void testPublishHeadline_EsFailureTriggerRollback() {
        // Given
        when(userService.getUserById(1)).thenReturn(Result.success(testUser));
        when(headlineMapper.selectTypeNameByType(any())).thenReturn("Technology");
        
        // Mock MySQL insert
        doAnswer(invocation -> {
            Headline headline = invocation.getArgument(0);
            headline.setHid(1001); // Simulate auto-increment ID
            return 1;
        }).when(headlineMapper).insertHeadline(any(Headline.class));

        // Mock MongoDB save
        NewsContent newsContent = new NewsContent();
        newsContent.setId("mongo-id-123");
        when(mongoTemplate.save(any(NewsContent.class))).thenReturn(newsContent);

        // Mock ES failure
        when(headlineEsRepository.save(any(HeadlineEsEntity.class))).thenThrow(new RuntimeException("ES Connection Timeout"));

        // Use MockedStatic to catch TransactionAspectSupport call
        try (MockedStatic<TransactionAspectSupport> mockedStatic = mockStatic(TransactionAspectSupport.class)) {
            mockedStatic.when(TransactionAspectSupport::currentTransactionStatus).thenReturn(transactionStatus);

            // When
            Result<String> result = headlineService.publishHeadline(publishDTO, 1);

            // Then
            assertFalse(result.isSuccess());
            assertEquals("发布失败，系统已触发柔性回滚保障数据一致性", result.getMessage());

            // Verify MongoDB compensation
            verify(mongoTemplate).remove(any(Query.class), eq(NewsContent.class));
            
            // Verify Transaction rollback marked
            verify(transactionStatus).setRollbackOnly();
            
            // Verify MySQL insert was called
            verify(headlineMapper).insertHeadline(any(Headline.class));
        }
    }

    @Test
    void testPublishHeadline_Success() {
        // Given
        when(userService.getUserById(1)).thenReturn(Result.success(testUser));
        when(headlineMapper.selectTypeNameByType(any())).thenReturn("Technology");
        
        doAnswer(invocation -> {
            Headline headline = invocation.getArgument(0);
            headline.setHid(1001);
            return 1;
        }).when(headlineMapper).insertHeadline(any(Headline.class));

        NewsContent newsContent = new NewsContent();
        newsContent.setId("mongo-id-123");
        when(mongoTemplate.save(any(NewsContent.class))).thenReturn(newsContent);

        // When
        Result<String> result = headlineService.publishHeadline(publishDTO, 1);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("发布成功", result.getMessage());

        // Verify no MongoDB compensation
        verify(mongoTemplate, never()).remove(any(Query.class), eq(NewsContent.class));
        
        // Verify ES was called
        verify(headlineEsRepository).save(any(HeadlineEsEntity.class));
    }
}
