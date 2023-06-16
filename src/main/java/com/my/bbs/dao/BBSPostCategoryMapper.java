package com.my.bbs.dao;

import com.my.bbs.entity.BBSPostCategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface BBSPostCategoryMapper {
    int deleteByPrimaryKey(Integer categoryId);

    int insert(BBSPostCategory record);

    int insertSelective(BBSPostCategory record);

    BBSPostCategory selectByPrimaryKey(Integer categoryId);

    int updateByPrimaryKeySelective(BBSPostCategory record);

    int updateByPrimaryKey(BBSPostCategory record);

    List<BBSPostCategory> getBBSPostCategories();
}