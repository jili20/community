package com.nowcoder.community.dao;

import com.nowcoder.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @author bing  @create 2020/5/9 3:05 下午
 */
@Repository
@Mapper
public interface LoginTicketMapper {
    // 查询数据
    @Insert({
            "insert into login_ticket(user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true,keyProperty = "id")  // id 为自键，自增长
    int insertLoginTicket(LoginTicket loginTicket);

    // 查询数据
    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);

    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket} "
    })
    int updateStatus(String ticket, int status); // 更新数据
}
























