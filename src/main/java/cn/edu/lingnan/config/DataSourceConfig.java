package cn.edu.lingnan.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @author dingchw
 * @date 2018/12/11.
 */
@Component
@Configuration
@PropertySource(value = {"classpath:conf/db.properties","classpath:conf/datasource.properties"})
public class DataSourceConfig {
    private final static Logger LOG = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${datasource.driverClass}")
    private String driverClass;
    @Value("${datasource.url}")
    private String url;
    @Value("${datasource.username}")
    private String username;
    @Value("${datasource.password}")
    private String password;


    @Value("${hikari.cachePrepStmts}")
    private Boolean cachePrepStmts;
    @Value("${hikari.prepStmtCacheSize}")
    private Integer prepStmtCacheSize;
    @Value("${hikari.prepStmtCacheSqlLimit}")
    private Integer prepStmtCacheSqlLimit;
    @Value("${hikari.useServerPrepStmts}")
    private Boolean useServerPrepStmts;
    @Value("${hikari.useLocalSessionState}")
    private Boolean useLocalSessionState;
    @Value("${hikari.rewriteBatchedStatements}")
    private Boolean rewriteBatchedStatements;
    @Value("${hikari.cacheResultSetMetadata}")
    private Boolean cacheResultSetMetadata;
    @Value("${hikari.cacheServerConfiguration}")
    private Boolean cacheServerConfiguration;
    @Value("${hikari.elideSetAutoCommits}")
    private Boolean elideSetAutoCommits;
    @Value("${hikari.maintainTimeStats}")
    private Boolean maintainTimeStats;

    @Value("${mybatis.basePackage}")
    private String basePackage;

    /**
     * 注入数据源
     * @return
     */
    @Bean
    public DataSource dataSource(){
        LOG.info("application is loading datasource");
        LOG.info("database connection pool is >>> HikariCP");
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(this.driverClass);
        hikariConfig.setJdbcUrl(this.url);
        hikariConfig.setUsername(this.username);
        hikariConfig.setPassword(this.password);

        hikariConfig.addDataSourceProperty("cachePrepStmts",this.cachePrepStmts);
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata",this.cacheResultSetMetadata);
        hikariConfig.addDataSourceProperty("cacheServerConfiguration",this.cacheServerConfiguration);
        hikariConfig.addDataSourceProperty("elideSetAutoCommits",this.elideSetAutoCommits);
        hikariConfig.addDataSourceProperty("maintainTimeStats",this.maintainTimeStats);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize",this.prepStmtCacheSize);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit",this.prepStmtCacheSqlLimit);
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements",this.rewriteBatchedStatements);
        hikariConfig.addDataSourceProperty("useLocalSessionState",this.useLocalSessionState);
        hikariConfig.addDataSourceProperty("useServerPrepStmts",this.useServerPrepStmts);
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        return dataSource;
    }

    @Bean
    public SqlSession sqlSession(){
        SqlSession session = null;
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setTypeAliasesPackage(this.basePackage);
        try {
            SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getObject();
            session = sqlSessionFactory.openSession();
        } catch (Exception e) {
            LOG.info("application creates sessionFactory is failed");
            e.printStackTrace();
        }
        return session;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(){
        // TODO 此处应当如何配置？
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
        mapperScannerConfigurer.setBasePackage(this.basePackage);
        mapperScannerConfigurer.setAnnotationClass(Component.class);
        return mapperScannerConfigurer;
    }

    @Bean
    public DataSourceTransactionManager dataSourceTransactionManager(){
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource());
        return dataSourceTransactionManager;
    }
}
