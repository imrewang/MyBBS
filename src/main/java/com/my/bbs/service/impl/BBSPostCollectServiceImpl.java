package com.my.bbs.service.impl;

import com.my.bbs.dao.BBSPostCollectMapper;
import com.my.bbs.dao.BBSPostMapper;
import com.my.bbs.dao.BBSUserMapper;
import com.my.bbs.entity.BBSPost;
import com.my.bbs.entity.BBSPostCollect;
import com.my.bbs.entity.BBSUser;
import com.my.bbs.service.BBSPostCollectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BBSPostCollectServiceImpl implements BBSPostCollectService {

    @Autowired
    private BBSPostCollectMapper bbsPostCollectMapper;

    @Autowired
    private BBSPostMapper bbsPostMapper;

    @Autowired
    private BBSUserMapper bbsUserMapper;


    ////收藏帖子
    @Override
    @Transactional
    public Boolean addCollectRecord(Long userId, Long postId) {

        BBSPostCollect bbsPostCollect = bbsPostCollectMapper.selectByUserIdAndPostId(userId, postId);

        BBSUser bbsUser = bbsUserMapper.selectByPrimaryKey(userId);

        if (bbsUser == null || bbsUser.getUserStatus().intValue() == 1) {
            //intValue 在扩大原始转换后将此 Byte 的值作为 int 返回。
            //账号已被封禁
            return false;
        }

        if (bbsPostCollect != null) {
            return true;
        } else {
            bbsPostCollect = new BBSPostCollect();
            bbsPostCollect.setPostId(postId);
            bbsPostCollect.setUserId(userId);

            //收藏数量加1
            BBSPost bbsPost = bbsPostMapper.selectByPrimaryKey(postId);

            bbsPost.setPostCollects(bbsPost.getPostCollects() + 1);

            if (bbsPostCollectMapper.insertSelective(bbsPostCollect) > 0 && bbsPostMapper.updateByPrimaryKey(bbsPost) > 0) {
                return true;
            }
        }
        return false;
    }

    ////取消收藏帖子
    @Override
    @Transactional
    public Boolean deleteCollectRecord(Long userId, Long postId) {

        BBSPostCollect bbsPostCollect = bbsPostCollectMapper.selectByUserIdAndPostId(userId, postId);

        BBSUser bbsUser = bbsUserMapper.selectByPrimaryKey(userId);

        if (bbsUser == null || bbsUser.getUserStatus().intValue() == 1) {
            //账号已被封禁
            return false;
        }

        if (bbsPostCollect == null) {
            return true;
        } else {
            //收藏数量减1
            BBSPost bbsPost = bbsPostMapper.selectByPrimaryKey(postId);

            Long collectCount = bbsPost.getPostCollects() - 1;
            if (collectCount >= 0) {
                bbsPost.setPostCollects(collectCount);
            }

            if (bbsPostCollectMapper.deleteByPrimaryKey(bbsPostCollect.getRecordId()) > 0 && bbsPostMapper.updateByPrimaryKey(bbsPost) > 0) {
                return true;
            }
        }
        return false;
    }

    ////验证用户是否收藏了帖子
    @Override
    public Boolean validUserCollect(Long userId, Long postId) {

        BBSPostCollect bbsPostCollect = bbsPostCollectMapper.selectByUserIdAndPostId(userId, postId);

        if (bbsPostCollect == null) {
            return false;
        }
        return true;
    }


    //获取收藏的帖子列表
    @Override
    public List<BBSPost> getCollectRecordsByUserId(Long userId) {

        List<BBSPostCollect> bbsPostCollects = bbsPostCollectMapper.listByUserId(userId);

        if (!CollectionUtils.isEmpty(bbsPostCollects)) {

            List<Long> postIds = bbsPostCollects.stream().map(BBSPostCollect::getPostId).collect(Collectors.toList());
            //stream返回以此集合作为源的顺序 Stream。//返回一个流，该流由将给定函数应用于此流的元素的结果组成。
            //使用 Collector 对此流的元素执行可变缩减操作。

            List<BBSPost> bbsPosts = bbsPostMapper.selectByPrimaryKeys(postIds);
            return bbsPosts;
        }

        return null;
    }
}
