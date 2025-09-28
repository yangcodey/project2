package com.example.project2.mapper;

import com.example.project2.entity.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReviewMapper {

    /**
     * 获取产品评价列表
     */
    @Select("SELECT r.*, u.real_name as user_name, u.avatar as user_avatar " +
            "FROM reviews r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "WHERE r.product_id = #{productId} " +
            "ORDER BY r.created_at DESC")
    List<Review> getProductReviews(@Param("productId") Long productId);

    /**
     * 插入评价
     */
    @Insert("INSERT INTO reviews (product_id, user_id, rating, content, images, like_count, is_anonymous, created_at, updated_at) " +
            "VALUES (#{productId}, #{userId}, #{rating}, #{content}, #{images, typeHandler=com.example.project2.typehandler.JsonTypeHandler}, " +
            "#{likeCount}, #{isAnonymous}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertReview(Review review);

    /**
     * 更新评价
     */
    @Update("UPDATE reviews SET rating = #{rating}, content = #{content}, images = #{images, typeHandler=com.example.project2.typehandler.JsonTypeHandler}, " +
            "is_anonymous = #{isAnonymous}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    void updateReview(Review review);

    /**
     * 删除评价
     */
    @Delete("DELETE FROM reviews WHERE id = #{id}")
    void deleteReview(@Param("id") Long id);

    /**
     * 增加点赞数
     */
    @Update("UPDATE reviews SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(@Param("id") Long id);

    /**
     * 获取用户评价
     */
    @Select("SELECT r.*, u.real_name as user_name, u.avatar as user_avatar " +
            "FROM reviews r " +
            "LEFT JOIN users u ON r.user_id = u.id " +
            "WHERE r.user_id = #{userId} " +
            "ORDER BY r.created_at DESC")
    List<Review> getUserReviews(@Param("userId") Long userId);
}
