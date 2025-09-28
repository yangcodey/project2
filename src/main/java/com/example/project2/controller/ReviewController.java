package com.example.project2.controller;

import com.example.project2.entity.Review;
import com.example.project2.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    /**
     * 获取产品评价列表
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getProductReviews(@PathVariable Long productId) {
        try {
            List<Review> reviews = reviewService.getProductReviews(productId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取评价成功");
            response.put("data", reviews);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取评价失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 创建评价
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview(@RequestBody Review review) {
        try {
            Review createdReview = reviewService.createReview(review);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "评价创建成功");
            response.put("data", createdReview);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "评价创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 更新评价
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateReview(@PathVariable Long id, @RequestBody Review review) {
        try {
            review.setId(id);
            Review updatedReview = reviewService.updateReview(review);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "评价更新成功");
            response.put("data", updatedReview);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "评价更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "评价删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "评价删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 点赞评价
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likeReview(@PathVariable Long id) {
        try {
            reviewService.likeReview(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "点赞成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "点赞失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取用户评价
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserReviews(@PathVariable Long userId) {
        try {
            List<Review> reviews = reviewService.getUserReviews(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取用户评价成功");
            response.put("data", reviews);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取用户评价失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取评价统计
     */
    @GetMapping("/stats/{productId}")
    public ResponseEntity<Map<String, Object>> getReviewStats(@PathVariable Long productId) {
        try {
            Map<String, Object> stats = reviewService.getReviewStats(productId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("message", "获取评价统计成功");
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("message", "获取评价统计失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
