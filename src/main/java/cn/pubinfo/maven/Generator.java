package cn.pubinfo.maven;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.DbType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * @author kuancz
 */
class Generator {

    /**
     * 需要生成的表
     */
    private String[] tables;

    /**
     * 父目录
     */
    private String parent;

    /**
     * 是否生成配置文件
     */
    private boolean useConfig;

    /**
     * 是否已经存在mybatis配置文件
     */
    private boolean isConfigFileExist = false;

    /**
     * 作者
     * */
    private String auther="kuancz";

    /**
     * 模板文件路径
     */
    private static final String BASE_TEMPLATE_PATH = "cn/pubinfo/maven/templates/";

    Generator(List<String> tables, String auther) {
        this.parent = getParent();
        this.useConfig = FileUtil.isFileNotExist(System.getProperty("user.dir") + "/src/main/java", "MybatisPlusConfig.java");
        if (tables!=null&&tables.size()>0){
            this.tables = new String[tables.size()];
            tables.toArray(this.tables);
        }
        if (auther != null){
            this.auther = auther;
        }

    }

    private String getParent() {
        String basePath = System.getProperty("user.dir") + "/src/main/java";
        String appPath = getPath(basePath);
        String[] paths = appPath.split("main\\\\java\\\\")[1].split("\\\\");
        if (paths.length == 1) {
            return paths[0];
        }
        StringBuilder parentPath = new StringBuilder();
        for (String p : Arrays.copyOfRange(paths, 0, paths.length - 1)) {
            parentPath.append(p).append(".");
        }
        return parentPath.substring(0, parentPath.length() - 1);
    }

    private String getPath(String path) {
        File file = new File(path);
        File[] fileList = file.listFiles();
        if (fileList == null) {
            return path;
        }
        for (File f : fileList) {
            if (f.getPath().endsWith(".java")) {
                return f.getPath();
            }
        }
        return getPath(fileList[0].getPath());
    }


    /**
     * 全局配置
     */
    private void globalConfig(AutoGenerator autoGenerator) {
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(System.getProperty("user.dir") + "/src/main/java/");
        gc.setFileOverride(true);
        gc.setActiveRecord(true);
        gc.setEnableCache(false);
        gc.setOpen(false);
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(false);
        gc.setAuthor(auther);
        gc.setServiceName("%sService");
        autoGenerator.setGlobalConfig(gc);
    }

    /**
     * 数据源配置
     */
    private void dataSourceConfig(AutoGenerator autoGenerator) {
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setTypeConvert(new MySqlTypeConvert() {
            // 自定义数据库表字段类型转换【可选】
            @Override
            public DbColumnType processTypeConvert(String fieldType) {
                System.out.println("转换类型：" + fieldType);
                // 注意！！processTypeConvert 存在默认类型转换，如果不是你要的效果请自定义返回、非如下直接返回。
                return super.processTypeConvert(fieldType);
            }
        });
        Map<String, String> dataSource = getDataSource();

        dsc.setDriverName(dataSource.get("driver-class-name"));
        dsc.setUsername(dataSource.get("username"));
        dsc.setPassword(String.valueOf(dataSource.get("password")));
        dsc.setUrl(dataSource.get("url"));
        autoGenerator.setDataSource(dsc);
    }

    /**
     * 策略和包配置
     */
    private void strategyAndPackageConfig(AutoGenerator autoGenerator) {
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setInclude(tables);
        autoGenerator.setStrategy(strategy);
        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(parent);
        pc.setEntity("model");
        pc.setMapper("dao");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        pc.setController("controller");
        autoGenerator.setPackageInfo(pc);
    }

    /**
     * 注入配置
     */
    private void injectionConfig(AutoGenerator autoGenerator) {
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>(1);
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        // mapper
        focList.add(new FileOutConfig("/templates/mapper.xml.vm") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return System.getProperty("user.dir") + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper.xml";
            }
        });
        // config
        if (useConfig) {
            focList.add(new FileOutConfig(BASE_TEMPLATE_PATH + "config.java.vm") {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    return System.getProperty("user.dir") + "/src/main/java/" + parent.replace(".", "/") + "/config/" + "MybatisPlusConfig.java";
                }
            });
        }
        cfg.setFileOutConfigList(focList);
        autoGenerator.setCfg(cfg);
    }

    private void templateEngineConfig(AutoGenerator autoGenerator) {
        autoGenerator.setTemplateEngine(new VelocityClassEngine());
    }

    private void templateConfig(AutoGenerator autoGenerator) {
        TemplateConfig tc = new TemplateConfig();
        tc.setEntity(BASE_TEMPLATE_PATH + "entity.java.vm");
        tc.setService(BASE_TEMPLATE_PATH + "service.java.vm");
        tc.setServiceImpl(BASE_TEMPLATE_PATH + "serviceImpl.java.vm");
        tc.setMapper(BASE_TEMPLATE_PATH + "mapper.java.vm");
        tc.setXml(null);
        tc.setController(null);
        autoGenerator.setTemplate(tc);
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getDataSource() {
        File file = new File(System.getProperty("user.dir") + "/src/main/resources/application.yml");
        Yaml yaml = new Yaml();
        Map<String, Map<String, Object>> map;
        try {
            map = yaml.load(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        isConfigFileExist = map.containsKey("mybatis-plus");
        return (Map<String, String>) map.get("spring").get("datasource");
    }

    private void writeApplicationYml() {
        String contentPath = System.getProperty("user.dir") + "/src/main/resources/application.yml";
        Path path = Paths.get(contentPath);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            if (!isConfigFileExist) {
                writer.write(getDefaultConfig());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void start() {
        AutoGenerator generator = new AutoGenerator();
        templateEngineConfig(generator);
        globalConfig(generator);
        dataSourceConfig(generator);
        strategyAndPackageConfig(generator);
        injectionConfig(generator);
        templateConfig(generator);
        generator.execute();
        writeApplicationYml();
    }

    private String getDefaultConfig() {
        return "\n\nmybatis-plus:\n" +
                "  mapper-locations: classpath:/mapper/*Mapper.xml\n" +
                "  typeAliasesPackage: cn.pubinfo.commentapi.entity\n" +
                "  global-config:\n" +
                "    #主键类型  0:\"数据库ID自增\", 1:\"用户输入ID\",2:\"全局唯一ID (数字类型唯一ID)\", 3:\"全局唯一ID UUID\";\n" +
                "    id-type: 1\n" +
                "    field-strategy: 2\n" +
                "    db-column-underline: true\n" +
                "    refresh-mapper: true\n" +
                "    logic-delete-value: 0\n" +
                "    logic-not-delete-value: 1\n" +
                "  configuration:\n" +
                "    map-underscore-to-camel-case: true\n" +
                "    cache-enabled: false";
    }


}
