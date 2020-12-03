package com.jia54321.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;

import java.beans.Introspector;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * ClassUtils
 */
public class ClassUtils {

	/** The package separator character: '.' */
	private static final char PACKAGE_SEPARATOR = '.';

	/** The inner class separator character: '$' */
	private static final char INNER_CLASS_SEPARATOR = '$';


	private static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			return method.invoke(target, args);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		throw new IllegalStateException("Should never get here");
	}

//
//    /**
//     * 获取类本身的PropertyDescriptor，包含父类属性
//     *
//     * @param clazz
//     * @return PropertyDescriptor[]
//     */
//    public static PropertyDescriptor[]  getPropertyDescriptors(Class<?> clazz) {
////    	return BeanUtils.getPropertyDescriptors(clazz);
//		return null;
//    }
//
//    /**
//     * Map keyed by class containing CachedIntrospectionResults.
//     * Needs to be a WeakHashMap with WeakReferences as values to allow
//     * for proper garbage collection in case of multiple class loaders.
//     */
//    private static final Map<Class<?>, BeanInfo> classCache = Collections.synchronizedMap(new WeakHashMap<Class<?>, BeanInfo>());
//
//
//    /**
//     * 获取类本身的PropertyDescriptor[]，不包含父类属性
//     *
//     * @param clazz
//     * @return PropertyDescriptor[]
//	 */
//    public static PropertyDescriptor[] getSelfPropertyDescriptors(Class<?> clazz) {
//        try {
//            BeanInfo beanInfo;
//            if (classCache.get(clazz) == null) {
//                beanInfo = Introspector.getBeanInfo(clazz, clazz.getSuperclass());
//                classCache.put(clazz, beanInfo);
//                // Immediately remove class from Introspector cache, to allow for proper
//                // garbage collection on class loader shutdown - we cache it here anyway,
//                // in a GC-friendly manner. In contrast to CachedIntrospectionResults,
//                // Introspector does not use WeakReferences as values of its WeakHashMap!
//                Class<?> classToFlush = clazz;
//                do {
//                    Introspector.flushFromCaches(classToFlush);
//                    classToFlush = classToFlush.getSuperclass();
//                } while (classToFlush != null);
//            } else {
//                beanInfo = classCache.get(clazz);
//            }
//            return beanInfo.getPropertyDescriptors();
//        } catch (IntrospectionException e) {
//            //LOG.error("获取BeanInfo失败", e);
//            throw new RuntimeException(e);
//        }
//    }
//
    /**
     *
     * @param propType
     * @return boolean
     */
    public static boolean isEntityOrDto(Class<?> propType) {
		boolean isPrimitiveOrWrapperOrStrProp = propType.isPrimitive()
				//
		        || Boolean.class.equals(propType)
				|| Byte.class.equals(propType)
				|| Character.class.equals(propType)
				|| Double.class.equals(propType)
				|| Float.class.equals(propType)
				|| Integer.class.equals(propType)
				|| Long.class.equals(propType)
				|| Short.class.equals(propType)
				|| Void.class.equals(propType)
				//

				|| BigDecimal.class.equals(propType)
				|| String.class.equals(propType);

		boolean isTimeProp = java.sql.Timestamp.class.equals(propType)
				|| java.sql.Date.class.equals(propType)
				|| java.util.Date.class.equals(propType);

		boolean isEnumProp = (null!=propType.getSuperclass()
				&& Enum.class.equals(propType.getSuperclass())) ;

    	return !isPrimitiveOrWrapperOrStrProp && !isTimeProp && !isEnumProp;
    }
    
    /**
     * 是否时间类型字段
     * @param propType  类型
     * @return true or false
     */
    public static boolean isTimePropType(Class<?> propType) {
		return java.sql.Timestamp.class.getName().equals(propType.getName())
				|| java.sql.Date.class.getName().equals(propType.getName())
				|| java.util.Date.class.getName().equals(propType.getName());
    }
    /**
     * 是否数字类型字段
     * @param propType  类型
     * @return true or false
     */
    public static boolean isNumbericPropType(Class<?> propType) {
		return Double.class.getName().equals(propType.getName())
				|| Float.class.getName().equals(propType.getName())
				|| Integer.class.getName().equals(propType.getName())
				|| Long.class.getName().equals(propType.getName())
				|| Short.class.getName().equals(propType.getName())

				|| double.class.getName().equals(propType.getName())
				|| float.class.getName().equals(propType.getName())
				|| int.class.getName().equals(propType.getName())
				|| long.class.getName().equals(propType.getName())
				|| short.class.getName().equals(propType.getName())

				|| BigDecimal.class.getName().equals(propType.getName());
    }

    /**
     * 创建数字类型对象，根据String
     * @param propType
     * @param val
     * @return Object
     */
	public static Object createNumbericPropTypeByVal(Class<?> propType, String val) {
		if (Double.class.getName().equals(propType.getName())) {
			return new Double(val);
		} else if (Float.class.getName().equals(propType.getName())) {
			return new Float(val);
		} else if (Integer.class.getName().equals(propType.getName())) {
			return new Integer(val);
		} else if (Long.class.getName().equals(propType.getName())) {
			return new Long(val);
		} else if (Short.class.getName().equals(propType.getName())) {
			return new Short(val);

		} else if (double.class.getName().equals(propType.getName())) {
			return new Double(val).doubleValue();
		} else if (float.class.getName().equals(propType.getName())) {
			return new Float(val).floatValue();
		} else if (int.class.getName().equals(propType.getName())) {
			return new Integer(val).intValue();
		} else if (long.class.getName().equals(propType.getName())) {
			return new Long(val).longValue();
		} else if (short.class.getName().equals(propType.getName())) {
			return new Short(val).shortValue();

		} else if (BigDecimal.class.getName().equals(propType.getName())) {
			return new BigDecimal(val);
		}

		return null;
	}

    /**
     * 创建时间类型对象，根据String
     * @param propType
     * @param val
     * @return Object
     */
	public static Object createTimePropTypeByVal(Class<?> propType, String val) {
		java.util.Date time = DateUtil.toTimestamp(val);

		if (null == time) {
			return null;
		}

		if (java.sql.Timestamp.class.getName().equals(propType.getName())) {
			return new java.sql.Timestamp(time.getTime());
		} else if (java.sql.Date.class.getName().equals(propType.getName())) {
			return new java.sql.Date(time.getTime());
		} else {
			return time;
		}
	}

    /**
     * get方法读取属性
     * @param target
     * @param propName
     * @return Object
     */
	public static Object invokeBeanReadMethodIfAvailable(final Object target, final String propName){
		if (null == propName) {
            return null;
        }
		String methodName = "get" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
		return invokeMethodIfAvailable(target, methodName, new Object[0]);
	}

	/**
	 * set方法设置属性
	 * @param target
	 * @param propName
	 * @param propVal
	 */
	public static void invokeBeanWriteMethodIfAvailable(final Object target, final String propName, final Object propVal){
		String methodName = "set" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
		invokeMethodIfAvailable(target, methodName, propVal);
	}

	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 */
	public static Object getFieldValue(final Object obj, final String fieldName) {
		Field field = getAccessibleField(obj, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
		}

		Object result = null;
		try {
			result = field.get(obj);
		} catch (IllegalAccessException e) {
			// logger.error("不可能抛出的异常{}", e.getMessage());
		}
		return result;
	}

	/**
	 * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
	 */
	public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
		Field field = getAccessibleField(obj, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
		}

		try {
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			// logger.error("不可能抛出的异常:{}", e.getMessage());
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
	 *
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	public static Field getAccessibleField(final Object obj, final String fieldName) {
		if (obj == null) {
			throw new IllegalArgumentException("object can't be null");
		}
		if (fieldName == null) {
			throw new IllegalArgumentException("fieldName can't be blank");
		}
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				makeAccessible(field);
				return field;
			} catch (NoSuchFieldException e) {// NOSONAR
				// Field不在当前类定义,继续向上转型
			}
		}
		return null;
	}


	/**
	 * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
	 */
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
				.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * 方法调用
	 * @param target
	 * @param methodName
	 * @param vals
	 * @return Object
	 */
	public static Object invokeMethodIfAvailable(final Object target, final String methodName, final Object... vals){
		Object returnObj = null;
		Method method = getMethodIfAvailable(target, methodName);
		if (method != null) {
			if (vals.length > 0 && method.getParameterTypes().length > 0) {
				Object[] newVals = new Object[vals.length];
				for (int i = 0; i < method.getParameterTypes().length; i++) {
					//
					if(vals[i] instanceof String
							&& !String.class.getName().equals(method.getParameterTypes()[i].getName())) {
						Class<?> propType = method.getParameterTypes()[i];
						String val = (String)vals[i];
						Object newVal = val;
						if(isTimePropType(propType)) {
							newVal = createTimePropTypeByVal(propType, val);
						} else if(isNumbericPropType(propType)){
							newVal = createNumbericPropTypeByVal(propType, val);
						} else if(Object.class.getName().equals(propType.getName())){
							newVal = val;
						}
						newVals[i] = newVal;
					} else {
						newVals[i] = vals[i];
					}

				}
				returnObj = invokeMethod(method, target, newVals);
			} else {
				returnObj = invokeMethod(method, target, new Object[]{});
			}
		}
		return returnObj;
	}

	public static Method getMethodIfAvailable(final Object target, final String methodName){
		 Set<Method> candidates = new HashSet<Method>(1);
		 Method[] methods = target.getClass().getMethods();
		 for (Method method : methods) {
		   if (methodName.equals(method.getName())) {
		     candidates.add(method);
		   }
		 }
		 if (candidates.size() == 1) {
		   return (Method)candidates.iterator().next();
		 }
		return null;
	}

    public static Class<?> getSuperClassGenericType(Class<?> c){
		return getSuperClassGenericType(c, 0);
    }

	public static Class<?> getSuperClassGenericType(Class<?> c, int index) {
		Type t = c.getGenericSuperclass();
		if (!(t instanceof ParameterizedType)) {
			return Object.class;
		}

		Type[] params = ((ParameterizedType) t).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			return Object.class;
		}

		if (!(params[index] instanceof Class)) {
			return Object.class;
		}

		return (Class<?>) params[index];
	}

	public static Method getStaticMethod(Class<?> clazz, String methodName, Class<?>... args)
			throws ClassNotFoundException, LinkageError {
//		Set<Method> candidates = new HashSet<Method>(1);
//		Method[] methods = clazz.getMethods();
		try {
//			for (Method method : methods) {
//				if (methodName.equals(method.getName())) {
//					candidates.add(method);
//				}
//			}
//			if (candidates.size() == 1) {
//				return (Method)candidates.iterator().next();
//			}
			Method method = clazz.getMethod(methodName, args);
			return Modifier.isStatic(method.getModifiers()) ? method : null;
		}
		catch (NoSuchMethodException ex) {
			return null;
		}
	}

	/**
	 * 获取属性值
	 *
	 * @param name 类名.方法名
	 * @param args 参数
	 * @return Object
	 */
	public static Object invokeStaticMethod(String name, Object... args) {
		final String[] names = getServiceNameAndMethodName(name);
		final String serviceName = names[0];
		final String methodName = names[1];
		try {
			Class<?> clazz = forName(serviceName, null);
			Method method = getStaticMethod(clazz, methodName, new Class[0]);
			if (method == null) {
				return null;
			}
			if (!Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
				method.setAccessible(true);
			}
			return method.invoke(null, args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return the default ClassLoader to use: typically the thread context
	 * ClassLoader, if available; the ClassLoader that loaded the ClassUtils
	 * class will be used as fallback.
	 * <p>Call this method if you intend to use the thread context ClassLoader
	 * in a scenario where you clearly prefer a non-null ClassLoader reference:
	 * for example, for class path resource loading (but not necessarily for
	 * {@code Class.forName}, which accepts a {@code null} ClassLoader
	 * reference as well).
	 * @return the default ClassLoader (only {@code null} if even the system
	 * ClassLoader isn't accessible)
	 * @see Thread#getContextClassLoader()
	 * @see ClassLoader#getSystemClassLoader()
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ClassUtils.class.getClassLoader();
			if (cl == null) {
				// getClassLoader() returning null indicates the bootstrap ClassLoader
				try {
					cl = ClassLoader.getSystemClassLoader();
				}
				catch (Throwable ex) {
					// Cannot access system ClassLoader - oh well, maybe the caller can live with null...
				}
			}
		}
		return cl;
	}

	public static Class<?> forName(String name, ClassLoader classLoader)
		 throws ClassNotFoundException, LinkageError{
		ClassLoader clToUse = classLoader;
		if (clToUse == null) {
			clToUse = getDefaultClassLoader();
		}
		try {
			return (clToUse != null ? clToUse.loadClass(name) : Class.forName(name));
		}
		catch (ClassNotFoundException ex) {
			int lastDotIndex = name.lastIndexOf(PACKAGE_SEPARATOR);
			if (lastDotIndex != -1) {
				String innerClassName =
						name.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR + name.substring(lastDotIndex + 1);
				try {
					return (clToUse != null ? clToUse.loadClass(innerClassName) : Class.forName(innerClassName));
				}
				catch (ClassNotFoundException ex2) {
					// Swallow - let original exception get through
				}
			}
			throw ex;
		}
	}
	
	/**
	 * 获取类名和方法名.
	 *
	 * @param serviceName
	 *            服务名
	 * @return [0] 类名 [1] 方法名
	 */
	public static String[] getServiceNameAndMethodName(final String serviceName) {
		final int index = serviceName.lastIndexOf('.');
		final String className = serviceName.substring(0, index);
		final String methodName = serviceName.substring(index + 1);
		return new String[] { className, methodName };
	}

    /**
     * 初始化实例
     *
     * @param clazz
     * @return Object
     */
    public static Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            //LOG.error("根据class创建实例失败", e);
        	throw new RuntimeException(e);
        }
    }

	public static Object newInstance(Class<?> clazz, Object... args) {
		Class<?>[] argTypes = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			argTypes[i] = args[i].getClass();
		}
		Constructor<?> ctor = getConstructor(clazz, argTypes);
		return instantiate(ctor, args);
	}

	public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... argTypes) {
		try {
			return clazz.getConstructor(argTypes);
		} catch (NoSuchMethodException e) {
			throw new IllegalStateException(e);
		}

	}

	public static Object instantiate(Constructor<?> ctor, Object... args) {
		try {
			return ctor.newInstance(args);
		} catch (Exception e) {
			String msg = "Unable to instantiate Permission instance with constructor [" + ctor + "]";
			throw new RuntimeException(msg, e);
		}
	}


}
