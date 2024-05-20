package com.wusu.wu.mybatis.parse;

/**
 * @author github.com/solano33
 * @date 2024/5/15 22:57
 */
public interface TokenHandler {

    /**
     * 这里支持解析不同类型
     * @param content
     * @return
     */
    String handleToken(String content);
}
