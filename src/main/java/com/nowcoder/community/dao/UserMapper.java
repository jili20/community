package com.nowcoder.community.dao;

import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author bing  @create 2020/5/6 4:53 下午
 */
@Repository
@Mapper
public interface UserMapper {

    User selectById(int id);
    User selectByName(String username);
    User selectByEmail(String email);
    int insertUser(User user);
    int updateStatus(int id,int status);
    int updateHeader(int id,String headerUrl);
    int updatePassword(int id, String password);

}
