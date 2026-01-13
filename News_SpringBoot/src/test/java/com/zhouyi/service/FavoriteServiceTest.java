package com.zhouyi.service;

import com.zhouyi.dto.FavoriteCreateDTO;
import com.zhouyi.dto.FavoriteUpdateNoteDTO;
import com.zhouyi.entity.Favorite;
import com.zhouyi.entity.Headline;
import com.zhouyi.mapper.FavoriteMapper;
import com.zhouyi.mapper.HeadlineMapper;
import com.zhouyi.service.impl.FavoriteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 收藏服务测试类
 */
@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {

    @Mock
    private FavoriteMapper favoriteMapper;

    @Mock
    private HeadlineMapper headlineMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    private Favorite testFavorite;
    private Headline testHeadline;
    private FavoriteCreateDTO favoriteCreateDTO;

    @BeforeEach
    void setUp() {
        // 创建测试收藏
        testFavorite = new Favorite();
        testFavorite.setId(1);
        testFavorite.setUserId(1);
        testFavorite.setNewsId(1001);
        testFavorite.setNote("测试收藏备注");
        testFavorite.setCreatedTime(LocalDateTime.now());
        testFavorite.setUpdatedTime(LocalDateTime.now());

        // 创建测试新闻
        testHeadline = new Headline();
        testHeadline.setHid(1001);
        testHeadline.setTitle("测试新闻标题");
        testHeadline.setSummary("测试新闻摘要");
        testHeadline.setCoverImage("test-image.jpg");
        testHeadline.setPublisher(1);
        testHeadline.setAuthor("测试作者");

        // 创建测试DTO
        favoriteCreateDTO = new FavoriteCreateDTO();
        favoriteCreateDTO.setHeadlineId(1001);
        favoriteCreateDTO.setNote("新的收藏备注");
    }

    @Test
    void testGetFavoriteById_Success() {
        // Given
        when(favoriteMapper.findById(1)).thenReturn(testFavorite);

        // When
        var result = favoriteService.getFavoriteById(1);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().getId());
        verify(favoriteMapper).findById(1);
    }

    @Test
    void testGetFavoriteById_NotFound() {
        // Given
        when(favoriteMapper.findById(999)).thenReturn(null);

        // When
        var result = favoriteService.getFavoriteById(999);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("收藏记录不存在", result.getMessage());
    }

    @Test
    void testAddFavorite_Success() {
        // Given
        when(headlineMapper.selectById(1001)).thenReturn(testHeadline);
        when(favoriteMapper.findByUserIdAndNewsId(1, 1001)).thenReturn(null);
        when(favoriteMapper.insert(any(Favorite.class))).thenReturn(1);

        // When
        var result = favoriteService.addFavorite(favoriteCreateDTO, 1);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        verify(favoriteMapper).insert(any(Favorite.class));
    }

    @Test
    void testAddFavorite_NewsNotFound() {
        // Given
        favoriteCreateDTO.setHeadlineId(999);
        when(headlineMapper.selectById(999)).thenReturn(null);

        // When
        var result = favoriteService.addFavorite(favoriteCreateDTO, 1);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        assertEquals("新闻不存在", result.getMessage());
        verify(favoriteMapper, never()).insert(any());
    }

    @Test
    void testAddFavorite_AlreadyFavorited() {
        // Given
        when(headlineMapper.selectById(1001)).thenReturn(testHeadline);
        when(favoriteMapper.findByUserIdAndNewsId(1, 1001)).thenReturn(testFavorite);

        // When
        var result = favoriteService.addFavorite(favoriteCreateDTO, 1);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(409, result.getCode());
        assertEquals("该新闻已收藏", result.getMessage());
        verify(favoriteMapper, never()).insert(any());
    }

    @Test
    void testRemoveFavorite_Success() {
        // Given
        when(favoriteMapper.findById(1)).thenReturn(testFavorite);
        when(favoriteMapper.deleteById(1)).thenReturn(1);

        // When
        var result = favoriteService.removeFavorite(1, 1);

        // Then
        assertTrue(result.isSuccess());
        verify(favoriteMapper).deleteById(1);
    }

    @Test
    void testRemoveFavorite_NotFound() {
        // Given
        when(favoriteMapper.findById(999)).thenReturn(null);

        // When
        var result = favoriteService.removeFavorite(999, 1);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
        verify(favoriteMapper, never()).deleteById(any());
    }

    @Test
    void testRemoveFavorite_Unauthorized() {
        // Given
        when(favoriteMapper.findById(1)).thenReturn(testFavorite);

        // When
        var result = favoriteService.removeFavorite(1, 999);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(403, result.getCode());
        verify(favoriteMapper, never()).deleteById(any());
    }

    @Test
    void testUpdateFavoriteNote_Success() {
        // Given
        FavoriteUpdateNoteDTO updateDTO = new FavoriteUpdateNoteDTO();
        updateDTO.setNote("更新后的收藏备注");

        when(favoriteMapper.findById(1)).thenReturn(testFavorite);
        when(favoriteMapper.updateNote(any(Favorite.class))).thenReturn(1);

        // When
        var result = favoriteService.updateFavoriteNote(1, updateDTO, 1);

        // Then
        assertTrue(result.isSuccess());
        verify(favoriteMapper).updateNote(any(Favorite.class));
    }

    @Test
    void testGetFavoriteCountByNews_Success() {
        // Given
        when(headlineMapper.selectById(1001)).thenReturn(testHeadline);
        when(favoriteMapper.countByNewsId(1001)).thenReturn(10L);

        // When
        var result = favoriteService.getFavoriteCountByNews(1001);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(10L, result.getData().get("favorite_count"));
        verify(favoriteMapper).countByNewsId(1001);
    }

    @Test
    void testCheckFavoriteStatus_Favorited() {
        // Given
        when(favoriteMapper.findByUserIdAndNewsId(1, 1001)).thenReturn(testFavorite);

        // When
        var result = favoriteService.checkFavoriteStatus(1, 1001);

        // Then
        assertTrue(result.isSuccess());
        assertTrue((Boolean) result.getData().get("is_favorited"));
        assertEquals(1, result.getData().get("favorite_id"));
    }

    @Test
    void testCheckFavoriteStatus_NotFavorited() {
        // Given
        when(favoriteMapper.findByUserIdAndNewsId(1, 1001)).thenReturn(null);

        // When
        var result = favoriteService.checkFavoriteStatus(1, 1001);

        // Then
        assertTrue(result.isSuccess());
        assertFalse((Boolean) result.getData().get("is_favorited"));
        assertFalse(result.getData().containsKey("favorite_id"));
    }
}
