package com.rocketmq.cloud.youxia.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.rocketmq.cloud.youxia.entity.CategoryEntity;
import com.rocketmq.cloud.youxia.mapper.CategoryMapper;

@Service
public class CategoryManager {
    @Resource
    private CategoryMapper categoryMapper;

    public Integer insert(CategoryEntity categoryEntity) {
        return categoryMapper.insert(categoryEntity);
    }

    public CategoryEntity select(Long categoryCode) {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("categoryCode", categoryCode);
        return categoryMapper.selectOne(queryWrapper);
    }

    public CategoryEntity select(String categoryName) {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", categoryName);
        return categoryMapper.selectOne(queryWrapper);
    }
}
