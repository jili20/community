package com.nowcoder.community.entity;

/**
 * 封闭分页相关的信息
 * @author bing  @create 2020/5/7 1:33 下午
 */
public class Page {
    private Integer current = 1; // 当前页码
    private Integer limit = 10;  // 显示上限
    private Integer rows;  // 数据总数（用于计算总页数 ）
    private String path;   // 查询路径 （ 用于复用分页连接 ）

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        if (current >= 1) {   // 限制查看的页码要大于等于1
            this.current = current;
        }
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        if (limit >= 1 && limit <= 100) {  // 限制用户查询页数
            this.limit = limit;
        }
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        if (rows >= 0) {  // 数据总数要大于 0
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @description: 获取当前页的起始行
     * @return: int
     */
    public int getOffset() {
        return (current - 1) * limit; // current 当前页码 * limit - limit
    }

    /**
     * @description: 获取总页数
     * @return: int
     */
    public int getTotal() {
        if (rows % limit == 0) {   // rows 数据总数 / limit [ + 1 ]
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * @description: 获取起始页码
     * @return: int
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * @description: 获取结束页码
     * @return: int
     */
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
