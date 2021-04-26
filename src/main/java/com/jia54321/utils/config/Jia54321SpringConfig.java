package com.jia54321.utils.config;

import com.jia54321.utils.EnvHelper;
import com.jia54321.utils.entity.dao.DynamicEntityDao;
import com.jia54321.utils.entity.impl.DynamicEntityContext;
import com.jia54321.utils.entity.impl.EntityEnvContext;
import com.jia54321.utils.entity.impl.StorageContext;
import com.jia54321.utils.entity.jdbc.IEntityTemplate;
import com.jia54321.utils.entity.service.context.IDynamicEntityContext;
import com.jia54321.utils.entity.service.context.IEntityEnvContext;
import com.jia54321.utils.entity.service.context.IStorageContext;
import org.apache.http.entity.EntityTemplate;

import javax.servlet.DispatcherType;
import javax.sql.DataSource;

public class Jia54321SpringConfig {

//    @Bean
//    public IEntityTemplate entityTemplate(DataSource dataSource)
//    {
//        return new EntityTemplate(dataSource);
//    }
//
//    @Bean
//    public IEntityEnvContext entityEnvContext()
//    {
//        return new EntityEnvContext();
//    }
//
//    @Bean
//    public IDynamicEntityContext dynamicEntity(IEntityTemplate entityTemplate)
//    {
//        return new DynamicEntityContext(new DynamicEntityDao(entityTemplate));
//    }
//
//    @Bean
//    public IStorageContext storageContext(IEntityEnvContext entityEnvContext, IDynamicEntityContext dynamicEntityContext)
//    {
//        return new StorageContext(entityEnvContext, dynamicEntityContext);
//    }
//
//    @Bean
//    public FilterRegistrationBean requestFilterRegistration()
//    {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setDispatcherTypes(DispatcherType.REQUEST);
//        registration.setFilter(new EnvHelper.RequestFilterHolder());
//        registration.setName("EnvHelperRequestFilter");
//        registration.setOrder(FilterRegistrationBean.HIGHEST_PRECEDENCE);
//        return registration;
//    }

}
