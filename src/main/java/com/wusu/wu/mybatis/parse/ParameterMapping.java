package com.wusu.wu.mybatis.parse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author github.com/solano33
 * @date 2024/5/15 22:57
 */
@AllArgsConstructor
@Getter
@Setter
public class ParameterMapping {

  /**
   * 这里保存了sql中#{name}的name
   */
  private String property;


}
