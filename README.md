### mybatis-plus 代码生成插件
    mybatis-plus:Mybatis 增强工具包
    http://mp.baomidou.com/
    仅用于代码生成的maven插件， 默认生成model, dao, xml
    service, serviceImpl, 生成但不覆盖
    首次运行， application.yml中添加mybatis-plus配置文件， 添加config/MybatisPlusConfig.java

##### 打包：
     mvn clean install安装；
##### 引入：

    <plugin>
        <groupId>cn.pubinfo</groupId>
        <artifactId>mp-generator</artifactId>
        <version>1.01-SNAPSHOT</version>
        <!--<configuration>-->
            <!--指定表生成， 不填写则生成全部-->
            <!--<tables>-->
                <!--<table>comment</table>-->
            <!--</tables>-->
            <!--作者-->
            <!--<auther>hehe</auther>-->
        <!--</configuration>-->
    </plugin>

##### 使用：
     mp-generator generate
