package com.example.project2.service;

import com.example.project2.entity.Review;
import com.example.project2.mapper.ReviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    /**
     * 获取产品评价列表
     */
    public List<Review> getProductReviews(Long productId) {
        return reviewMapper.getProductReviews(productId);
    }

    /**
     * 创建评价
     */
    public Review createReview(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        review.setLikeCount(0);
        
        reviewMapper.insertReview(review);
        return review;
    }

    /**
     * 更新评价
     */
    public Review updateReview(Review review) {
        review.setUpdatedAt(LocalDateTime.now());
        reviewMapper.updateReview(review);
        return review;
    }

    /**
     * 删除评价
     */
    public void deleteReview(Long id) {
        reviewMapper.deleteReview(id);
    }

    /**
     * 点赞评价
     */
    public void likeReview(Long id) {
        reviewMapper.incrementLikeCount(id);
    }

    /**
     * 获取用户评价
     */
    public List<Review> getUserReviews(Long userId) {
        return reviewMapper.getUserReviews(userId);
    }

    /**
     * 获取评价统计
     */
    public Map<String, Object> getReviewStats(Long productId) {
        List<Review> reviews = getProductReviews(productId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReviews", reviews.size());
        
        if (reviews.isEmpty()) {
            stats.put("averageRating", 0.0);
            stats.put("ratingDistribution", new int[]{0, 0, 0, 0, 0});
            return stats;
        }
        
        // 计算平均评分
        double totalRating = reviews.stream().mapToInt(Review::getRating).sum();
        double averageRating = totalRating / reviews.size();
        stats.put("averageRating", Math.round(averageRating * 10.0) / 10.0);
        
        // 计算评分分布
        int[] ratingDistribution = new int[5];
        for (Review review : reviews) {
            if (review.getRating() >= 1 && review.getRating() <= 5) {
                ratingDistribution[review.getRating() - 1]++;
            }
        }
        stats.put("ratingDistribution", ratingDistribution);
        
        return stats;
    }
}
