package com.zhouyi.mapper;

import com.zhouyi.entity.RssFeedItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RssFeedItemMapper {

    int insert(RssFeedItem item);

    int countByLinkOrGuid(@Param("link") String link, @Param("guid") String guid);

    List<RssFeedItem> findBySubscriptionIdAndLimit(@Param("subscriptionId") Long subscriptionId,
                                                   @Param("limit") int limit);

    List<RssFeedItem> findAll();
}
