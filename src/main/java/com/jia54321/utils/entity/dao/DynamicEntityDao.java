package com.jia54321.utils.entity.dao;

import com.jia54321.utils.entity.DynamicEntity;
import com.jia54321.utils.entity.jdbc.IEntityTemplate;
import com.jia54321.utils.entity.query.QueryContent;

import java.util.ArrayList;
import java.util.List;

public class DynamicEntityDao extends CrudDao<DynamicEntity> {

    public DynamicEntityDao(IEntityTemplate template) {
        super(template);
    }
}
