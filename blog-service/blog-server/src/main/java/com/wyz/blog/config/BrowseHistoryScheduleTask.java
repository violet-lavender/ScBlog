package com.wyz.blog.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyz.blog.mapper.ViewBlogMapper;
import com.wyz.blog.pojo.domain.ViewBlog;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled 配置类 - 定时更新博客浏览历史
 */
@Slf4j
@Configuration
public class BrowseHistoryScheduleTask {

    @Resource
    private ViewBlogMapper viewBlogMapper;

    // 每天凌晨3点清理过期数据（保留7天）
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredData() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(7);
        // 直接删除超过7天的记录
        viewBlogMapper.delete(
                new LambdaQueryWrapper<ViewBlog>()
                        .lt(ViewBlog::getCreateTime, threshold)
        );
    }

    // 每天凌晨4点清理超额数据（每个用户保留最新500条）
    @Scheduled(cron = "0 0 4 * * ?")
    public void cleanExcessData() {
        int pageSize = 1000; // 每页处理1000个用户
        int currentPage = 1;
        Page<ViewBlog> page;

        do {
            // 分页获取去重后的用户ID列表
            page = viewBlogMapper.selectPage(
                    new Page<>(currentPage, pageSize, false), // 不查询总记录数
                    new LambdaQueryWrapper<ViewBlog>()
                            .select(ViewBlog::getUserId)
                            .groupBy(ViewBlog::getUserId) // 等效于DISTINCT
            );

            // 提取用户ID
            List<Integer> userIds = page.getRecords().stream()
                    .map(ViewBlog::getUserId).toList();

            // 处理每个用户的记录
            userIds.forEach(userId -> {
                long count = viewBlogMapper.selectCount(
                        new LambdaQueryWrapper<ViewBlog>()
                                .eq(ViewBlog::getUserId, userId)
                );

                if (count > 500) {
                    int deleteCount = (int) (count - 500);
                    viewBlogMapper.deleteOldestByUser(userId, deleteCount);
                }
            });

            currentPage++;
        } while (page.getRecords().size() == pageSize); // 循环直到处理完所有用户
    }

}
