package com.jia54321.utils.jfinal.activerecord.generator;

import com.jfinal.plugin.activerecord.generator.TableMeta;

import java.util.List;

/**
 *
 */
public interface ITemplateGenerator<S extends TableMeta> {

    void generate(List<S> tableMetaExtends);

}
