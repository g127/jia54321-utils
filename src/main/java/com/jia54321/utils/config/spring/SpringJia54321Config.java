package com.jia54321.utils.config.spring;

import com.jia54321.utils.EnvHelper;
import com.jia54321.utils.entity.dao.CrudDdlDao;
import com.jia54321.utils.entity.dao.DynamicEntityDao;
import com.jia54321.utils.entity.impl.DdlContext;
import com.jia54321.utils.entity.impl.DynamicEntityContext;
import com.jia54321.utils.entity.impl.EntityEnvContext;
import com.jia54321.utils.entity.impl.StorageContext;
import com.jia54321.utils.entity.jdbc.IEntityTemplate;
import com.jia54321.utils.entity.service.context.IDdlContext;
import com.jia54321.utils.entity.service.context.IDynamicEntityContext;
import com.jia54321.utils.entity.service.context.IEntityEnvContext;
import com.jia54321.utils.entity.service.context.IStorageContext;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Role;

import javax.servlet.DispatcherType;
import javax.sql.DataSource;

/**
 * Spring Bean 对象
 */
public class SpringJia54321Config {

    /**
     * 初始化自定义的Bean(SpringUtils)
     *
     * @return SpringUtils
     */
    @Bean("jia54321.springUtils")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static SpringUtils springUtils() {
        return new SpringUtils();
    }

    @Bean
    public IEntityTemplate entityTemplate(DataSource dataSource)
    {
        return new SpringJia54321EntityJdbcTemplate(dataSource);
    }

    @Bean
    public IEntityEnvContext entityEnvContext()
    {
        return new EntityEnvContext();
    }

    @Bean
    public IDynamicEntityContext dynamicEntity(IEntityTemplate entityTemplate)
    {
        return new DynamicEntityContext(new DynamicEntityDao(entityTemplate));
    }

    @Bean
    public IDdlContext ddlContext(IEntityTemplate entityTemplate)
    {
        return new DdlContext(new CrudDdlDao(entityTemplate));
    }

    @Bean
    public IStorageContext storageContext(IEntityEnvContext entityEnvContext, IDynamicEntityContext dynamicEntityContext)
    {
        return new StorageContext(entityEnvContext, dynamicEntityContext);
    }

    @Bean
    public FilterRegistrationBean requestFilterRegistration()
    {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new EnvHelper.RequestFilterHolder());
        registration.setName("EnvHelperRequestFilter");
        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
        return registration;
    }

}
