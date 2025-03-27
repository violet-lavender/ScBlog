package com.wyz.blog.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wyz.blog.mapper.BlogGeneralMapper;
import com.wyz.blog.mapper.BlogMapper;
import com.wyz.blog.pojo.domain.Blog;
import com.wyz.blog.pojo.domain.BlogGeneral;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Scheduled 配置类 每12小时更新一次博客的评分
 */
@Slf4j
@Configuration
public class BlogScoreScheduleTask {

    @Resource
    private BlogGeneralMapper blogGeneralMapper;

    @Resource
    private BlogMapper blogMapper;

    // 每12小时执行一次(0时/12时)
    @Scheduled()
    private void updateBlogScore() {
        final int pageSize = 1000; // 分页大小
        int pageNo = 1;

        while (true) {
            // 分页查询博客基础信息
            Page<Blog> page = new Page<>(pageNo, pageSize);
            Page<Blog> blogPage = blogMapper.selectPage(page, new LambdaQueryWrapper<Blog>()
                    .select(Blog::getId, Blog::getCreateTime));
            List<Blog> blogs = blogPage.getRecords();
            if (blogs.isEmpty()) break;

            // 提取ID集合
            List<Integer> blogIds = blogs.stream().map(Blog::getId).toList();

            // 批量查询对应的通用数据
            Map<Integer, BlogGeneral> generalMap = blogGeneralMapper.selectList(new LambdaQueryWrapper<BlogGeneral>()
                    .in(BlogGeneral::getBlogId, blogIds)
            ).stream().collect(Collectors.toMap(
                    BlogGeneral::getBlogId,
                    v -> v
            ));

            // 计算新分数并准备批量更新
            List<BlogGeneral> updates = new ArrayList<>();
            for (Blog blog : blogs) {
                BlogGeneral general = generalMap.get(blog.getId());
                if (general == null) continue; // 异常情况处理

                // 计算时间衰减因子 (λ=0.03)
                long days = Duration.between(
                        (Temporal) blog.getCreateTime(),
                        LocalDateTime.now()
                ).toDays();
                double decay = Math.exp(-0.03 * days);

                // 计算基础互动分 (权重公式)
                double baseScore = general.getLikeNum() * 0.6
                        + general.getCollectionNum() * 0.25
                        + general.getCommentNum() * 0.1
                        + general.getViewNum() * 0.05
                        + 10;   // 初始分10

                // 最终得分
                double newScore = baseScore * decay;

                // 准备更新对象
                BlogGeneral update = new BlogGeneral();
                update.setBlogId(blog.getId());
                update.setScore(newScore);
                updates.add(update);
            }

            // 批量更新分数
            if (!updates.isEmpty()) {
                blogGeneralMapper.batchUpdateScore(updates);
            }

            if (!blogPage.hasNext()) break;
            pageNo++;
        }

    }

}
