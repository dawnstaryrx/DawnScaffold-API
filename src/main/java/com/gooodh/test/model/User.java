package com.gooodh.test.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "测试用户实体", description = "测试用户实体description")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Schema(description = "主键id", example = "1")
    private Integer id;
    @Schema(description = "用户名", example = "zhangsan")
    private String name;
}
