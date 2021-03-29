package com.jia54321.utils.entity.converter;

/**
 * IConverter
 * @author gg
 * @date 2019-08-25
 *
 * @param <S>
 * @param <T>
 */
public abstract interface IConverter<S, T> {
	public abstract T convert(S paramS);
}