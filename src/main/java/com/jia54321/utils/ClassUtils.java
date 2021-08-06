package com.jia54321.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.lang.Nullable;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * ClassUtils
 */
public class ClassUtils {
	static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

	/** Is javax.servlet.ServletRequest available */
	static protected boolean servletRequestIsAvailable = false;
    /** Is org.springframework.core.LocalVariableTableParameterNameDiscoverer available */
    static protected boolean springCoreIsAvailable = false;

    static protected Object parameterNameDiscoverer;
	static {

		// Is ServletRequest Available?
		try {
			servletRequestIsAvailable = null != resolveClassName("javax.servlet.ServletRequest", null);
		} catch (Throwable t) {
			servletRequestIsAvailable = false;
		}

        // Is org.springframework.core Available?
		Class localVariableTableParameterNameDiscoverer = null;
        try {
			localVariableTableParameterNameDiscoverer = resolveClassName("org.springframework.core.LocalVariableTableParameterNameDiscoverer", null);
            springCoreIsAvailable = null != localVariableTableParameterNameDiscoverer;
        } catch (Throwable t) {
            springCoreIsAvailable = false;
        }

        if(springCoreIsAvailable) {
            try {
                parameterNameDiscoverer = localVariableTableParameterNameDiscoverer.newInstance();
            } catch (Throwable e) {
            }
        }
	}
	/** The package separator character: '.' */
	private static final char PACKAGE_SEPARATOR = '.';

	/** The inner class separator character: '$' */
	private static final char INNER_CLASS_SEPARATOR = '$';

	private static final Map<String, Method> METHOD_MAP = new ConcurrentHashMap<>();

	/**
	 * 标识 “当前Class 是否是给定的 Class 的超类或者超接口”。是 返回true，否则返回false。
	 * 标识 “当前Class 是否与定的 Class 的相同”。是 返回true，否则返回false。
	 * 如果 “如果该 Class 表示一个基本类型，且指定的 Class 参数正是该 Class 对象”，是则返回true，否则放回false。
	 * @param cls 当前Class
	 * @param target 给定的 Class
	 * @return 是 返回true，否则返回false
	 */
	public static boolean isAssignableFrom(Class<?> cls, Object target) {
		if(target instanceof Class) {
			return cls.isAssignableFrom((Class<?>)target);
		}
		return cls.isAssignableFrom(target.getClass());
	}

	/**
	 * 标识 “当前Class 是否是给定的 Class 的超类或者超接口”。是 返回true，否则返回false。
	 * 标识 “当前Class 是否与定的 Class 的相同”。是 返回true，否则返回false。
	 * 如果 “如果该 Class 表示一个基本类型，且指定的 Class 参数正是该 Class 对象”，是则返回true，否则放回false。
	 * @param clsName 当前Class
	 * @param target 给定的 Class
	 * @return 是 返回true，否则返回false
	 */
	public static boolean isAssignableFrom(String clsName, Object target) {
		try {
			Class<?> cls = Class.forName(clsName);
			return cls.isAssignableFrom(target.getClass());
		} catch (Exception e) {
		}
		return false;
	}


	private static Object invokeMethod(Method method, Object target, Object... args) {
		try {
			return method.invoke(target, args);
		}
		catch (Exception ex) {
			throw new RuntimeException("方法调用失败", ex.getCause());
		}
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
    /**
     * Map keyed by class containing CachedIntrospectionResults.
     * Needs to be a WeakHashMap with WeakReferences as values to allow
     * for proper garbage collection in case of multiple class loaders.
     */
    private static final Map<Class<?>, BeanInfo> classCache = Collections.synchronizedMap(new WeakHashMap<Class<?>, BeanInfo>());


    /**
     * 获取类本身的PropertyDescriptor[]，不包含父类属性
     *
     * @param clazz
     * @return PropertyDescriptor[]
	 */
    public static PropertyDescriptor[] getSelfPropertyDescriptors(Class<?> clazz) {
        try {
            BeanInfo beanInfo;
            if (classCache.get(clazz) == null) {
                beanInfo = Introspector.getBeanInfo(clazz, clazz.getSuperclass());
                classCache.put(clazz, beanInfo);
                // Immediately remove class from Introspector cache, to allow for proper
                // garbage collection on class loader shutdown - we cache it here anyway,
                // in a GC-friendly manner. In contrast to CachedIntrospectionResults,
                // Introspector does not use WeakReferences as values of its WeakHashMap!
                Class<?> classToFlush = clazz;
                do {
                    Introspector.flushFromCaches(classToFlush);
                    classToFlush = classToFlush.getSuperclass();
                } while (classToFlush != null);
            } else {
                beanInfo = classCache.get(clazz);
            }
            return beanInfo.getPropertyDescriptors();
        } catch (IntrospectionException e) {
            //LOG.error("获取BeanInfo失败", e);
            throw new RuntimeException(e);
        }
    }
//
    /**
     *
     * @param propType
     * @return boolean
     */
    public static boolean isEntityOrDto(Class<?> propType) {
		boolean isPrimitiveOrWrapperOrStrProp = isPrimitiveOrWrapper(propType)
				|| Void.class.equals(propType)
				//

				|| BigDecimal.class.equals(propType)
				|| String.class.equals(propType);;

		boolean isTimeProp = java.sql.Timestamp.class.equals(propType)
				|| java.sql.Date.class.equals(propType)
				|| java.util.Date.class.equals(propType);

		boolean isEnumProp = (null!=propType.getSuperclass()
				&& Enum.class.equals(propType.getSuperclass())) ;

    	return !isPrimitiveOrWrapperOrStrProp && !isTimeProp && !isEnumProp;
    }

	/**
	 * 是否为原生对象，或者包装类
	 * @param propType
	 * @return
	 */
	public static boolean isPrimitiveOrWrapper(Class<?> propType) {
		return propType.isPrimitive()
				//
				|| isAssignableFrom(Boolean.class, propType)
				|| isAssignableFrom(Byte.class, propType)
				|| isAssignableFrom(Character.class, propType)
				|| isAssignableFrom(Double.class, propType)
				|| isAssignableFrom(Float.class, propType)
				|| isAssignableFrom(Integer.class, propType)
				|| isAssignableFrom(Long.class, propType)
				|| isAssignableFrom(Short.class, propType)
				|| isAssignableFrom(BigInteger.class, propType)

				|| isAssignableFrom(boolean.class, propType)
				|| isAssignableFrom(double.class, propType)
				|| isAssignableFrom(float.class, propType)
				|| isAssignableFrom(int.class, propType)
				|| isAssignableFrom(long.class, propType)
				|| isAssignableFrom(short.class, propType);
	}

	/**
     * 是否时间类型字段
     * @param propType  类型
     * @return true or false
     */
    public static boolean isTimePropType(Class<?> propType) {
		return 	isAssignableFrom(java.sql.Timestamp.class, propType)
				|| isAssignableFrom(java.sql.Date.class, propType)
				|| isAssignableFrom(java.util.Date.class, propType);
//		return java.sql.Timestamp.class.getName().equals(propType.getName())
//				|| java.sql.Date.class.getName().equals(propType.getName())
//				|| java.util.Date.class.getName().equals(propType.getName());
    }
    /**
     * 是否数字类型字段
     * @param propType  类型
     * @return true or false
     */
    public static boolean isNumbericPropType(Class<?> propType) {
		return isAssignableFrom(Double.class, propType)
				|| isAssignableFrom(Float.class, propType)
				|| isAssignableFrom(Integer.class, propType)
				|| isAssignableFrom(Long.class, propType)
				|| isAssignableFrom(Short.class, propType)
				|| isAssignableFrom(BigInteger.class, propType)

				|| isAssignableFrom(double.class, propType)
				|| isAssignableFrom(float.class, propType)
				|| isAssignableFrom(int.class, propType)
				|| isAssignableFrom(long.class, propType)
				|| isAssignableFrom(short.class, propType)

				|| isAssignableFrom(BigDecimal.class, propType);
    }

    /**
     * 创建数字类型对象，根据String
     * @param propType
     * @param val
     * @return Object
     */
	public static Object createNumbericPropTypeByVal(Class<?> propType, String val) {
		if (isAssignableFrom(Double.class, propType)) {
			return new Double(val);
		} else if (isAssignableFrom(Float.class, propType)) {
			return new Float(val);
		} else if (isAssignableFrom(Integer.class, propType)) {
			return new Integer(val);
		} else if (isAssignableFrom(Long.class, propType)) {
			return new Long(val);
		} else if (isAssignableFrom(Short.class, propType)) {
			return new Short(val);
		} else if (isAssignableFrom(BigInteger.class, propType)) {
			return new BigInteger(val);

		} else if (isAssignableFrom(double.class, propType)) {
			return new Double(val).doubleValue();
		} else if (isAssignableFrom(float.class, propType)) {
			return new Float(val).floatValue();
		} else if (isAssignableFrom(int.class, propType)) {
			return new Integer(val).intValue();
		} else if (isAssignableFrom(long.class, propType)) {
			return new Long(val).longValue();
		} else if (isAssignableFrom(short.class, propType)) {
			return new Short(val).shortValue();

		} else if (isAssignableFrom(BigDecimal.class, propType)) {
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

		if (	isAssignableFrom(java.sql.Timestamp.class, propType) ) {
			return new java.sql.Timestamp(time.getTime());
		} else if (	isAssignableFrom(java.sql.Date.class, propType)) {
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
	 *
	 * 限制：
	 * 1 在JDK 8之后，可以通过在编译时指定-parameters选项，将方法的参数名记入class文件，并在运行时通过反射机制获取相关信息。
	 *   Java8以前，读取Class中的LocalVariableTable属性表，需要编译时加入参数-g或者-g:vars 获取方法局部变量调试信息；
	 *   Java8及其以后，通过java.lang.reflect.Parameter#getName即可获取，但需要编译时加入参数-parameters参数。
	 * 2 方法名必须在类中唯一
	 * @param target
	 * @param methodName
	 * @param parameters
	 * @return
	 */
	public static Object invokeMethodWithParameters(final Object target, final String methodName, final Map<String, Object> parameters)
	{
        if(!springCoreIsAvailable) {
            return null;
        }

		Object       returnObj  = null;
		Method       method     = getMethodIfAvailable(target, methodName);
		Assert.notNull(target, "target not found !");
		Assert.notNull(method, String.format("%s not found in %s !", methodName, target.getClass().getSimpleName()));
		//
		String[] paraNames = null;
		try {
			Method getParameterNames = parameterNameDiscoverer.getClass().getMethod("getParameterNames", Method.class);
			paraNames = (String[]) getParameterNames.invoke(parameterNameDiscoverer , method);
		} catch (NoSuchMethodException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}
		Map<String, Object> input = new HashMap<>();
		if(parameters != null) {
			input.putAll(parameters);
		}
		final Object[] vals = new Object[paraNames.length];
		for (int i = 0; i < paraNames.length; i++) {
			Object val = input.get(paraNames[i]);
			vals[i] = val;
		}
		return invokeMethodIfAvailable(target, methodName, vals);
	}

	/**
	 * 方法调用
	 * @param target
	 * @param methodName
	 * @param vals
	 * @return Object
	 */
	public static Object invokeMethodIfAvailable(final Object target, final String methodName, final Object... vals) {
		Object returnObj = null;
		Method method = getMethodIfAvailable(target, methodName);
		if (method != null) {
			// parameterTypes
			Class<?>[] parameterTypes = method.getParameterTypes();
			if (vals.length > 0 && parameterTypes.length > 0) {
				Object[] newVals = new Object[vals.length];
				for (int i = 0; i < parameterTypes.length; i++) {
					if( null == vals[i] ) {
						newVals[i] = vals[i];
					}
					// 两个字段类型不一致
					else if( null != vals[i]  && ! vals[i].getClass().equals(parameterTypes[i].getClass()) ) {
						Class<?> propType = parameterTypes[i];
						Object val = vals[i];
						Object newVal = val;
						// 待设置的值为字符串。两个字段类型不一致
						if(vals[i] instanceof String ) {
							if(isTimePropType(propType)) {
								newVal = createTimePropTypeByVal(propType, String.valueOf(val));
							} else if(isNumbericPropType(propType)){
								newVal = createNumbericPropTypeByVal(propType,  String.valueOf(val));
							} else if(Object.class.isAssignableFrom(propType)) {
								newVal = val;
							}
							newVals[i] = newVal;
						}
						// 待设置的值为数字。 两个字段类型不一致
						else if(isNumbericPropType(vals[i].getClass())) {
							newVal = createNumbericPropTypeByVal(propType, String.valueOf(val));
							newVals[i] = newVal;

						}
						// 待设置的值为时间。 两个字段类型不一致
						else if(isTimePropType(vals[i].getClass())) {
							newVal = createTimePropTypeByVal(propType, String.valueOf(val));
						} else {
							newVals[i] = vals[i];
						}
					}
					// 两个字段类型一致
					else if( vals[i].getClass().equals(parameterTypes[i].getClass()) ) {
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

	/**
	 * getMethodIfAvailable
	 * @param target 对象
	 * @param methodName 唯一方法名
	 * @return
	 */
	public static Method getMethodIfAvailable(final Object target, final String methodName) {
		// key
		String key = target.getClass().getName() + "." + methodName;
		if(METHOD_MAP.get(key) != null){
			return METHOD_MAP.get(key);
		}
		//
		Set<Method> candidates = new HashSet<Method>(1);
		Method[]    methods    = target.getClass().getMethods();
		for (Method method : methods) {
			Parameter[] parameters = method.getParameters();
			if (methodName.equals(method.getName())) {
				candidates.add(method);
			}
		}
		if (candidates.size() == 1) {
			Method m = (Method)candidates.iterator().next();
			METHOD_MAP.put(key, m);
			return m;
		}
		if (candidates.size() > 1) {
			log.error(key + " 不唯一");
		}
		if (candidates.size() == 0 ) {
			log.error(key + "不存在");
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

	public static Class<?> resolveClassName(String className, ClassLoader classLoader)
			throws IllegalArgumentException {

		try {
			return forName(className, classLoader);
		}
		catch (IllegalAccessError err) {
			throw new IllegalStateException("Readability mismatch in inheritance hierarchy of class [" +
					className + "]: " + err.getMessage(), err);
		}
		catch (LinkageError err) {
			throw new IllegalArgumentException("Unresolvable class definition for class [" + className + "]", err);
		}
		catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException("Could not find class [" + className + "]", ex);
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
		Assert.hasLength(serviceName, "The serviceName must not be null or empty");
		final int index = serviceName.lastIndexOf('.');
		Assert.isTrue( index > 0, String.format("The serviceName[%s] lastIndexOf('.') must be greater than zero", serviceName));
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
